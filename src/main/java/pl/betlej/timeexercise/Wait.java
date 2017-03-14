package pl.betlej.timeexercise;

import java.time.Clock;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class Wait<T>
{
    private static final int DEFAULT_INTERVAL_COUNT = 1;
    private static final TimeUnit DEFAULT_INTERVAL_UNIT = TimeUnit.MILLISECONDS;
    public static final int NO_TIMEOUT = Integer.MIN_VALUE;
    private TimeUnit intervalUnit;
    private int intervalCount;
    private final T objectWaitedFor;
    private Instant start;
    private final Clock clock;
    private final Sleeper sleeper;
    private long maxNano;

    Wait(T objectWaitedFor, Clock clock, Sleeper sleeper)
    {
        this.objectWaitedFor = objectWaitedFor;
        this.clock = clock;
        this.sleeper = sleeper;
    }

    private Wait(T objectWaitedFor)
    {
        this.objectWaitedFor = objectWaitedFor;
        this.clock = Clock.systemUTC();
        this.sleeper = new DefaultSleeper();
        this.intervalCount = DEFAULT_INTERVAL_COUNT;
        this.intervalUnit = DEFAULT_INTERVAL_UNIT;
        this.maxNano = NO_TIMEOUT;

    }

    public static <T> Wait<T> forItem(T testObject)
    {
        return new Wait(testObject);
    }

    public Wait atIntervals(TimeUnit intervalUnit, int intervalCount)
    {
        this.intervalUnit = intervalUnit;
        this.intervalCount = intervalCount;

        return this;
    }

    public boolean waitUntil(Predicate<T> condition)
    {
        this.start = this.clock.instant();
        boolean conditionMet;
        while (!(conditionMet = objectMeetsCondition(condition)) && notTimedOut())
        {
            this.sleeper.doSleep(intervalUnit.toMillis(intervalCount));
        }
        return conditionMet;
    }

    private boolean objectMeetsCondition(Predicate<T> condition)
    {
        return condition.test(objectWaitedFor);
    }

    private boolean notTimedOut()
    {
        int duration = clock.instant().getNano() - start.getNano();
        return maxNano > duration || maxNano == NO_TIMEOUT;
    }

    public Wait atMost(TimeUnit unit, int count)
    {
        maxNano = unit.toNanos(count);
        return this;
    }


    public interface Sleeper
    {
        void doSleep(long l);

    }

    private static class DefaultSleeper implements Sleeper
    {
        @Override
        public void doSleep(long millis)
        {
            try
            {
                Thread.sleep(millis);
            } catch (InterruptedException e)
            {
                throw new RuntimeException(e);
            }
        }
    }
}
