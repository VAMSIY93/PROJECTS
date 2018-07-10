import java.io.*;
import java.util.*;

public class Message 
{
	protected String mType;
	private String message;
	protected Node src;
	
	public Message()
	{
		this.mType = "";
		this.message = "";
	}
	
	public Message(String type,Node source)
	{
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
