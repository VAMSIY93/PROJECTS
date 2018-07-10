import java.io.*;
import java.util.*;

public class Main extends Thread
{
	int nodes, n;
	Node[] nodeList;
	private boolean stop;
	private Vector<Edge> mstEdges;
	
	
	public Main()
	{
		nodes = 0;
		mstEdges = new Vector<Edge>();
		stop = false;
	}
	
	protected synchronized void addMSTEdges(Edge e)
	{
		if(mstEdges.contains(e)==false)
		{
			int size = mstEdges.size();
			boolean added = false;
			for(int i=0;i<size;i++)
				if(mstEdges.get(i).wt > e.wt)
				{
					mstEdges.add(i,e);
					added = true;
					break;
				}
			
			if(added==false)
				mstEdges.add(e);
		}
	}
	
	private synchronized void writeToFile()
	{
		try
        {
			FileWriter fw = new FileWriter("output"+n+".txt",false);   
			int size = mstEdges.size();
			for(int i=0;i<size;i++)
			{
				String data = mstEdges.get(i).src.getNodeId()+","+mstEdges.get(i).dst.getNodeId()+":"+mstEdges.get(i).wt; 
				fw.write(data+"\n");
			}
            fw.close();    
        }
		catch(Exception e)
		{}

	}

	protected synchronized void setStop()
	{
		if(stop==false)
		{
			int size = mstEdges.size();
			writeToFile();
			System.out.println("Stopped.");
			System.exit(0);
		}
		stop = true;
	}
    
	public ArrayList<ArrayList<Long>> readGraph(String fileName)
	{
		ArrayList<ArrayList<Long>> edges = new ArrayList<ArrayList<Long>>();
		
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			for(String line;(line=br.readLine())!=null;)
			{
				String wt[] = line.split(":");
				String nodes[] = wt[0].split(",");
				ArrayList<Long> edge = new ArrayList<Long>();
				edge.add(Long.parseLong(nodes[0]));
				edge.add(Long.parseLong(nodes[1]));
				edge.add(Long.parseLong(wt[1]));
				edges.add(edge);
			}
		}
		catch(Exception e) {}
		
		return edges;
	}
	
	public void constructGraph(ArrayList<ArrayList<Long>> graph)
	{
		int edges = graph.size();
		ArrayList<Integer> neighbours = new ArrayList<Integer>();
		long max = 0;
		for(int i=0;i<edges;i++)
		{
			ArrayList<Long> e = graph.get(i);
			long n1 = e.get(0);
			long n2 = e.get(1);
			if(max<n1)
				max = n1;
			if(max<n2)
				max = n2;
			
			int size = neighbours.size();
			if(n2>=size)
				for(int j=size;j<=n2;j++)
					neighbours.add(0);
			
			
			neighbours.set((int)n2, neighbours.get((int)n2)+1);
			neighbours.set((int)n1, neighbours.get((int)n1)+1);
		}
		nodes = (int)(max+1);
		nodeList = new Node[nodes];
		for(int i=0;i<nodes;i++)
		{
			nodeList[i] = new Node(i, neighbours.get(i));
			nodeList[i].setMain(this);
		}
	
		for(int i=0;i<edges;i++)
		{
			ArrayList<Long> e = graph.get(i);
			Node n1 = nodeList[(int)(long)e.get(0)];
			Node n2 = nodeList[(int)(long)e.get(1)];
			Edge ed = new Edge(n1, n2, (long)e.get(2));
			n1.addEdge(ed);
			n2.addEdge(ed);
		}
	}
	
	protected void executeCommand(String cmd, Message msg)
	{
		if(cmd.equals("stop"))
		{
			int size = mstEdges.size();
			for(int i=0;i<size;i++)
				System.out.println(mstEdges.get(i).src.getNodeId()+" "+mstEdges.get(i).dst.getNodeId()+" : "+mstEdges.get(i).wt);
			
			System.out.println("Stopped.");
			System.exit(0);
		}
	}
	
	protected void startComputation()
	{
		for(int i=0;i<nodes;i++)
		{	
			Thread th = new Thread(nodeList[i], ""+nodeList[i].getNodeId());
			th.start();
		}
	}
	
	public static void main(String[] args) 
	{
		Main obj = new Main();
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter the testcase number: ");
		obj.n = sc.nextInt();
		String fileName = "input"+obj.n+".txt";
		ArrayList<ArrayList<Long>> graph = obj.readGraph(fileName);
		int s = graph.size();
		
		obj.constructGraph(graph);
		
		int size = obj.nodeList.length;
		
		System.out.println("Starting computation.....");
		obj.startComputation();
	}
}
