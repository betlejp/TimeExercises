package pl.betlej.timeexercise;

import java.util.concurrent.*;

public class ThrottlingCleanedPeriodically extends Throttling
{

    private static final int PERIOD_OF_EVICTION = 10;
    private final ScheduledExecutorService scheduledExecutorService;

    public ThrottlingCleanedPeriodically()
    {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(() -> getTasksReceived().removeIf((peek) -> System.currentTimeMillis() - peek > TASK_ACTIVE_MILLISECONDS), 0, PERIOD_OF_EVICTION, TimeUnit.MILLISECONDS);
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

    public void shutdown()
    {
        scheduledExecutorService.shutdown();
    }

    public static void main(String[] args)
    {
        ThrottlingCleanedPeriodically throttlingCleanedPeriodically = new ThrottlingCleanedPeriodically();
        Throttling.throttlingSampleUsage(throttlingCleanedPeriodically);
        throttlingCleanedPeriodically.shutdown();
    }


}
