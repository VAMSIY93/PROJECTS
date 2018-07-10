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

class BPlusTree
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

	void printVectorElements(Vector vec)
	{
		for(int i=0;i<vec.size();i++)
		{			
			Object ob=vec.get(i);
			Print p=(Print)ob;
			for(int j=0;j<p.str.length;j++)
				System.out.print(p.str[j]+"\t");
			System.out.println("");
		}
	}

	int search(Table tb,int key)
	{
		if(tb.root==null)
			return -1;
		Node leaf=getLeafNode(tb,key);
		//if(key==14)
		//	System.out.println("l-2"+leaf.keys[1]);
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
		//System.out.println("key:"+key);

		for(i=0;i<node.n;i++)
			if(node.keys[i]==-1 || key<node.keys[i])
				break;
		//System.out.println("Node first:"+tb.root.keys[0]+"\tK2:"+tb.root.keys[1]+"\tK3:"+tb.root.keys[2]);	
		//System.out.println("n:"+node.n+"   i:"+i+"\tkey:"+key);	
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
				//System.out.println("parent:"+node.parent+"\tkey:"+key);
				if(node.parent!=null)
					insertIntoParent(node.parent,node.keys[2],p,tb);
				else
					tb.root=insertIntoNewRoot(node.keys[2],node,p);	

				//System.out.println("p:"+p.parent);				
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
		//System.out.println("key---:"+key);
		Node leaf;
		if(tb.root!=null)
			leaf=getLeafNode(tb,key);
		else
		{
			leaf=new Node(c);
			tb.root=leaf;
		}
		//System.out.println("Node first:"+tb.root.keys[0]+"\tK2:"+tb.root.keys[1]+"\tK3:"+tb.root.keys[2]);
		for(i=0;i<leaf.n;i++)
			if(leaf.keys[i]==-1 || key<leaf.keys[i])
				break;
		//System.out.println("n:"+tb.root.n+"   i:"+i+"\tkey:"+key);	
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
			//if(leaf.parent!=null)
			//	System.out.println("leaf's parent:"+leaf.parent.keys[0]+"--"+leaf.parent.keys[1]);
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
			//System.out.println("Should COME:");
			int i=0;
			for(i=0;i<node.n;i++)
				if(node.keys[i]==-1 || key<node.keys[i])
					break;

			oce=helperDelete(node,node.child[i],key,oce,tb);
			//System.out.println("oce:"+oce);
			if(oce==null)
				return oce;
			else
			{
				int j=0;
				//System.out.println("node:"+node.keys[0]);
				for(j=0;j<=node.n;j++)
					if(node.child[j]==oce)
						break;
				//System.out.println("j:"+j);	
				for(int k=j;k<node.n;k++)
				{
					node.child[k]=node.child[k+1];
					node.keys[k-1]=node.keys[k];
				}
				node.child[node.n]=null;
				node.keys[node.n-1]=-1;
				//System.out.println("Reached:");
				/*if(node.isLeaf==false && node.child[j]!=null)
				{
					System.out.println("WRONG>>>>:"+node.keys[j-1]+"==="+key);
					node.keys[j-1]=node.child[j].keys[0];	
				}*/
				node.n--;
				//System.out.println("n:"+node.n+"\tn.p:"+node.parent+"\tch1:"+node.child[0]);
				if(node.n==0 && node.parent==null && node.child[0]!=null)
				{
					tb.root=node.child[0];
					tb.root.parent=null;
				}
				//System.out.println("parent:"+parent);
				//System.out.println("lc:"+node.child[node.n]+"\tlk:"+node.keys[node.n-1]);
				if(node.n>=1)
				{
					oce=null;
					return oce;
				}
				else if(parent!=null)
				{
					//System.out.println("parent:"+parent+"\tnode:"+node);
					int ch=getChild(parent,node);
					//System.out.println("ch:"+ch);
					//if(parent.child[ch+1]!=null)
					//	System.out.println("\tc:"+parent.child[ch+1].n+"\tpr:"+parent.child[ch+1].parent);
					if(ch>=0 && node.n<1 && parent.child[ch+1]!=null && parent.child[ch+1].n>2 && parent.child[ch+1].parent==node.parent)
					{
						//System.out.println("EE_1");
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
						//parent.keys[ch]=getMinKey(parent.child[ch+1]);
						oce=null;
						return oce;
					}
					else if(ch>0 && node.n<1 && parent.child[ch-1]!=null && parent.child[ch-1].n>2 && parent.child[ch-1].parent==node.parent)
					{
						Node leftC=parent.child[ch-1];
						//System.out.println("EE_2");
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
						//parent.keys[ch-1]=getMinKey(node.child[0]);
						//node.child[0].parent=node;
						oce=null;
						//System.out.println("LC:"+leftC.child[0]+"--"+leftC.child[1]+"\tn:"+leftC.n);
						return oce;
					}
					else if(ch>=0 && node.n<1 && parent.child[ch+1]!=null && parent.child[ch+1].n<3 && parent.child[ch+1].parent==node.parent)
					{
						//System.out.println("Exception here;");
						//printInternalElements(tb);
						//System.out.println("EE_3");
						node.keys[0]=parent.keys[ch];
						node.keys[1]=parent.child[ch+1].keys[0];
						node.keys[2]=parent.child[ch+1].keys[1];
						//System.out.println("EE_3-2");
						for(j=1;j<=3;j++)
						{
							node.child[j]=parent.child[ch+1].child[j-1];
							if(node.child[j]!=null)
								node.child[j].parent=node;
						}
						//System.out.println("EE_3-2");
						if(node.child[3]==null)
							node.n=node.n+2;
						else
							node.n=node.n+3;

						//System.out.println("\nTEST:");
						//printInternalElements(tb);
						oce=parent.child[ch+1];
						return oce;
					}
					else if(ch>0 && node.n<1 && parent.child[ch-1]!=null && parent.child[ch-1].n<3 && parent.child[ch-1].parent==node.parent)
					{
						//System.out.println("EE_4");
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
			//Node leaf=getLeafNode(tb,key);
			//System.out.println("third time");
			int index=search(tb,key);
			//System.out.println("index:"+index+"  N:"+node.n);
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
				//System.out.println("Key:"+key+"  N:"+leaf.n);
				//System.out.println("l.l:"+node.left+"\tn:"+node.left.n+"\tn.r:"+node.right+"\tparents:"+node.parent+"-"+node.left.parent);
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
					//System.out.println("should not come here");
					if(node.n<2 && node.right!=null && node.right.n==2 && node.right.parent==node.parent)
					{
						for(int j=0;j<2;j++)
						{
							node.keys[j+1]=node.right.keys[j];
							node.records[j+1]=node.right.records[j];
							node.n++;
						}	
						oce=node.right;
						//System.out.println("ce:"+oce);
						if(node.right.right!=null)
							node.right.right.left=node;
						node.right=node.right.right;
						//System.out.println("node-right:"+node.right);
						return oce;
					}
					else if(node.n<2 && node.left!=null && node.left.n==2 && node.left.parent==node.parent)
					{
						//System.out.println("LM:"+node.keys[0]);
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
		//System.out.println("GOINH out  oce:"+oce);
		return oce;
	}

	boolean deleteFromBPlus(Table tb,int key)
	{
		//System.out.println("key:"+key);
		int index=-1;
		Node x;
		if(tb.root.n==0)
			tb.root=null;
		else
			index=search(tb,key);
		//System.out.println("key:"+key);
		/*if(key==29)
		{
			System.out.println("INDEX:"+index);
			printInternalElements(tb);
			//System.out.println("29th element:"+tb.root.child[1].child[1].child[2].keys[0]);
		}*/
		if(index>=0)
		{
			x=helperDelete(null,tb.root,key,null,tb);
			//System.out.println("GOT OUT");
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

	Vector fecthAtbGreaterThan(Table tb,int atbNo[],String cond,boolean e)
	{
		/*int atbNo[];
		boolean change=false;
		for(int i=0;i<iatbNo.length;i++)
			if(iatbNo[i]==0)
			{
				change=true;
				int t=iatbNo[0];
				iatbNo[0]=iatbNo[i];
				iatbNo[i]=t;
			}

		if(change)
			atbNo=iatbNo;
		else
		{
			atbNo=new int[iatbNo.length+1];
			atbNo[0]=0;
			for(int j=0;j<iatbNo.length;j++)
				atbNo[j+1]=iatbNo[j];
		}
		String atbSet[]=new String[atbNo.length];
		for(int i=0;i<atbNo.length;i++)
			atbSet[i]=tb.attrib[atbNo[i]];*/

		Vector vec=new Vector();	
		String cAtb[]=cond.split(">");
		int value,c=atbNo.length;
		//Table output=new Table(0,atbNo.length,"FAGT",atbSet);
		if(e)
			value=Integer.parseInt(cAtb[1])-1;
		else
			value=Integer.parseInt(cAtb[1]);
		for(int j=0;j<tb.attrib.length;j++)
			if(cAtb[0].equals(tb.attrib[j]))
			{
				Node p;
				if(j==0)
				{
					p=getLeafNode(tb,value);
					while(p!=null)
					{
						if(p.keys[0]>value && p.keys[0]!=-1)
						{
							Print rec=new Print(c);
							for(int i=0;i<c;i++)
								rec.str[i]=p.records[0][atbNo[i]];
							vec.add(rec);
						}
						if(p.keys[1]>value && p.keys[1]!=-1)
						{
							Print rec=new Print(c);
							for(int i=0;i<c;i++)
								rec.str[i]=p.records[1][atbNo[i]];
							vec.add(rec);
						}
						if(p.keys[2]>value && p.keys[2]!=-1)
						{
							Print rec=new Print(c);
							for(int i=0;i<c;i++)
								rec.str[i]=p.records[2][atbNo[i]];
							vec.add(rec);
						}

						p=p.right;
					}
				}
				else
				{
					p=getMinLeaf(tb);
					while(p!=null)
					{
						if(p.keys[0]!=-1 && Integer.parseInt(p.records[0][j])>value)
						{
							Print rec=new Print(c);
							for(int i=0;i<c;i++)
								rec.str[i]=p.records[0][atbNo[i]];
							vec.add(rec);
						}
						if(p.keys[1]!=-1 && Integer.parseInt(p.records[1][j])>value)
						{
							Print rec=new Print(c);
							for(int i=0;i<c;i++)
								rec.str[i]=p.records[1][atbNo[i]];
							vec.add(rec);
						}
						if(p.keys[2]!=-1 && Integer.parseInt(p.records[2][j])>value)
						{
							Print rec=new Print(c);
							for(int i=0;i<c;i++)
								rec.str[i]=p.records[2][atbNo[i]];
							vec.add(rec);
						}

						p=p.right;
					}

				}
			}

		return vec;	
	}

	Vector fetchAtbLessThan(Table tb,int atbNo[],String cond,boolean e)
	{
		String cAtb[]=cond.split("<");
		int value,c=atbNo.length;
		//Table output=new Table(0,tb.attrib.length,"FALT",tb.attrib);
		Vector vec=new Vector();

		if(e)
			value=Integer.parseInt(cAtb[1])+1;
		else
			value=Integer.parseInt(cAtb[1]);
		for(int j=0;j<tb.attrib.length;j++)
			if(cAtb[0].equals(tb.attrib[j]))
			{
				Node p;
				if(j==0)
				{
					p=getLeafNode(tb,value);
					while(p!=null)
					{
						if(p.keys[0]<value && p.keys[0]!=-1)
						{
							Print rec=new Print(c);
							for(int i=0;i<c;i++)
								rec.str[i]=p.records[0][atbNo[i]];
							vec.add(rec);
						}
						if(p.keys[1]<value && p.keys[1]!=-1)
						{
							Print rec=new Print(c);
							for(int i=0;i<c;i++)
								rec.str[i]=p.records[1][atbNo[i]];
							vec.add(rec);
						}
						if(p.keys[2]<value && p.keys[2]!=-1)
						{
							Print rec=new Print(c);
							for(int i=0;i<c;i++)
								rec.str[i]=p.records[2][atbNo[i]];
							vec.add(rec);
						}

						p=p.right;
					}
				}
				else
				{
					p=getMinLeaf(tb);
					while(p!=null)
					{
						if(p.keys[0]!=-1 && Integer.parseInt(p.records[0][j])<value)
						{
							Print rec=new Print(c);
							for(int i=0;i<c;i++)
								rec.str[i]=p.records[0][atbNo[i]];
							vec.add(rec);
						}
						if(p.keys[1]!=-1 && Integer.parseInt(p.records[1][j])<value)
						{
							Print rec=new Print(c);
							for(int i=0;i<c;i++)
								rec.str[i]=p.records[1][atbNo[i]];
							vec.add(rec);
						}
						if(p.keys[2]!=-1 && Integer.parseInt(p.records[2][j])<value)
						{
							Print rec=new Print(c);
							for(int i=0;i<c;i++)
								rec.str[i]=p.records[2][atbNo[i]];
							vec.add(rec);
						}

						p=p.right;
					}
				}
			}

		return vec;	
	}

	Vector fetchAtbEquals(Table tb,int atbNo[],String cond)
	{
		String cAtb[]=cond.split("=");
		int value,c=atbNo.length;
		//Table output=new Table(0,tb.attrib.length,"FAEQ",tb.attrib);
		Vector vec=new Vector();

		for(int j=0;j<tb.attrib.length;j++)
			if(cAtb[0].equals(tb.attrib[j]))
			{
				Node p;
				if(j==0)
				{
					value=Integer.parseInt(cAtb[1]);
					p=getLeafNode(tb,value);
					if(p!=null)
					{
						if(p.keys[0]==value)
						{
							Print rec=new Print(c);
							for(int i=0;i<c;i++)
								rec.str[i]=p.records[0][atbNo[i]];
							vec.add(rec);
						}
						else if(p.keys[1]==value)
						{
							Print rec=new Print(c);
							for(int i=0;i<c;i++)
								rec.str[i]=p.records[1][atbNo[i]];
							vec.add(rec);
						}
						else if(p.keys[2]==value)
						{
							Print rec=new Print(c);
							for(int i=0;i<c;i++)
								rec.str[i]=p.records[2][atbNo[i]];
							vec.add(rec);
						}

					}
				}
				else
				{
					p=getMinLeaf(tb);
					while(p!=null)
					{
						if(p.keys[0]!=-1 && cAtb[1].equals(p.records[0][j]))
						{
							Print rec=new Print(c);
							for(int i=0;i<c;i++)
								rec.str[i]=p.records[0][atbNo[i]];
							vec.add(rec);
						}
						else if(p.keys[1]!=-1 && cAtb[1].equals(p.records[1][j]))
						{
							Print rec=new Print(c);
							for(int i=0;i<c;i++)
								rec.str[i]=p.records[1][atbNo[i]];
							vec.add(rec);
						}
						else if(p.keys[2]!=-1 && cAtb[1].equals(p.records[2][j]))
						{
							Print rec=new Print(c);
							for(int i=0;i<c;i++)
								rec.str[i]=p.records[2][atbNo[i]];
							vec.add(rec);
						}

						p=p.right;
					}
				}
			}
		return vec;	
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

	Vector performOperation(Vector vect1,Vector vect2,int op,int atbNo1[])
	{
		System.out.println("op:"+op);
		int len=atbNo1.length;
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
					System.out.println(i+"--"+j);
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

	public static void main(String args[])
	{
		int r=0,c=0,not=0;
		String line,tName="";
		BPlusTree bpt=new BPlusTree();
		try
		{
			BufferedReader br=new BufferedReader(new FileReader("sample1.txt"));			
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
						//System.out.println("key:="+key);
						if(bpt.search(tb[i],key)<0)
							bpt.insertIntoBPlus(tb[i],temp,c);
						else
							System.out.println("PRIMARY KEY CONSTRAINT VIOLATED.");
						//bpt.printInternalElements(tb[0]);
					}
				}

				if((line=br.readLine())!=null)
					System.out.print("");
			}


			System.out.println("\n");
			br=new BufferedReader(new FileReader("input2.txt"));

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

						String setAtb[]=temp[2].split(",");
						String tbAtb[][]=new String[setAtb.length][2];
						for(int i=0;i<setAtb.length;i++)
						{
							String tmp[]=setAtb[i].split(".");
							tbAtb[i]=tmp;
						}
						int atbNo[]=new int[setAtb.length];
						String setTb[]=temp[3].split(",");
						String setCond[]=temp[4].split(",");
						for(int i=0;i<tb.length;i++)
							if(tb[i].name.equals(temp[3]))
							{
								int j,k;
								for(j=0;j<setAtb.length;j++)
									for(k=0;k<tb[i].attrib.length;k++)
										if(setAtb[j].equals(tb[i].attrib[k]))
											atbNo[j]=k;

								if(temp.length<5)
								{
									Node p=bpt.getMinLeaf(tb[i]);
									while(p!=null)
									{
										for(int m=0;m<3;m++)
											if(p.keys[m]>=0)
											{
												for(j=0;j<setAtb.length;j++)
													System.out.print(setAtb[j]+":"+p.records[m][atbNo[j]]+"\t");
												System.out.println("");
											}
										p=p.right;	
									}
								}
								else if(temp.length==5)
								{
									Vector output=new Vector();
									if(temp[4].indexOf('>')>0 && temp[4].indexOf('=')<0)
										output=bpt.fecthAtbGreaterThan(tb[i],atbNo,temp[4],false);
									else if(temp[4].indexOf('<')>0 && temp[4].indexOf('=')<0)
										output=bpt.fetchAtbLessThan(tb[i],atbNo,temp[4],false);
									else if(temp[4].indexOf('=')>0 && temp[4].indexOf('<')<0 && temp[4].indexOf('>')<0)
										output=bpt.fetchAtbEquals(tb[i],atbNo,temp[4]);
									else if(temp[4].indexOf('=')>0 && temp[4].indexOf('<')>0)
										output=bpt.fetchAtbLessThan(tb[i],atbNo,temp[4],true);
									else if(temp[4].indexOf('>')>0 && temp[4].indexOf('=')>0)
										output=bpt.fecthAtbGreaterThan(tb[i],atbNo,temp[4],true);
										
									System.out.println("size-1:"+output.size());
									bpt.printVectorElements(output);
								}		
							}
					}
					/*else if(temp.length>5)
					{
						int tno1,tno2;
						flag=true;
						String setAtb1[]=temp[2].split(",");
						String setAtb2[]=temp[2].split(",");
						int atbNo1[]=new int[setAtb1.length];
						int atbNo2[]=new int[setAtb1.length];
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

						if(temp[5].equals("union"))
							op=1;
						else if(temp[5].equals("intersection"))
							op=2;
						else if(temp[5].equals("difference"))
							op=3;
						if(flag)
						{	
						Vector output1=new Vector();	
						Vector output2=new Vector();							
						for(tno1=0;tno1<tb.length;tno1++)
							if(temp[3].equals(tb[tno1].name))
							{
								int j,k;
								for(j=0;j<setAtb1.length;j++)
									for(k=0;k<tb[tno1].attrib.length;k++)
										if(setAtb1[j].equals(tb[tno1].attrib[k]))
											atbNo1[j]=k;
								
								if(temp[4].indexOf('>')>0 && temp[4].indexOf('=')<0)
									output1=bpt.fecthAtbGreaterThan(tb[tno1],atbNo1,temp[4],false);
								else if(temp[4].indexOf('<')>0 && temp[4].indexOf('=')<0)
									output1=bpt.fetchAtbLessThan(tb[tno1],atbNo1,temp[4],false);
								else if(temp[4].indexOf('=')>0 && temp[4].indexOf('<')<0 && temp[4].indexOf('>')<0)
									output1=bpt.fetchAtbEquals(tb[tno1],atbNo1,temp[4]);
								else if(temp[4].indexOf('=')>0 && temp[4].indexOf('<')>0)
									output1=bpt.fetchAtbLessThan(tb[tno1],atbNo1,temp[4],true);
								else if(temp[4].indexOf('>')>0 && temp[4].indexOf('=')>0)
									output1=bpt.fecthAtbGreaterThan(tb[tno1],atbNo1,temp[4],true);
							}
							
						for(tno2=0;tno2<tb.length;tno2++)
							if(temp[8].equals(tb[tno2].name))
							{	
								int j,k;
								for(j=0;j<setAtb1.length;j++)
									for(k=0;k<tb[tno2].attrib.length;k++)
										if(setAtb1[j].equals(tb[tno2].attrib[k]))
											atbNo2[j]=k;

								if(temp[9].indexOf('>')>0 && temp[9].indexOf('=')<0)
									output2=bpt.fecthAtbGreaterThan(tb[tno2],atbNo2,temp[9],false);
								else if(temp[9].indexOf('<')>0 && temp[9].indexOf('=')<0)
									output2=bpt.fetchAtbLessThan(tb[tno2],atbNo2,temp[9],false);
								else if(temp[9].indexOf('=')>0 && temp[9].indexOf('<')<0 && temp[9].indexOf('>')<0)
									output2=bpt.fetchAtbEquals(tb[tno2],atbNo2,temp[9]);
								else if(temp[9].indexOf('=')>0 && temp[9].indexOf('<')>0)
									output2=bpt.fetchAtbLessThan(tb[tno2],atbNo2,temp[9],true);
								else if(temp[9].indexOf('>')>0 && temp[9].indexOf('=')>0)
									output2=bpt.fecthAtbGreaterThan(tb[tno2],atbNo2,temp[9],true);

							}

							Vector fOutput=new Vector();
							fOutput=bpt.performOperation(output1,output2,op,atbNo1);
							//System.out.println("size-v1::"+output1.size()+"\tsize-v2:"+output2.size()+"\tOP:"+fOutput.size());
							//bpt.printVectorElements(output1);
							System.out.println("\n");
							//bpt.printVectorElements(output2);
							System.out.println("\n");
							bpt.printVectorElements(fOutput);

						}
						else
							System.out.println("Attribute Sets Not Matching !");	
					}*/




				}
			//System.out.println("Marks:"+tb[0].root.child[1].child[0].child[2].records[1][2]);
				
			}

			bpt.printInternalElements(tb[2]);
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}

}

