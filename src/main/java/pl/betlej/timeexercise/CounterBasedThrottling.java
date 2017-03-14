package pl.betlej.timeexercise;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class CounterBasedThrottling implements Throttling
{
    private AtomicInteger counter = new AtomicInteger(0);
    private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new DaemonThreadFactory());

    @Override
    public boolean accept()
    {
        if (counter.get() >= MAX_REQUESTS_PER_UNIT)
        {
            return false;
        }
        counter.incrementAndGet();
        registerDecrement();
        return true;
    }

    private void registerDecrement()
    {
        scheduledExecutorService.schedule(() -> counter.decrementAndGet(), TASK_ACTIVE_MILLISECONDS, TimeUnit.MILLISECONDS);
    }

    public static void main(String[] args)
    {
        Throttling.throttlingSampleUsage(new CounterBasedThrottling());
    }
}
