

public class Replay
{
	ReplayEntry replayQueue[];
	int curSize;

	public Replay(int size)
	{
		curSize = 0;
		replayQueue = new ReplayEntry[size];
	}

	public void enQueue(ReplayEntry entry)
	{
		replayQueue[curSize]=entry;
		curSize++;
	}

	public ReplayEntry deQueue()
	{
		ReplayEntry entry = replayQueue[0];
		for(int i=0;i<curSize-1;i++)
			replayQueue[i]=replayQueue[i+1];

		curSize--;
		return entry;
	}

}