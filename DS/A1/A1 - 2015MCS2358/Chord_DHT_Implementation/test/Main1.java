package test;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import implementation.Chord;
import implementation.ChordException;
import implementation.HashFunc;
import implementation.Node;
import sun.misc.Lock;

public class Main1 extends Thread {
	
   	public static int NUM_OF_NODES = 4;
	
	public static final int THREAD_SLEEP_TIME=20;
	
	public static final int MESSAGE_QUEUE_CAPACITY=100;
	
	public static int findSuccessorCount = 0;
	
    public static int fingerLookupCount=0;
    
    public static Lock lock = new Lock();
	
	public static final ThreadGroup root = Thread.currentThread().getThreadGroup( );
	public static final ThreadMXBean thbean = ManagementFactory.getThreadMXBean( );
	public static Thread[] nodeThreads= new Thread[NUM_OF_NODES];
	
	public static Node mainNode;
	
	public static void createOverlayNetwork(PrintStream out,String host) throws MalformedURLException {
	
		int port=Console.getRandomArbitrary(); 
		
		for (int i = 0; i < NUM_OF_NODES; i++) {
			URL url = new URL("http", host, (port +i)+"");
			try {
				Chord.createNode(url.toString());
			} catch (ChordException e) {
				e.printStackTrace();
				System.exit(0);
			}
		}
		System.out.println(NUM_OF_NODES + " Nodes are created but need to be stabilized");

		System.out.println("\n Going to Create Ring for the Overlay Network");
		
		for (int i = 1; i < NUM_OF_NODES; i++) {
			Node node = Chord.getNode(i);
			node.join(Chord.getNode(0));
			Node preceding = node.getSuccessor().getPredecessor();
			node.stabilize();
			if (preceding == null) {
				node.getSuccessor().stabilize();
			} else {
				preceding.stabilize();
			}
		}
		System.out.println("Chord Overlay Network Ring is established ");		

		out.println("\n Chord Overlay Nodes in the arrival order :\n");
		
		for (int i = 0; i < NUM_OF_NODES; i++) {
			Node node = Chord.getNode(i);
			out.println("Node["+i+"]="+node);
		}
		
		out.println("\n Chord Overlay Nodes in the sorted order :\n");
		
		for (int i = 0; i < NUM_OF_NODES; i++) {
			Node node = Chord.getSortedNode(i);
			out.println("Node["+i+"]="+node);
		}
		
		System.out.println("Chord Overlay Network Ring is established. Will Init Fingers Now");	
		
		for (int i = 0; i < NUM_OF_NODES; i++) {
			Node node = Chord.getNode(i);
			node.initFingers();
		}
		
		System.out.println("Finger Tables are fixed. Chord Network is stabilized");
		out.println("Finger Tables are fixed. Chord Network is stabilized");

		for (int i = 0; i < NUM_OF_NODES; i++) {
			Node node = Chord.getSortedNode(i);			
			node.printFingerTable(out);
		}
		
				
	}

	public static double avgLookupCount(PrintStream out)
	{
		Node targetNode;
		double average = 0;
		for (int i = 0; i < NUM_OF_NODES; i++) {
		Node node = Chord.getSortedNode(i);
		
		out.println("Lookup from: " + node);
		int totalLookupCount = 0;
		int maxCount = 0;
		
		for (int j = 0; j < NUM_OF_NODES; j++) {
			targetNode = Chord.getSortedNode(j);
			Main1.findSuccessorCount=0;
			node.findSuccessor(targetNode.getNodeKey());
			totalLookupCount +=Main1.findSuccessorCount=0;
						
			if (maxCount < Main1.findSuccessorCount) {
				maxCount =Main1.findSuccessorCount;
			}
			// out.println("Total lookup hop count for run"+i+"="+totalLookupCount);	
		}
		average = (double)(totalLookupCount/NUM_OF_NODES);
		out.println("Num of Nodes="+NUM_OF_NODES+", Average Count ="+(double)(totalLookupCount/NUM_OF_NODES) + ",MaxLookupCount: " + maxCount);
		}
		return (double)average;
	}
	
	
	
	public static void startNodeThreads(PrintStream threadOutput)
	{
		for (int i = 0; i < NUM_OF_NODES; i++) {
			Node node = Chord.getNode(i);
			node.startThread(threadOutput);			
		}
	}
	
	public static void main(String[] args) throws Exception{

		int n=0;
		int i=0;
		
		PrintStream out,out1,out2;
		out = new PrintStream("result.log");
		out1 = new PrintStream("result1.log");
		out2 = new PrintStream("result2.log");


		long start = System.currentTimeMillis();

		String host = InetAddress.getLocalHost().getHostAddress();
		
		Console.consoleScreen(out,out1,out2,host);

        /**
		n = root.enumerate(nodeThreads,true );	
		out.println("\nPrinting all threads :\n");
		for (i = 0; i < n; i++) 
	    out.println("Thread No:" + i + " = "+nodeThreads[i].getId());
        **/
	
		
	}
	
}
