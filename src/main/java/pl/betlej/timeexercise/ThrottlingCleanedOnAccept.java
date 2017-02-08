package pl.betlej.timeexercise;

public class ThrottlingCleanedOnAccept extends Throttling
{

    public ThrottlingCleanedOnAccept()
    {
        super();
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
        registerTask();
        return true;
    }

    public static void main(String[] args)
    {
        Throttling.throttlingSampleUsage(new ThrottlingCleanedOnAccept());
    }
}
