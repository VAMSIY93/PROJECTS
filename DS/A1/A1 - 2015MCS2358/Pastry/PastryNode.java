import java.io.*;
import java.util.*;
import java.net.*;

public class PastryNode extends Node implements Runnable
{
	protected PastryNode lSet[], nSet[], rTable[][];
	PastryNode parent;
	private Hashtable<String,String> map;
	int rows;
	boolean isRunning;
	
	public PastryNode()
	{
		lSet = new PastryNode[4];
		nSet = new PastryNode[4];
		rTable = new PastryNode[8][16];
		parent = this;
		map = new Hashtable<String,String>();
		rows = 0;
		isRunning = true;
	}
	
	public int numOfNodes()
	{
		return this.nodeCount;
	}	
	
	private long hexStringToInt(String str)
	{
		int x;
		long sum=0;
		for(int i=0;i<8;i++)
		{
			x=hexToDecimal(str.charAt(i));
			sum+= x* (long)(Math.pow(16,7-i));
		}

		return sum;
	}
	
	private int prefix(String s1,String s2)
	{
		int i=0;
		for(;i<8;i++)
		{
			if(s1.charAt(i)!=s2.charAt(i))
				break;
		}
		return i;
	}
	
	private static int hexToDecimal(char ch)
	{
		if(ch>=48 && ch<=57)
			return (int)(ch-48);
		else
			return (int)(ch-87);
	}
	
	private static char decimalToHex(int i)
	{
		if(i>=0&&i<=9)
			return ((char)(i+48));
		else
			return ((char)(i+87));
	}
	
	private static String decrement(String s,int i)
	{
		if(s.charAt(i)=='0')
		{	
			String s1 = s.substring(0,i);
			String s2 = s.substring(i+1,8);
			s = s1+"f"+s2;
			PastryNode.decrement(s,i-1);
		}	
		else if(s.charAt(i)=='a')
		{
			String s1 = s.substring(0,i);
			String s2 = s.substring(i+1,8);
			s = s1+"9"+s2;
		}
		else
		{
			char ch = s.charAt(i);
			ch = (char)((int)ch -1);
			String s1 = s.substring(0,i);
			String s2 = s.substring(i+1,8);
			s = s1+ch+s2;
		}
		return s;
	}
	
	private String hexDifference(String s1,String s2)
	{
		String res = "";
		if(s1.compareTo(s2)<0)
		{
			String temp = s1;
			s1 = s2;
			s2 = temp;
		}	

		for(int i=7;i>=0;i--)
		{
			if(s1.charAt(i)>s2.charAt(i))
				res = "" + PastryNode.decimalToHex(PastryNode.hexToDecimal(s1.charAt(i)) - PastryNode.hexToDecimal(s2.charAt(i))) + res;
			else if(s1.charAt(i)<s2.charAt(i))
			{
				res = "" + PastryNode.decimalToHex(16+ PastryNode.hexToDecimal(s1.charAt(i)) - PastryNode.hexToDecimal(s2.charAt(i))) + res;
				PastryNode.decrement(s1,i-1);
			}
			else
				res = "0" + res;	
		}			
		return res;
	}
	
	private String performXor(String s1, String s2)
	{
		String res="";
		for(int i=7;i>=0;i--)
		{
			int n1 = hexToDecimal(s1.charAt(i));
			int n2 = hexToDecimal(s2.charAt(i));
			int n3 = n1^n2;
			res = ""+decimalToHex(n3)+res;
		}
		
		return res;
	}
	
	private void sendStateTables(PastryNode node,int flag)
	{
		Message msg = new Message("update",this);
		if(flag==1)
			msg.setMessage("last");
		else
			msg.setMessage("notLast");
		if(node!=null)
			node.addMessage(msg);
	}
	
	private void sendMyStateTables()
	{
		for(int i=0;i<4;i++)
			if(lSet[i]!=null)
			{
				Message msg = new Message("updateMe",this);
				lSet[i].addMessage(msg);
			}
		
		for(int i=0;i<4;i++)
			if(nSet[i]!=null)
			{
				Message msg = new Message("updateMe",this);
				nSet[i].addMessage(msg);
			}
		
		for(int i=0;i<8;i++)
			for(int j=0;j<16;j++)
				if(rTable[i][j]!=null && rTable[i][j]!=this)
				{
					Message msg = new Message("updateMe",this);
					rTable[i][j].addMessage(msg);
				}
	}
	
	private void updateStateDelete(PastryNode recvd,String id)
	{
		/*if(recvd.getNodeId().compareTo(this.getNodeId())>0)
			this.lSet[2] = recvd;
		else
			this.lSet[1] = recvd;*/
		
		
		for(int i=0;i<4;i++)
		{
			if(recvd.lSet[i]!=null && recvd.lSet[i].getNodeId().equals(id)==false && this.getNodeId().compareTo(recvd.lSet[i].getNodeId())>0)
			{
				if(this.lSet[1]==null)
					this.lSet[1] = recvd.lSet[i];
				else if(this.lSet[1]!=null && (this.lSet[0]==null || recvd.lSet[i].getNodeId().compareTo(this.lSet[0].getNodeId())>0) && recvd.lSet[i].getNodeId().compareTo(this.lSet[1].getNodeId())<0)
					this.lSet[0] = recvd.lSet[i];
				else if(this.lSet[1]!=null && recvd.lSet[i].getNodeId().compareTo(this.lSet[1].getNodeId())>0 && (this.lSet[0]==null || recvd.lSet[i].getNodeId().compareTo(this.lSet[0].getNodeId())>0))
				{
					this.lSet[0] = this.lSet[1];
					this.lSet[1] = recvd.lSet[i];
				}
					
			}
			else if(recvd.lSet[i]!=null && recvd.lSet[i].getNodeId().equals(id)==false && this.getNodeId().compareTo(recvd.lSet[i].getNodeId())<0)
			{
				if(this.lSet[2]==null)
					this.lSet[2] = recvd.lSet[i];
				else if(this.lSet[2]!=null && (this.lSet[3]==null || recvd.lSet[i].getNodeId().compareTo(this.lSet[3].getNodeId())<0) && recvd.lSet[i].getNodeId().compareTo(this.lSet[2].getNodeId())>0)
					this.lSet[3] = recvd.lSet[i];
				else if(this.lSet[2]!=null && recvd.lSet[i].getNodeId().compareTo(this.lSet[2].getNodeId())<0 && (this.lSet[3]==null || recvd.lSet[i].getNodeId().compareTo(this.lSet[3].getNodeId())<0))
				{
					this.lSet[3] = this.lSet[2];
					this.lSet[2] = recvd.lSet[i];
				}
			}
		}
	}
	
	private void updateLeafSet(PastryNode recvd)
	{
		if(recvd.getNodeId().compareTo(this.getNodeId())>0)
			this.lSet[2] = recvd;
		else
			this.lSet[1] = recvd;
		
		
		for(int i=0;i<4;i++)
		{
			if(recvd.lSet[i]!=null && this.getNodeId().compareTo(recvd.lSet[i].getNodeId())>0)
			{
				if(this.lSet[1]==null)
					this.lSet[1] = recvd.lSet[i];
				else if(this.lSet[1]!=null && (this.lSet[0]==null || recvd.lSet[i].getNodeId().compareTo(this.lSet[0].getNodeId())>0) && recvd.lSet[i].getNodeId().compareTo(this.lSet[1].getNodeId())<0)
					this.lSet[0] = recvd.lSet[i];
				else if(this.lSet[1]!=null && recvd.lSet[i].getNodeId().compareTo(this.lSet[1].getNodeId())>0 && (this.lSet[0]==null || recvd.lSet[i].getNodeId().compareTo(this.lSet[0].getNodeId())>0))
				{
					this.lSet[0] = this.lSet[1];
					this.lSet[1] = recvd.lSet[i];
				}
					
			}
			else if(recvd.lSet[i]!=null && this.getNodeId().compareTo(recvd.lSet[i].getNodeId())<0)
			{
				if(this.lSet[2]==null)
					this.lSet[2] = recvd.lSet[i];
				else if(this.lSet[2]!=null && (this.lSet[3]==null || recvd.lSet[i].getNodeId().compareTo(this.lSet[3].getNodeId())<0) && recvd.lSet[i].getNodeId().compareTo(this.lSet[2].getNodeId())>0)
					this.lSet[3] = recvd.lSet[i];
				else if(this.lSet[2]!=null && recvd.lSet[i].getNodeId().compareTo(this.lSet[2].getNodeId())<0 && (this.lSet[3]==null || recvd.lSet[i].getNodeId().compareTo(this.lSet[3].getNodeId())<0))
				{
					this.lSet[3] = this.lSet[2];
					this.lSet[2] = recvd.lSet[i];
				}
			}
		}
	}
	
	private void buildStateTables(Message msg)
	{		
		PastryNode recvd = (PastryNode)msg.src;
		
		this.insertToNset(recvd);
		
		if(msg.getMessage().equals("last"))
		{
			// update LeafSet
			this.updateLeafSet(recvd);	
			//optional
			this.updateNodeEntry(recvd);
		}
		
		
		// Update RouteTable
		int l = this.prefix(recvd.getNodeId(), this.getNodeId());
		for(int i = this.rows;i<=l;i++)
			for(int j=0;j<16;j++)
			{
				if(i==l && j==(int)recvd.getNodeId().charAt(i))
					this.rTable[i][j] = recvd;
				else if(j!=(int)this.getNodeId().charAt(i))
					this.rTable[i][j] = recvd.rTable[i][j];
			}
		this.rows = l+1;	
		
				
		// Send my state tables to all
		if(msg.getMessage().equals("last"))
			this.sendMyStateTables();	
		
		try
		{	Thread.sleep(100); }
		catch(Exception e)
		{}
		this.stabilize();
		this.stabilize();
		
		//Get my keys
		if(msg.getMessage().equals("last"))
		{
			for(int i=0;i<4;i++)
				if(this.lSet[i]!=null)
				{
					Message reqMsg = new Message("sendMyKeys",this);
					this.lSet[i].addMessage(reqMsg);
				}
		}
	}
	
	private void joinPastry(Message msg)
	{
		String id = msg.src.getNodeId();		
		int row = prefix(id,this.getNodeId());
		int col = hexToDecimal(id.charAt(row));
		
		if(this.rTable[row][col]==null)
		{
			this.rTable[row][col] = (PastryNode)msg.src;
			this.sendStateTables(this.rTable[row][col],1);
			this.writeToFile(msg.hops,1);
		}
		else
		{
			msg.hops = msg.hops +1;
			if(this.rTable[row][col]!=null && this.rTable[row][col]!=msg.src)
				this.rTable[row][col].addMessage(msg);
			this.sendStateTables((PastryNode)msg.src,0);
		}		
				
	}
	
	private void insertToNset(PastryNode node)
	{
		if(node.isRunning==false)
			return;
		long diff = this.hexStringToInt(this.performXor(node.getNodeId(),this.getNodeId()));
		for(int i=0;i<4;i++)
			if(node.getNodeId().equals(this.getNodeId()) || (this.nSet[i]!=null && this.nSet[i].getNodeId().equals(node.getNodeId())))
				return;

		for(int i=0;i<4;i++)
		{	

			long iNode = 0;
			if(this.nSet[i]!=null)
				iNode = this.hexStringToInt(this.performXor(this.getNodeId(),this.nSet[i].getNodeId()));
			
			if(this.nSet[i]!=null && diff<iNode)
			{
				for(int j=3;j>i;j--)
					this.nSet[j] = this.nSet[j-1];
			
				this.nSet[i] = node;
				return;
			}
			else if(this.nSet[i]==null)
			{
				this.nSet[i] = node;
				return;
			}
		}
	}
	
	private void insertToLset(PastryNode node)
	{
		if(node.isRunning==false)
			return;
		if(this.getNodeId().compareTo(node.getNodeId())<0)
		{
			if(this.lSet[2]==null)
				this.lSet[2] = node;
			else if(this.lSet[2]!=null && this.lSet[2].getNodeId().compareTo(node.getNodeId())>0)
			{
				this.lSet[3] = this.lSet[2];
				this.lSet[2] = node;
			}
			else if((this.lSet[3]==null || (this.lSet[3]!=null && this.lSet[3].getNodeId().compareTo(node.getNodeId())>0)) && this.lSet[2].getNodeId().compareTo(node.getNodeId())<0)
				this.lSet[3] = node;
		}
		else
		{
			if(lSet[1]==null)
				this.lSet[1] = node;
			else if(this.lSet[1]!=null && this.lSet[1].getNodeId().compareTo(node.getNodeId())<0)
			{
				this.lSet[0] = this.lSet[1];
				this.lSet[1] = node;
			}
			else if((this.lSet[0]==null || (this.lSet[0]!=null && this.lSet[0].getNodeId().compareTo(node.getNodeId())<0)) && this.lSet[1].getNodeId().compareTo(node.getNodeId())>0)
				this.lSet[0] = node;
		}
	}
	
	private synchronized void insertToRTable(PastryNode node)
	{
		if(node==this || node.isRunning==false)
			return;
		int row = prefix(this.getNodeId(),node.getNodeId());
		int col = hexToDecimal(node.getNodeId().charAt(row));
		if(rTable[row][col]==null)
			rTable[row][col] = node;
	}
	
	private void updateNodeEntry(PastryNode node)
	{
		this.insertToLset(node);
		this.insertToNset(node);
		int row = prefix(this.getNodeId(),node.getNodeId());
		int col = hexToDecimal(node.getNodeId().charAt(row));
		if(this.rTable[row][col]==null)
			this.rTable[row][col] = node;
		
	}
	
	private void quitMyNode()
	{
		this.isRunning = false;
		for(int i=0;i<4;i++)
			if(this.lSet[i]!=null && this.lSet[i]!=this)
			{
				Message msg = new Message("remove",this);
				this.lSet[i].addMessage(msg);
			}
		
		for(int i=0;i<8;i++)
			for(int j=0;j<16;j++)
				if(this.rTable[i][j]!=null && this.rTable[i][j]!=this)
				{
					Message msg = new Message("remove",this);
					this.rTable[i][j].addMessage(msg);
				}
		
		for(int i=0;i<4;i++)
			if(this.nSet[i]!=null && this.nSet[i]!=this)
			{
				Message msg = new Message("remove",this);
				this.nSet[i].addMessage(msg);
			}

		try
		{	Thread.sleep(5000);  }
		catch(Exception e)
		{}
		
		int i=0;
		for(String key : this.map.keySet())
		{
			String keyHash = this.getId(key);
			String value = this.map.get(key);
			Message msg = new Message("put",this);
			msg.setMessage(key+" "+value);
			boolean flag = false;
			if(keyHash.compareTo(this.getNodeId())<0)
			{				
				if(this.lSet[1 - i%2]!=null)
					this.lSet[1 - i%2].addMessage(msg);
				else if(this.lSet[1]!=null)
					this.lSet[1].addMessage(msg);
				else if(this.lSet[2]!=null)
					this.lSet[2].addMessage(msg);
				else
					flag=true;
			}
			else
			{
				if(this.lSet[2 + i%2]!=null)
					this.lSet[2 + i%2].addMessage(msg);
				else if(this.lSet[2]!=null)
					this.lSet[2].addMessage(msg);
				else if(this.lSet[1]!=null)
					this.lSet[1].addMessage(msg);
				else
					flag=true;
			}
			
			if(flag)
				for(int j=0;j<8;j++)
					for(int k=0;k<16;k++)
						if(this.rTable[j][k]!=null && this.rTable[j][k]!=this)
							this.rTable[j][k].addMessage(msg);
		}
		
		try
		{	Thread.currentThread().interrupt(); }
		catch(Exception e)
		{}
	}
	
	private void removeNode(Message msg)
	{
		boolean found=false;
		PastryNode recvd = (PastryNode)msg.src;
		String recvdNodeId = recvd.getNodeId();
		if(recvd==this)
			return;
		
		
		//Leaf Set
		if(recvd.getNodeId().compareTo(this.getNodeId())<0)
		{
			if(this.lSet[0]!=null && this.lSet[0]==recvd)
			{
				this.lSet[0]=null;
				found = true;
			}
			else if(this.lSet[1]!=null && this.lSet[1]==recvd)
			{
				this.lSet[1] = this.lSet[0];
				this.lSet[0] = null;
				found = true;
			}
			
			PastryNode node = null;
			if(this.lSet[1]!=null)
				node = this.lSet[1];

			
			if(node!=null && found)
				this.updateStateDelete(node,recvdNodeId);
		}
		else
		{	
			if(this.lSet[3]!=null && this.lSet[3]==recvd)
			{
				this.lSet[3]=null;
				found = true;
			}		
			else if(this.lSet[2]!=null && this.lSet[2]==recvd)
			{
				this.lSet[2] = this.lSet[3];
				this.lSet[3] = null;
				found = true;
			}
			
			PastryNode node = null;
			if(this.lSet[2]!=null)
				node = this.lSet[2];

			
			if(node!=null && found)
				this.updateStateDelete(node,recvdNodeId);
		}	
		
		
		//Neighbor Set
		for(int i=0;i<4;i++)
			if(this.nSet[i]!=null && this.nSet[i]==recvd)
			{
				for(int j=i;j<3;j++)
					this.nSet[j] = this.nSet[j+1];
				
				this.nSet[3] = null;
				found = true;
			}
		
		//Route Table
		int row = prefix(recvd.getNodeId(),this.getNodeId());
		int col = hexToDecimal(recvd.getNodeId().charAt(row));
		if(this.rTable[row][col]!=null && this.rTable[row][col]==recvd)
		{
			this.rTable[row][col] = null;
			found = true;
			
			boolean flag = true;
			for(int j=row;j<8 && flag;j++)
				for(int i=1;i<16 && flag;i++)
				{
					if((col+i)<16 && this.rTable[j][col+i]!=this && this.rTable[j][col+i]!=null && this.rTable[j][col+i].rTable[row][col]!=recvd)
					{
						this.rTable[row][col] = this.rTable[j][col+i].rTable[row][col];
						flag = false;
					}
					
					if(flag && (col-i)>=0 && this.rTable[j][col-i]!=this && this.rTable[j][col-i]!=null && this.rTable[j][col-i].rTable[row][col]!=recvd)
					{
						this.rTable[row][col] = this.rTable[j][col-i].rTable[row][col];
						flag = false;
					}
				}
		}
		
		//Forward To All
		if(found)
		{
			if(recvd!=null && recvd.getNodeId().compareTo(this.getNodeId())<0)
			{
				for(int i=2;i<4;i++)
					if(this.lSet[i]!=null && this.lSet[i]!=recvd)
					{
						Message forwardMsg = new Message("remove",recvd);
						this.lSet[i].addMessage(forwardMsg);
					}
			}
			else if(recvd!=null && recvd.getNodeId().compareTo(this.getNodeId())>0)
			{
				for(int i=0;i<2;i++)
					if(this.lSet[i]!=null && this.lSet[i]!=recvd)
					{
						Message forwardMsg = new Message("remove",recvd);
						this.lSet[i].addMessage(forwardMsg);
					}
			}
		}
				
	}
	
	private void stabilize()
	{
		for(int i=0;i<4;i++)
			if(this.lSet[i]!=null)
			{
				for(int j=0;j<4;j++)
					if(this.lSet[i].lSet[j]!=null && this.lSet[i].lSet[j].isRunning && this.lSet[i].lSet[j]!=this)
					{
						this.insertToRTable(this.lSet[i].lSet[j]);
						this.insertToNset(this.lSet[i].lSet[j]);
						this.insertToLset(this.lSet[i].lSet[j]);
					}
			}
		
		int min = 7;
		for(int i=0;i<4;i++)
			if(this.lSet[i]!=null)
			{
				int match = prefix(this.getNodeId(),this.lSet[i].getNodeId());
				if(match<min)
					min = match;
			}
		
		int col = hexToDecimal(this.getNodeId().charAt(min));
		for(int i=1;i<=10;i++)
		{
			if((col+i)<16 && this.rTable[min][col+i]!=null)
			{
				for(int j=0;j<4;j++)
					if(this.rTable[min][col+i].lSet[j]!=null && this.rTable[min][col+i].lSet[j].isRunning && this.rTable[min][col+i].lSet[j]!=this)
					{
						this.insertToLset(this.rTable[min][col+i].lSet[j]);
						this.insertToNset(this.rTable[min][col+i].lSet[j]);
						this.insertToRTable(this.rTable[min][col+i].lSet[j]);
					}
			}
			
			if((col-i)>=0 && this.rTable[min][col-i]!=null)
			{
				for(int j=0;j<4;j++)
					if(this.rTable[min][col-i].lSet[j]!=null && this.rTable[min][col-i].lSet[j].isRunning && this.rTable[min][col-i].lSet[j]!=this)
					{
						this.insertToLset(this.rTable[min][col-i].lSet[j]);
						this.insertToNset(this.rTable[min][col-i].lSet[j]);
						this.insertToRTable(this.rTable[min][col-i].lSet[j]);
					}
			}
		}
	}
	
	private String getId(String key)
	{
		String id = "";
		try 
		{
            id = MD5.hash(key);
        } 
		catch (Exception ex) 
		{
            ex.printStackTrace();
		}	
		
		return id;
	}
	
	private boolean checkBoundary(PastryNode lhs, PastryNode rhs,String key)
	{
		if(key.compareTo(lhs.getNodeId())>=0 && key.compareTo(rhs.getNodeId())<=0)
			return true;
		else
			return false;
	}
	
	private synchronized void put(Message msg)
	{
		String data[] = msg.message.split(" ");
		String key = data[0];
		String keyHash = getId(key);
		String value = data[1];
		
		if(keyHash.equals(this.getNodeId()))
		{
			this.map.put(key, value);
			System.out.println("key:"+key+" hash:"+keyHash+"\tstored at:"+this.getNodeId());
			System.out.print("\n>>> ");
			this.writeToFile(msg.hops,2);
			return;
		}
		else
		{
			// Leaf Set part
			int ls=0,rs=3;
			PastryNode dest = null;
			if(this.lSet[0]==null)
			{
				ls++;
				if(this.lSet[1]==null)
					ls=-1;
			}
			if(this.lSet[3]==null)
			{
				rs--;
				if(this.lSet[2]==null)
					rs=-1;;
			}
			
			boolean check = false;
			long min = hexStringToInt(hexDifference(this.getNodeId(),keyHash));
			if(ls>=0 && rs>0)
			{
				check = this.checkBoundary(this.lSet[ls],this.lSet[rs],keyHash);
				if(check)
				{
					dest = this;
					for(int i=ls;i<=rs;i++)
					{
						long diff = hexStringToInt(hexDifference(this.lSet[i].getNodeId(),keyHash));
						if(min>diff)
						{
							dest = this.lSet[i];
							min = diff;
						}
					}
				}
			}
			else if(ls==-1 && rs>0)
			{
				check = this.checkBoundary(this,this.lSet[rs],keyHash);
				if(check)
				{
					dest = this;
					for(int i=2;i<=rs;i++)
					{
						long diff = hexStringToInt(hexDifference(this.lSet[i].getNodeId(),keyHash));
						if(min>diff)
						{
							dest = this.lSet[i];
							min = diff;
						}
					}
				}
			}
			else if(ls>=0 && rs==-1)
			{
				check = this.checkBoundary(this.lSet[ls],this,keyHash);
				if(check)
				{
					dest = this;
					for(int i=ls;i<=1;i++)
					{
						long diff = hexStringToInt(hexDifference(this.lSet[i].getNodeId(),keyHash));
						if(min>diff)
						{
							dest = this.lSet[i];
							min = diff;
						}
					}
				}
			}
			
			if(dest==this && check)
			{
				this.map.put(key, value);
				System.out.println("key:"+key+" hash:"+keyHash+"\tstored at:"+this.getNodeId());
				System.out.print("\n>>> ");
				this.writeToFile(msg.hops,2);
				return;
			}
			else if(dest!=null && check)
			{
				msg.hops++;
				dest.addMessage(msg);
				return;
			}
			
			
			// RTable part
			int l = prefix(this.getNodeId(),keyHash);
			int col = hexToDecimal(keyHash.charAt(l));
			if(this.rTable[l][col]!=null)
			{
				msg.hops++;
				this.rTable[l][col].addMessage(msg);
			}
			else
			{
				min = hexStringToInt(hexDifference(this.getNodeId(),keyHash));
				dest = this;
				//LeafSet
				for(int i=0;i<4;i++)
					if(this.lSet[i]!=null && this.lSet[i]!=this)
					{
						long diff = hexStringToInt(hexDifference(this.lSet[i].getNodeId(),keyHash));
						if(prefix(this.lSet[i].getNodeId(),keyHash)>=l && diff<min)
						{
							dest = this.lSet[i];
							min = diff;
						}
					}
				
				//RTable
				for(int i=0;i<8;i++)
					for(int j=0;j<16;j++)
						if(this.rTable[i][j]!=null && this.rTable[i][j]!=this)
						{
							long diff = hexStringToInt(hexDifference(this.rTable[i][j].getNodeId(),keyHash));
							if(prefix(this.rTable[i][j].getNodeId(),keyHash)>=l && diff<min)
							{
								dest = this.rTable[i][j];
								min = diff;
							}
						}
				
				//NeighbourSet
				for(int i=0;i<4;i++)
					if(this.nSet[i]!=null && this.nSet[i]!=this)
					{
						long diff = hexStringToInt(hexDifference(this.nSet[i].getNodeId(),keyHash));
						if(prefix(this.nSet[i].getNodeId(),keyHash)>=l && diff<min)
						{
							dest = this.nSet[i];
							min = diff;
						}
					}
				
				//Result
				if(dest==this)
				{
					this.map.put(key, value);
					System.out.println("key:"+key+" hash:"+keyHash+"\tstored at:"+this.getNodeId());
					System.out.print("\n>>> ");
					this.writeToFile(msg.hops,2);
					return;
				}
				else if(dest!=null)
				{
					msg.hops++;
					dest.addMessage(msg);
				}				
			}			
		}
		
	}
	
	private synchronized void serveKeyRequest(Message msg)
	{
		String key = msg.message;
		Message response = new Message("keyRequest",this);
		PastryNode dest = (PastryNode)msg.src;
		String value = this.map.get(key);
		this.writeToFile(msg.hops,3);
		if(value!=null)
			response.setMessage(key+" "+value);
		else
			response.setMessage(key+" notFound");
		dest.addMessage(response);
		
	}

	private synchronized void get(Message msg)
	{
		String key = msg.message;
		String keyHash = this.getId(key);
		
		if(keyHash.equals(this.getNodeId()))		
		{
			this.serveKeyRequest(msg);
			return;
		}
		else
		{
			// Leaf Set part
			int ls=0,rs=3;
			PastryNode dest = null;
			if(this.lSet[0]==null)
			{
				ls++;
				if(this.lSet[1]==null)
					ls=-1;
			}
			if(this.lSet[3]==null)
			{
				rs--;
				if(this.lSet[2]==null)
					rs=-1;;
			}
			
			boolean check = false;
			long min = hexStringToInt(hexDifference(this.getNodeId(),keyHash));
			if(ls>=0 && rs>0)
			{
				check = this.checkBoundary(this.lSet[ls],this.lSet[rs],keyHash);
				if(check)
				{
					dest = this;
					for(int i=ls;i<=rs;i++)
					{
						long diff = hexStringToInt(hexDifference(this.lSet[i].getNodeId(),keyHash));
						if(min>diff)
						{
							dest = this.lSet[i];
							min = diff;
						}
					}
				}
			}
			else if(ls==-1 && rs>0)
			{
				check = this.checkBoundary(this,this.lSet[rs],keyHash);
				if(check)
				{
					dest = this;
					for(int i=2;i<=rs;i++)
					{
						long diff = hexStringToInt(hexDifference(this.lSet[i].getNodeId(),keyHash));
						if(min>diff)
						{
							dest = this.lSet[i];
							min = diff;
						}
					}
				}
			}
			else if(ls>=0 && rs==-1)
			{
				check = this.checkBoundary(this.lSet[ls],this,keyHash);
				if(check)
				{
					dest = this;
					for(int i=ls;i<=1;i++)
					{
						long diff = hexStringToInt(hexDifference(this.lSet[i].getNodeId(),keyHash));
						if(min>diff)
						{
							dest = this.lSet[i];
							min = diff;
						}
					}
				}
			}

			if(dest==this && check)
			{
				this.serveKeyRequest(msg);
				return;
			}
			else if(dest!=null && check)
			{
				msg.hops++;
				dest.addMessage(msg);
				return;
			}
			
			
			// RTable part
			int l = prefix(this.getNodeId(),keyHash);
			int col = hexToDecimal(keyHash.charAt(l));
			if(this.rTable[l][col]!=null)
			{
				msg.hops++;
				this.rTable[l][col].addMessage(msg);
			}
			else
			{
				min = hexStringToInt(hexDifference(this.getNodeId(),keyHash));
				dest = this;
				//LeafSet
				for(int i=0;i<4;i++)
					if(this.lSet[i]!=null && this.lSet[i]!=this)
					{
						long diff = hexStringToInt(hexDifference(this.lSet[i].getNodeId(),keyHash));
						if(prefix(this.lSet[i].getNodeId(),keyHash)>=l && diff<min)
						{
							dest = this.lSet[i];
							min = diff;
						}
					}
				
				//RTable
				for(int i=0;i<8;i++)
					for(int j=0;j<16;j++)
						if(this.rTable[i][j]!=null && this.rTable[i][j]!=this)
						{
							long diff = hexStringToInt(hexDifference(this.rTable[i][j].getNodeId(),keyHash));
							if(prefix(this.rTable[i][j].getNodeId(),keyHash)>=l && diff<min)
							{
								dest = this.rTable[i][j];
								min = diff;
							}
						}
				
				//NeighbourSet
				for(int i=0;i<4;i++)
					if(this.nSet[i]!=null && this.nSet[i]!=this)
					{
						long diff = hexStringToInt(hexDifference(this.nSet[i].getNodeId(),keyHash));
						if(prefix(this.nSet[i].getNodeId(),keyHash)>=l && diff<min)
						{
							dest = this.nSet[i];
							min = diff;
						}
					}
				
				//Result
				if(dest==this)
				{
					this.serveKeyRequest(msg);
					return;
				}
				else if(dest!=null)
				{
					msg.hops++;
					dest.addMessage(msg);
				}			
			}			
		}
	}
	
	private synchronized void displayResult(Message msg)
	{
		String data[] = msg.message.split(" ");
		String key = data[0];
		String value = data[1];
		String keyHash = this.getId(key);
		System.out.println("key:"+key+" hash:"+keyHash+"\tvalue:"+value+"\tfound at:"+msg.src.getNodeId());
		System.out.print("\n>>> ");
	}
	
	private synchronized void sendKeys(PastryNode dest,String key)
	{
		String value = this.map.get(key);
		Message response = new Message("requestedKey",this);
		response.setMessage(key+" "+value);
		dest.addMessage(response);
	}
	
	private synchronized void sendKeysToNewNode(Message msg)
	{	
		Vector<String> list = new Vector<String>();
		for(String key : this.map.keySet())
		{
			String keyHash = this.getId(key);
			int nodeMatch = prefix(keyHash,msg.src.getNodeId());
			int myMatch = prefix(keyHash,this.getNodeId());
			
			if(myMatch<nodeMatch)
			{
				String k = key;
				list.add(k);
				this.sendKeys((PastryNode)msg.src,key);
			}
			else if(myMatch==nodeMatch)
			{
				String k = key;
				long myDiff = hexStringToInt(hexDifference(this.getNodeId(),keyHash));
				long nodeDiff = hexStringToInt(hexDifference(msg.src.getNodeId(),keyHash));
				if(nodeDiff<myDiff)
				{
					this.sendKeys((PastryNode)msg.src, key);
					list.addElement(k);
				}
			}
		}
		
		int size = list.size();
		for(int i=0;i<size;i++)
		{
			String key = list.get(i);
			this.map.remove(key);
		}
	}
	
	private synchronized void addKeys(Message msg)
	{
		String data[] = msg.message.split(" ");
		this.map.put(data[0],data[1]);
	}
	
	private void initRTable()
	{
		for(int i=0;i<8;i++)
		{
			int index = PastryNode.hexToDecimal(this.nodeId.charAt(i));
			this.rTable[i][index] = this;
		}
	}
	
	private synchronized void writeToFile(int count,int op)
	{
		try
        {
			String data = ""+this.parent.nodeCount+","+count+","+op;
            FileWriter fw = new FileWriter("output3.csv",true);    
            //fw.write("from " + execute.node.getNodeId() + " to " + this.nodeId + " -- " + execute.command + "  " + execute.numberOfHops+"  "+execute.numberOfNodes + "\n");    
            fw.write(data+"\n");
            fw.close();    
        }
		catch(Exception e)
		{}

	}
	
	private void executeCommand(String cmd, Message msg)
	{
		if(cmd.equals("join"))
			this.joinPastry(msg);
		else if(cmd.equals("update"))
			this.buildStateTables(msg);
		else if(cmd.equals("updateMe"))
			this.updateNodeEntry((PastryNode)msg.src);
		else if(cmd.equals("sendMyKeys"))
			this.sendKeysToNewNode(msg);
		else if(cmd.equals("requestedKey"))
			this.addKeys(msg);
		else if(cmd.equals("put"))
			this.put(msg);
		else if(cmd.equals("get"))
			this.get(msg);
		else if(cmd.equals("keyRequest"))
			this.displayResult(msg);
		else if(cmd.equals("quit"))
			this.quitMyNode();
		else if(cmd.equals("remove"))
			this.removeNode(msg);
	}
	
	public void run()
	{
		try
		{
		this.initRTable(); //only pastry
		Message msg = new Message("join",this);
        Random rand = new Random();
        int range = this.parent.getNodeListLength();
        int	num = rand.nextInt(range);
        Node node = this.parent.getNode(num);
        if(node!=null && node!=this)
        	node.addMessage(msg);
        else if(this!=this.parent)
        	this.parent.addMessage(msg);
        
        
		while(true)
		{
			if(this.queue.isEmpty()==false)
			{
				msg = this.getNextMessage();
				String cmd = msg.mType;
				this.executeCommand(cmd,msg);				
			}	
			this.stabilize();
		}
		}
		catch(Exception e)
		{}
	}	
}
