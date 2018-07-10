import java.util.*;
import java.io.*;

public class BitCoin extends Thread
{
	ArrayList<Event> queue;
	ArrayList<Node> peers;
	Vector<Message> channel;
	int adjMatrix[][];
	int N,z,transTime, blockTime, linkSpeed;
	
	public BitCoin(int n,int z,int tt, int tb, int ls)
	{
		N = n;
		this.z = z;
		transTime = tt;
		blockTime = tb;
		linkSpeed = ls;
		queue = new ArrayList<Event>();
		peers = new ArrayList<Node>(n);
		channel = new Vector<Message>(10);
		adjMatrix = new int[N][N];
				
		for(int i=0;i<N;i++)
			peers.add(i,new Node(i, this));
				
	}
	
	public int generateExponential(int mean)
	{
		return (int)(-mean * Math.log(Math.random()));
	}
	
	public int generateUniform()
	{
		return (int)(10+(500 -10) * Math.random());
	}
	
	public void addEvent(Event e)
	{		
		int len = queue.size();
		int i=0;
		for(;i<len;i++)
		{
			Event x = queue.get(i);
			if(e.time<x.time)
			{
				queue.add(i,e);
				break;
			}
		}
		
		if(i==len)
			queue.add(i,e);
	}
	
	private void writeToFile(String data)
	{
		try
        {
            FileWriter fw = new FileWriter("graphs2/graph.csv",false);    
            fw.write(data+"\n");
            fw.close();    
        }
		catch(Exception e)
		{}
	}
	
	public void createNetwork()
	{	
		Random rand = new Random();
        int fast = (int)Math.ceil(z*N/100);
		boolean speed[] = new boolean[N];
		for(int i=0;i<fast;i++)
			speed[i] = true;
		
		for(int i=0;i<N;i++)
		{
			int E = rand.nextInt(N-i) + 1;
			
			for(int j=0;j<E;j++)
			{
				int ind=i;
				do
				{
					ind = rand.nextInt(N);
				}
				while(ind==i);
				
				if(speed[i] && speed[ind])
				{
					adjMatrix[i][ind] = 2;
					adjMatrix[ind][i] = 2;
				}
				else
				{
					adjMatrix[i][ind] = 1;
					adjMatrix[ind][i] = 1;
				}
			}
				
		}

		String data = "";
		for(int i=0;i<N;i++)
		{
			for(int j=0;j<N;j++)
			{
				System.out.print(adjMatrix[i][j]+" ");
				if(j==N-1)
					data = data+adjMatrix[i][j];
				else
					data = data+adjMatrix[i][j]+",";
			}
			System.out.println();
			if(i<(N-1))
				data = data + "\n";
		}
		writeToFile(data);
	}
	
	public void initialization()
	{
		Block genesis = new Block(0);
		genesis.chainId = 0;
		Random rand = new Random();
		int num = rand.nextInt(1000) + 1;
		String id = ""+num;
		try {
			genesis.blockId = MD5.hash(id); 
		}
		catch(Exception ex)
		{}
		
		try
        {
			String fileName = "graphs2/NodeTransactions.csv";
            FileWriter fw = new FileWriter(fileName,false);
            fw.close();    
        }
		catch(Exception e)
		{}
		
		
		for(int i=0;i<N;i++)
		{
			//Genesis
			Node node = peers.get(i);
			node.last = genesis;
			node.recvdBlocks.add(genesis);
			node.recvdBlockTime.add((long)0);
			
			//Transaction event
			long delay = generateExponential(transTime);
			Event e = new Event(delay, "generateTxn", node, node, null);
			addEvent(e);
		}
		
		for(int i=0;i<N;i++)
		{
			Node node = peers.get(i);
			node.balance = rand.nextInt(4500) + 500;
			
			long delay = generateExponential(blockTime);
			Event e = new Event(delay, "generateBlock", node, node, null);
			addEvent(e);
		}
		
		
	}
	
	public void run()
	{
		createNetwork();
		
		initialization();
		
		int i=0;
		while(queue.size()>0)
		{
			Event e = queue.remove(0);
			//System.out.println(i+": "+e.operation+"  "+e.time);
			e.dest.processEvent(e);
			
			while(channel.isEmpty()==false)
			{
				Message msg = channel.remove(0);
				if(msg.msgType.equals("print"))
					msg.src.printChain(e.time);
				else
					msg.src.saveChain(e.time);
			}
			
			try {
				Thread.sleep(100);
			}
			catch(Exception ex)
			{}
			
			i++;
		}
	}
	
	public void printChain(int n)
	{
		Message msg = new Message("print",peers.get(n));
		channel.add(msg);
	}
	
	public void printAllChains()
	{
		for(int i=0;i<N;i++)
			printChain(i);
	}
	
	public void saveAllChains()
	{
		for(int i=0;i<N;i++)
		{
			Message msg = new Message("save",peers.get(i));
			channel.add(msg);
		}
		System.out.print("\n>>> ");
	}
	
	public void printHelpMenu()
	{
		System.out.println("print <n>: shows the chain of node n at present time");
		System.out.println("showAll: shows the chains of all nodes at present time");
		System.out.println("saveAll: saves all the chains of all the nodes at present time");
		System.out.println("exit: stop simulation and exit\n");
		System.out.print(">>> ");
	}
	
	public void executeCommand(String[] command)
	{
		if(command[0].equals("print"))
			printChain(Integer.parseInt(command[1]));
		else if(command[0].equals("help"))
			printHelpMenu();
		else if(command[0].equals("showAll"))
			printAllChains();
		else if(command[0].equals("saveAll"))
			saveAllChains();
		else if(command[0].equals("exit"))
			System.exit(0);
		else
			System.out.print(">>> ");
	}
	
	public static void main(String args[])
	{
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter number of nodes in network: ");
		int n = sc.nextInt();
		
		System.out.println("Enter percentage of nodes to be fast: ");
		int z = sc.nextInt();
		
		System.out.println("Enter exponential mean for transaction time: ");
		int tt = sc.nextInt();
		
		System.out.println("Enter exponential mean for block gneration time: ");
		int tb = sc.nextInt();
		
		System.out.println("Enter bottleneck link speed: ");
		int ls = sc.nextInt();
		
		BitCoin coin = new BitCoin(n, z, tt, tb, ls);
		int i=0;
		coin.start();

		try
		{
			Thread.sleep(1000);
		}
		catch(Exception e)
		{}
		
		System.out.println("Press 'help' for possible options\n");
		System.out.print(">>> ");
		while(i<100)
		{
			String command = sc.nextLine();
			String cmd[] = command.split(" ");
			coin.executeCommand(cmd);
			
			i++;
		}
		
	}
}
