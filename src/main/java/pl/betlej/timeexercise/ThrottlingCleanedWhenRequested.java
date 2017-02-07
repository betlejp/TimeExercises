package pl.betlej.timeexercise;

import java.util.concurrent.ConcurrentLinkedQueue;

public class ThrottlingCleanedWhenRequested implements Throttling
{
    private final ConcurrentLinkedQueue<Long> tasksReceived;

    public ThrottlingCleanedWhenRequested()
    {
        tasksReceived = new ConcurrentLinkedQueue<>();
    }

    @Override
    public boolean accept()
    {
        if (serverOverloaded())
        {
            tryCleaningTheQueue();
            if (serverOverloaded())
            {
                return false;
            }
        }
        tasksReceived.add(System.currentTimeMillis());
        return true;
    }

    private void tryCleaningTheQueue()
    {
        tasksReceived.removeIf((peek) -> System.currentTimeMillis() - peek > TASK_ACTIVE_MILLISECONDS);
    }

    private boolean serverOverloaded()
    {
        return tasksReceived.size() >= THROTTLING_REQUESTS_PER_UNIT;
    }

    public static void main(String[] args)
    {
        Throttling.throttlingSampleUsage(new ThrottlingCleanedWhenRequested());
    }
}
