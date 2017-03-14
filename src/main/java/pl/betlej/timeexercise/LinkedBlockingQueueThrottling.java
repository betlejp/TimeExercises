package pl.betlej.timeexercise;

import java.util.concurrent.LinkedBlockingQueue;

public class LinkedBlockingQueueThrottling extends QueueBasedThrottling
{

    public LinkedBlockingQueueThrottling()
    {
        super(new LinkedBlockingQueue<>(MAX_REQUESTS_PER_UNIT));
    }

    @Override
    public boolean accept()
    {
        cleanTheQueue();
        return offerTaskToQueue();
    }

    public static void main(String[] args)
    {
        Throttling.throttlingSampleUsage(new LinkedBlockingQueueThrottling());
    }
}
