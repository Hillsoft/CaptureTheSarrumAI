package AI;

public class ExtendThread implements Runnable
{
    private Thread t;
    private Move owner;
    private int depth;

    public ExtendThread(Move target, int depth)
    {
        owner = target;
        this.depth = depth;
    }

    public void run()
    {
        owner.extend(depth);

        if (owner.prev.turn == owner.prev.owner.colour && owner.resultValue < owner.prev.resultValue)
            owner.prev.resultValue = owner.resultValue;
        else if (owner.prev.turn != owner.prev.owner.colour && owner.resultValue > owner.prev.resultValue)
            owner.prev.resultValue = owner.resultValue;
    }

    public void start()
    {
        if (t == null)
        {
            t = new Thread(this);
            t.start();
        }
    }

    public final boolean isAlive()
    {
        if (t == null)
            return true;
        else
            return t.isAlive();
    }
}
