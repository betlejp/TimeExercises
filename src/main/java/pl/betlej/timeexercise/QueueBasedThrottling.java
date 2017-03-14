package pl.betlej.timeexercise;

import java.time.Clock;
import java.util.Queue;
import java.util.function.Predicate;

public abstract class QueueBasedThrottling implements Throttling
{
    private final Queue<Long> tasksReceived;
    private final Clock clock;


    QueueBasedThrottling(Queue<Long> tasksReceived)
    {
        this.tasksReceived = tasksReceived;
        clock = Clock.systemUTC();
    }

    private Queue<Long> getTaskQueue()
    {
        return tasksReceived;
    }

    protected boolean serverOverloaded()
    {
        return getTaskQueue().size() >= MAX_REQUESTS_PER_UNIT;
    }

    protected void cleanTheQueue()
    {
        getTaskQueue().removeIf(taskInactive());
    }

    private Predicate<Long> taskInactive()
    {
        return (taskAccepted) -> clock.millis() - taskAccepted > TASK_ACTIVE_MILLISECONDS;
    }

    protected boolean offerTaskToQueue()
    {
        return getTaskQueue().offer(clock.millis());
    }
}
