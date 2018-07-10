public class Node<T> implements Runnable{
    /**
     * Each node has its own message queue. Create thread for each node
     */

    private int nodeId;// Unique node id for each node

    private static Vector<Node> nodeList = new Vector<Node>();

    private MessageQueue msg= new MessageQueue(capacity);

    public Node(int nodeid,...){
        this.nodeId=nodeid;
         // add the necessary arguments for a node in the constructor
    }

    /**
     *Each thread will perform its function inside run
     */
    public void run() {
            //TODO
       }

    public Node getNode(int index) {
        return nodeList.get(index);
    }

    public Node getNodeListLength(){
        return nodeList.length();
    }
   
    public addMessage(T mssg) {
        msg.add(mssg);
    }
   
    public void addNode(Node<T> node)
    {
         // add this node to the list
    }

    /* Blocking Version*/
    private T getNextMessage(){
        return msg.getMessage();
    }

    /* Non blocking version */
    private T pollMessage(){
        return msg.poll();
    }
}

