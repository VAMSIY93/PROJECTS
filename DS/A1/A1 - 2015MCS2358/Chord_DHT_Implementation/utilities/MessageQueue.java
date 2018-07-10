package implementation;
import java.util.Vector;

import implementation.Message.msgEnum;

public class MessageQueue<T> {
    		 
	private int capacity;//Number of messages that can be stored in the queue.
    private Vector<T> queue;
//The queue for receiving all incoming messages.

    /**
     * Constructor, initializes the queue.
     * 
     * @param capacity The number of messages allowed in the queue.
     */
    public MessageQueue(int capacity) {
        this.capacity = capacity;
        queue = new Vector<T>(capacity);
    }

  
  /** Blocking Queue Implementation **/
    /**
     * send function adds message in the queue  
     * @param message
     * @throws InterruptedException
     */
    
    public synchronized void send(T message) throws InterruptedException {
    	
    	while (this.queue.size()==capacity)
            wait();
        this.queue.addElement(message);
        notify();
    }
	
    public synchronized Message receive() throws InterruptedException {
    	notify();
        while (this.queue.isEmpty())
            wait();
        Message msg = (Message)queue.firstElement();
        queue.removeElement(msg);
        return msg; 
 	
    }
    
    public synchronized Message poll() throws InterruptedException {
    	    Message msg=new Message();
    	    msg.msg_enum=msgEnum.EMPTY;
    	    while (!this.queue.isEmpty())
    	    { 
    	    	//wait();
    	    	msg = (Message)queue.firstElement();
    	    	System.out.println("Inside poll(), Message received by "+msg.destNode);
    	    	System.out.println("Inside poll(), Message send by "+msg.sender);
    	    	System.out.println("Inside poll(), Message source "+msg.srcNode);
    	    	System.out.println("Inside poll(), Message dequed is "+msg.msgEnumToString(msg.msg_enum));
    	    	queue.removeElement(msg);
    	    }
        
        return msg; 
 	
    }
    
    /** Non Blocking Queue Implementation **/
 
    private boolean isEmpty() {
        	return queue.size() == 0;
    }
    public boolean isFull() {
    	return queue.size() == capacity;
    }
}