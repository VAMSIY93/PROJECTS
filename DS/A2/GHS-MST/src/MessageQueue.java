import java.io.*;
import java.util.*;

public class MessageQueue 
{
    
    private Vector<Message> msgList;
    
    public MessageQueue(int capacity) 
    {
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
