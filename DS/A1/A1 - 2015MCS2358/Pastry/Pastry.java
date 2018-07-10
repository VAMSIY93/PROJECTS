import java.net.InetAddress;
import java.util.Random;
import java.util.Scanner;

public class Pastry
{
	public int numOfNodes;
	private PastryNode myNode;
	
	public Pastry()
	{
		numOfNodes = 1;
		myNode = new PastryNode();
		
	}
	
	private void printCommands()
	{
		System.out.println("\ncreate <n>: creates 'n' new nodes");
		System.out.println("put <key> <value>: inserts key value pair in appropriate node");
		System.out.println("get <key>: fetches key value pair from the network");
		System.out.println("randomPut <n1> <n2>: inserts keys values from range [n1,n2]");
		System.out.println("randomGet <n> <n1> <n2>: fetches n random keys from inserted data in range [n1,n2]");
		System.out.println("quit <n>: removes 'n' random nodes from network");
		System.out.println("lset: prints all the leaves of all nodes in the network");
	}
	
	private String getNewId()
	{
		String id = "";
		try 
		{
            InetAddress ipAddr = InetAddress.getLocalHost();
            String ip = ipAddr.getHostAddress();
            Random rand = new Random();
            int port = rand.nextInt(1000000) + 1;
            String ipPort = ip+":"+port;
            id = MD5.hash(ipPort);
        } 
		catch (Exception ex) 
		{
            ex.printStackTrace();
		}	
		
		return id;
	}
	
	private void createNodes(int n)
	{
		for(int i=0;i<n;i++)
		{
			PastryNode node = new PastryNode();
			node.parent = this.myNode;
			String id = this.getNewId();
			node.setId(id,++numOfNodes);
			myNode.nodeCount = numOfNodes;
			myNode.addNodeToList(node);
			node.nodeList = myNode.nodeList;
			System.out.println(id);
			
			Thread th = new Thread(node,id);
			th.start();
			try
			{
				Thread.sleep(100);
			}
			catch(Exception e)
			{}
		}
	}
	
	private void quitNodes(int n)
	{
		for(int i=0;i<n;i++)
		{
			Random rand = new Random();
			int size = this.myNode.getNodeListLength();
            int num = rand.nextInt(size);
            if(n>=size && i==0)
            {
            	System.out.println("Too many nodes to delete");
            	return;
            }
            	
            if(num==0)
            	i--;
            else
            {
            	Message msg = new Message("quit",this.myNode);
            	PastryNode node = (PastryNode)this.myNode.getNode(num);
            	this.myNode.nodeList.remove(node);
            	System.out.println(node.getNodeId());
            	node.addMessage(msg);
            }  
            try
			{
				Thread.sleep(500);
			}
			catch(Exception e)
			{}
		}
	}
	
	private void generateKeyValues(int n1,int n2)
	{
		for(int i=n1;i<=n2;i++)
		{
			Random rand = new Random();
            String key = ""+i+" "+i;
            int range = this.myNode.getNodeListLength();
            int	num = rand.nextInt(range);
            Node node = this.myNode.getNode(num);            
            Message msg = new Message("put",node);
            msg.setMessage(key);
            node.addMessage(msg);
            
            try
			{
				Thread.sleep(50);
			}
			catch(Exception e)
			{}
		}
	}
	
	private void generateRandomRequest(int n,int n1,int n2)
	{
		for(int i=0;i<n;i++)
		{
			Random rand = new Random();
            int keyN = rand.nextInt(n2-n1) + n1;
            String key = ""+keyN;
            int range = this.myNode.getNodeListLength();
            int	num = rand.nextInt(range);
            Node node = this.myNode.getNode(num);            
            Message msg = new Message("get",node);
            msg.setMessage(key);
            node.addMessage(msg);
            
            try
			{
				Thread.sleep(50);
			}
			catch(Exception e)
			{}
		}
	}
	
	private void insertData(String[] cmd)
	{
        Random rand = new Random();
        int range = this.myNode.getNodeListLength();
        int	num = rand.nextInt(range);
        Node node = this.myNode.getNode(num);
        Message msg = new Message(cmd[0],node);
        if(cmd[0].equals("put"))
        	msg.setMessage(cmd[1]+" "+cmd[2]);
        else
        	msg.setMessage(cmd[1]);
        node.addMessage(msg);
	}
	
	private void getLeafSet(String[] cmd)
	{
		int size = this.myNode.getNodeListLength();
		for(int j=0;j<size;j++)
		{
			PastryNode node = (PastryNode)this.myNode.getNode(j);
			System.out.print("\n"+node.getNodeId()+":  ");
			for(int i=0;i<4;i++)
				if(node.lSet[i]!=null)
					System.out.print(node.lSet[i].getNodeId()+"  ");
				else
					System.out.print("--------  ");
			
			System.out.print("\tnset: ");
			for(int i=0;i<4;i++)
				if(node.nSet[i]!=null)
					System.out.print(node.nSet[i].getNodeId()+"  ");
				else
					System.out.print("--------  ");
		}		
	}
	
	private void executeCommand(String[] cmd)
	{
		if(cmd[0].equals("create"))
			this.createNodes(Integer.parseInt(cmd[1]));
		else if(cmd[0].equals("put"))
			this.insertData(cmd);
		else if(cmd[0].equals("get"))
			this.insertData(cmd);
		else if(cmd[0].equals("help"))
			this.printCommands();
		else if(cmd[0].equals("lset"))
			this.getLeafSet(cmd);
		else if(cmd[0].equals("quit"))
			this.quitNodes(Integer.parseInt(cmd[1]));
		else if(cmd[0].equals("randomPut"))
			this.generateKeyValues(Integer.parseInt(cmd[1]),Integer.parseInt(cmd[2]));
		else if(cmd[0].equals("randomGet"))
			this.generateRandomRequest(Integer.parseInt(cmd[1]),Integer.parseInt(cmd[2]),Integer.parseInt(cmd[3]));
		else if(cmd[0].equals("rTable"))
			this.printRouteTable();
		else if(cmd[0].equals("skip"))
		{}
	}
	
	private void printRouteTable()
	{
		int size = this.myNode.getNodeListLength();
		for(int k=0;k<size;k++)
		{
			PastryNode node = (PastryNode)this.myNode.getNode(k);
			System.out.print(node.getNodeId()+":  \n");
			for(int i=0;i<8;i++)
			{
				for(int j=0;j<16;j++)
					if(node.rTable[i][j]!=null)
						System.out.print(node.rTable[i][j].getNodeId()+" ");
					else
						System.out.print("-------- ");
				
				System.out.println();
			}
		}
	}
	
	public static void main(String args[])
	{
		Scanner sc = new Scanner(System.in);
		Pastry pastry = new Pastry();
		String id = pastry.getNewId();
		pastry.myNode.setId(id,++pastry.numOfNodes);
		pastry.myNode.addNodeToList(pastry.myNode);
		Thread myThread = new Thread(pastry.myNode,id);
		myThread.start();
		System.out.println("Enter a valid command or 'help' to view list of commands\n");
		System.out.print(">>> ");
				
		int i=0;
		while(true)
		{
			String command = sc.nextLine();
			if(command.equals("exit"))
				break;
			String[] cmd = command.split(" ");
			pastry.executeCommand(cmd);
			i++;
			if(i==50)
				break;

			System.out.print("\n>>> ");
		}
		
		
		System.out.println("Pastry going down");
		System.exit(0);
	}
}
