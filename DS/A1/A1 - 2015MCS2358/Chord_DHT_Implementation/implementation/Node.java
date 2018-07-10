package implementation;
import test.Main1;

import java.io.Console;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;

import javax.swing.plaf.synth.SynthScrollBarUI;

import com.sun.istack.internal.logging.Logger;

import kmaru.jchord.ChordKey;
import kmaru.jchord.ChordNode;
import kmaru.jchord.Finger;
import kmaru.jchord.Hash;
import kmaru.jchord.test.Main3;
import sun.applet.Main;
import sun.misc.Lock;

public class Node extends Message implements Runnable {
	
    
    public int nodeLookUpCount=0; 
    Thread t;
	long threadId;
    String nodeId; 
	private Node predecessor;
	private Node successor;
	private DHTKey nodeKey;
	private FingerTable fingerTable;	
    private static Vector<File> fileList = new Vector<File>();
    private class File{
    	String fileId;
    	DHTKey fileKey;
    }
	
    final static Logger log = Logger.getLogger(Node.class);
	
	private MessageQueue<Message> msgQueue = new MessageQueue(Main1.MESSAGE_QUEUE_CAPACITY);
	
	Message msg = new Message();
	
	PrintStream out=System.out;
	
	public void setPrintStream(PrintStream out)
	{
		this.out = out;
	}
	
	public void addFile(String fileId,DHTKey fileKey)
	{
		File file = new File();
		file.fileId = fileId;
		file.fileKey = fileKey;
		
		fileList.add(file);
	}
	
	
	public Node(String nodeId) {
		this.nodeId = nodeId;
		this.nodeKey = new DHTKey(nodeId);
		this.fingerTable = new FingerTable(this);
		this.create();
	}
	
	
	public void deleteNode() throws InterruptedException
	{
		Message mssg = new Message();
		Chord.nodeList.remove(this);
		Chord.sortedNodeMap.remove(this.nodeKey);
		
		
		successor.notifyPredecessor(predecessor);
		predecessor.notifySuccessor(successor);
		/**
		mssg.msg_enum = msgEnum.NOTIFY_NEW_PREDECESSOR;
		mssg.data = (Node)predecessor;
		mssg.sender = this;
		mssg.destNode = successor;
		if(predecessor!=null && msg.srcNode!=msg.destNode){
		sendMessage(mssg.destNode,mssg);}
		
		mssg.msg_enum = msgEnum.NOTIFY_NEW_SUCCESSOR;
		mssg.data = (Node)successor;
		mssg.sender = this;
		mssg.destNode = predecessor;
		if(predecessor!=null && msg.srcNode!=msg.destNode){
		sendMessage(mssg.destNode,mssg);} **/
		
		
	}

	/**
	 * Lookup a successor of given identifier
	 * 
	 * @param identifier
	 *            an identifier to lookup
	 * @return the successor of given identifier
	 */

	private Node findSuccessor(String identifier) {
		DHTKey key = new DHTKey(identifier);
		return findSuccessor(key);
		
		
	}

	
	/**
	 * Lookup a successor of given key
	 * 
	 * @param identifier
	 *            an identifier to lookup
	 * @return the successor of given identifier
	 * @throws InterruptedException 
	 */
	
	public Node findSuccessor(DHTKey key) {

		// add counter to evaluate lookup cost
		Main1.findSuccessorCount++;

		if (this == successor) {
			return this;
		}

		if (key.isBetween(this.getNodeKey(), successor.getNodeKey())
				|| key.compareTo(successor.getNodeKey()) == 0
				|| key.compareTo(this.getNodeKey()) == 0) {
			return successor;
		} else {
			Node node = closestPrecedingNode(key);
			if (node == this) {
				return successor.findSuccessor(key);
			}
			return node.findSuccessor(key);
		}
	}

	 /** Below Function is to process IPC **/	
	 private void findSuccessor(PrintStream out,Message mssg) throws InterruptedException {
			
		out.println("Inside findSuccessor");
		 
		 // add counter to evaluate lookup cost
	    test.Main1.findSuccessorCount++; 
		
	    DHTKey key= new DHTKey("dummy");
	    key = (DHTKey)mssg.data;
	   
	    /**
	     *  Log Info Messages
	     */
	    System.out.println("out ="+out);
		out.println("Inside findSuccessor");
		out.println("Find successor for:"+key.toString());
		out.println("findSuccesor asked by node :"+mssg.srcNode.toString());
		
		// add counter to evaluate lookup cost
		test.Main1.findSuccessorCount++;

		mssg.sender = this;
		
		if (this == successor) {
			//return this; ((SINGLE THREADED FUNCTION STATEMENT))
			mssg.msg_enum = msgEnum.PUT_SUCCESSOR;
			mssg.data = (Node) this;
			mssg.destNode = mssg.srcNode;
			sendMessage(mssg.destNode,mssg); // put message in queue of the originator(srcNode)
			return;
		}

		else if (key.isBetween(this.getNodeKey(), successor.getNodeKey())
				|| key.compareTo(successor.getNodeKey()) == 0
				|| key.compareTo(this.getNodeKey()) == 0) {
			//return successor;	((SINGLE THREADED FUNCTION STATEMENT))	
			mssg.msg_enum = msgEnum.PUT_SUCCESSOR;
			mssg.data = (Node) successor;
			mssg.destNode = mssg.srcNode;
			sendMessage(mssg.destNode,mssg); // put message in queue of the originator(srcNode)
			return;
		} 
		else 
		{		
			Node node = closestPrecedingNode(key);
		
			if (node == this) {
				//return successor.findSuccessor(key); (SINGLE THREADED FUNCTION STATEMENT)
				mssg.msg_enum = msgEnum.FIND_SUCCESSOR;
				mssg.destNode = successor;
				sendMessage(mssg.destNode,mssg); // put message in queue where request is made
			    return;
			}
			//return node.findSuccessor(key);((SINGLE THREADED FUNCTION STATEMENT))
			mssg.msg_enum = msgEnum.FIND_SUCCESSOR;
			mssg.destNode = node;
			sendMessage(mssg.destNode,mssg);// put message in queue where request is made
			return;
		}
	}

	/**
	 *  Function to find the closestPrecedingNode. Called as local function.
	 * @param key (DHTkey)
	 * @return Node (closest node for the key)
	 */
	private Node closestPrecedingNode(DHTKey key) {
		
		for (int i = HashFunc.KEY_LENGTH - 1; i >= 0; i--) {
			Finger finger = fingerTable.getFinger(i);
			DHTKey fingerKey = finger.getNode().getNodeKey();
			if (fingerKey.isBetween(this.getNodeKey(), key)) {
				return finger.getNode();
			}
		}
		return this;
	}

	/**
	 * Creates a new Chord ring.
	 */
	public void create() {
		predecessor = null;
		successor = this;
		t =new Thread(this);
		t.setName(this.nodeKey.identifier );
	}

	/**
	 * Joins a Chord ring with a node in the Chord ring
	 * 
	 * @param node
	 *            a bootstrapping node
	 * @throws InterruptedException 
	 */
	
	public void join(Node node) {
		predecessor = null;
		successor = node.findSuccessor(this.getNodeId());		
	}

	private void join(PrintStream out,Node node) throws InterruptedException {
		Message mssg = new Message();
		
		predecessor = null; // Make predecessor as null initially
		
		//successor = node.findSuccessor(this.getNodeKey());(SINGLE THREADED FUNCTION STATEMENT)
		
		/**
		 * destNode is the node you are joining . Make a request to destNode
		 * by putting FIND_SUCCESSOR message in its queue
		 */
		out.println("Node"+this);
					
		mssg.msg_enum = msgEnum.FIND_SUCCESSOR;
		mssg.destNode = node; 
		mssg.srcNode = this;
		mssg.sender = this;
		mssg.data = (DHTKey) this.getNodeKey();
		
		if(mssg.srcNode!=mssg.destNode){
		sendMessage(mssg.destNode,mssg); // put message in queue where request is made
		/**
		 * Now keep polling till you get the successor
		 */
		
		out.println("Node"+this+"is waiting for successor");
		while(true)
		{
			mssg = this.pollMessage();
			if(mssg.msg_enum!=msgEnum.EMPTY){
			if(mssg.msg_enum == msgEnum.PUT_SUCCESSOR)
			{
				this.successor = (Node)mssg.data;
				out.println("Node"+this+"has got successor as"+successor);
				break; // exit loop on getting desired predecessor node
			}
			else { this.parse(out,mssg); }}
		 }}
	
		/**
		 * successor will be populated during poll mode when the node
		 * receives the message PUT_SUCCESSOR message. Since this node cannot 
		 * join the network till it updates its successor and finger table,
		 * the first message received it should be PUT_SUCCESSOR
		 */
	    
	}//End of join 

	/**
	 * Verifies the successor, and tells the successor about this node. Should
	 * be called periodically.
	 * @throws InterruptedException 
	 */
	
	public void stabilize() {
		Node node = successor.getPredecessor();
		if (node != null) {
			DHTKey key = node.getNodeKey();
			if ((this == successor)
					|| key.isBetween(this.getNodeKey(), successor.getNodeKey())) {
				successor = node;
			}
		}
		successor.notifyPredecessor(this);
	}
	
	public void stabilize(PrintStream out) throws InterruptedException {
		
		Message mssg = new Message();
		Node predessorOfSuccessor=null;
		System.out.println("Stabilize called by node :"+this.toString());
		
		//Node node = successor.getPredecessor();
		
		mssg.srcNode = this;
		mssg.sender = this;
		mssg.destNode = successor;
		if(mssg.srcNode!=msg.destNode){
		mssg.msg_enum = msgEnum.GET_PREDECESSOR;	
		sendMessage(mssg.destNode,mssg);
		
		System.out.println("In Stabilize, Node :"+this.toString()+" is waiting for predecessor");
		out.println("In Stabilize, Node :"+this.toString()+" is waiting for predecessor");
		/**
		 * Now keep polling till you get the predecessor
		 */
		while(true)
		{
			mssg = this.pollMessage();
			if(mssg.msg_enum!=msgEnum.EMPTY){
			if(mssg.msg_enum == msgEnum.PUT_PREDECESSOR)
			{
				predessorOfSuccessor = (Node)mssg.data;
				System.out.println("In Stabilize, Node :"+this.toString()+"found predecessor \n"+
				                    predessorOfSuccessor.toString());
				break; // exit loop on getting desired predecessor node
			}
			else {this.parse(out,mssg);}
			}
		}// End of while 
		}//if(mssg.srcNode!=msg.destNode)
		
		//System.out.println("successor.getPredecessor :"+node.toString());
		if (predessorOfSuccessor!= null) {
			DHTKey key = predessorOfSuccessor.getNodeKey();
			/**
			 *  If my successor's predecessor key is between myKey and mySuccessorKey
			 *  then a node has come between me and my successor . So my successor becomes 
			 *  the new node which is the new predecessor Of my Successor  
			 */
			if ((this == successor)
					|| key.isBetween(this.getNodeKey(), successor.getNodeKey())) {
			
				successor = predessorOfSuccessor;
			}
		}//End of if (predessorOfSuccessor!= null)
		
		
		/**
		 * Now since my successor has changed . The new node which has become my 
		 * successor needs to be notified that i am its predecessor. So i will
		 * notify successor about predecessor
		 */
		// successor.notifyPredecessor(this);
		/**
		 * Notify your new successor that it has a new predecessor (this.node)
		 */
		mssg.msg_enum = msgEnum.NOTIFY_NEW_PREDECESSOR;
		mssg.data = (Node)this;
		mssg.sender = this;
		mssg.destNode = successor;
		if(msg.srcNode!=msg.destNode){
		sendMessage(mssg.destNode,mssg);}
	}

	private void notifyPredecessor(Node node) {
		
		DHTKey key = node.getNodeKey();
		if (predecessor == null
				|| key.isBetween(predecessor.getNodeKey(), this.getNodeKey())) {
			predecessor = node;
		}
	}

	private void notifySuccessor(Node node) {
		
		DHTKey key = node.getNodeKey();
		if (key.isBetween(this.getNodeKey(),successor.getNodeKey())) {
			successor = node;
		}
	}
	/**
	 * Refreshes finger table entries.
	 * @throws InterruptedException 
	 */
	
	public void initFingers() {
		int i=0;
		Finger finger = fingerTable.getFinger(0);
		Finger next_finger;
		finger.setNode(successor);// successor is always first finger
		Node n = successor;
		DHTKey key;
		
		for (i = 1; i < (HashFunc.KEY_LENGTH-1); i++) {
			finger = fingerTable.getFinger(i);
			next_finger = fingerTable.getFinger(i+1);
			key = finger.getStart();
			if(next_finger.getStart().isBetween(finger.getStart(),n.getNodeKey())){
						finger.setNode(n);}
		finger.setNode(successor.findSuccessor(key));}
		
		/** Initialise the last finger **/
		finger = fingerTable.getFinger(i);
		key = finger.getStart();
		finger.setNode(successor.findSuccessor(key));
		}
	
	private void fixFingers(PrintStream out) throws InterruptedException {
		Message mssg= new Message();
		for (int i = 0; i < HashFunc.KEY_LENGTH; i++) {
			Finger finger = fingerTable.getFinger(i);
			DHTKey key = finger.getStart();
			//finger.setNode(findSuccessor(key));
			mssg.msg_enum = msgEnum.FIND_SUCCESSOR;
			mssg.destNode = successor;
			mssg.data = (DHTKey)key;
			mssg.srcNode = this;
			mssg.sender = this;
			sendMessage(successor, mssg);			
			while(true)
			{
				mssg = this.pollMessage();
				if(mssg.msg_enum!=msgEnum.EMPTY){
				if(mssg.msg_enum == msgEnum.PUT_SUCCESSOR &&
					mssg.sender == this.successor)
					//Verify sender is the successor 
				{
					finger.node = (Node)mssg.data;
					break; // exit loop on getting desired predecessor node
				}
				else
				{
					this.parse(out,mssg);
				}
			}}//End of while
		}
	}
	
	
	private void initFingerTable(PrintStream out) throws InterruptedException {
		
		Message mssg = new Message();
		
		/**
		 * Successor's Predecessor becomes my predecessor. So ask successor for it.
		 * Wait till you get the PUT_PREDECESSOR message
		 */
		mssg.msg_enum = msgEnum.GET_PREDECESSOR;
		mssg.srcNode = this;
		mssg.sender = this;
		mssg.destNode = successor;
		sendMessage(mssg.destNode,mssg);
		
		/**
		 * Now keep polling till you get the predecessor
		 */
		while(true)
		{
			mssg = this.pollMessage();
			if(mssg.msg_enum!=msgEnum.EMPTY){
			if(mssg.msg_enum == msgEnum.PUT_PREDECESSOR &&
				mssg.sender == this.successor)
				//Verify sender is the successor 
			{
				predecessor = (Node)mssg.data;
				break; // exit loop on getting desired predecessor node
			}
			else
			{
				this.parse(out,mssg);
			}}
		}
		
		
		/**
		 * Notify successor that it has a new predecessor (this.node)
		 */
		mssg.msg_enum = msgEnum.NOTIFY_NEW_PREDECESSOR;
		mssg.data = (Node)this;
		mssg.sender = this;
		mssg.destNode = successor;
		sendMessage(mssg.destNode,mssg);
		
		/**
		 * Since Successor's Predecessor has become my predecessor. I have 
		 * become its successor. So,
		 * Notify predecessor that it has new successor(this.node) 
		 */
		if(predecessor!=null){
		mssg.msg_enum = msgEnum.NOTIFY_NEW_SUCCESSOR;
		mssg.data = (Node)this;
		mssg.sender = this;
		mssg.destNode = predecessor;
		sendMessage(mssg.destNode,mssg);}
		
			
        /**
         *  INIT Fingers
         */
		
		Finger finger = fingerTable.getFinger(0);
		Finger next_finger;
		finger.setNode(successor);// successor is always first finger
		Node n = successor;
		
		for (int i = 1; i < (HashFunc.KEY_LENGTH-1); i++) {
			finger = fingerTable.getFinger(i);
			next_finger = fingerTable.getFinger(i+1);
			DHTKey key = finger.getStart();
			{
				if(next_finger.getStart().isBetween(finger.getStart(),n.getNodeKey()))
						finger.setNode(n);
			}
			
			mssg.msg_enum = msgEnum.FIND_SUCCESSOR;
			mssg.destNode = successor;
			mssg.data = (DHTKey)key;
			mssg.srcNode = this;
			mssg.sender = this;
			sendMessage(mssg.destNode,mssg);
			//finger.setNode(findSuccessor(key));
			while(true)
			{
				mssg = this.pollMessage();
				if(mssg.msg_enum!=msgEnum.EMPTY){
				if(mssg.msg_enum == msgEnum.PUT_SUCCESSOR &&
					mssg.sender == this.successor)
					//Verify sender is the successor 
				{
					n = (Node)mssg.data;
					break; // exit loop on getting desired predecessor node
				}
				else
				{
					this.parse(out,mssg);
				}
			}}//End of while
			
			finger.setNode(n);
		}// End of For
	}
	
	public void parse(PrintStream out,Message mssg) throws InterruptedException
	{	
		out.println("\nNode"+this+"is Parsing message");
		System.out.println("\nNode"+this+"is Parsing message");
		
		switch(mssg.msg_enum)
		{
		  case FIND_SUCCESSOR: 
			  out.println("\nMessage parsed is FIND_SUCCESSOR ");
			  /** 
			   * Here processing is for the case when this 
			   * message will generally put in the queue
			   * by some other node. When the node needs to find successor
			   * itself it will directly put the message in destination node 
			   * queue 
			   */		  
			  findSuccessor(out,mssg);
			  break;
		 
		    case PUT_SUCCESSOR:
		    	out.println("\nMessage parsed is PUT_SUCCESSOR ");
		    	out.println("\nMessage parsed is PUT_SUCCESSOR ");
		    	/**
		    	 * This message indicates you asked for successor and someone else
		    	 * has put the message in your queue and you need to consume the message
		    	 * by updating your successor		  
		    	 */
		    	if(mssg.srcNode == this)// cross check that I (this.node)requested 
		    	{
		    		this.successor = (Node)mssg.data; //mssg.dataNode;
		    	}
			  
			  break;
		 	  
		  case GET_PREDECESSOR:
			  out.println("\nMessage parsed is GET_PREDECESSOR ");
			   // this is asked by a node directly to its successor
			  mssg.data = (Node) this.predecessor;
			  mssg.msg_enum = msgEnum.PUT_PREDECESSOR;
			  mssg.sender = this;
			  mssg.destNode=mssg.srcNode; 
			  mssg.destNode.addMessage(mssg);	
			  
		  case PUT_PREDECESSOR:
			  System.out.println("\nMessage parsed is PUT_PREDECESSOR ");
			  out.println("\nMessage parsed is PUT_PREDECESSOR ");
			  
			   /**
		    	 * This message indicates you asked for successor and someone else
		    	 * has put the message in your queue and you need to consume the message
		    	 * by updating your successor		  
		    	 */
		    	if(mssg.srcNode == this)// cross check that I (this.node)requested 
		    	{
		    		this.predecessor = (Node)mssg.data; //mssg.dataNode;
		    	}
			  
			  break;
			  
		  case NOTIFY_NEW_SUCCESSOR:
			  System.out.println("\nMessage parsed is NOTIFY_NEW_SUCCESSOR ");
			  out.println("\nMessage parsed is NOTIFY_NEW_SUCCESSOR ");
			  this.notifySuccessor((Node) mssg.data);
			  break;
			  
		  case NOTIFY_NEW_PREDECESSOR:
			  System.out.println("\nMessage parsed is NOTIFY_NEW_PREDECESSOR ");
			  out.println("\nMessage parsed is NOTIFY_NEW_PREDECESSOR ");
			  this.notifyPredecessor((Node) mssg.data);
			  break;
		  
		  case STABILIZE:
			  System.out.println("\nMessage parsed is STABILIZE ");
			  out.println("\nMessage parsed is STABILIZE ");
			  this.stabilize(out);
			  break;
			  
			  default :
				  out.println("\n Invalid Message Type");
			  
		}//End of switch()
		
	}// End of parse(Message)
		

	

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Node[");
		sb.append("ID=" + nodeId);
		sb.append(",KEY=" + nodeKey);
		sb.append(",THREAD_ID=" + threadId);
		sb.append("]");
		return sb.toString();
	}

	public void printFingerTable(PrintStream out) {
		out.println("=======================================================");
		out.println("FingerTable: " + this);
		out.println("-------------------------------------------------------");
		out.println("Predecessor: " + predecessor);
		out.println("Successor: " + successor);
		out.println("-------------------------------------------------------");
		for (int i = 0; i < HashFunc.KEY_LENGTH; i++) {
			Finger finger = fingerTable.getFinger(i);
			out.println(finger.getStart() + "\t" + finger.getNode());
		}
		out.println("=======================================================");
	}

	public DHTKey getNodeKey() {
		return nodeKey;
	}

	public void setNodeKey(DHTKey nodeKey) {
		this.nodeKey = nodeKey;
	}
	
	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	

	public Node getPredecessor() {
		return predecessor;
	}

	public void setPredecessor(Node predecessor) {
		this.predecessor = predecessor;
	}

	public Node getSuccessor() {
		return successor;
	}

	public void setSuccessor(Node successor) {
		this.successor = successor;
	}

	public FingerTable getFingerTable() {
		return fingerTable;
	}

	public void setFingerTable(FingerTable fingerTable) {
		this.fingerTable = fingerTable;
	}
	
	public void startThread(PrintStream out) {
		this.setPrintStream(out);
		this.t.start();
	}

	
	
	
	public void sendMessage(Node destNode,Message mssg) throws InterruptedException {
        if(destNode.isMessageQueueFull()==true)
        {	
        	System.out.println("destNode ="+destNode);
        	/**
        	 * Thread which tries to add message in the destination node queue
        	 * gets blocked
        	 */
        	wait();
        }
        
        destNode.addMessage(mssg);   
        return;
    }
	
	public void addMessage(Message mssg) throws InterruptedException {
        msgQueue.send(mssg);
    }
    
	/* Blocking Version*/
    /**private Message getNextMessage() throws InterruptedException{
        return msgQueue.receive();
    }
     * @throws InterruptedException */

    
    /* Non blocking version */
    private Message pollMessage() throws InterruptedException{
        return msgQueue.poll();
    }
    
    public boolean isMessageQueueFull() throws InterruptedException {
        return msgQueue.isFull();
    }
    
	
    
	
	
	public void run() 
	{
		
		int threadRunCount=0;
		Message mssg = new Message();
		Message poll_mssg = new Message();
		
		mssg.msg_enum = msgEnum.EMPTY;
		poll_mssg.msg_enum = msgEnum.EMPTY;
		
		 threadId =Thread.currentThread().getId();
		 out.println("Inside thread ="+threadId);
		 out.println("Method Run starting of Node="+this);
		 out.println("Inside thread ="+threadId);
		
		 /*******************************************/
		 /*         NODE INITIALIZATION CODE         */
		/*******************************************/
		
	    try{
	    	  
	    while(true)//for(int i=0;i<20;i++)// while(true)
		{
		 
	    	/*******************************************/
			/*         Poll Message                    */
			/*******************************************/
	    	
	    	TimeUnit.SECONDS.sleep(1);
	    	out.println("\n Thread "+threadId+"Polling for Run count of thread ="+threadRunCount);	  
	    	poll_mssg = this.pollMessage();
			if(poll_mssg.msg_enum!=msgEnum.EMPTY){
				 this.parse(out,poll_mssg);}// end of 
	    	
	    	
		/*******************************************/
		/*         NODE STABILIZATION CODE         */
		/*******************************************/
	
      Node preceding=null; 	 
	  if(threadRunCount==0)
	  {    
		  if(this!=Chord.getNode(0)) {
		  Main1.lock.lock();
		  out.println("Lock Grabbed by thread"+threadId); }
		  
		  if(this!=Chord.getNode(0))
		  {
			    out.println("thread "+threadId+":calling join");
				this.join(out,Chord.getNode(0));
		
			
       /**
        * Create a msg for getting predecessor 
        */  
		
		mssg.srcNode = this;
		mssg.sender = this;
		mssg.destNode = successor;
		
			if(mssg.srcNode!=mssg.destNode){
			mssg.msg_enum = msgEnum.GET_PREDECESSOR;	
			sendMessage(mssg.destNode,mssg); 
			
			while(true)
			{
				
			    mssg = this.pollMessage();
				if(mssg.msg_enum!=msgEnum.EMPTY){
				if(mssg.msg_enum == msgEnum.PUT_PREDECESSOR &&
					mssg.sender == this.successor)//Verify sender is the successor 
				{
					preceding = (Node)mssg.data;
					break; // exit loop on getting desired predecessor node
				}// end of if
				else
				{ this.parse(out,mssg);}}// end of (if mssg!=null)
			}// end of while loop 
			}// end of if(mssg.srcNode!=mssg.destNode)
			
		
		/**
		 * Now keep polling till you get the predecessor
		 */
		    
			this.stabilize(out);
		    if (preceding == null) {
			//this.getSuccessor().stabilize();
			   
				mssg.srcNode = this;
				mssg.sender = this;
				mssg.destNode = successor;
				
					if(mssg.srcNode!=mssg.destNode){
				    mssg.msg_enum = msgEnum.STABILIZE;	
					sendMessage(mssg.destNode,mssg);}}// if (preceding == null)
				
		  else {
			//preceding.stabilize();
			    
				mssg.srcNode = this;
				mssg.sender = this;
				mssg.destNode = preceding;
				
					if(mssg.srcNode!=mssg.destNode){
					mssg.msg_enum = msgEnum.STABILIZE;	
					sendMessage(mssg.destNode,mssg);}}// end of else 		
		
	    }
		  if(this!=Chord.getNode(0))
		  {
			  Main1.lock.unlock();
		      out.println("Lock Released by thread"+threadId);
		  }
		  
	  }  
	   
	
 
	  threadRunCount++;	}// while(true)
	   // System.out.println("\n Polling Over for Node "+this);
	    }//try
	    catch(InterruptedException e) {
			e.printStackTrace();}
	    
	    
	    }
	}


