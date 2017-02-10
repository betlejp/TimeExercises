package pl.betlej.timeexercise;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Clock;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.LongStream;

import static java.time.Instant.ofEpochMilli;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class WaitTest
{


    @Mock
    private Clock forcedTickClock;

    @Mock
    private Wait.Sleeper doNothingSleep;

    @Mock
    private Predicate<Object> alwaysFalse;

    @Mock
    private Predicate<Object> trueOnSecondCall;

    private Object objectWaitedFor;
    private Wait<Object> testedWait;

    @BeforeClass
    public void initMocks()
    {
        MockitoAnnotations.initMocks(this);

        when(forcedTickClock.instant()).thenReturn(ofEpochMilli(1), LongStream.rangeClosed(2, 10).mapToObj(Instant::ofEpochMilli).toArray(Instant[]::new));

        doNothing().when(doNothingSleep).doSleep(anyLong());

        when(alwaysFalse.test(any())).thenReturn(false);
        when(trueOnSecondCall.test(any())).thenReturn(false, true);
    }

    @BeforeMethod
    public void refreshObject()
    {
        testedWait = new Wait<>(objectWaitedFor, forcedTickClock, doNothingSleep);
    }

    @Test
    public void shouldNotExitBeforeTimeoutForConditionNotMet()
    {
        //when
        boolean waitResult = testedWait.atMost(TimeUnit.MILLISECONDS, 4).atIntervals(TimeUnit.MILLISECONDS, 1).waitUntil(alwaysFalse);

        //then
        verify(alwaysFalse, times(4)).test(objectWaitedFor);
        assertFalse(waitResult);
    }


    @Test
    public void shouldExitBeforeTimeoutWhenConditionMet()
    {

        //when
        boolean waitResult = testedWait.atMost(TimeUnit.MILLISECONDS, 10).atIntervals(TimeUnit.MILLISECONDS, 1).waitUntil(trueOnSecondCall);

        //then
        verify(trueOnSecondCall, times(2)).test(objectWaitedFor);
        assertTrue(waitResult);
    }

}