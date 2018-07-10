import java.util.*;
import java.io.*;

public class Message 
{
	String msgType;
	Node src;
	
	public Message(String msg, Node node)
	{
		msgType = msg;
		src = node;
	}
}
