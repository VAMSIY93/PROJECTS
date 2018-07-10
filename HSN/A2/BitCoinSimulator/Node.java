import java.util.*;
import java.io.*;

public class Node 
{
	ArrayList<Block> recvdBlocks;
	ArrayList<Long> recvdBlockTime;
	ArrayList<String> recvdTransactions;
	ArrayList<Boolean> deleted;
	Block last;
	int nodeId;
	long count;
	int balance;
	BitCoin parent;
	
	public Node(int id, BitCoin coin)
	{
		recvdBlocks = new ArrayList<Block>();
		recvdBlockTime = new ArrayList<Long>();
		recvdTransactions = new ArrayList<String>();
		deleted = new ArrayList<Boolean>();
		nodeId = id;
		count = 0;
		parent = coin;
	}
	
	/*i/p this function takes transaction string
	 * o/p will return the final transaction id ie transaction_id X 1000 + node id if 
	 * transaction id is in correct format 
	 * else
	 * it will raise exception and return -1.
	 *
	 * assumption that number of nodes is not more than 1000.
	 * */
	public int getTxnId(String txn)
	{
		String[] txnDetails = new String[4];
		txnDetails = txn.split(" ");
		try
		{
		return ((Integer.parseInt(txnDetails[0]) * 1000) + Integer.parseInt(txnDetails[1]));
		}
		catch(IllegalArgumentException e)
		{
			System.out.print("txn format or index");
		}
		
		return(-1);
	}
	/*dependents : getTxnId
	 * i/p transaction details as string
	 * o/p return true if transaction present in list else return false
	 *
	 * */
	public boolean txnPresent(String txn)
	{
		int checkId = getTxnId(txn);
		
		for(int count =0; count<recvdTransactions.size();count++)
		{
			if(txn.equals(recvdTransactions.get(count)))// getTxnId(recvdTransactions.get(count)))
			{
				return(true);
			}
		}
		
		return (false);
	}
	
	public boolean txnDeleted(String txn)
	{
		for(int i=0;i<recvdTransactions.size();i++)
			if(txn.equals(recvdTransactions.get(i)) && deleted.get(i)==true)
				return true;
			else if(txn.equals(recvdTransactions.get(i)) && deleted.get(i)==false)
				return false;
		
		return false;
	}
	
	/*public void updateTxnList(String txn)
	{		
		if(!txnPresent(txn))
		{
			recvdTransactions.add(txn);
		}
	}*/
	
	public void updateTxnListwrtBlock(Block blk)
	{ 				
		for(int i=0; i<blk.transactions.size();i++)
		{			
			String txn = blk.transactions.get(i);
			if(txnPresent(txn))
				deleted.set(recvdTransactions.indexOf(txn), true);
			else
			{
				recvdTransactions.add(0,txn);
				deleted.add(0, false);
			}
		}
	}
	
	public boolean blkPresent(Block blk)
	{
		if(recvdBlocks.contains(blk))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	private int computeDelay(boolean fast, boolean block)
	{
		int prop_delay = parent.generateUniform();
		int cij = 0;
		if(fast==true)
			cij = 100;
		else
			cij = 5;
			
		int mean = 120/cij;
		int dij = parent.generateExponential(mean);
		
		if(block)
		{
			return (prop_delay + dij + 8/cij);
		}
		else
			return (prop_delay + dij);
		
	}
	
	private void updateNextBlockTime(long time)
	{
		int len = parent.queue.size();
		for(int i=0;i<len;i++)
		{
			Event e = parent.queue.get(i);
			if(e.operation.equals("generateBlock") && e.src==this && e.dest==this)
			{
				parent.queue.remove(e);
				e.time = time;
				parent.addEvent(e);
				break;
			}
		}
	}
	
	protected void generateTransaction(Event e)
	{
		Random rand = new Random();
		Node node = e.dest;
		int nodeInd = parent.peers.indexOf(node);
		
		int amount = rand.nextInt(balance/2);
		int ind = rand.nextInt(parent.N);
		while(ind==nodeInd)
			ind = rand.nextInt(parent.N);
		
		Node payee = parent.peers.get(ind);
		node.balance = node.balance - amount;
		payee.balance = payee.balance + amount;
		count = count + 1;
			
		String txn = ""+count+" "+node.nodeId+" "+payee.nodeId+" "+amount;
		recvdTransactions.add(0,txn);
		deleted.add(0,false);
		
		
		for(int i=0;i<parent.N;i++)
		{				
			int delay = 0;
			int edge = parent.adjMatrix[nodeInd][i];
			if(edge==2)
				delay = computeDelay(true, false);
			else if(edge==1)
				delay = computeDelay(false, false);
			
			String transaction = new String(txn);
			if(edge>0)
			{
				Event event = new Event(e.time+delay,"receiveTxn",node,parent.peers.get(i),transaction);
				parent.addEvent(event);				
			}					
		}
		
		int delay = parent.generateExponential(parent.transTime);
		//System.out.println("GEN TXN: "+(delay));
		Event event = new Event(e.time+delay, "generateTxn", node, node, null);
		parent.addEvent(event);
		
		if(node.nodeId==0)
		{
			String data = "generateTransaction,"+e.time+","+(e.time+delay);
			String fileName = "graphs2/NodeTransactions.csv";
			writeFile(fileName,data);
		}
	}
	
	protected void receiveTransaction(Event e)
	{
		String txn = e.getTransaction();
		Node node = this;
		int nodeInd = parent.peers.indexOf(node);		
		
		if(!txnPresent(txn))
		{
			recvdTransactions.add(0,txn);
			deleted.add(0,false);
			
			//Forward Transaction
			for(int i=0;i<parent.N;i++)
			{				
				int delay = 0;
				int edge = parent.adjMatrix[nodeInd][i];;
				if(edge==2)
					delay = computeDelay(true, false);
				else if(edge==1)
					delay = computeDelay(false, false);
				
				String transaction = new String(txn);
				if(edge>0 && parent.peers.get(i)!=e.src)
				{
					Event event = new Event(e.time+delay,"receiveTxn", this, parent.peers.get(i), transaction);
					parent.addEvent(event);
				}					
			}
		}
		
		if(node.nodeId==0)
		{
			String data = "recieveTransaction,"+e.time+","+txn;
			String fileName = "graphs2/NodeTransactions.csv";
			writeFile(fileName,data);
		}
	}
	
	protected void generateBlock(Event e)
	{		
		Block block = new Block(e.time);
		int len = recvdTransactions.size();
		for(int i=0;i<len;i++)
		{
			String txn = recvdTransactions.get(i);
			if(deleted.get(i)==false)
			{
				block.addTransaction(txn);
				deleted.set(i, true);
			}
		}
		
		String id = ""+nodeId;
		if(last!=null)
			id = id + last.blockId;
		
		try {
		block.blockId = MD5.hash(id); }
		catch(Exception ex)
		{}
		
		block.prev = last;
		block.chainId = last.chainId + 1;		
		last = block;
		recvdBlocks.add(block);
		recvdBlockTime.add(e.time);
		
		for(int i=0;i<parent.N;i++)
		{				
			int delay = 0;
			int edge = parent.adjMatrix[nodeId][i];
			if(edge==2)
				delay = computeDelay(true, true);
			else if(edge==1)
				delay = computeDelay(false, true);
			
			if(edge>0)
			{
				Event event = new Event(e.time+delay,"receiveBlock", this, parent.peers.get(i), block);
				parent.addEvent(event);
				//System.out.println("Next block time: "+ (e.time+delay));
			}					
		}
		
		int delay = parent.generateExponential(parent.blockTime);
		Event event = new Event(e.time+delay, "generateBlock", this, this, null);
		parent.addEvent(event);

		if(this.nodeId==0)
		{
			String data = "generateBlock,"+e.time+","+(e.time+delay);
			String fileName = "graphs2/NodeTransactions.csv";
			writeFile(fileName,data);
		}
	}
	
	protected void receiveBlock(Event e)
	{
		Block block = e.getBlock();
		
		if(!blkPresent(block))
		{
			recvdBlocks.add(block);
			recvdBlockTime.add(e.time);
			//Forward Block
			for(int i=0;i<parent.N;i++)
			{				
				int delay = 0;
				int edge = parent.adjMatrix[nodeId][i];;
				if(edge==2)
					delay = computeDelay(true, true);
				else if(edge==1)
					delay = computeDelay(false, true);

				if(edge>0 && parent.peers.get(i)!=e.src)
				{
					Event event = new Event(e.time+delay,"receiveBlock", this, parent.peers.get(i), block);
					parent.addEvent(event);
				}					
			}
		}
		
		if(last==null)
			last = block;
		else if(last.timeGenerated<block.timeGenerated)
			last = block;
		
		updateTxnListwrtBlock(block);
		
		int delay = parent.generateExponential(parent.blockTime);
		updateNextBlockTime(delay + e.time);
		
		if(this.nodeId==0)
		{
			String data = "recieveBlock,"+e.time+","+(e.time+delay);
			String fileName = "graphs2/NodeTransactions.csv";
			writeFile(fileName,data);
		}
	}
	
	protected void processEvent(Event e)
	{		
		if(e.operation.equals("generateTxn"))
			this.generateTransaction(e);
		else if(e.operation.equals("receiveTxn"))
			this.receiveTransaction(e);
		else if(e.operation.equals("generateBlock"))
			this.generateBlock(e);
		else if(e.operation.equals("receiveBlock"))
			this.receiveBlock(e);
	}
	
	private void writeFile(String fileName, String data)
	{
		try
        {
			FileWriter fw = new FileWriter(fileName,true);    
            fw.write(data+"\n");
            fw.close();    
        }
		catch(Exception e)
		{}
	}
	
	private void writeToFile(String fileName, String curr, String prev, long genr, long recv)
	{
		try
        {
			String data = curr+","+prev+","+genr+","+recv;
            FileWriter fw = new FileWriter(fileName,true);    
            fw.write(data+"\n");
            fw.close();    
        }
		catch(Exception e)
		{}
	}
	
	protected void printChain(long time)
	{		
		int numOfBlocks = recvdBlocks.size();
		boolean printed[] = new boolean[numOfBlocks];
		
		Block block = last;
		int ind = recvdBlocks.indexOf(block);
		
		while(block!=null && ind>=0 && numOfBlocks>0 && printed[ind]==false)
		{
			printed[ind] = true;
			System.out.print(block.blockId+"("+block.chainId+") "+block.timeGenerated+" --->");
			block = block.prev;
			ind = recvdBlocks.indexOf(block);			
		}		
		System.out.println();
		
		for(int i=0;i<numOfBlocks;i++)
		{
			if(printed[i]==false)
			{
				block = recvdBlocks.get(i);
				ind = i;
				while(block!=null && ind>=0 && printed[ind]==false)
				{
					printed[ind] = true;
					System.out.print(block.blockId+"("+block.chainId+") "+block.timeGenerated+" --->");
					block = block.prev;
					ind = recvdBlocks.indexOf(block);			
				}
				System.out.println();
			}
		}
		System.out.println();
		System.out.print(">>> ");
	}
	
	protected void saveChain(long time)
	{		
		int numOfBlocks = recvdBlocks.size();
		boolean printed[] = new boolean[numOfBlocks];
		
		Block block = last;
		int ind = recvdBlocks.indexOf(block);
		String fileName = "graphs2/node-"+nodeId+".csv";
		
		try
        {
            FileWriter fw = new FileWriter(fileName,false);
            fw.close();    
        }
		catch(Exception e)
		{}
		
		while(block!=null && ind>=0 && numOfBlocks>0 && printed[ind]==false)
		{
			printed[ind] = true;
			if(block.prev==null)
				writeToFile(fileName,block.blockId, "null", block.timeGenerated, recvdBlockTime.get(ind));
			else
				writeToFile(fileName,block.blockId, block.prev.blockId, block.timeGenerated, recvdBlockTime.get(ind));
			block = block.prev;
			ind = recvdBlocks.indexOf(block);			
		}		
		
		for(int i=0;i<numOfBlocks;i++)
		{
			if(printed[i]==false)
			{
				block = recvdBlocks.get(i);
				ind = i;
				while(block!=null && ind>=0 && printed[ind]==false)
				{
					printed[ind] = true;
					if(block.prev==null)
						writeToFile(fileName,block.blockId, "null", block.timeGenerated, recvdBlockTime.get(ind));
					else
						writeToFile(fileName,block.blockId, block.prev.blockId, block.timeGenerated, recvdBlockTime.get(ind));
					block = block.prev;
					ind = recvdBlocks.indexOf(block);			
				}
			}
		}
	}
}
