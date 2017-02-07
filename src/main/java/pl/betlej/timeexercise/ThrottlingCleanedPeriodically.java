package pl.betlej.timeexercise;

import java.util.concurrent.*;

public class ThrottlingCleanedPeriodically implements Throttling
{


    private static final int PERIOD_OF_EVICTION = 10;
    private final ConcurrentLinkedQueue<Long> tasksReceived;
    private final ScheduledExecutorService scheduledExecutorService;

    public ThrottlingCleanedPeriodically()
    {
        tasksReceived = new ConcurrentLinkedQueue<>();
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(() -> tasksReceived.removeIf((peek) -> System.currentTimeMillis() - peek > TASK_ACTIVE_MILLISECONDS), 0, PERIOD_OF_EVICTION, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean accept()
    {
        if (serverOverloaded())
        {
            return false;
        }
        tasksReceived.add(System.currentTimeMillis());
        return true;
    }

    private boolean serverOverloaded()
    {
        return tasksReceived.size() >= THROTTLING_REQUESTS_PER_UNIT;
    }

    private ScheduledExecutorService getScheduledExecutorService()
    {
        return scheduledExecutorService;
    }

    public static void main(String[] args)
    {
        ThrottlingCleanedPeriodically throttlingCleanedPeriodically = new ThrottlingCleanedPeriodically();
        Throttling.throttlingSampleUsage(throttlingCleanedPeriodically);
        throttlingCleanedPeriodically.getScheduledExecutorService().shutdown();
    }


}
