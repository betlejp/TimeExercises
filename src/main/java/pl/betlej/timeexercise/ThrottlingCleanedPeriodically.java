package pl.betlej.timeexercise;

import java.util.concurrent.*;

public class ThrottlingCleanedPeriodically extends QueueBasedThrottling
{

    private static final int PERIOD_OF_EVICTION = 10;

    public ThrottlingCleanedPeriodically()
    {
        super(new ConcurrentLinkedQueue<>());
        Executors.newSingleThreadScheduledExecutor(new DaemonThreadFactory())
                .scheduleAtFixedRate(this::cleanTheQueue, 0, PERIOD_OF_EVICTION, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean accept()
    {
        return !serverOverloaded() && offerTaskToQueue();
    }

    public static void main(String[] args)
    {
        Throttling.throttlingSampleUsage(new ThrottlingCleanedPeriodically());
    }


}
