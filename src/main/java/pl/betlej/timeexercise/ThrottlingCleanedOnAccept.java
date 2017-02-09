package pl.betlej.timeexercise;

import java.util.concurrent.ConcurrentLinkedQueue;

public class ThrottlingCleanedOnAccept extends Throttling
{

    public ThrottlingCleanedOnAccept()
    {
        super(new ConcurrentLinkedQueue<>());
    }

    @Override
    public boolean accept()
    {
        if (serverOverloaded())
        {
            cleanTheQueue();
            if (serverOverloaded())
            {
                return false;
            }
        }
        return  registerTask();
    }

    public static void main(String[] args)
    {
        Throttling.throttlingSampleUsage(new ThrottlingCleanedOnAccept());
    }
}
