package config;

public class LSPredictorConfig 
{
	public int PCBits;
	public LSP predictorMode;
	
	public static enum LSP 
	{
		AnyStore, LoadStorePair, StoreSet		
	}
}