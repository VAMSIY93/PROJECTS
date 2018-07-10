//import java.io.*;
import java.util.*;

public class MessageQueue {
    
    private int capacity;//Number of messages that can be stored in the queue.

    private Vector<Message> msgList;// = new Vector();//The queue for receiving all incoming messages.

    /**
     * Constructor, initializes the queue.
     * 
     * @param capacity The number of messages allowed in the queue.
     */
    public MessageQueue(int capacity) 
    {
        this.capacity = capacity;
        this.msgList = new Vector<Message>(capacity);
    }

    public synchronized void addMessage(Message msg)
    {
    	msgList.add(msg);
    }
 
    public synchronized boolean isEmpty() 
    {
        return msgList.size() == 0;
    }
    
    public synchronized Message viewMesaage(int index)
    {
    	return (Message)msgList.get(index);
    }
    
    public synchronized Message getMessage(int index)
    {
        return (Message)msgList.remove(index);
    }
    
    public synchronized int getSize()
    {
    	return msgList.size();
    }
}
