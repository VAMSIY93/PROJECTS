
public class Event 
{
	long time;
	String operation;
	Node src;
	Node dest;
	Object recvd;
	
	public Event(long T, String op, Node source,Node destination,Object obj)
	{
		time = T;
		src = source;
		dest = destination;
		operation = op;
		recvd = obj;
	}
	
	public String getTransaction()
	{
		return (String)recvd;
	}
	
	public Block getBlock()
	{
		return (Block)recvd;
	}
}
