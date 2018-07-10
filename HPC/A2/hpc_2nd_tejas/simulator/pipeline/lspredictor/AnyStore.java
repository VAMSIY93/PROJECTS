package pipeline.lspredictor;

import pipeline.*;

public class AnyStore extends LSPredictor
{
	int CHT[],CHTSize;
	long maskBits;

	public AnyStore(ExecutionEngine containingExecEngine)
	{
		super(containingExecEngine);
		CHTSize=8192;
		maskBits=8191;
		CHT=new int[CHTSize];
		for(int i=0;i<CHTSize;i++)
			CHT[i]=2;
	}	

	public void Train(long address,boolean predict,boolean outcome, long c, boolean d)
	{
		int index=(int)(address & maskBits);
		if(predict!=outcome)
		{
			if(outcome==true)
				CHT[index]=1;
			else
				CHT[index]++;
		}
		else
		{
			if(outcome==true && CHT[index]>0)
				CHT[index]--;
		}
	}

/*	public void train(long address,boolean predict,boolean outcome)
	{
		int index=(int)(address & maskBits);
		if(predict!=outcome)
		{
			if(outcome==true)
				CHT[index]--;
			else
				CHT[index]++;
		}
		else
		{
			if(outcome==true && CHT[index]>0)
				CHT[index]--;
			else if(outcome==false && CHT[index]<3)
				CHT[index]++;
		}
	}
*/
	public long predict(long address, long operand_store)
	{
		int index=(int)(address & maskBits);
		if(CHT[index]>1)
			return -1;
		else
			return 1;
	}
}
