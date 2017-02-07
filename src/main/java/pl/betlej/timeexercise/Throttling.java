package pl.betlej.timeexercise;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public abstract class Throttling
{
    private final ConcurrentLinkedQueue<Long> tasksReceived;
    int TASK_ACTIVE_MILLISECONDS = 1000;
    int THROTTLING_REQUESTS_PER_UNIT = 10;

    public Throttling()
    {
        tasksReceived = new ConcurrentLinkedQueue<>();
    }

    public abstract boolean accept();


    protected ConcurrentLinkedQueue<Long> getTasksReceived()
    {
        return tasksReceived;
    }

    protected boolean serverOverloaded()
    {
        return getTasksReceived().size() >= THROTTLING_REQUESTS_PER_UNIT;
    }

    protected static void throttlingSampleUsage(final Throttling throttlingCleanedPeriodically)
    {
        long start = System.currentTimeMillis();
        long numberOfRequestsAccepted = IntStream.range(0, 10_000)
                .parallel()
                .peek((x) -> slowDown())
                .mapToObj(x -> throttlingCleanedPeriodically.accept())
                .filter(x -> x).count();
        System.out.println("numberOfRequestsAccepted: " + numberOfRequestsAccepted);
        System.out.println("time in seconds: " + (System.currentTimeMillis() - start) / 1000);
    }

    private static void slowDown()
    {
        try
        {
            Thread.sleep(ThreadLocalRandom.current().nextLong(10L));
        } catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        }
    }
}
