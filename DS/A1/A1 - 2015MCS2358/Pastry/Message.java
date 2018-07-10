import java.io.*;
import java.util.*;

public class Message 
{
	String mType;
	String message;
	Node src;
	int hops;
	
	public Message()
	{
		this.hops = 1;
		this.mType = "";
		this.message = "";
	}
	
	public Message(String type,Node source)
	{
		this.hops = 1;
		this.mType = type;
		this.src = source;
	}
	
	public void setMessage(String msg)
	{
		this.message = msg;
	}
	
	public String getMessage()
	{
		return this.message;
	}
}
