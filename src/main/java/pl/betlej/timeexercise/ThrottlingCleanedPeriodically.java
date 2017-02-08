package pl.betlej.timeexercise;

import java.util.concurrent.*;

public class ThrottlingCleanedPeriodically extends Throttling
{

    private static final int PERIOD_OF_EVICTION = 10;
    private final ScheduledExecutorService scheduledExecutorService;

    public ThrottlingCleanedPeriodically()
    {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(daemonThreadFactory());
        scheduledExecutorService.scheduleAtFixedRate(this::cleanTheQueue, 0, PERIOD_OF_EVICTION, TimeUnit.MILLISECONDS);
    }

    private ThreadFactory daemonThreadFactory()
    {
        return (r)->{
            Thread thread = Executors.defaultThreadFactory().newThread(r);
            thread.setDaemon(true);
            return thread;
        };
    }

    @Override
    public boolean accept()
    {
        if (serverOverloaded())
        {
            return false;
        }
        getTasksReceived().add(System.currentTimeMillis());
        return true;
    }

    public static void main(String[] args)
    {
        Throttling.throttlingSampleUsage(new ThrottlingCleanedPeriodically());
    }


}
