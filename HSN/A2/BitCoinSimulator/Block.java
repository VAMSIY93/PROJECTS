import java.util.*;
import java.io.*;

public class Block 
{
	ArrayList<String> transactions;
	Block prev;
	String blockId;
	int chainId;
	long timeGenerated;
	
	public Block(long time)
	{
		transactions = new ArrayList<String>();
		prev = null;
		timeGenerated = time;
	}
	
	public void addTransaction(String txn)
	{
		transactions.add(txn);
	}
}
