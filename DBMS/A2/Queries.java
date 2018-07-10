import java.io.*;
import java.util.*;

class Node
{
	int keys[],n;
	String records[][];
	Node child[],left,right,parent;
	boolean isLeaf;
	Node()
	{
		n=0;
		keys=new int[3];
		left=right=parent=null;
		child=new Node[4];
		child[3]=null;
		isLeaf=false;
		for(int i=0;i<3;i++)
		{
			keys[i]=-1;
			child[i]=null;
		}

	}
	Node(int c)
	{
		n=0;
		isLeaf=true;
		keys=new int[3];
		left=right=parent=null;
		records=new String[3][c];
		for(int i=0;i<3;i++)
			keys[i]=-1;
	}
}

class Print
{
	String str[];
	Print(int n)
	{
		str=new String[n];
	}
}

class Table
{
	String name;
	String attrib[];
	int rows,col;
	Node root;
	Table(int r,int c,String tname,String temp[])
	{
		name=tname;
		rows=r;
		col=c;
		attrib=new String[c];
		attrib=temp;
		root=null;
	}
}

class Queries
{
	void printParents(Table tb)
	{
		Node p=tb.root;
		System.out.println("Root:\tK1:"+p.parent);
		for(int i=0;i<p.n+1;i++)
			if(p.isLeaf==false && p.child[i]!=null)
			{
				System.out.println("Child"+i+"\tK1:"+p.child[i].parent);
				for(int j=0;j<p.child[i].n+1;j++)
					if(p.child[i].isLeaf==false && p.child[i].child[j]!=null)
						System.out.println("child"+i+"-"+j+"\tK1:"+p.child[i].child[j].parent);
			}
	}

	void printInternalElements(Table tb)
	{
		Node p=tb.root;
		System.out.println("Root:\tK1:"+p.keys[0]+"\tK2:"+p.keys[1]+"\tK3:"+p.keys[2]);
		for(int i=0;i<p.n+1;i++)
			if(p.isLeaf==false && p.child[i]!=null)
			{
				System.out.println("Child:"+i+"\tK1:"+p.child[i].keys[0]+"\tK2:"+p.child[i].keys[1]+"\tK3:"+p.child[i].keys[2]);
				for(int j=0;j<p.child[i].n+1;j++)
					if(p.child[i].isLeaf==false && p.child[i].child[j]!=null)
					{
						System.out.println("child:"+i+"-"+j+"\tK1:"+p.child[i].child[j].keys[0]+"\tK2:"+p.child[i].child[j].keys[1]+"\tK3:"+p.child[i].child[j].keys[2]);
						for(int k=0;k<p.child[i].child[j].n+1;k++)
							if(p.child[i].child[j].isLeaf==false && p.child[i].child[j].child[k]!=null)
								System.out.println("child:"+i+"-"+j+"-"+k+"\tK1:"+p.child[i].child[j].child[k].keys[0]+"\tK2:"+p.child[i].child[j].child[k].keys[1]+"\tK3:"+p.child[i].child[j].child[k].keys[2]);
					}
			}
	}

	void printLeafElements(Table tb)
	{
		Node p=tb.root;
		while(p.isLeaf==false)
			p=p.child[0];

		while(p!=null)
		{
			System.out.println("K1:"+p.keys[0]+"\tK2:"+p.keys[1]+"\tK3:"+p.keys[2]);
			p=p.right;
		}
	}

	void printAttributeValues(Table tb,int atbNo[])
	{
		Node p=getMinLeaf(tb);
		for(int i=0;i<atbNo.length;i++)
			System.out.print(tb.attrib[atbNo[i]]+"\t");
		System.out.println("\n");
		int noa=atbNo.length;
		while(p!=null)
		{
			if(p.keys[0]>=0)
				for(int i=0;i<noa;i++)
					System.out.print(p.records[0][i]+"\t");
			System.out.println("\n");
			if(p.keys[1]>=0)
				for(int i=0;i<noa;i++)
					System.out.print(p.records[1][i]+"\t");
			System.out.println("\n");
			if(p.keys[2]>=0)
				for(int i=0;i<noa;i++)
					System.out.print(p.records[2][i]+"\t");
			System.out.println("\n");

			p=p.right;	
		}
	}

	void printVectorElements(Vector vec,int count)
	{
		try
		{
		String fname="output"+count+".txt";
		PrintWriter pw=new PrintWriter(new FileWriter(fname));
		pw.println("#"+vec.size());
		for(int i=0;i<vec.size();i++)
		{			
			Object ob=vec.get(i);
			Print p=(Print)ob;
			for(int j=0;j<p.str.length;j++)
				pw.print(p.str[j]+"\t");
			pw.println("");
		}
		pw.close();
		}
		catch(Exception e)
		{}
	}

	int search(Table tb,int key)
	{
		if(tb.root==null)
			return -1;
		Node leaf=getLeafNode(tb,key);
		int i;
		for(i=0;i<leaf.n;i++)
			if(key==leaf.keys[i])
				return i;

		return -1;
	}

	Node getInternalNode(Table tb,int key)
	{
		Node p=tb.root;
		while(p.isLeaf==false)
		{
			int i=0;
			for(i=0;i<p.n;i++)
				if(p.keys[i]==-1 || key<p.keys[i])
					break;

			if(key==p.keys[i])
				return p;
			p=p.child[i];
		}

		return null;
	}

	Node getLeafNode(Table tb,int key)
	{
		Node p=tb.root;
		while(p.isLeaf==false)
		{
			int i=0;
			for(i=0;i<p.n;i++)
				if(p.keys[i]==-1 || key<p.keys[i])
					break;

			p=p.child[i];	
		}
		return p;
	}

	Node getMinLeaf(Table tb)
	{
		Node p=tb.root;
		while(p.isLeaf==false && p.child[0]!=null)
			p=p.child[0];

		return p;
	}

	Node insertIntoNewRoot(int key,Node lchild,Node rchild)
	{
		Node root=new Node();

		root.keys[0]=key;
		root.child[0]=lchild;
		root.child[1]=rchild;
		root.parent=null;
		lchild.parent=root;
		rchild.parent=root;
		root.n=1;

		return root;
	}

	void insertIntoParent(Node node,int key,Node rchild,Table tb)
	{
		int i,j;
		for(i=0;i<node.n;i++)
			if(node.keys[i]==-1 || key<node.keys[i])
				break;

		if(node.n<3)
		{
			for(j=node.n;j>i;j--)
			{
				node.keys[j]=node.keys[j-1];
				node.child[j+1]=node.child[j];
			}
			node.keys[i]=key;
			node.child[i+1]=rchild;
			node.n++;
		}	
		else
		{
			Node p=new Node();
			if(i>2)
			{
				p.keys[0]=key;
				p.child[0]=node.child[3];
				p.child[1]=rchild;
				if(node.parent!=null)
					insertIntoParent(node.parent,node.keys[2],p,tb);
				else
					tb.root=insertIntoNewRoot(node.keys[2],node,p);	
			}
			else if(i==2)
			{
				p.keys[0]=node.keys[2];
				p.child[0]=rchild;
				p.child[1]=node.child[3];

				if(node.parent!=null)
					insertIntoParent(node.parent,key,p,tb);
				else
					tb.root=insertIntoNewRoot(key,node,p);
			}
			else if(i<2)
			{
				p.keys[0]=node.keys[2];
				p.child[0]=node.child[2];
				p.child[1]=node.child[3];

				if(node.parent!=null)
					insertIntoParent(node.parent,node.keys[2],p,tb);
				else
					tb.root=insertIntoNewRoot(node.keys[2],node,p);

				if(i==0)
				{
					node.child[2]=node.child[1];
					node.child[1]=rchild;
					node.keys[1]=node.keys[0];
					node.keys[0]=key;
				}
				else
				{
					node.child[2]=rchild;
					node.keys[1]=key;
				}
			}
			node.keys[2]=-1;
			node.child[3]=null;
			node.n=2;
			p.n=1;
			p.parent=node.parent;
			p.child[0].parent=p;
			p.child[1].parent=p;
		}
	}

	void insertIntoBPlus(Table tb,String record[],int c)
	{
		int i=0,j=0,key=0;
		key=Integer.parseInt(record[0]);
		Node leaf;
		if(tb.root!=null)
			leaf=getLeafNode(tb,key);
		else
		{
			leaf=new Node(c);
			tb.root=leaf;
		}
		for(i=0;i<leaf.n;i++)
			if(leaf.keys[i]==-1 || key<leaf.keys[i])
				break;
		if(leaf.n<3)
		{			
			for(j=leaf.n;j>i;j--)
			{
				leaf.keys[j]=leaf.keys[j-1];
				leaf.records[j]=leaf.records[j-1];
			}
			leaf.keys[i]=key;
			leaf.records[i]=record;
			leaf.n++;	
		}
		else
		{
			Node p=new Node(c);
			if(i>2)
			{
				p.keys[0]=leaf.keys[2];
				p.records[0]=leaf.records[2];
				p.keys[1]=key;
				p.records[1]=record;
			}
			else if(i==2)
			{
				p.keys[1]=leaf.keys[2];
				p.records[1]=leaf.records[2];
				p.keys[0]=key;
				p.records[0]=record;

			}
			else
			{
				for(j=0;j<2;j++)
				{
					p.keys[j]=leaf.keys[j+1];
					p.records[j]=leaf.records[j+1];
				}
				if(i==0)
				{
					leaf.keys[1]=leaf.keys[0];
					leaf.records[1]=leaf.records[0];
					leaf.keys[0]=key;
					leaf.records[0]=record;
				}
				else
				{
					leaf.records[1]=record;
					leaf.keys[1]=key;
				}
			}
			leaf.n--;
			p.n=2;
			leaf.keys[2]=-1;
			leaf.records[2]=null;
			p.right=leaf.right;
			if(leaf.right!=null)
				leaf.right.left=p;
			leaf.right=p;
			p.left=leaf;
			p.parent=leaf.parent;
			if(leaf.parent==null)
				tb.root=insertIntoNewRoot(p.keys[0],leaf,p);
			else
				insertIntoParent(leaf.parent,p.keys[0],p,tb);
		}

		tb.rows++;	
	}

	int getChild(Node parent,Node child)
	{
		for(int i=0;i<=parent.n;i++)
			if(parent.child[i]==child)
				return i;

		return -1;
	}

	int getMinKey(Node node)
	{
		while(node.isLeaf==false && node.child[0]!=null)
			node=node.child[0];
		return node.keys[0];
	}

	int getMaxKey(Node node)
	{
		while(node.isLeaf==false && node.child[node.n]!=null)
			node=node.child[node.n];
		return node.keys[node.n-1];
	}

	Node helperDelete(Node parent,Node node,int key,Node oce,Table tb)
	{
		if(node.isLeaf==false)
		{
			int i=0;
			for(i=0;i<node.n;i++)
				if(node.keys[i]==-1 || key<node.keys[i])
					break;

			oce=helperDelete(node,node.child[i],key,oce,tb);
			if(oce==null)
				return oce;
			else
			{
				int j=0;
				for(j=0;j<=node.n;j++)
					if(node.child[j]==oce)
						break;
				for(int k=j;k<node.n;k++)
				{
					node.child[k]=node.child[k+1];
					node.keys[k-1]=node.keys[k];
				}
				node.child[node.n]=null;
				node.keys[node.n-1]=-1;
				node.n--;
				if(node.n==0 && node.parent==null && node.child[0]!=null)
				{
					tb.root=node.child[0];
					tb.root.parent=null;
				}
				if(node.n>=1)
				{
					oce=null;
					return oce;
				}
				else if(parent!=null)
				{
					int ch=getChild(parent,node);
					if(ch>=0 && node.n<1 && parent.child[ch+1]!=null && parent.child[ch+1].n>2 && parent.child[ch+1].parent==node.parent)
					{
						node.keys[0]=parent.keys[ch];
						node.keys[1]=parent.child[ch+1].keys[0];
						parent.keys[ch]=parent.child[ch+1].keys[1];
						node.child[1]=parent.child[ch+1].child[0];
						node.child[2]=parent.child[ch+1].child[1];
						node.child[1].parent=node;
						node.child[2].parent=node;
						node.n=node.n+2;
						for(j=0;j<parent.child[ch+1].n-1;j++)	
							parent.child[ch+1].child[j]=parent.child[ch+1].child[j+2];
						parent.child[ch+1].keys[0]=parent.child[ch+1].keys[2];
						parent.child[ch+1].keys[2]=-1;
						parent.child[ch+1].keys[1]=-1;
						for(j=0;j<parent.child[ch+1].n-1;j++)
							parent.child[ch+1].child[j+2]=null;
						parent.child[ch+1].child[parent.child[ch+1].n]=null;
						parent.child[ch+1].keys[parent.child[ch+1].n-1]=-1;
						parent.child[ch+1].n=parent.child[ch+1].n-2;
						oce=null;
						return oce;
					}
					else if(ch>0 && node.n<1 && parent.child[ch-1]!=null && parent.child[ch-1].n>2 && parent.child[ch-1].parent==node.parent)
					{
						Node leftC=parent.child[ch-1];
						node.child[2]=node.child[0];
						node.child[1]=leftC.child[leftC.n];
						node.child[0]=leftC.child[leftC.n-1];
						node.child[1].parent=node;
						node.child[0].parent=node;
						node.keys[0]=leftC.keys[2];
						node.keys[1]=parent.keys[ch-1];
						node.n=node.n+2;
						parent.keys[ch-1]=leftC.keys[1];
						for(j=0;j<2;j++)
						{	
							leftC.child[leftC.n-j]=null;
							leftC.keys[leftC.n-1-j]=-1;
						}	
						leftC.n=leftC.n-2;						
						oce=null;
						return oce;
					}
					else if(ch>=0 && node.n<1 && parent.child[ch+1]!=null && parent.child[ch+1].n<3 && parent.child[ch+1].parent==node.parent)
					{
						node.keys[0]=parent.keys[ch];
						node.keys[1]=parent.child[ch+1].keys[0];
						node.keys[2]=parent.child[ch+1].keys[1];
						for(j=1;j<=3;j++)
						{
							node.child[j]=parent.child[ch+1].child[j-1];
							if(node.child[j]!=null)
								node.child[j].parent=node;
						}
						if(node.child[3]==null)
							node.n=node.n+2;
						else
							node.n=node.n+3;

						oce=parent.child[ch+1];
						return oce;
					}
					else if(ch>0 && node.n<1 && parent.child[ch-1]!=null && parent.child[ch-1].n<3 && parent.child[ch-1].parent==node.parent)
					{
						if(parent.child[ch-1].n<2)
						{
							parent.child[ch-1].child[2]=node.child[0];
							parent.child[ch-1].child[2].parent=parent.child[ch-1];
							parent.child[ch-1].keys[1]=parent.keys[ch-1];
						}
						else
						{
							parent.child[ch-1].child[3]=node.child[0];
							parent.child[ch-1].child[3].parent=parent.child[ch-1];
							parent.child[ch-1].keys[2]=parent.keys[ch-1];							
						}
						parent.child[ch-1].n++;
						oce=node;
						return oce;
					}
				}
			}
		}
		else
		{
			int index=search(tb,key);
			if(index>=0 && index<3 && node.n>2)
			{
			for(int i=index;i<node.n-1;i++)
			{
				node.keys[i]=node.keys[i+1];
				node.records[i]=node.records[i+1];
			}
			node.keys[node.n-1]=-1;
			node.records[node.n-1]=null;
			node.n--;
			if(node.parent!=null && index==0)
				for(int i=0;i<node.parent.n;i++)
					if(key==node.parent.keys[i])
						node.parent.keys[i]=node.keys[0];
			oce=null;
			return oce;
			}
			else
			{
				Node leaf=node;
				for(int i=index;i<node.n-1;i++)
				{
					node.keys[i]=node.keys[i+1];
					node.records[i]=node.records[i+1];
				}
				node.keys[node.n-1]=-1;
				node.records[node.n-1]=null;
				node.n--;
				if(node.parent!=null && index==0)
					for(int i=0;i<node.parent.n;i++)
						if(key==node.parent.keys[i])
							node.parent.keys[i]=node.keys[0];
				if(leaf.n<2 && leaf.right!=null && leaf.right.n==3 && leaf.right.parent==leaf.parent)
				{
				leaf.keys[1]=leaf.right.keys[0];
				leaf.records[1]=leaf.right.records[0];	//DELETING PARENT LEFT
				for(int i=0;i<2;i++)
				{
					leaf.right.keys[i]=leaf.right.keys[i+1];
					leaf.right.records[i]=leaf.right.records[i+1];
				}
				leaf.right.keys[2]=-1;
				leaf.right.records[2]=null;
				leaf.right.n--;
				leaf.n++;
				for(int i=0;i<leaf.parent.n;i++)
					if(leaf.keys[1]==leaf.parent.keys[i])
						leaf.parent.keys[i]=leaf.right.keys[0];

				oce=null;
				return oce;
				}	
				else if(leaf.n<2 && leaf.left!=null && leaf.left.n==3 && leaf.left.parent==leaf.parent)
				{
				leaf.keys[1]=leaf.keys[0];	// UPDATING PARENT REMAINING
				leaf.records[1]=leaf.records[0];
				leaf.keys[0]=leaf.left.keys[2];
				leaf.records[0]=leaf.left.records[2];
				leaf.left.keys[2]=-1;
				leaf.left.records[2]=null;
				leaf.left.n--;
				leaf.n++;
				for(int i=0;i<leaf.parent.n;i++)
					if(leaf.keys[1]==leaf.parent.keys[i])
						leaf.parent.keys[i]=leaf.keys[0];

				oce=null;
				return oce;
				}
				else
				{
					if(node.n<2 && node.right!=null && node.right.n==2 && node.right.parent==node.parent)
					{
						for(int j=0;j<2;j++)
						{
							node.keys[j+1]=node.right.keys[j];
							node.records[j+1]=node.right.records[j];
							node.n++;
						}	
						oce=node.right;
						if(node.right.right!=null)
							node.right.right.left=node;
						node.right=node.right.right;
						return oce;
					}
					else if(node.n<2 && node.left!=null && node.left.n==2 && node.left.parent==node.parent)
					{
						node.left.keys[2]=node.keys[0];
						node.left.records[2]=node.records[0];
						node.left.n++;
						oce=node;
						if(node.right!=null)
							node.right.left=node.left;
						node.left.right=node.right;
						return oce;
					}
				}
			}

		}
		return oce;
	}

	boolean deleteFromBPlus(Table tb,int key)
	{
		int index=-1;
		Node x;
		if(tb.root.n==0)
			tb.root=null;
		else
			index=search(tb,key);
		if(index>=0)
		{
			x=helperDelete(null,tb.root,key,null,tb);
			return true;
		}
		else
			return false;
	}

	void deleteLessThan(Table tb[],String cond[],String temp[],boolean e)
	{
		boolean flag=true;
		for(int i=0;i<tb.length;i++)
			if(tb[i].name.equals(temp[1]))
			{
				int j,value;
				if(e)
					value=Integer.parseInt(cond[1])+1;
				else
					value=Integer.parseInt(cond[1]);
				for(j=0;j<tb[i].attrib.length;j++)
					if(cond[0].equals(tb[i].attrib[j]))
						break;

				Node p=getMinLeaf(tb[i]);	
				if(j==0)
					while(p.keys[0]<value)
						flag=deleteFromBPlus(tb[i],p.keys[0]);
				else
				{
					while(p!=null)
					{
						flag=true;						
						while(p.keys[0]>=0 && flag && p.records[0]!=null && Integer.parseInt(p.records[0][j])<value)
							flag=deleteFromBPlus(tb[i],Integer.parseInt(p.records[0][0]));	
						flag=true;
						while(p.keys[1]>=0 && flag && p.records[1]!=null && Integer.parseInt(p.records[1][j])<value)
							flag=deleteFromBPlus(tb[i],Integer.parseInt(p.records[1][0]));
						flag=true;
						while(p.keys[2]>=0 && flag && p.records[2]!=null && Integer.parseInt(p.records[2][j])<value)
							flag=deleteFromBPlus(tb[i],Integer.parseInt(p.records[2][0]));

						p=p.right;
					}
				}
			}
	}

	void deleteGreaterThan(Table tb[],String cond[],String temp[],boolean e)
	{
		boolean flag=true;
		for(int i=0;i<tb.length;i++)
			if(tb[i].name.equals(temp[1]))
			{
				int j,value;
				if(e)
					value=Integer.parseInt(cond[1])-1;
				else
					value=Integer.parseInt(cond[1]);
				for(j=0;j<tb[i].attrib.length;j++)
					if(cond[0].equals(tb[i].attrib[j]))
						break;

				Node p;	
				if(j==0)
				{
					p=getLeafNode(tb[i],value);
					while(p.keys[0]>value)
						deleteFromBPlus(tb[i],p.keys[0]);
				}
				else
				{
					p=getMinLeaf(tb[i]);
					while(p!=null)
					{
					flag=true;
					while(p.keys[0]>=0 && flag && p.records[0]!=null && Integer.parseInt(p.records[0][j])>value)
						flag=deleteFromBPlus(tb[i],Integer.parseInt(p.records[0][0]));		
					flag=true;				
					while(p.keys[1]>=0 && flag && p.records[1]!=null && Integer.parseInt(p.records[1][j])>value)
						flag=deleteFromBPlus(tb[i],Integer.parseInt(p.records[1][0]));
					flag=true;
					while(p.keys[2]>=0 && flag && p.records[2]!=null && Integer.parseInt(p.records[2][j])>value)
						flag=deleteFromBPlus(tb[i],Integer.parseInt(p.records[2][0]));

					p=p.right;
					}
				}
			}

	}

	void deleteIfEquals(Table tb[],String cond[],String temp[])
	{
		boolean flag=false,str=false;
		for(int i=0;i<tb.length;i++)
			if(tb[i].name.equals(temp[1]))
			{
				int j;
				for(j=0;j<tb[i].attrib.length;j++)
					if(cond[0].equals(tb[i].attrib[j]))
						break;

				Node p;
				if(j==0)
					flag=deleteFromBPlus(tb[i],Integer.parseInt(cond[1]));
				else
				{
					p=getMinLeaf(tb[i]);
					while(p!=null)
					{
						flag=false;
						if(p.keys[0]>=0 && p.records[0]!=null && p.records[0][j].equals(cond[1]))
							flag=deleteFromBPlus(tb[i],Integer.parseInt(p.records[0][0]));
						if(p.keys[1]>=0 && p.records[1]!=null && p.records[1][j].equals(cond[1]))
							flag=deleteFromBPlus(tb[i],Integer.parseInt(p.records[1][0]));
						if(p.keys[2]>=0 && p.records[2]!=null && p.records[2][j].equals(cond[1]))
							flag=deleteFromBPlus(tb[i],Integer.parseInt(p.records[2][0]));
						if(flag)
							break;

						p=p.right;
					}
				}
			}

	}

	Vector getEqualValues(Vector vect,int atb,String value)
	{
		Vector result=new Vector();
		for(int i=0;i<vect.size();i++)
		{
			Print pOb=(Print)vect.get(i);
			if(pOb.str[atb].equals(value))
				result.add(pOb);
		}

		return result;
	}

	Vector getGreaterValues(Vector vect,int atb,int value)
	{
		Vector result=new Vector();
		for(int i=0;i<vect.size();i++)
		{
			Print pOb=(Print)vect.get(i);
			if(Integer.parseInt(pOb.str[atb])>value)
				result.add(pOb);
		}
		return result;
	}

	Vector getLesserValues(Vector vect,int atb,int value)
	{
		Vector result=new Vector();
		for(int i=0;i<vect.size();i++)
		{
			Print pOb=(Print)vect.get(i);
			if(Integer.parseInt(pOb.str[atb])<value)
				result.add(pOb);
		}
		return result;
	}

	boolean checkIfPresent(Vector vect,Print p)
	{
		for(int i=0;i<vect.size();i++)
		{
			Print pi=(Print)vect.get(i);
			boolean flag=false;
			for(int j=0;j<p.str.length;j++)
				if(p.str[j].equals(pi.str[j])==false)
					break;
				else
					flag=true;

			if(flag)
				return true;	
		}
		return false;
	}

	Vector performOperation(Vector vect1,Vector vect2,int op)
	{
		Print pOb=(Print)vect1.get(0);
		int len=pOb.str.length;
		Vector vecI=new Vector();
		Vector vecU=new Vector();
		Vector vecD=new Vector();
		for(int i=0;i<vect1.size();i++)
		{
			Object ob1=vect1.get(i);
			Print p1=(Print)ob1;
			for(int j=0;j<vect2.size();j++)
			{
				Object ob2=vect2.get(j);
				Print p2=(Print)ob2;
				boolean flag=true;
				for(int k=0;k<p2.str.length;k++)
					if(p1.str[k].equals(p2.str[k])==false)
						flag=false;

				if(flag)
				{	
					if(checkIfPresent(vecI,p2)==false)
				    	vecI.add(p2);
				    if(checkIfPresent(vecU,p2)==false)
						vecU.add(p2);
				}	
				else	
				{
					if(checkIfPresent(vecU,p1)==false)
						vecU.add(p1);
					if(checkIfPresent(vecU,p2)==false)
						vecU.add(p2);
				}
			}
		}

		if(op==1)
			return vecU;
		else if(op==2)
			return vecI;
		else
		{
			for(int i=0;i<vecI.size();i++)
			{
				Print p=(Print)vecI.get(i);
				if(checkIfPresent(vecU,p))
						vecU.remove(p);
			}
			return vecU;
		}
	}

	Vector copyTableToVector(Table tb)
	{
		Vector vect=new Vector();
		Node p=getMinLeaf(tb);
		int c=tb.attrib.length;
		while(p!=null)
		{
			for(int i=0;i<3;i++)
				if(p.keys[i]>0)
				{
					Print rec=new Print(c);
					for(int j=0;j<c;j++)
						rec.str[j]=p.records[i][j];
					vect.add(rec);
				}
			p=p.right;
		}
		return vect;
	}

	Vector crossProduct(Vector vect,Table tb)
	{
		Print pOb=(Print)vect.get(0);
		int noAtb1=pOb.str.length;
		int noAtb2=tb.attrib.length;
		int tAb=noAtb1+noAtb2;
		Vector output=new Vector();
		for(int i=0;i<vect.size();i++)
		{
			pOb=(Print)vect.get(i);
			Node p=getMinLeaf(tb);
			while(p!=null)
			{
				for(int j=0;j<3;j++)
					if(p.keys[j]>0)
					{
						Print rec=new Print(tAb);
						int k=0;
						for(k=0;k<noAtb1;k++)
							rec.str[k]=pOb.str[k];
						for(int l=0;l<noAtb2;l++)
							rec.str[l+k]=p.records[j][l];
						output.add(rec);
					}

				p=p.right;
			}
		}

		return output;
	}

	Print getFinalAtbSet(Print p,Table tb)
	{
		int i=0;
		Print output=new Print(p.str.length+tb.attrib.length);
		for(i=0;i<p.str.length;i++)
			output.str[i]=p.str[i];
		for(int j=0;j<tb.attrib.length;j++)
			output.str[i+j]=tb.attrib[j];

		return output;
	}

	Print getJoinAtbSet(Print p,Table tb,int atb2)
	{
		Print output=new Print(p.str.length+tb.attrib.length-1);
		int i=0;
		for(i=0;i<p.str.length;i++)
			output.str[i]=p.str[i];
		for(int j=0,k=0;j<tb.attrib.length;j++)
			if(j!=atb2)
				output.str[i+(k++)]=tb.attrib[j];

		return output;	
	}

	Vector doJoin(Vector vect,int atb1,Table tb,int atb2)
	{
		Print pOb=(Print)vect.get(0);
		int noAtb1=pOb.str.length;
		int noAtb2=tb.attrib.length;
		int tAb=noAtb1+noAtb2-1;
		Vector output=new Vector();
		for(int i=0;i<vect.size();i++)
		{
			pOb=(Print)vect.get(i);
			Node p=getMinLeaf(tb);
			while(p!=null)
			{
				for(int j=0;j<3;j++)
					if(p.keys[j]>0 && pOb.str[atb1].equals(p.records[j][atb2]))
					{
						Print rec=new Print(tAb);
						int k=0;
						for(k=0;k<noAtb1;k++)
							rec.str[k]=pOb.str[k];
						for(int l=0,m=0;l<noAtb2;l++)
							if(l!=atb2)
								rec.str[(m++)+k]=p.records[j][l];
						output.add(rec);
					}

				p=p.right;
			}
		}

		return output;

	}

	Vector removeDuplicates(Vector vec)
	{
		boolean flag=true;
		Print ob1=(Print)vec.get(0);
		Print ob2=ob1;
		Vector output=new Vector(ob1.str.length);
		output.add(ob1);
		for(int i=1;i<vec.size();i++)
		{
			ob2=(Print)vec.get(i);
			for(int j=0;j<i;j++)
			{
				int k=0;
				flag=true;
				ob1=(Print)vec.get(j);
				for(k=0;k<ob1.str.length;k++)
					if(ob1.str[k].equals(ob2.str[k]))
						flag=false;
					else
						break;
				if(k<ob1.str.length)
					flag=true;
				else
					break;								
			}
			if(flag)
				output.add(ob2);
		}

		return output;
	}

	Vector performJoin(String condList[][],int condType[],Table tb[],String setAtb[])
	{
		int value,i=0;
		for(i=0;i<tb.length;i++)
				if(tb[i].name.equals(condList[0][0]))
					break;
		Vector vect1=copyTableToVector(tb[i]);
		Print vect1Atb=new Print(tb[i].attrib.length);
		for(int j=0;j<tb[i].attrib.length;j++)
			vect1Atb.str[j]=tb[i].attrib[j];
		boolean cross=true;			
		for(i=0;i<condType.length;i++)
		{
			int tbno1=0,tbno2=0,atb1=0,atb2=0;
			for(tbno1=0;tbno1<tb.length;tbno1++)
				if(tb[tbno1].name.equals(condList[i][0]))
					break;

			if(condList[i][3]==null)
			{
				if(i>0)
					for(int j=0;j<i;j++)
						if(condList[i][0].equals(condList[j][0]) || condList[i][0].equals(condList[j][2]))
							cross=false;

				if(i>0 && cross)
				{
					vect1=crossProduct(vect1,tb[tbno1]);
					vect1Atb=getFinalAtbSet(vect1Atb,tb[tbno1]);
					cross=true;			
				}

				for(atb1=vect1Atb.str.length-1;atb1>=0;atb1--)
					if(condList[i][1].equals(vect1Atb.str[atb1]))
						break;		

				if(condList[i][2].charAt(0)>47 && condList[i][2].charAt(0)<58)
				{
					value=Integer.parseInt(condList[i][2]);
					if(condType[i]==1)
						vect1=getGreaterValues(vect1,atb1,value);
					else if(condType[i]==2)
						vect1=getLesserValues(vect1,atb1,value);
					else if(condType[i]==4)
					{
						value=value-1;
						vect1=getGreaterValues(vect1,atb1,value);
					}
					else if(condType[i]==5)
					{
						value=value+1;
						vect1=getLesserValues(vect1,atb1,value);
					}
				}
				else if(condType[i]==3)
					vect1=getEqualValues(vect1,atb1,condList[i][2]);
			}
			else
			{
				int ctb=0;
				if(i>0)
				{
					for(int j=0;j<i;j++)
						if(condList[i][0].equals(condList[j][0]) || condList[i][0].equals(condList[j][2]))
						{
							cross=false;
							ctb=1;
						}

					for(int j=0;j<i;j++)
						if(condList[i][2].equals(condList[j][0]) || condList[i][2].equals(condList[j][2]))
						{
							cross=false;
							ctb=3;
						}

					if(cross)
					{
						vect1=crossProduct(vect1,tb[tbno1]);
						vect1Atb=getFinalAtbSet(vect1Atb,tb[tbno1]);
						cross=true;
						ctb=1;	
					}	
				}
				for(atb1=vect1Atb.str.length-1;atb1>=0;atb1--)
					if(condList[i][1].equals(vect1Atb.str[atb1]))
						break;

				for(tbno2=0;tbno2<tb.length;tbno2++)
					if(tb[tbno2].name.equals(condList[i][2]))
						break;	
				
				if(ctb==1 || i==0)
				{
					for(atb2=0;atb2<tb[tbno2].attrib.length;atb2++)
						if(condList[i][3].equals(tb[tbno2].attrib[atb2]))
							break;
				}
				else if(ctb==3)
				{
					for(atb2=0;atb2<tb[tbno1].attrib.length;atb2++)
						if(condList[i][1].equals(tb[tbno1].attrib[atb2]))
							break;
					if(i>0)	
						tbno2=tbno1;	
				}
				vect1=doJoin(vect1,atb1,tb[tbno2],atb2);
				vect1Atb=getJoinAtbSet(vect1Atb,tb[tbno2],atb2);
			}	

		}
		int atbNo[]=new int[setAtb.length];
		for(i=0;i<setAtb.length;i++)
			for(int j=0;j<vect1Atb.str.length;j++)
			{
				if(setAtb[i].equals(vect1Atb.str[j]))
					atbNo[i]=j;
				if(setAtb[i].equals(vect1Atb.str[j]))
					break;
			}

		Print pOb;	
		Vector result=new Vector();
		for(i=0;i<vect1.size();i++)
		{
			pOb=(Print)vect1.get(i);
			Print record=new Print(setAtb.length);
			for(int j=0;j<setAtb.length;j++)
				record.str[j]=pOb.str[atbNo[j]];
			result.add(record);
		}
				
		return result;
	}

	Vector helpOperate(String temp[],int count,Table tb[])
	{
		Vector result=new Vector();
		String exc[]=temp[2].split(",");
		Print atributes;
		String tbAtb[][]=new String[exc.length][2];
		String setAtb[]=new String[exc.length];
		for(int i=0;i<setAtb.length;i++)
		{
			String tmp[]=exc[i].split("\\.");
			tbAtb[i]=tmp;
			setAtb[i]=tmp[1];
		}
		int atbNo[]=new int[setAtb.length];
		String setTb[]=temp[3].split(",");
		int tbno[]=new int[setTb.length];
		for(int i=0;i<setTb.length;i++)
			for(int j=0;j<tb.length;j++)
				if(setTb[i].equals(tb[j].name))
					tbno[i]=j;

		if(temp.length<5)
		{
			if(setTb.length>1)
			{
				atributes=new Print(tb[tbno[0]].attrib.length);
				result=copyTableToVector(tb[tbno[0]]);
				for(int i=0;i<tb[tbno[0]].attrib.length;i++)
					atributes.str[i]=tb[tbno[0]].attrib[i];
				for(int i=1;i<setTb.length;i++)
				{
					atributes=getFinalAtbSet(atributes,tb[tbno[i]]);
					result=crossProduct(result,tb[tbno[i]]);
				}
				for(int j=0;j<setAtb.length;j++)
					for(int i=0;i<atributes.str.length;i++)
						if(setAtb[j].equals(atributes.str[i]))
							atbNo[j]=i;

				Print pOb;	
				for(int i=0;i<result.size();i++)
				{
					pOb=(Print)result.get(i);
					Print record=new Print(setAtb.length);
					for(int j=0;j<setAtb.length;j++)
						record.str[j]=pOb.str[atbNo[j]];
					result.add(record);
				}		
				result=removeDuplicates(result);
				printVectorElements(result,count);
			}
			else
			{
				Node p=getMinLeaf(tb[tbno[0]]);
				for(int j=0;j<setAtb.length;j++)
					for(int i=0;i<tb[tbno[0]].attrib.length;i++)
						if(setAtb[j].equals(tb[tbno[0]].attrib[i]))
							atbNo[j]=i;
		
				while(p!=null)
				{
					for(int m=0;m<3;m++)
						if(p.keys[m]>=0)
						{
							Print record=new Print(setAtb.length);
							for(int j=0;j<setAtb.length;j++)
								record.str[j]=p.records[m][atbNo[j]];
							result.add(record);
						}
					p=p.right;	
				}
				result=removeDuplicates(result);
				printVectorElements(result,count);
			}
		}		
		else
		{	
			String setCond[]=temp[4].split(",");
			int condType[]=new int[setCond.length];
			boolean valueArr[]=new boolean[setCond.length];
			String condTables[][]=new String[setCond.length][4];
			String sp="=";
			for(int i=0;i<setCond.length;i++)
			{
				if(setCond[i].indexOf('>')>0 && setCond[i].indexOf('=')<0)
				{
					condType[i]=1;
					sp=">";
				}
				else if(setCond[i].indexOf('<')>0 && setCond[i].indexOf('=')<0)
				{
					condType[i]=2;
					sp="<";
				}
				else if(setCond[i].indexOf('=')>0 && setCond[i].indexOf('<')<0 && setCond[i].indexOf('>')<0)
				{
					condType[i]=3;
					sp="=";
				}
				else if(setCond[i].indexOf('>')>0 && setCond[i].indexOf('=')>0)
				{
					condType[i]=4;
					sp=">";
				}
				else if(setCond[i].indexOf('<')>0 && setCond[i].indexOf('=')>0)
				{
					condType[i]=5;
					sp="<";
				}
	
				String parts[]=setCond[i].split(sp);
				String first[]=parts[0].split("\\.");
				condTables[i][0]=first[0];
				condTables[i][1]=first[1];
				if(condType[i]>3)
					parts[1]=parts[1].substring(1);
				if(parts[1].indexOf('.')<0)
				{
					valueArr[i]=true;
					condTables[i][2]=parts[1];
					condTables[i][3]=null;
				}
				else
				{
					valueArr[i]=false;
					String second[]=parts[1].split("\\.");
					condTables[i][2]=second[0];
					condTables[i][3]=second[1];
				}
			}
			result=performJoin(condTables,condType,tb,setAtb);
			result=removeDuplicates(result);					
		}

		return result;				
	}

	public static void main(String args[])
	{
		int r=0,c=0,not=0;
		String line,tName="";
		Queries bpt=new Queries();
		try
		{
			BufferedReader br=new BufferedReader(new FileReader("finalInput.txt"));			
			if((line=br.readLine())!=null)
				not=Integer.parseInt(line.substring(1));

			Table tb[]=new Table[not];
			for(int i=0;i<not;i++)
			{
				if((line=br.readLine())!=null)
					tName=line;

				if((line=br.readLine())!=null)
					c=Integer.parseInt(line.substring(1));

				String temp[]=new String[c];
				if((line=br.readLine())!=null)
					temp=line.split(",");

				if((line=br.readLine())!=null)
					r=Integer.parseInt(line.substring(1));

				tb[i]=new Table(0,c,tName,temp);

				for(int j=0;j<r;j++)
				{
					if((line=br.readLine())!=null)
					{
						temp=line.split(",");
						int key=Integer.parseInt(temp[0]);

						if(bpt.search(tb[i],key)<0)
							bpt.insertIntoBPlus(tb[i],temp,c);
						else
							System.out.println("PRIMARY KEY CONSTRAINT VIOLATED.");
					}
				}

				if((line=br.readLine())!=null)
					System.out.print("");
			}

			br=new BufferedReader(new FileReader("sample2.txt"));
			int count=1;
			while((line=br.readLine())!=null)
			{
				String temp[]=line.split(" ");
				boolean flag=true;
				if(temp[0].equals("insert"))
				{
					String record[]=temp[2].split(",");
					for(int i=0;i<tb.length;i++)
						if(tb[i].name.equals(temp[1]) && bpt.search(tb[i],Integer.parseInt(record[0]))<0)
							bpt.insertIntoBPlus(tb[i],record,tb[i].attrib.length);	
						else if(tb[i].name.equals(temp[1]) && bpt.search(tb[i],Integer.parseInt(record[0]))>=0)
							System.out.println("PRIMARY KEY CONSTRAINT VIOLATED");
				}
				else if(temp[0].equals("delete"))
				{
					if(temp[2].indexOf('<')>0 && temp[2].indexOf('=')<0)
					{
						String cond[]=temp[2].split("<");
						bpt.deleteLessThan(tb,cond,temp,false);													
					}
					else if(temp[2].indexOf('>')>0 && temp[2].indexOf('=')<0)
					{
						String cond[]=temp[2].split(">");
						bpt.deleteGreaterThan(tb,cond,temp,false);
					}
					else if(temp[2].indexOf('<')<0 && temp[2].indexOf('>')<0 && temp[2].indexOf('=')>0)
					{
						String cond[]=temp[2].split("=");
						bpt.deleteIfEquals(tb,cond,temp);	
					}
					else if(temp[2].indexOf('<')>0 && temp[2].indexOf('=')>0)
					{
						String cond[]=temp[2].split("<");
						cond[1]=cond[1].substring(1);
						bpt.deleteLessThan(tb,cond,temp,true);
					}
					else if(temp[2].indexOf('>')>0 && temp[2].indexOf('=')>0)
					{
						String cond[]=temp[2].split(">");
						cond[1]=cond[1].substring(1);
						bpt.deleteLessThan(tb,cond,temp,true);
					}
				}
				else if(temp[0].equals("update"))
				{
					String cond[]=temp[2].split("=");
					String setAtb[]=temp[3].split("=");
					for(int i=0;i<tb.length;i++)
						if(tb[i].name.equals(temp[1]))
						{
							int j,k;
							for(j=0;j<tb[i].attrib.length;j++)
								if(cond[0].equals(tb[i].attrib[j]))
									break;

							for(k=0;k<tb[i].attrib.length;k++)
								if(setAtb[0].equals(tb[i].attrib[k]))
									break;
							Node p;
							if(j==0)
							{
								int value=Integer.parseInt(cond[1]);
								p=bpt.getLeafNode(tb[i],value);
								int index=bpt.search(tb[i],value);
								if(index>=0 && k>0)
									p.records[index][k]=setAtb[1];
								else if(index>=0 && k==0 && bpt.search(tb[i],Integer.parseInt(setAtb[1]))<0)
								{
									String record[]=p.records[index];
									record[0]=setAtb[1];
									flag=bpt.deleteFromBPlus(tb[i],value);
									bpt.insertIntoBPlus(tb[i],record,tb[i].attrib.length);
								}
								else if(index>=0 && k==0 && bpt.search(tb[i],Integer.parseInt(setAtb[1]))>=0)
									System.out.println("PRIMARY KEY CONSTRAINT VIOLATED");
							}
							else
							{
								p=bpt.getMinLeaf(tb[i]);
								while(p!=null)
								{
									flag=false;
									for(int m=0;m<3;m++)
										if(p.keys[m]>=0 && p.records[m][j].equals(cond[1]))
										{
											if(k==0 && bpt.search(tb[i],Integer.parseInt(setAtb[1]))<0)
											{
												String record[]=p.records[m];
												record[k]=setAtb[1];
												flag=bpt.deleteFromBPlus(tb[i],Integer.parseInt(record[0]));
												bpt.insertIntoBPlus(tb[i],record,tb[i].attrib.length);
											}
											else if(k==0 && bpt.search(tb[i],Integer.parseInt(setAtb[1]))>=0)
												System.out.println("PRIMARY KEY CONSTRAINT VIOLATED");
											else
												p.records[m][k]=setAtb[1];
											flag=true;
										}	

									if(flag)
										break;	
									p=p.right;
								}
							}
						}
				}
				else if(temp[0].equals("project"))
				{
					int op=0;
					if(temp.length<=5)
					{
						Vector output=bpt.helpOperate(temp,count,tb);
						bpt.printVectorElements(output,count);
						count++;						
					}
					else
					{
						op=0;
						String query2[],query1[];
						if(temp[5].equals("select"))
						{
							query1=new String[4];
							query2=new String[temp.length-4];
							for(int i=1;i<query2.length;i++)
								query2[i]=temp[i+4];
							for(int i=0;i<query1.length;i++)
								query1[i]=temp[i];

							if(temp[4].equals("union"))
								op=1;
							else if(temp[4].equals("intersection"))
								op=2;
							else if(temp[4].equals("difference"))
								op=3;
						}
						else
						{
							query1=new String[5];
							query2=new String[temp.length-5];
							for(int i=1;i<query2.length;i++)
								query2[i]=temp[i+5];
							for(int i=0;i<query1.length;i++)
								query1[i]=temp[i];

							if(temp[5].equals("union"))
								op=1;
							else if(temp[5].equals("intersection"))
								op=2;
							else if(temp[5].equals("difference"))
								op=3;		
						}
						Vector output1=bpt.helpOperate(query1,count,tb);
						Vector output2=bpt.helpOperate(query2,count,tb);

						int tno1,tno2;
						flag=true;
						String exc1[]=query1[2].split(",");
						String exc2[]=query2[2].split(",");
						String setAtb1[]=new String[exc1.length];
						String setAtb2[]=new String[exc2.length];
						for(int i=0;i<exc1.length;i++)
						{
							String tmp1[]=exc1[i].split("\\.");
							setAtb1[i]=tmp1[1];
							String tmp2[]=exc2[i].split("\\.");
							setAtb2[i]=tmp2[1];
						}

						if(setAtb1.length==setAtb2.length)
							for(int i=0;i<setAtb1.length;i++)
							{
								int j;
								for(j=0;j<setAtb2.length;j++)
								{
									if(setAtb1[i].equals(setAtb2[j]))
										break;
								}
								if(j==setAtb2.length)
									flag=false;
								if(flag==false)
									break;
							}
						else
							flag=false;

						Vector fOutput=bpt.performOperation(output1,output2,op);
						bpt.printVectorElements(fOutput,count);
					}			
				}
				count++;
			}
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}
}