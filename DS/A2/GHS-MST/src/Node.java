import java.io.*;
import java.util.*;

public class Node implements Runnable
{
	private int nbrs;
	private int eCount;
	private int nodeId;
	private int level;
	private int rec;
	private long bestWt;
	private boolean stop;
	private String state;
	private String name;
	public Edge[] edge;
	private String[] status;
	private MessageQueue queue;
	private Main obj;
	private Node parent, testNode, bestNode;
	
	protected Node(int id, int count)
	{
		nodeId = id;
		nbrs = count;
		eCount = 0;
		level = -1;
		rec = -1;
		stop = false;
		name = "-"+100+""+id;
		state = "sleep";
		bestWt = Long.MAX_VALUE;
		queue = new MessageQueue(100);
		edge = new Edge[nbrs];
		status = new String[nbrs];
		bestNode = null;
		testNode = null;
		parent = null;
		for(int i=0;i<nbrs;i++)
			status[i] = "basic";
	}
	
	protected void setMain(Main mn)
	{
		obj = mn;
	}
	
	protected int getNodeId()
	{
		return nodeId;
	}
	
	protected void addEdge(Edge ed)
	{
		edge[eCount++] = ed;
	}
	
	protected int getNeighbourCount()
	{
		return nbrs;
	}
	
	protected synchronized void addMessage(Message msg) 
    {
        queue.addMessage(msg);
    }
   
    protected synchronized Message getNextMessage()
    {
    	if(this.queue.getSize()>0)
    	{
    		return this.queue.getMessage(0);
    	}
    	
    	return null;
    }
    
    private int findEdge(Node node)
    {
    	for(int i=0;i<edge.length;i++)
    		if(edge[i].src==node || edge[i].dst==node)
    			return i;
    	
    	return -1;
    }
    
    private synchronized void addEdgeToMST(int ind)
    {
    	obj.addMSTEdges(edge[ind]);
    }
    
	private int findMinEdge()
	{
		int minInd = 0;
		long minWt = Long.MAX_VALUE;
		for(int i=0;i<edge.length;i++)
			if(minWt>edge[i].wt)
			{
				minWt = edge[i].wt;
				minInd = i;
			}
		
		return minInd;
	}
	
	private void relax(long t)
	{
		for(int i=0;i<t;i++)
		{}
	}
	
	private void processConnect(Message msg)
	{
		int L = Integer.parseInt(msg.getMessage());
		int ind = findEdge(msg.src);
		
		if(L<level)
		{
			status[ind] = "branch";
			
			Message m = new Message("initiate",this);
			String data = ""+level+" "+name+" "+state;
			m.setMessage(data);
			
			msg.src.addMessage(m);
		}
		else if(status[ind].equals("basic"))
		{
			this.relax(100);
			this.addMessage(msg);
		}
		else
		{
			Message m = new Message("initiate",this);
			String data = ""+(level+1)+" "+edge[ind].wt+" find";
			m.setMessage(data);
			
			msg.src.addMessage(m);
		}
	}
	
	private void report()
	{
		int count=0;
		Node q;
		
		for(int i=0;i<status.length;i++)
		{
			if(edge[i].src==this)
				q = edge[i].dst;
			else
				q = edge[i].src;
			
			if(status[i].equals("branch") && q!=parent)
				count++;
		}
		
		if(rec==count && testNode==null)
		{
			state = "found";
			Message msg = new Message("report", this);
			msg.setMessage(""+bestWt);
			
			parent.addMessage(msg);
		}
	}
	
	private void test()
	{
		int minInd = -1;
		long minWt = Long.MAX_VALUE;
		boolean exists = false;
		for(int i=0;i<edge.length;i++)
			if(status[i].equals("basic") && minWt>edge[i].wt)
			{
				minInd = i;
				minWt = edge[i].wt;
				exists = true;
			}
		
		if(exists)
		{
			if(edge[minInd].src!=this)
				testNode = edge[minInd].src;
			else
				testNode = edge[minInd].dst;
			
			Message msg = new Message("test", this);
			String data = ""+level+" "+name;
			msg.setMessage(data);
			
			testNode.addMessage(msg);
		}
		else
		{
			testNode = null;
			report();
		}
	}
	
	private void processInitiate(Message msg)
	{
		String[] data = msg.getMessage().split(" ");
		level = Integer.parseInt(data[0]);
		name = data[1];
		state = data[2];
		parent = msg.src;
		bestNode = null;
		bestWt = Long.MAX_VALUE;
		Node q;
		
		for(int i=0;i<edge.length;i++)
		{
			if(edge[i].src==this)
				q = edge[i].dst;
			else
				q = edge[i].src;
			if(status[i].equals("branch") && msg.src!=q)
			{
				Message m = new Message("initiate",this);
				m.setMessage(msg.getMessage());
				
				if(edge[i].src!=this)
					edge[i].src.addMessage(m);
				else
					edge[i].dst.addMessage(m);
			}
		}
		
		if(state.equals("find"))
		{
			rec = 0;
			test();
		}
	}
	
	private void processTest(Message msg)
	{
		String[] data = msg.getMessage().split(" ");
		int L = Integer.parseInt(data[0]);
		String F = data[1];
		int ind = findEdge(msg.src);
		
		if(L>level)
		{
			this.relax(100);
			this.addMessage(msg);
		}
		else if(F.equals(name))
		{
			if(status[ind].equals("basic"))
				status[ind] = "reject";
			
			if(msg.src!=testNode)
			{
				Message m = new Message("reject", this);
				msg.src.addMessage(m);
			}
			else
				test();
		}
		else
		{
			Message m = new Message("accept", this);
			msg.src.addMessage(m);
		}
	}
	
	private void processAccept(Message msg)
	{
		int ind = findEdge(msg.src);
		testNode = null;
		
		if(edge[ind].wt<bestWt)
		{
			bestWt = edge[ind].wt;
			bestNode = msg.src;
		}
		
		report();
	}
	
	private void processReject(Message msg)
	{
		int ind = findEdge(msg.src);
		if(status[ind].equals("basic"))
			status[ind] = "reject";
		
		test();
	}
	
	private void changeRoot()
	{
		int ind = findEdge(bestNode);
		
		if(status[ind].equals("branch"))
		{
			Message msg = new Message("changeRoot", this);
			bestNode.addMessage(msg);
		}
		else
		{
			status[ind] = "branch";
			Message msg = new Message("connect", this);
			msg.setMessage(""+level);
			bestNode.addMessage(msg);
			
			addEdgeToMST(ind);
		}
	}
	
	private void processReport(Message msg)
	{
		int ind = findEdge(msg.src);
		long W = Long.parseLong(msg.getMessage());
		
		if(msg.src!=parent)
		{
			if(W<bestWt)
			{
				bestWt = W;
				bestNode = msg.src;
			}
			rec = rec + 1;
			report();
		}
		else
		{
			if(state.equals("find"))
			{
				this.relax(100);
				this.addMessage(msg);
			}
			else if(W>bestWt)
				changeRoot();
			else if(W==bestWt && W==Long.MAX_VALUE)
			{
				Message m = new Message("stop", this);
				obj.setStop();
			}
		}
	}
	
	private void processChangeRoot(Message msg)
	{
		changeRoot();
	}
	
	private void executeCommand(String cmd,Message msg)
	{
		if(cmd.equals("connect"))
			processConnect(msg);
		else if(cmd.equals("initiate"))
			processInitiate(msg);
		else if(cmd.equals("test"))
			processTest(msg);
		else if(cmd.equals("accept"))
			processAccept(msg);
		else if(cmd.equals("reject"))
			processReject(msg);
		else if(cmd.equals("report"))
			processReport(msg);
		else if(cmd.equals("changeRoot"))
			processChangeRoot(msg);
		else
			{ init(); System.out.println("Illegal reach");}
	}
	
	public void init()
	{
		int ind = findMinEdge();
		status[ind] = "branch";
		level = 0;
		state = "found";
		rec = 0;
		
		addEdgeToMST(ind);
		
		Message msg = new Message("connect",this);
		msg.setMessage("0");
		if(edge[ind].src!=this)
			edge[ind].src.addMessage(msg);
		else
			edge[ind].dst.addMessage(msg);
	}
	
	public void run()
	{
		try
		{
			while(true)
			{
				if(state.equals("sleep"))
					init();
				if(this.queue.isEmpty()==false)
				{
					Message msg = this.getNextMessage();
					String cmd = msg.mType;
					executeCommand(cmd,msg);				
				}
			}
		}
		catch(Exception e)
		{ System.out.println(e); }
	}
}
