package pipeline.lspredictor;

import java.io.FileWriter;
import java.io.IOException;
import config.EnergyConfig;
import pipeline.ExecutionEngine;
import generic.Core;

public abstract class LSPredictor 
{	
	ExecutionEngine containingExecutionEngine;
	long numAccesses;
	
	public LSPredictor(ExecutionEngine containingExecutionEngine) 
	{
		this.containingExecutionEngine = containingExecutionEngine;
	}
	
    public abstract void Train(long address, boolean outcome,boolean predict, long c , boolean d);

//	public abstract void  train(long address, boolean outcome,boolean predict);
  
	public abstract long predict(long address,long z);

	public EnergyConfig calculateAndPrintEnergy(FileWriter outputFileWriter, String componentName) throws IOException
	{
		EnergyConfig power = new EnergyConfig(containingExecutionEngine.getContainingCore().getbPredPower(), numAccesses);
		power.printEnergyStats(outputFileWriter, componentName);
		return power;
	}

	public void incrementNumAccesses(int incrementBy)
	{
		numAccesses += incrementBy;
	}

}