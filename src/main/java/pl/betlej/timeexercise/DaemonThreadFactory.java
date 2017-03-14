package pl.betlej.timeexercise;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

class DaemonThreadFactory implements ThreadFactory
{
    @Override
    public Thread newThread(Runnable r)
    {
        Thread thread = Executors.defaultThreadFactory().newThread(r);
        thread.setDaemon(true);
        return thread;
    }
}
