package pl.betlej.timeexercise;

public class ThrottlingCleanedWhenRequested extends Throttling
{

    public ThrottlingCleanedWhenRequested()
    {
        super();
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
        getTasksReceived().add(System.currentTimeMillis());
        return true;
    }

    private void tryCleaningTheQueue()
    {
        getTasksReceived().removeIf((peek) -> System.currentTimeMillis() - peek > TASK_ACTIVE_MILLISECONDS);
    }

    public static void main(String[] args)
    {
        Throttling.throttlingSampleUsage(new ThrottlingCleanedWhenRequested());
    }
}
