import java.io.*;
import java.util.*;

public class Node
{
    /**
     * Each node has its own message queue. Create thread for each node
     */
	public Node successor,predecessor;
	public int nodeCount;
    protected String nodeId;// Unique node id for each node

    public Vector<Node> nodeList;// = new Vector<Node>();

    protected MessageQueue queue;//= new MessageQueue(capacity);   
    
    protected Node()
    {
        nodeList = new Vector<Node>();
        queue = new MessageQueue(100);
		successor = this;
		predecessor = this;
		nodeCount = 1;
         // add the necessary arguments for a node in the constructor
    }

    /**
     *Each thread will perform its function inside run
     */
    public synchronized void addNodeToList(Node node)
    {
    	nodeList.add(node);
    }
      
    protected String getNodeId()
    {
    	return this.nodeId;
    }
    
    protected void setId(String id,int num)
    {
    	nodeId = id;
    	nodeCount = num;
    }
    
    public synchronized Node getNode(int index) {
        return nodeList.get(index);
    }

    protected synchronized int getNodeListLength(){
        return nodeList.size();
    }

    public synchronized void addMessage(Message msg) 
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

}

