package pl.betlej.timeexercise;

import java.util.concurrent.LinkedBlockingQueue;

public class LinkedBlockingQueueThrottling extends Throttling
{

    public LinkedBlockingQueueThrottling()
    {
        super(new LinkedBlockingQueue<>(THROTTLING_REQUESTS_PER_UNIT));
    }

      @Override
    public boolean accept()
    {
        cleanTheQueue();
        return registerTask();
    }

    public static void main(String[] args)
    {
        Throttling.throttlingSampleUsage(new LinkedBlockingQueueThrottling());
    }
}
