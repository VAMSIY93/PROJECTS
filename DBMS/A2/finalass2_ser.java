import java.io.*; 
import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.Math.*;

class Table implements java.io.Serializable	
{
	private static final long serialVersionUID = 1381717712208555866L;
	String tableName;
	//String ref[][];		philhal use nai kiya
	String attr_name[];
	int rows,cols;
	//String pkattr;
	//int pkattrcol;
	//Boolean isInvalid=false;
	//Boolean hasPK=false;			comment kiye hue sab jarrorat peh use karlenge
	Node root;
	Table(String name , int attr)
	{
		this.tableName = name;
		this.cols = attr;
		this.attr_name = new String[attr];
		this.root = new LeafNode(attr);
	}


	Table(String name)
	{
		this.tableName = name;
	}


	public String[] select(int key)
	{
		LeafNode leaf = findLeafNodeOfKey(key);
		int index = leaf.search(key);
		if(index==-1)
		{
			System.out.println("Key Not Found");
			return null;
		}
		return leaf.getRecord(index);

	}

	public int getColIndex(String colName)
	{
		for(int i=0;i<this.attr_name.length;i++)
		{
			if(this.attr_name[i].compareTo(colName)==0)
				return i;
		}
		return -1;
	}

	/*public String[][] selectAll(String[] col_names)
	{
		int col_ind[col_names.length];
		//Assigning Column Indices of selected Columns
		for(int i=0;i<col_names.length;i++)
		{
			col_ind[i]=this.getColIndex(col_names[i]);
		}
		String result[this.rows][col_names.length];
		LeafNode leaf = traverseLeft(this.root);
		int ind=0;
		while(leaf.rightSibling!=null)
		{
			for(int i=0;i<leaf.keyCount;i++)
			{
				for(int j=0;j<col_names.length.j++)
				{

				}
			}
		}
	}*/

	/*public String[][] selectAll()
	{
		String result[][] = new String[this.rows][this.attr_name.length];
		LeafNode leaf = traverseLeft(this.root);
		int ind=0;
		while(leaf!=null)
		{
			for(int i=0;i<leaf.keyCount;i++)
			{
				result[ind] = leaf.getRecord(i);
				ind++;
			}
			if(leaf.rightSibling!=null)
				leaf=(LeafNode)leaf.rightSibling;
		}
		return result;
	}*/

	// overloaded method for testing efficiency
	/*public String[][] selectAll(String[] col_names)
	{
		String result[][] = new String[this.rows][this.attr_name.length];
		LeafNode leaf = traverseLeft(this.root);
		int ind=0;
		int col_ind[] = new int[col_names.length];
		for(int i=0 ; i<col_ind.length ; i++)
			col_ind[i] = getColIndex(col_names[i]);
		while(leaf!=null)
		{
			for(int i=0;i<leaf.keyCount;i++)
			{
				for(int j=0;j<col_ind.length;j++)
					result[ind][j] = (leaf.getRecord(i))[j];
				ind++;
			}
			if(leaf.rightSibling!=null)
				leaf=(LeafNode)leaf.rightSibling;
			else 
				break;
		}
		return result;
	}*/

	public LeafNode traverseLeft(Node root)
	{
		Node node = root;
		while(node.getType()==0)
			node=node.getChild(0);
		return (LeafNode)node;
	}

	public void insertIntoTable(String tabName,String cols,String values)
	{
		String col_names[] = cols.split(",");
		String col_values[] = values.split(",");
		int no_of_cols = col_values.length;
		try
		{
			StringBuilder strBuilder = new StringBuilder();
			int pk = Integer.parseInt(col_values[0]);
			LeafNode leaf =findLeafNodeOfKey(pk);			// Leaf node that should contain key
			if(leaf.search(pk) != -1 && leaf.getIndex(pk)<=leaf.getKeyCount())
			{
				System.out.println("Key is already present. Press Y|y to Continue however. Otherwise press N|n ");
				Scanner sc = new Scanner(System.in);
				String ip = sc.next();
				if((ip.compareTo("N")==0) || (ip.compareTo("n")==0))	
					return;
			}
			for(int i=0,j=0;i<this.attr_name.length;i++)
			{
				if(j<no_of_cols && (this.attr_name[i].compareTo(col_names[j])==0))
				{
					strBuilder.append(col_values[j]);
					j++;

				}
				if(j<no_of_cols && getColIndex(col_names[j])==-1)
				{
					System.out.println("Not Satisfying the Column Names");
					return;
				}
				if(i != this.attr_name.length -1)
				{
					strBuilder.append(",");
				}
			}
			this.insertRecord(strBuilder.toString());
			//System.out.println("Record has been Successfully Inserted");
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}


	/*public void deleteFromTable(String tabName, String cond)
	{
		//String cond_spec[] = cond.split(",");			// for the case of multiple
		//handle for multiple if thought a=by looking in notes
		String cond_col="",cond_sym="",cond_val="";
		String cond_temp[] = new String[2];
		// don't forget here to take <= & >= before < & >
		if(cond.contains("<="))
		{
			cond_temp = cond.split("<=");
			cond_col = cond_temp[0];
			cond_sym = "<";
			cond_val = cond_temp[1];
			System.out.println(cond_val);
			/*if(this.getColIndex(cond_col)==0)
			{
				deleteOnPK(cond_col,cond_sym,cond_val);
			}
			else
			{
				deleteWithoutPK(cond_col,"=",cond_val);
			}*/
		/*}
		else if(cond.contains("<"))
		{
			cond_temp = cond.split("<");
			cond_col = cond_temp[0];
			cond_sym = "<";
			cond_val = cond_temp[1];
		}
		else if(cond.contains(">"))
		{
			cond_temp = cond.split(">");
			cond_col = cond_temp[0];
			cond_sym = ">";
			cond_val = cond_temp[1];
		}
		else if(cond.contains("="))
		{
			cond_temp = cond.split("=");
			cond_col = cond_temp[0];
			cond_sym = "=";
			cond_val = cond_temp[1];
		}
		if(this.getColIndex(cond_col)==0)
		{
			deleteOnPK(cond_col,cond_sym,cond_val);
		}
		else
		{
			deleteWithoutPK(cond_col,cond_sym,cond_val);
		}
	}

	public void deleteOnPK(String cColName,String cSym,String condVal)
	{
		int cVal = Integer.parseInt(condVal);
		LeafNode leaf = findLeafNodeOfKey(cVal);
		if(cSym.contains("<"))
		{
			int index = leaf.getIndex(cVal);
		}
	}

	public void deleteWithoutPK(String cColName,String cSym,String condVal)
	{

		LeafNode leaf = traverseLeft(this.root);
		int colInd = this.getColIndex(cColName);
		Boolean moveBack=false;
		while(leaf != null)
		{
			for(int i=0 ; i<leaf.keyCount ; )
			{
				if(cSym.compareTo("<")==0)
				{
					int cVal = Integer.parseInt(condVal);
					if(Integer.parseInt(leaf.record[i][colInd]) < cVal)
					{
						//this.print_hier();
						
						System.out.println("Deleting : "+leaf.keys[i]+" i,keyCount : " +i+","+leaf.keyCount );
						this.delete(leaf.keys[i]);
						if(leaf.keyCount<(Math.ceil((float)(leaf.no_of_children-1)/2)) && leaf.rightSibling!=null && leaf.rightSibling.leftSibling!=leaf)
						{
							moveBack=true;
							break;
						}
						//this.printTable();
						//this.print_hier();
						
						
					}
					else
						i++;

				}
				else if(cSym.compareTo(">")==0)
				{
					int cVal = Integer.parseInt(condVal);
					if(Integer.parseInt(leaf.record[i][colInd]) > cVal)
					{
						//this.print_hier();
						System.out.println("Deleting : " + leaf.keys[i]+ " i,keyCount : "+i+";"+leaf.keyCount);
						this.delete(leaf.keys[i]);
						if(leaf.keyCount<(Math.ceil((float)(leaf.no_of_children-1)/2)) && leaf.rightSibling!=null && leaf.rightSibling.leftSibling!=leaf)
						{
							moveBack=true;
							break;
						}
						this.printTable();
						this.print_hier();
					}
					else 
						i++;
				}
				if(cSym.compareTo("=")==0)
				{
					//int cVal = Integer.parseInt(condVal);
					condVal=condVal.replace("'","");
					//System.out.println(condVal.replace("'","").length() +"---"+leaf.record[i][colInd].length());
					if(leaf.record[i][colInd].compareTo(condVal)==0)
					{
						//this.print_hier();
						System.out.println("Deleting : " + leaf.keys[i]+ " i,keyCount : "+i+";"+leaf.keyCount);
						this.delete(leaf.keys[i]);
						if(leaf.keyCount<(Math.ceil((float)(leaf.no_of_children-1)/2)) && leaf.rightSibling!=null && leaf.rightSibling.leftSibling!=leaf)
						{
							moveBack=true;
							break;
						}
						this.printTable();
						this.print_hier();
					}
					else 
						i++;
				}
			}
			System.out.println("Traersing next node");
			if(moveBack)
			{
				moveBack=false;
				leaf = (LeafNode) leaf.leftSibling;
			}
			else
				leaf =(LeafNode) leaf.rightSibling;
		}
	}*/


	public void deleteFromTable(String tabName,String cond)
	{
		List<String[]> condList = new ArrayList<String[]>();
		condList = breakDownCond(cond.split(","));
		int n_cond = condList.size(),i,key=0,count=0;
		String sym = "";
		boolean hasPKInCond = false,canDelete=false;
		LeafNode leaf;
		/*calculating the col INdices of cond cols*/
		int condColInd[] = new int[n_cond];
		for(i=0;i<n_cond;i++)
		{
			condColInd[i] = this.getColIndex(condList.get(i)[0]);
			if(condColInd[i]==0)
			{
				hasPKInCond = true;
				key = Integer.parseInt(condList.get(i)[2]);
				sym = condList.get(i)[1];
			}
		}
		if(hasPKInCond)
			leaf = findLeafNodeOfKey(key);
		else
			leaf = traverseLeft(this.root);
		while(leaf != null)
		{
			for(i=0;i<leaf.keyCount;i++)
			{
				canDelete = satisfyCond(leaf.record[i],condList,condColInd);
				if(canDelete)
				{
					this.delete(Integer.parseInt(leaf.record[i][0]));
					count++;
				}
			}
			if(!hasPKInCond)
				leaf = (LeafNode) leaf.rightSibling;
			else if(sym.equals("<") || sym.equals("<="))
				leaf = (LeafNode) leaf.leftSibling;
			else if(sym.equals(">") || sym.equals(">="))
				leaf = (LeafNode) leaf.rightSibling;
			else
				break;
		}
		System.out.println(count + " Records Deleted Succesffully");
	}

	/*public void updateTable(String where, String update)
	{
		String temp[] = where.split("=");
		String where_col = temp[0];
		String where_val = temp[1].replace("'","");
		String temp_up[] = update.split(",");
		String upd_col[] = new String[temp_up.length];
		String upd_val[] = new String[temp_up.length];
		int count=0;
		for(int i = 0 ; i<temp_up.length; i++)
		{
			temp = temp_up[i].split("=");
			upd_col[i] = temp[0];
			upd_val[i] = temp[1].replace("'","");
		}
		int upd_ind[] = new int[temp_up.length];
		for(int i=0 ; i<temp_up.length;i++)
		{
			upd_ind[i]= this.getColIndex(upd_col[i]);
		}
		int whereInd = this.getColIndex(where_col);
		if(whereInd==0)
		{
			int value = Integer.parseInt(where_val);
			LeafNode leaf = findLeafNodeOfKey(value);
			int index = leaf.getIndex(value);
			for(int i=0 ; i< upd_col.length ; i++)
			{
				leaf.record[index][upd_ind[i]] = upd_val[i].replace("'","");
			}
		}
		else
		{
			LeafNode leaf = traverseLeft(this.root);
			int j=0;
			while(leaf != null)
			{
				for(int i=0 ; i<leaf.keyCount ; i++)
				{
					if(leaf.record[i][whereInd].compareTo(where_val)==0)
					{
						for(j=0 ; j<upd_col.length ; j++)
						{
							leaf.record[i][upd_ind[j]] = upd_val[j].replace("'","");
						}
						count++;
					}
					else 
						i++;
				}
				leaf = (LeafNode)leaf.rightSibling;
			}
		}
		System.out.println("Updated " + count+" Records Succesffully");
	}*/


	public void updateTable(String where, String update)
	{
		String where_condList[] = where.split(",");
		String update_valList[] = update.split(",");
		int n_cond = where_condList.length,i,j,key=0,count=0;
		boolean canUpdate = false,hasPKInCond=false;
		String sym="";
		List<String[]> where_cond = new ArrayList<String[]>();
		List<String[]> update_val = new ArrayList<String[]>();
		where_cond = breakDownCond(where_condList);
		update_val = breakDownCond(update_valList);
		int condColInd[] = new int[n_cond];
		LeafNode leaf ;
		/*calculating colIndices of cond cols*/
		for(i=0;i<n_cond;i++)
		{
			condColInd[i] = this.getColIndex(where_cond.get(i)[0]);
			if(condColInd[i] == 0)
			{
				hasPKInCond = true;
				key = Integer.parseInt(where_cond.get(i)[2]);
				sym = where_cond.get(i)[1];
			}
		}
		/*calculating update col indices*/
		int n_update_col = update_val.size();
		int updateColInd[] = new int[n_update_col];
		for(i=0;i<n_update_col;i++)
		{
			updateColInd[i] = this.getColIndex(update_val.get(i)[0]);
		}
		if(hasPKInCond)
			leaf = findLeafNodeOfKey(key);
		else
			leaf = traverseLeft(this.root);
		while(leaf != null)
		{
			for(i=0;i<leaf.keyCount;i++)
			{
				canUpdate = satisfyCond(leaf.record[i],where_cond,condColInd);
				if(canUpdate)
				{
					for(j=0;j<n_update_col;j++)
						leaf.record[i][updateColInd[j]] = update_val.get(j)[2].replace("'","");
					count++;
				}
			}
			if(!hasPKInCond)
				leaf = (LeafNode)leaf.rightSibling;
			else if(sym.equals("<")||sym.equals("<="))
				leaf = (LeafNode)leaf.leftSibling;
			else if(sym.equals(">")||sym.equals(">="))
				leaf = (LeafNode)leaf.rightSibling;
			else
				break;
		}
		System.out.println("Updated "+count+" Record(s) Successfully");
	}

	public List<String[]> breakDownCond(String[] condList)
	{
		int n_cond = condList.length;
		List<String[]> cond = new ArrayList<String[]>();
		int i=0;
		String temp[] = new String[2];
		String temp_help[] = new String[3];
		for(i=0;i<n_cond;i++)
		{
			if(condList[i].contains("<="))
			{
				temp = condList[i].split("<=");
				temp_help[0]=temp[0];
				temp_help[2]=temp[1];
				temp_help[1]="<=";
				cond.add(temp_help);
			}
			else if(condList[i].contains(">="))
			{
				temp = condList[i].split(">=");
				temp_help[0]=temp[0];
				temp_help[2]=temp[1];
				temp_help[1]=">=";
				cond.add(temp_help);
			}
			else if(condList[i].contains("<"))
			{
				temp = condList[i].split("<");
				temp_help[0]=temp[0];
				temp_help[2]=temp[1];
				temp_help[1]="<";
				cond.add(temp_help);
			}
			else if(condList[i].contains(">"))
			{
				temp = condList[i].split(">");
				temp_help[0]=temp[0];
				temp_help[2]=temp[1];
				temp_help[1]=">";
				cond.add(temp_help);
			}
			else if(condList[i].contains("="))
			{
				temp = condList[i].split("=");
				temp_help[0]=temp[0];
				temp_help[2]=temp[1];
				temp_help[1]="=";
				cond.add(temp_help);
			}
		}
		return cond;

	}

	public boolean satisfyCond(String[] record,List<String[]> cond,int[] condColInd)
	{
		int n_cond = cond.size();
		boolean canAddRec= true;
		int j=0;
		//LeafNode leaf 
		for(j=0; j<n_cond;j++)
		{

			if(cond.get(j)[1].equals("="))
			{
				if(record[condColInd[j]].equals(cond.get(j)[2].replace("'","")))
					canAddRec = canAddRec && true;
				else
					canAddRec = false;
			}
			else if(cond.get(j)[1].equals("<"))
			{
				if(Integer.parseInt(record[condColInd[j]])<Integer.parseInt(cond.get(j)[2]))
					canAddRec = canAddRec && true;
				else 
					canAddRec =false;
			}
			else if(cond.get(j)[1].equals(">"))
			{
				if(Integer.parseInt(record[condColInd[j]])>Integer.parseInt(cond.get(j)[2]))
					canAddRec = canAddRec && true;
				else
					canAddRec = false;
			}
			else if(cond.get(j)[1].equals("<="))
			{
				if(Integer.parseInt(record[condColInd[j]])<=Integer.parseInt(cond.get(j)[2]))
					canAddRec = canAddRec && true;
				else
					canAddRec = false;
			}
			else if(cond.get(j)[1].equals(">="))
			{
				if(Integer.parseInt(record[condColInd[j]])>=Integer.parseInt(cond.get(j)[2]))
					canAddRec = canAddRec && true;
				else
					canAddRec = false;
			}
		}
		return canAddRec;


	}

	

	public ResultSet selectAll(String[] colList,String[] condList)
	{
		ResultSet rs = new ResultSet();
		boolean hasCond = false;
		int i=0;
		if(condList.length == 0)
		{
			hasCond = false;
		}
		else
		{
			hasCond = true;
		}
		if(colList.length==1 && (colList[0].compareTo("*")==0))
			rs.attr = Arrays.asList(this.attr_name);
		else
			rs.attr = Arrays.asList(colList);
		if(this.attr_name.length == rs.attr.size())
		{
			if(!hasCond)
			{
				LeafNode leaf = traverseLeft(this.root);
				while(leaf!=null)
				{
					for(i=0;i<leaf.getKeyCount();i++)
						rs.records.add(leaf.record[i]);
					leaf = (LeafNode)leaf.rightSibling;
				}
			}
			else 
			{
				int n_cond = condList.length;
				int j=0;
				boolean hasPKInCond =false,canAddRec=false;
				int key=0 ;
				String sym="";
				List<String[]> cond = new ArrayList<String[]>();
				LeafNode leaf;
				cond = breakDownCond(condList);
				int condColInd[] =new int[n_cond];
				for(i=0;i<n_cond;i++)
				{
					condColInd[i]= this.getColIndex(cond.get(i)[0]);
					if(condColInd[i]==0)
					{
						hasPKInCond = true;
						key = Integer.parseInt(cond.get(i)[2]);
						sym = cond.get(i)[1];
					}
				}
				if(hasPKInCond)
					leaf = findLeafNodeOfKey(key);
				else
					leaf = traverseLeft(this.root);
				while(leaf!=null)
				{
					for(i=0;i<leaf.keyCount;i++)
					{
						canAddRec = this.satisfyCond(leaf.record[i],cond,condColInd);
						if(canAddRec)
							rs.records.add(leaf.record[i]);
					}
					if(!hasPKInCond)
						leaf = (LeafNode) leaf.rightSibling;
					else if(sym.equals(">") || sym.equals(">="))	
						leaf = (LeafNode) leaf.rightSibling;
					else if(sym.equals("<") || sym.equals("<="))
						leaf = (LeafNode) leaf.leftSibling;
					else
						break;
				}
			}
		}
		else
		{
			int j=0;
			int colInd[] = new int[colList.length];
			for(i=0 ; i<colList.length;i++)
				colInd[i] = this.getColIndex(colList[i]);
			int noc = colInd.length;
			LeafNode leaf;
			String helper[][]; 
			if(!hasCond)
			{
				leaf = traverseLeft(this.root);
				helper = new String[this.rows][noc];
				int k=0;
				while(leaf!=null)
				{
					for(i=0;i<leaf.getKeyCount();i++)
					{
						for(j=0;j<noc;j++)
						{
							helper[k][j]=leaf.record[i][colInd[j]];
						}
						rs.records.add(helper[k]);
						k++;
					}
					leaf = (LeafNode)leaf.rightSibling;
				}
			}
			else
			{
				/*code for selected attributes along with cond*/
				int n_cond = condList.length;
				//int j=0;
				boolean hasPKInCond =false,canAddRec=false;
				int key=0 ;
				String sym="";
				List<String[]> cond = new ArrayList<String[]>();
				cond = breakDownCond(condList);
				int condColInd[] =new int[n_cond];
				for(i=0;i<n_cond;i++)
				{
					condColInd[i]= this.getColIndex(cond.get(i)[0]);
					if(condColInd[i]==0)
					{
						hasPKInCond = true;
						key = Integer.parseInt(cond.get(i)[2]);
						sym = cond.get(i)[1];
					}
				}
				if(hasPKInCond)
					leaf = this.findLeafNodeOfKey(key);
				else 
					leaf = this.traverseLeft(this.root);
				helper = new String[this.rows][noc];
				int k=0;
				while(leaf!=null)
				{
					for(i=0;i<leaf.getKeyCount()	;i++)
					{
						canAddRec = this.satisfyCond(leaf.record[i],cond,condColInd);
						if(canAddRec)
						{
							for(j=0;j<noc;j++)
								helper[k][j]=leaf.record[i][colInd[j]];
							rs.records.add(helper[k]);
							k++;
						}
					}
					if(!hasPKInCond)
						leaf = (LeafNode)leaf.rightSibling;
					else if(sym.equals(">") || sym.equals(">="))
						leaf = (LeafNode)leaf.leftSibling;
					else if(sym.equals("<") || sym.equals("<="))
						leaf = (LeafNode)leaf.leftSibling;
					else
						break;
				}
			}
		}
		return rs;
	}

	/*public ResultSet selectOnCond(String[] colList,String[] condList)
	{
		ResultSet rs = new ResultSet();
		int i=0;
		if(colList.length==1 && colList[0].compareTo("*"))
			rs.attr = Arrays.asList(this.attr_name);
		else
			rs.attr = Arrays.asList(colList);

	}*/

	public void insertRecord(String recVals)
	{
		//System.out.println("insertRecord ");
		String rec_temp[] = recVals.split(",");
		int newKey = Integer.parseInt(rec_temp[0]);
		LeafNode leaf =findLeafNodeOfKey(newKey);			// Leaf node that should contain key
		//System.out.println("Expect IN");
		int newInd = leaf.getIndex(newKey);					//def in node
		//System.out.println("Expect OUT");
		//System.out.println(recVals + "--" + leaf.keyCount );

		if(leaf.isNodeFull()) 
		{
			Node n = leaf.splitAndInsert(recVals);
			if(n!=null)
			{
				//System.out.println("Split occured new root formed");
				//System.out.println("key --> "+ n.keyCount);
				this.root = n;
			}
		}
		else
		{
			//System.out.println("Inside insertRecord	");
			leaf.insertAt(newInd,newKey,recVals);
		}
	}


	/**
	 * Delete a key and its associated value from the tree.(C)
	 */
	public void delete(int key) {
		LeafNode leaf = this.findLeafNodeOfKey(key);
		//System.out.println("Testing:" + (leaf.keyCount-1));
		if (leaf.delete(key) && leaf.isUnderflow()) {
			Node n = leaf.dealUnderflow();
			if (n != null)
				this.root = n; 
		}
	}

	public LeafNode findLeafNodeOfKey(int key)
	{
		Node node = this.root;
		while(node.getType()==0)		// 0 for internal nodes
		{
			//System.out.println("KeyCount on Leaf:" + node.keyCount);
			/*for(int i=0;i<node.keyCount;i++)
			{
				System.out.print(node.keys[i]+" --- ");
			}
			System.out.println("");*/
			node = node.getChild(node.getIndex(key));				//removed typecast to INTNOde
		}
		//System.out.println("KeyCount on Leaf:" + node.keyCount);
		int i=0;
		/*while(i<node.keyCount)
		{
			System.out.print(node.keys[i]+ "    ");
			i++;
		}*/
		//System.out.println("Hello");
		return (LeafNode)node;
	}

	public void printTable()
	{
		Node n = this.root;
		LeafNode leaf = traverseLeft(n);
		int ind=0;
		while(leaf!=null)
		{
			System.out.print("Node-"+ind+"	");
			for(int i=0;i<leaf.keyCount;i++)
			{
				System.out.print(leaf.keys[i]+"	");
			}
			System.out.println("");
			ind++;
			if(leaf.rightSibling!=null)
				leaf=(LeafNode)leaf.rightSibling;
			else 
				break;
		}
	}


	public int pathToLeaf(Node root)
	{
		int count=0;
		Node n = this.root;
		while(n.getType()==0)
		{
			count++;
			n = n.getChild(0);
		}
		return count+1;
	}

	public void print_hier()
	{
		Node node_root = this.root;
		int n = pathToLeaf(this.root);
		for(int i=0;i<n-1;i++)
		{
			Node node = node_root;
			while(node!=null)
			{
				System.out.println(node.keys[0]);
				for(int j=0;j<=node.keyCount;j++)
				{
					System.out.print("C"+i+" : "+ node.getChild(j).keys[0]+"~~"+node.getChild(j).parent.keys[0] +"====");
				}
				System.out.println("");
				node = node.rightSibling;
			}
			node_root=node_root.getChild(0);
		}
	}
}


abstract class Node implements java.io.Serializable
{
	private static final long serialVersionUID = 1381717712208666866L;
	Integer keys[]=new Integer[3];
	int keyCount;
	Node parent;
	Node leftSibling;
	Node rightSibling;
	int no_of_children=4;
	public void disp()
   {
      System.out.println("disp() method of parent class");
   }
   public void updateRecord(String rec_string)
   {
   	System.out.println("Hello_Node");
   }

   public int getKeyCount()
   {
   		return keyCount;
   }

   public int getKey(int index)
   {
   		return this.keys[index];
   }

   public void setKey(int index,int value)
   {
   		this.keys[index]=value;
   }

   public int getIndex(int sKey)
   {
   		int index;
   		//System.out.println("inside getIndex");
   		//System.out.println(this.keyCount);
   		for(index=0;index<this.keyCount;index++)
   		{
   			//System.out.println(index + "---->" + keys[index]);
   			if(keys[index].compareTo(sKey) == 0)
   				return index+1;
   			else if(keys[index].compareTo(sKey) > 0)
   				return index;
   		}
   		return index;
   }


   public Node getParent() {
		return this.parent;
	}

	public void setParent(Node par)
	{
		this.parent = par;
	}


   public Node getChild(int index)
   {
   		System.out.println("Get the Child in Node");
   		return null;
   }
  
  	public void setChild(int index , Node child)
  	{
  		System.out.println("Setting Child");
  	}

   public Boolean isNodeFull()
   {
   		if(this.keyCount == this.keys.length)
   			return true;
   		return false;
   }

   public void insertAt(int index,int key,String recVals)
   {
   		System.out.println("insertAt in Node");
   }

   public void insertAt(int ind,int key,Node lc,Node rc)
   {
   		System.out.println("4 arg insertAt");
   }

   public Node pushUpKey(int upKey, Node lc , Node rc)
   {
   		return null;
   }

   public void setRecord(int index,String[] rec)
   	{
   		System.out.println("setRecord of Node");
   	}

   	public String accessRecord(int ind)
   	{
   		System.out.println("accessRecord of Node");	
   		return null;
   	}
   //abstract public Node dealOverflow();


   Node()
   {
   	keyCount=0;
   	parent=null;
   	leftSibling=null;
   	rightSibling=null;
   }


   /* The codes below are used to support deletion operation (C) */
	
	public boolean isUnderflow() {
		//System.out.println("Leaf UnderFlow " + (int)(Math.ceil(((float)(no_of_children)) / 2)));
		return this.getKeyCount() < ((int)(Math.ceil(((float)(no_of_children-1)) / 2)));
	}
	
	public boolean canLendAKey() {
		return this.getKeyCount() >  ((int)(Math.ceil(((float)(no_of_children-1)) / 2)));
	}
	
	public Node getLeftSibling() {
		if (this.leftSibling != null && this.leftSibling.getParent() == this.getParent())
			return this.leftSibling;
		return null;
	}

	public void setLeftSibling(Node sibling) {
		this.leftSibling = sibling;
	}

	public Node getRightSibling() {
		if (this.rightSibling != null && this.rightSibling.getParent() == this.getParent())
			return this.rightSibling;
		return null;
	}

	public void setRightSibling(Node silbling) {
		this.rightSibling = silbling;
	}
	
	public Node dealUnderflow() {
		if (this.getParent() == null)
			return null;
		
		// try to borrow a key from sibling
		Node leftSibling = this.getLeftSibling();
		if (leftSibling != null && leftSibling.canLendAKey()) {
			this.getParent().processChildrenTransfer(this, leftSibling, leftSibling.getKeyCount() - 1);
			return null;
		}
		
		Node rightSibling = this.getRightSibling();
		if (rightSibling != null && rightSibling.canLendAKey()) {
			//System.out.println("It should nt happen");
			this.getParent().processChildrenTransfer(this, rightSibling, 0);
			return null;
		}
		
		// Can not borrow a key from any sibling, then do fusion with sibling
		if (leftSibling != null) {
			//System.out.println("It has to happen");
			return this.getParent().processChildrenFusion(leftSibling, this);
		}
		else {
			//System.out.println("III"+this.keys[0]);
			return this.getParent().processChildrenFusion(this, rightSibling);
		}
	}
	
	public abstract int search(int key);

	protected abstract void processChildrenTransfer(Node borrower, Node lender, int borrowIndex);
	
	protected abstract Node processChildrenFusion(Node leftChild, Node rightChild);
	
	protected abstract void fusionWithSibling(int sinkKey, Node rightSibling);
	
	protected abstract int transferFromSibling(int sinkKey, Node sibling, int borrowIndex);

   abstract public int getType();

}


class IntNode extends Node
{
	private static final long serialVersionUID = 1381717712208777866L;
	Object child_ref[];					// makin as 5 pointer to child
	IntNode()
	{
		this.child_ref = new Object[4];
	}

	public int getType()
	{
		return 0;
	}

	public Node getChild(int index)
	{
		return (Node)this.child_ref[index];				//error-prone in typecasting
	}

	public void setChild(int index,Node child)
	{
		this.child_ref[index]=child;
	}

	public boolean isUnderflow() {
		//System.out.println("FFFFFFFFFFFFFFFFFF"+(Math.ceil(((float)no_of_children) / 2)-1) + "  "+"this.getKeyCount() "+this.getKeyCount()+(this.getKeyCount() < Math.ceil(((float)this.keys.length+1) / 2)));
		return this.getKeyCount() < ((int)Math.ceil(((float)no_of_children) / 2)-1);
	}
	
	public boolean canLendAKey() {
		//System.out.println("LLLLLLLLLLLLLLLLLLLLL"+(Math.ceil(((float)no_of_children) / 2)-1) + "  "+"this.getKeyCount() "+this.getKeyCount()+(this.getKeyCount() > Math.ceil(((float)this.keys.length+1) / 2)));
		return this.getKeyCount() > ((int)Math.ceil(((float)no_of_children) / 2)-1);
	}

	public Node pushUpKey(int key, Node leftChild , Node rightChild)
	{
		//System.out.println("UUUUUPPP"+ key);
		if(this.isNodeFull())
		{
			//System.out.println("Int Node is Full");
 			int midInd = (int)Math.ceil(((float)this.keyCount) / 2);
 			int index = this.getIndex(key);
 			Node newNode = new IntNode();
 			int upKey=0;
 			if(index == midInd)
 			{
 				upKey = key;
 				int count = this.keyCount;
 				for(int ind=midInd,i=0;ind < count && i<count-midInd;ind++,i++)
 				{
 					newNode.setKey(i,this.getKey(ind));
 					newNode.setChild(i+1,this.getChild(ind+1));
 					newNode.getChild(i+1).parent = newNode;
 					this.setKey(ind,-1);
 					this.setChild(ind+1,null);
 					this.keyCount--;
 					newNode.keyCount++;
 				}
 				newNode.setChild(0,rightChild);
 				newNode.getChild(0).parent = newNode;
 				this.setChild(midInd,leftChild);
 			}
 			else if( index > midInd )
 			{
 				upKey = this.getKey(midInd);
 				//System.out.println("Upkey --> "+upKey);
 				int count = this.keyCount;
 				this.setKey(midInd,-1);
 				this.keyCount--;
 				int i;
 				//System.out.println("count-midINd-1 --> "+ (count-midInd-1));
 				for(i=0 ; i<(count-midInd-1) ; i++)
 				{
 					newNode.setKey(i,this.getKey(i+midInd+1));
 					newNode.setChild(i,this.getChild(i+midInd+1));
 					newNode.getChild(i).parent = newNode;
 					this.setKey(i+midInd+1,-1);
 					this.setChild(i+midInd+1,null);
 					this.keyCount--;
 					newNode.keyCount++;
 				}
 				newNode.setChild(i,this.getChild(i+midInd+1));
 				newNode.getChild(i).parent = newNode;
 				this.setChild(i+midInd+1,null);
 				//System.out.println("Helllll");
 				//System.out.println(newNode.keyCount);
 				newNode.insertAt(index-midInd-1,key,leftChild,rightChild);
 				//System.out.println("NewNOde kc :" + newNode.keys[0] + " LC : " + newNode.getChild(0).keys[0] + " RC :" + newNode.getChild(1).keys[0]);
 				newNode.getChild(index-midInd-1).parent=newNode;
 				newNode.getChild(index-midInd).parent=newNode;
 				//System.out.println("Helllll22");
 			}
 			else 
 			{
 				upKey = this.getKey(midInd-1);
 				index = this.getIndex(key);
 				int count = this.keyCount;
 				this.setKey(midInd-1,-1);
 				this.keyCount--;
 				int i;
 				//int count =this.keyCount;
 				for(i=0;i<count-midInd;i++)
 				{
 					newNode.setKey(i,this.getKey(i+midInd));
 					newNode.setChild(i,this.getChild(i+midInd));
 					newNode.getChild(i).parent = newNode;
 					this.setKey(i+midInd,-1);
 					this.setChild(i+midInd,null);
 					this.keyCount--;
 					newNode.keyCount++;
 				}
 				newNode.setChild(i,this.getChild(i+midInd));
 				newNode.getChild(i).parent = newNode;
 				this.setChild(i+midInd,null);
 				this.insertAt(index,key,leftChild,rightChild);
 				//this.getChild(index).parent = this;
 				//this.getChild(index+1).parent = this;
 			}

 			if(this.parent == null)
 				this.parent = new IntNode();

 			newNode.parent = this.parent;

 			newNode.leftSibling = this;
 			newNode.rightSibling = this.rightSibling;
 			if(this.rightSibling != null)
 				this.rightSibling.leftSibling = newNode;
 			this.rightSibling=newNode;
 			//System.out.println("Split at Int Node"+ this.keys[0] + newNode.keys[0]);
 			return this.parent.pushUpKey(upKey,this,newNode);
		}	
		else 
		{
			int index = this.getIndex(key);
			//System.out.println("Inserrt Index of pushup key : "+key);
			//System.out.println("PushUpKey");
			this.insertAt(index,key,leftChild,rightChild);
			///System.out.println("LC:"+this.getChild(index).keys[0] + " RC : "+this.getChild(index+1).keys[0]);
		}
		return this.parent==null ? this : null;
	}

	public void insertAt(int index,int key,Node leftChild,Node rightChild)
	{
		//System.out.println("MG1");
		if(this.keyCount==0)
		{
			//System.out.println("KeyCount is zero");
			this.setKey(index,key);
			this.setChild(index+1,rightChild);
			this.setChild(index,leftChild);
			this.getChild(index+1).parent=this;
			this.getChild(index).parent=this;
			//System.out.println(this.getChild(index).keys[0] +"~~~~~ " + this.getChild(index+1	).keys[0]);
		}
		else
		{
			//System.out.println("MG");
			for(int i=keyCount;i>index;i--)
			{
				this.setKey(i,this.getKey(i-1));
			}
			for(int i=keyCount+1;i>index;i--)
			{
				this.setChild(i,this.getChild(i-1));
				this.getChild(i).parent=this;
			}
			this.setKey(index,key);
			this.setChild(index,leftChild);
			this.setChild(index+1,rightChild);
			this.getChild(index).parent=this;
			this.getChild(index+1).parent=this;
		}
		this.keyCount++;
		//System.out.println("this.keyCount --> "+this.keyCount);
	}


	/* The codes below are used to support delete operation (C)*/
	
	@Override
	public int search(int key) {
		int index = 0;
		for (index = 0; index < this.getKeyCount(); ++index) {
			int cmp = keys[index].compareTo(key);
			if (cmp == 0) {
				return index + 1;
			}
			else if (cmp > 0) {
				return index;
			}
		}
		
		return index;
	}

	public void deleteAt(int index) {
		int i = 0;
		for (i = index; i < this.getKeyCount()-1; ++i) {
			this.setKey(i, this.getKey(i + 1));
			this.setChild(i + 1, this.getChild(i + 2));

		}
		this.setKey(i, -1);
		this.setChild(i + 1, null);
		--this.keyCount;
	}
	
	
	@Override
	protected void processChildrenTransfer(Node borrower, Node lender, int borrowIndex) {
		int borrowerChildIndex = 0;
		while (borrowerChildIndex < this.getKeyCount() + 1 && this.getChild(borrowerChildIndex) != borrower)
			++borrowerChildIndex;
		
		if (borrowIndex == 0) {
			// borrow a key from right sibling
			int upKey = borrower.transferFromSibling(this.getKey(borrowerChildIndex), lender, borrowIndex);
			this.setKey(borrowerChildIndex, upKey);
		}
		else {
			// borrow a key from left sibling
			int upKey = borrower.transferFromSibling(this.getKey(borrowerChildIndex - 1), lender, borrowIndex);
			this.setKey(borrowerChildIndex - 1, upKey);
		}
	}
	
	@Override
	protected Node processChildrenFusion(Node leftChild, Node rightChild) {
		int index = 0;
		while (index < this.getKeyCount() && this.getChild(index) != leftChild)
			++index;
		//System.out.println("Index in PCF:"+index);
		int sinkKey = this.getKey(index);
		//System.out.println("SinkKey PCF:"+sinkKey);
		
		// merge two children and the sink key into the left child node
		leftChild.fusionWithSibling(sinkKey, rightChild);
		
		// remove the sink key, keep the left child and abandon the right child
		this.deleteAt(index);
		
		// check whether need to propagate borrow or fusion to parent
		if (this.isUnderflow()) {
			if (this.getParent() == null) {
				// current node is root, only remove keys or delete the whole root node
				if (this.getKeyCount() == 0) {
					leftChild.setParent(null);
					return leftChild;
				}
				else {
					return null;
				}
			}
			
			return this.dealUnderflow();
		}
		
		return null;
	}
	
	
	@Override
	protected void fusionWithSibling(int sinkKey, Node rightSibling) {
		IntNode rightSiblingNode = (IntNode)rightSibling;
		
		int j = this.getKeyCount();
		this.setKey(j++, sinkKey);
		//System.out.println("FWS sinkKey " + sinkKey);
		for (int i = 0; i < rightSiblingNode.getKeyCount(); ++i) {
			this.setKey(j + i, rightSiblingNode.getKey(i));
		}
		for (int i = 0; i < rightSiblingNode.getKeyCount() + 1; ++i) {
			this.setChild(j + i, rightSiblingNode.getChild(i));
			this.getChild(j+i).parent=this;
		}
		this.keyCount += 1 + rightSiblingNode.getKeyCount();
		
		this.setRightSibling(rightSiblingNode.rightSibling);
		if (rightSiblingNode.rightSibling != null)
			rightSiblingNode.rightSibling.setLeftSibling(this);
	}
	
	@Override
	protected int transferFromSibling(int sinkKey, Node sibling, int borrowIndex) {
		IntNode siblingNode = (IntNode)sibling;
		
		int upKey = -1;
		if (borrowIndex == 0) {
			// borrow the first key from right sibling, append it to tail
			int index = this.getKeyCount();
			this.setKey(index, sinkKey);
			this.setChild(index + 1, siblingNode.getChild(borrowIndex));
			this.getChild(index + 1).parent = this;			
			this.keyCount += 1;
			
			upKey = siblingNode.getKey(0);
			if(sibling.getType()==0)
				siblingNode.moveForward(borrowIndex);
			else
				siblingNode.deleteAt(borrowIndex);
		}
		else {
			// borrow the last key from left sibling, insert it to head
			this.insertAt(0, sinkKey, siblingNode.getChild(borrowIndex + 1), this.getChild(0));
			upKey = siblingNode.getKey(borrowIndex);
			siblingNode.deleteAt(borrowIndex);
		}
		
		return upKey;
	}


	public void moveForward(int index)
	{
		int i;
		for(i=index;i<this.keyCount-1;i++)
		{
			this.setKey(i,this.getKey(i+1));
		}
		this.setKey(i,-1);
		for(i=index;i<this.keyCount;i++)
		{
			this.setChild(i,this.getChild(i+1));
		}
		this.setChild(i,null);
		this.keyCount--;
	}


}


class LeafNode extends Node
{
	private static final long serialVersionUID = 1381717712208999866L;
	String record[][];

	public void disp()
	{
      System.out.println("disp() method of Child class");
   	}

   	public void insertAt(int index,int newKey,String recVals)
   	{
   		//record[index] = recVals.split(","); 
   		//System.out.println("insertAt()"+ this.keyCount);
   		for(int ind=this.keyCount-1;ind>=index;ind--)
   		{
   			//System.out.println("insertAt");
   			setKey(ind+1, this.getKey(ind));
   			setRecord(ind+1, this.getRecord(ind));						//yet to be completed
   		}
   		setKey(index,newKey);
   		setRecord(index,recVals.split(","));
   		this.keyCount++;
   		//System.out.println("Index:"+ index);
   		//System.out.println(this.record[0][1]);
   		//System.out.println(this.keyCount);
   	}

   	public String[] getRecord(int index)
   	{
   		return this.record[index];
   	}

   	public void setRecord(int index,String[] rec)
   	{
   		this.record[index] = rec;
   	}

   	//(C)
   	@Override
	public int search(int key) {
		for (int i = 0; i < this.getKeyCount(); ++i) {
			 int cmp = keys[i].compareTo(key);
			 if (cmp == 0) {
				 return i;
			 }
			 else if (cmp > 0) {
				 return -1;
			 }
		}
		
		return -1;
	}

	LeafNode(int attr_count)
	{
		this.record = new String[3][attr_count];
	}


	public int getType()
	{
		return 1;
	}

	//need to include in Node methods
	public Node splitAndInsert(String recVals)					
	{
		String rec_temp[] = recVals.split(",");
		int newKey = Integer.parseInt(rec_temp[0]);
		int newInd = this.getIndex(newKey);
		int midInd = (int)Math.ceil(((float)this.keyCount) / 2);
		//System.out.println("FUUUUUUUUUUUUUUUUUKKKKKKKKKKK"+midInd+"    MMMMMMMMM"+Math.round(this.keyCount/2));
		Node newNode =new LeafNode(rec_temp.length);
		int upKey=0;
		if(newInd>=midInd)
		{
			if(newInd==midInd)
				upKey = newKey;
			else 
				upKey = this.getKey(midInd);
			int key=this.keyCount;
			for(int ind=0; ind<key-midInd;ind++)
			{
				newNode.setKey(ind,this.getKey(ind+midInd));
				this.setKey(ind+midInd,-1); 							//can also change to -1 if erroneoous
				newNode.setRecord(ind, this.getRecord(ind+midInd));
				this.setRecord(ind+midInd,null);
				this.keyCount--;
				newNode.keyCount++;
			}
			newNode.insertAt(newInd-midInd,newKey,recVals);
		}
		else if(newInd<midInd)
		{
			upKey = this.getKey(midInd-1);
			int key = this.keyCount;
			for(int ind=0,i=midInd-1 ; (ind<key-midInd+1)&&i<key ;ind++,i++)
			{
				newNode.setKey(ind, this.getKey(i));
				this.setKey(i,-1);							//can also change to -1 if erroneoous
				newNode.setRecord(ind , this.getRecord(i));
				this.setRecord(i,null);
				this.keyCount--;
				newNode.keyCount++;
			}
			this.insertAt(newInd,newKey,recVals);
		}

		if(this.parent == null)
			this.parent = new IntNode();

		newNode.parent = this.parent;
		//this.rightSibling = newNode;
		newNode.leftSibling = this;
		newNode.rightSibling = this.rightSibling;
		if(this.rightSibling != null)	
			this.rightSibling.leftSibling = newNode;
		this.rightSibling =newNode;
		return ((this.parent).pushUpKey(upKey, this , newNode));

	}

	// writing this method only for checking
	public String accessRecord(int ind)
	{
		//System.out.println(this.keyCount);
		//System.out.println(this.record[ind][0]);
		return this.record[ind][0];
	}
	

	/* The codes below are used to support deletion operation (C)*/
	
	public boolean delete(int key) {
		int index = this.search(key);
		if (index == -1)
		{
			System.out.println("Required Element "+key+" is not present in DB");
			return false;
		}
		
		this.deleteAt(index);
		return true;
	}
	
	private void deleteAt(int index) {
		int i = index;
		for (i = index; i < this.getKeyCount() - 1; ++i) {
			this.setKey(i, this.getKey(i + 1));
			this.setRecord(i, this.getRecord(i + 1));
		}
		this.setKey(i, -1);
		this.setRecord(i, null);
		--this.keyCount;
	}
	
	@Override
	protected void processChildrenTransfer(Node borrower, Node lender, int borrowIndex) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	protected Node processChildrenFusion(Node leftChild, Node rightChild) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Notice that the key sunk from parent is be abandoned. 
	 */
	@Override
	//@SuppressWarnings("unchecked")
	protected void fusionWithSibling(int sinkKey, Node rightSibling) {
		LeafNode siblingLeaf = (LeafNode)rightSibling;
		//System.out.println("Leaf FWS : "+sinkKey);
			
		int j = this.getKeyCount();
		for (int i = 0; i < siblingLeaf.getKeyCount(); ++i) {
			this.setKey(j + i, siblingLeaf.getKey(i));
			this.setRecord(j + i, siblingLeaf.getRecord(i));
		}
		this.keyCount += siblingLeaf.getKeyCount();
		
		this.setRightSibling(siblingLeaf.rightSibling);
		if (siblingLeaf.rightSibling != null)
			siblingLeaf.rightSibling.setLeftSibling(this);
	}
	

	protected void insertKey(int key,String[] rec)		
	{
		int index = getIndex(key);				
		for(int i = this.keyCount; i>index; i--)		//changed
		{
			this.setKey(i,this.getKey(i-1));
			this.setRecord(i, this.getRecord(i-1));
		}
		this.setKey(index,key);
		this.setRecord(index,rec);
		this.keyCount++;
	}

	@Override
	//@SuppressWarnings("unchecked")
	protected int transferFromSibling(int sinkKey, Node sibling, int borrowIndex) {
		LeafNode siblingNode = (LeafNode)sibling;
		
		this.insertKey(siblingNode.getKey(borrowIndex), siblingNode.getRecord(borrowIndex));
		siblingNode.deleteAt(borrowIndex);
		
		return borrowIndex == 0 ? sibling.getKey(0) : this.getKey(0);
	}

}


public class finalass2_ser
{
	public static void main(String args[])
	{
		System.out.println("Hello World");
		Boolean type1=true;
		long startTime = System.currentTimeMillis();
		try
		{
			FileReader frq=new FileReader("query");
			BufferedReader brq=new BufferedReader(frq);
			String sqString = brq.readLine();
			/*if(sqSParam.length==4)
			{
				String cond[] = sqSParam[3].split(",");
				//t.selectWithCond();
			}
			else
			{
				//String result[][]= tab[0].selectAll();
				int cols=(col_names.length);
				int col_ind[] = new int[cols];
				System.out.println("Printing Tables");
				for(int i=0;i<col_names.length;i++)
				{
					col_ind[i] = tab[0].getColIndex(col_names[i]);
					if(col_ind[i]==-1)
						throw new ArrayIndexOutOfBoundsException();
				}
				/*if(result != null)
				{
					//int cols = col_names.length;
					for(int i=0 ; i<cols ; i++)
						System.out.print(col_names[i]+"	");
					System.out.println("");
					for(int i=0 ; i<result.length ; i++)
					{
						for(int j=0 ; j<cols ; j++)
							System.out.print(result[i][col_ind[j]]+"	");
						System.out.println("");
					}
				}*/
			//}
			if(sqString.contains("project"))
				type1 = false;
			//LeafNode leaf1 = tab[0].findLeafNodeOfKey(2371);
			//tab[0].printTable();
			// System.out.println(leaf1.leftSibling.keys[1]+"~~~~~~~~~~~~"+leaf1.parent.keys[0]+"~~~~~~~~~"+leaf1.parent.getChild((leaf1.parent.getKeyCount()-1)).keys[1]);

			if(type1)
			{
				String sqSParam[] = sqString.split(" ");
				String col_names[] = sqSParam[1].split(",");
				String tab_names[] = sqSParam[2].split(",");
				Table tab = new Table(sqSParam[2]);
				boolean hasExecuted = false;
				try
				{
					FileInputStream fileIn = new FileInputStream(sqSParam[1]+".ser");
					ObjectInputStream in = new ObjectInputStream(fileIn);
					tab = (Table) in.readObject();
					in.close();
				}catch(Exception e)
				{
					System.out.println("Type 1 Eror in deser");
					e.printStackTrace();
				}
				//tab.printTable();
				if(sqSParam[0].compareTo("insert")==0)
				{
					
					if(sqSParam.length == 4)
					{
						tab.insertIntoTable(sqSParam[1],sqSParam[2],sqSParam[3]);
						//tab.printTable();
						hasExecuted = true;
					}
					else
					{
						System.out.println("The INSERT Query is InComplete");
					}
					
				}
				else if(sqSParam[0].compareTo("delete")==0)
				{
					System.out.println("Length" + sqSParam.length);
					if(sqSParam.length == 3)
					{
						//System.out.println("before Deleting");
						//tab.printTable();
						tab.deleteFromTable(sqSParam[1],sqSParam[2]);
						//System.out.println("After Deleting");
						//tab.printTable();
						hasExecuted = true;
					}
					else
					{
						System.out.println("Cnt Delete");
					}
				}
				else if(sqSParam[0].compareTo("update")==0)
				{
					if(sqSParam.length==4)
					{
						tab.updateTable(sqSParam[2],sqSParam[3]);
						hasExecuted = true;
					}
					else
					{
						System.out.println("No of Attributes of Update Query are not satisfied");
					}
				}
				if(hasExecuted)
				{
					try
					{
						//System.out.println("tableName:"+tab.tableName);
						FileInputStream fileIn = new FileInputStream(tab.tableName+".ser");
						ObjectInputStream in = new ObjectInputStream(fileIn);
						tab = (Table) in.readObject();
						in.close();
					}catch(Exception e)
					{
						System.out.println("Type 1 Eror in deser");
						e.printStackTrace();
					}
				}
				
			}
			else
			{
				String subQuery = sqString.substring(sqString.indexOf('(')+1).replaceAll("[()]","");
				System.out.println(subQuery);
				ResultSet result = new ResultSet();
				String sq[] ;
				if(subQuery.contains("UNION"))
				{
					ResultSet rs1,rs2,r1,r2;
					sq = subQuery.split("UNION");
					r1 = processQuery(sq[0]);
					r2 = processQuery(sq[1]);
					rs1 = r1.projectRS();
					rs2 = r2.projectRS();
					result = processKeyword(rs1,rs2,"UNION");
				}
				else if(subQuery.contains("INTERSECTION"))
				{
					ResultSet rs1,rs2,r1,r2;
					sq = subQuery.split("INTERSECTION");
					r1 = processQuery(sq[0]);
					r2 = processQuery(sq[1]);
					rs1 = r1.projectRS();
					rs2 = r2.projectRS();
					result = processKeyword(rs1,rs2,"INTERSECTION");
				}
				else if(subQuery.contains("DIFFERENCE"))
				{
					ResultSet rs1,rs2,r1,r2;
					sq = subQuery.split("DIFFERENCE");
					r1 = processQuery(sq[0]);
					r2 = processQuery(sq[1]);
					rs1 = r1.projectRS();
					rs2 = r2.projectRS();
					result = processKeyword(rs1,rs2,"DIFFERENCE");
				}
				else
				{
					result = processQuery(subQuery);
					result = result.projectRS();
				}
				//ResultSet rs = processQuery(sqString,tab);
				if(result== null)
					System.out.println("No Rows Selected");
				else
					result.printResultSetProj();
			}

		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			System.out.println("Arrays Prob");
			e.printStackTrace();
		}
		catch(Exception e)
		{
			System.out.println("Table Error");
			e.printStackTrace();
		}
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("Execution of query"+totalTime);

		/*try {
      //create a buffered reader that connects to the console, we use it so we can read lines
      BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

      //read a line from the console
      String lineFromInput = in.readLine();

      //create an print writer for writing to a file
      PrintStream out = new PrintStream(new FileOutputStream("output.txt"));
		System.setOut(out);
      //output to the file a line
      out.println(lineFromInput);

      //close the file (VERY IMPORTANT!)
	      out.close();
	   }
	      catch(IOException e1) {
	        System.out.println("Error during reading/writing");
   		}*/
      	/*String rec[];
      	if((rec = tab[0].select(2347))!=null)
      	{
	      	for(int i=0 ; i<rec.length ; i++)
	      		System.out.print(tab[0].attr_name[i]+ "	" );
	      	System.out.println("");
	      	for(int i=0 ; i<rec.length ; i++)
	      		System.out.print(rec[i]+ "		" );
	    }	
      	System.out.println("\n"+tab[0].root.keys[0]);*/
	}

	public static int getTableIndex(String tableName,Table[] tab)
	{
		for(int i=0;i<tab.length;i++)
			if((tab[i].tableName).compareTo(tableName)==0)
				return i;

		return -1;
	}

	

	public static ResultSet processKeyword(ResultSet rs1, ResultSet rs2,String keyWord)
	{
		if(rs1.proj_attr.size() != rs2.proj_attr.size())
		{
			System.out.println(keyWord+" Cannot be performed as Columns are not Matching");
			return null;
		}
		else
		{
			int i,j;
			ResultSet result = new ResultSet();
			result.attr = rs1.proj_attr;
			result.proj_attr =rs1.proj_attr;
			boolean isRedundant = false;
			String tab1_record[],tab2_record[];
			int no_of_recs = rs1.records.size();
			//System.out.println("no_of_recs:"+rs2.records.size());
			for(i=0;i<no_of_recs;i++)
			{
				tab1_record = rs1.records.get(i);
				isRedundant=false;
				for(j=0;j<rs2.records.size();j++)
				{
					isRedundant = isRedundant || Arrays.deepEquals(tab1_record,rs2.records.get(j));
				}
				if(keyWord.equals("UNION"))
				{
					if(!isRedundant)
						result.records.add(rs1.records.get(i));
				}
				else if(keyWord.equals("INTERSECTION") && isRedundant)
				{
					result.records.add(rs1.records.get(i));
				}
				else if(keyWord.equals("DIFFERENCE") && !isRedundant)
				{
					result.records.add(rs1.records.get(i));
				}	
			}
			if(keyWord.equals("UNION") && rs2.records.size()!=0)
				result.records.addAll(rs2.records);
			else if(keyWord.equals("UNION") && rs2.records.size()==0)
				result.records.addAll(rs1.records);
			else if(keyWord.equals("UNION") && rs1.records.size()==0)
				result.records.addAll(rs2.records);
			return result;
		}
	}

	public static ResultSet processQuery(String query)
	{
		String[] temp = query.split(" ");
		String colList[];
		String tableList[];
		/*when there are no cond*/
		if(temp.length == 3)
		{
			tableList = temp[2].split(",");
			colList = temp[1].split(",");
			int no_of_tabs = tableList.length,i=0,j;
			if(no_of_tabs == 1)
			{
				Table tab = new Table(tableList[0]);
				//deserialising table
				try
				{
					FileInputStream fileIn = new FileInputStream(tableList[0]+".ser");
					ObjectInputStream in = new ObjectInputStream(fileIn);
					tab = (Table) in.readObject();
					in.close();
				}catch(Exception e)
				{
					System.out.println("Exception while deserialising");
					//e.printStackTrace();
				}
				ResultSet result = new ResultSet();
				result = tab.selectAll(colList,new String[0]);
				if(colList.length==1 && colList[0].equals("*"))
					result.proj_attr.addAll(Arrays.asList(tab.attr_name));
				else
					result.proj_attr.addAll(Arrays.asList(colList));
				return result;
			}
			else		// need to do the cartesian product
			{
				/*if(colList.length == 1 && colList[0].equals("*"))
				{
					//List<List<String>> colListTab= new ArrayList<List<String>>(tableList.length);
					List<String[]> colListTab = new ArrayList<String[]>();
					for(i=0;i<no_of_tabs;i++)
					{	
						colListTab.add(i,tab[getTableIndex(tableList[i],tab)].attr_name);
					}
					ResultSet rs[] = new ResultSet[no_of_tabs];
					for(i=0;i<no_of_tabs;i++)
					{
						rs[i] = tab[getTableIndex(tableList[i],tab)].selectAll(colListTab.get(i),new String[0]);
					}
					ResultSet result = new ResultSet();
					result = rs[0];
					for(i=1;i<no_of_tabs;i++)
					{
						result = cartesian(result,rs[i]);
					}
					return result;
				}
				else 	// if there is a projection list
				{*/
					String temp_tab_col[] = new String[2];
					String temp_each_tab_cols[];
					List<String[]> colListTab = new ArrayList<String[]>();
					Table tab[] =new Table[no_of_tabs];
					//deserialising
					try
					{
						for(i=0;i<no_of_tabs;i++)
						{
							FileInputStream fileIn = new FileInputStream(tableList[i]+".ser");
							ObjectInputStream in = new ObjectInputStream(fileIn);
							tab[i] = (Table) in.readObject();
							in.close();
						}
					}catch(Exception e)
					{
						System.out.println("Exception while deserialising multiple Table");
						//e.printStackTrace();
					}
					for(i=0;i<no_of_tabs;i++)
					{
						List<String> temp_col_add = new ArrayList<String>();
						/*for(j=0;j<colList.length;j++)
						{
							temp_tab_col = colList[j].split("\\.");
							if(tab[i].tableName.equals(temp_tab_col[0]))
								temp_col_add.add(temp_tab_col[1]);
						}*/
						/*augmenting the colList with table name*/
						//if(colList.length==1 && colList[0].equals("*"))
							int no_of_cols = tab[i].attr_name.length;
							temp_each_tab_cols = tab[i].attr_name;
							for(j=0;j<no_of_cols;j++)
							{
								temp_col_add.add(tableList[i]+"."+temp_each_tab_cols[j]);
							}
						/*if(temp_col_add.size()==0)
						{
							System.out.println("It is not logical of having a table name but not any column of it");
							System.exit(0);
						}
						else*/
							colListTab.add(temp_col_add.toArray(new String[0]));
					}
					ResultSet rs[] = new ResultSet[no_of_tabs];
					for(i=0;i<no_of_tabs;i++)
						rs[i] = tab[i].selectAll(colListTab.get(i),new String[0]);
					ResultSet result = new ResultSet();
					result = rs[0];
					for(i=1;i<no_of_tabs;i++)
						result = cartesian(result,rs[i]);
					if(colList.length==1 && colList[0].equals("*"))
						result.proj_attr.addAll(result.attr);
					else
						result.proj_attr.addAll(Arrays.asList(colList));
				return result;
				//}
			}
			//return null;
		}
		else
		{
			tableList = temp[2].split(",");
			colList = temp[1].split(",");
			String condList[] = temp[3].split(",");
			int i,j;
			if(tableList.length == 1)
			{
				//System.out.println("Table NAme is :"+tableList[0]);
				Table tab = new Table(tableList[0]) ;
				try
				{
					FileInputStream fileIn = new FileInputStream(tableList[0]+".ser");
					ObjectInputStream in = new ObjectInputStream(fileIn);
					tab = (Table) in.readObject();
					in.close();
				}catch(Exception e)
				{
					System.out.println("Exception while deserialising");
					//e.printStackTrace();
				}
				ResultSet result = new ResultSet();
				result = tab.selectAll(colList,condList);
				if(colList.length==1 && colList.equals("*"))
					result.proj_attr.addAll(Arrays.asList(tab.attr_name));
				else
					result.proj_attr.addAll(Arrays.asList(colList));
				return result;
			}
			else	//join or cartesian with cond
			{
				List<String>joinCondList = new ArrayList<String>();
				List<String>generalCondList = new ArrayList<String>();
				ResultSet rs[] = new ResultSet[tableList.length];
				int index,attr_count;
				String tableName,condition ;
				int no_of_tabs = tableList.length;
				Table tab[] =new Table[no_of_tabs];
				/*try
				{
						FileInputStream fileIn = new FileInputStream(tableList[0]+".ser");
						ObjectInputStream in = new ObjectInputStream(fileIn);
						tab[0] = (Table) in.readObject();
						in.close();
						System.out.println(tab[0].tableName);

					FileInputStream fileIn1 = new FileInputStream(tableList[1]+".ser");
						ObjectInputStream in1 = new ObjectInputStream(fileIn1);
						tab[1] = (Table) in1.readObject();
						in.close();
						System.out.println(tab[1].tableName);
				}
				catch(Exception e)
				{
					System.out.println("Error in single Serialising");
					e.printStackTrace();
				}*/
				//deserialising
				try
				{
					FileInputStream fileIn;
					ObjectInputStream in;
					for(i=0;i<no_of_tabs;i++)
					{
						fileIn = new FileInputStream(tableList[i]+".ser");
						in = new ObjectInputStream(fileIn);
						tab[i] = (Table) in.readObject();
						in.close();
					}
				}catch(Exception e)
				{
					System.out.println("Exception while deserialising multiple Table");
					//e.printStackTrace();
				}
				for(i=0;i<condList.length;i++)
				{
					index = condList[i].indexOf('=');
					if(index>0  && condList[i].substring(index+1).contains("."))
						joinCondList.add(condList[i]);
					else
						generalCondList.add(condList[i]);
				}
				int no_of_gen_cond = generalCondList.size();
				for(i=0;i<no_of_tabs;i++)
				{
					List<String> temp_cond = new ArrayList<String>();
					List<String> new_attr_name = new ArrayList<String>();
					tableName = tableList[i];
					for(j=0;j<no_of_gen_cond;j++)
					{
						condition = generalCondList.get(j);
						if(condition.contains(tableName))
							temp_cond.add(condition.substring(condition.indexOf('.')+1));
					}
					/*augmenting attr_names with table name*/
					attr_count = tab[i].attr_name.length;
					String[] old_attr = tab[i].attr_name;
					for(j=0;j<attr_count;j++)
					{
						new_attr_name.add(tableName+"."+old_attr[j]);
					}
					if(temp_cond.size() == 0)
					{
						//System.out.println("1st cond");
						rs[i] = tab[i].selectAll(new_attr_name.toArray(new String[0]),new String[0]);
					}
					else
					{
						//System.out.println("size:"+temp_cond.size());
						rs[i] = tab[i].selectAll(new_attr_name.toArray(new String[0]),temp_cond.toArray(new String[0]));
					}

				}
				if(joinCondList.size() == 0)
				{
					ResultSet result = new ResultSet();
					result=rs[0];
					for(i=1;i<no_of_tabs;i++)
					{
						result = cartesian(result,rs[i]);
					}
					if(colList.length==1 && colList[0].equals("*"))
						result.proj_attr.addAll(result.attr);
					else
						result.proj_attr.addAll(Arrays.asList(colList));
					return result;
				}
				else
				{
					List<String> joinedTable = new ArrayList<String>();
					ResultSet result = new ResultSet();
					result = rs[0];
					joinedTable.add(tableList[0]);
					String join_cond;
					for(i=1;i<no_of_tabs;i++)
					{
						List<String[]> temp_join_cond_val = new ArrayList<String[]>();
						List<Integer> colInd = new ArrayList<Integer>();
						for(j=0;j<joinCondList.size();j++)
						{
							join_cond = joinCondList.get(j);
							if(join_cond.contains("="+tableList[i]) && joinedTable.contains(join_cond.substring(0,join_cond.indexOf('.'))))
							{
								colInd.add(result.getRSColIndex(result.attr,join_cond.substring(0,join_cond.indexOf('='))));
								//System.out.println("ColName :"+join_cond.substring(0,join_cond.indexOf('='))+" --> Index:"+result.getRSColIndex(result.attr,join_cond.substring(0,join_cond.indexOf('=')))+"Size:"+colInd.size());
								Integer val_colInd = rs[i].getRSColIndex(rs[i].attr,join_cond.substring(join_cond.indexOf('=')+1));
								//System.out.println("val_colInd:"+val_colInd);
								List<String> join_val = new ArrayList<String>();
								for(int k=0;k<rs[i].records.size();k++)
									join_val.add(rs[i].records.get(k)[val_colInd]);
								temp_join_cond_val.add(join_val.toArray(new String[0]));
							}
							else if(join_cond.contains(tableList[i]) && joinedTable.contains(join_cond.substring(join_cond.indexOf('=')+1,join_cond.lastIndexOf("."))))
							{
								colInd.add(result.getRSColIndex(result.attr,join_cond.substring(join_cond.indexOf('=')+1)));
								//System.out.println("ColName :"+join_cond.substring(join_cond.indexOf('=')+1)+" ~~~> Index:"+result.getRSColIndex(result.attr,join_cond.substring(join_cond.indexOf('=')+1))+"Size:"+colInd.size());
								Integer val_colInd = rs[i].getRSColIndex(rs[i].attr,join_cond.substring(0,join_cond.indexOf('=')));
								//System.out.println("val_colInd:"+val_colInd);
								List<String> join_val = new ArrayList<String>();
								for(int k=0;k<rs[i].records.size();k++)
									join_val.add(rs[i].records.get(k)[val_colInd]);
								temp_join_cond_val.add(join_val.toArray(new String[0]));
							}
						}
						joinedTable.add(tableList[i]);
						if(temp_join_cond_val.size() == 0)
							result = cartesian(result,rs[i]);
						else
						{
							System.out.println("Join Happening");
							//System.out.println("Vals size:"+temp_join_cond_val.get(0).length+"Result:"+result.records.size());
							result = join(result,rs[i],temp_join_cond_val,colInd);
						}
					}
					if(colList.length==1 && colList[0].equals("*"))
						result.proj_attr.addAll(result.attr);
					else
						result.proj_attr.addAll(Arrays.asList(colList));
					return result;
				}
			}
		}
		//return null;
	}
	

	public static String[] combine(String arr1[],String arr2[])
	{
		List<String> help = new ArrayList<String>();
		help.addAll(Arrays.asList(arr1));
		help.addAll(Arrays.asList(arr2));
		String result[] = help.toArray(new String[0]);
		return result;
	}

	public static ResultSet cartesian(ResultSet rs1 , ResultSet rs2)
	{
		ResultSet rs = new ResultSet();
		System.out.println(rs1.attr.size());
		rs.attr.addAll(rs1.attr);			//error prone
		rs.attr.addAll(rs2.attr);
		for(int i=0; i<rs1.records.size();i++)
		{
			String[] help = rs1.records.get(i);
			for(int j=0; j<rs2.records.size();j++)
			{
				rs.records.add(combine(help,rs2.records.get(j)));	//very inefficient
			}
		}
		return rs;
	}

	public static ResultSet join(ResultSet rs1,ResultSet rs2, List<String[]> joined_val,List<Integer> colInd)
	{
		ResultSet result = new ResultSet();
		Boolean canAddRec = true;
		int i,j,k;
		result.attr.addAll(rs1.attr);			//error prone
		result.attr.addAll(rs2.attr);
		if(joined_val.size()==1)
		{
			int col = colInd.get(0);
			System.out.println(col);
			String[] values = joined_val.get(0);
			for(j=0;j<values.length;j++)
			{
				for(i=0;i<rs1.records.size();i++)
				{
					if(rs1.records.get(i)[col].equals(values[j]))
						result.records.add(combine(rs1.records.get(i),rs2.records.get(j)));
				}
			}
		}
		else
		{
			int count=0;
			//System.out.println("Attrr:"+rs1.attr.get(colInd.get(0))+"--Index: "+colInd.get(0)+"		"+rs1.attr.get(colInd.get(1))+"--Index: "+colInd.get(1));

			for(j=0;j<joined_val.get(0).length;j++)
			{
				for(i=0;i<rs1.records.size();i++)
				{
					String temp[] = rs1.records.get(i);
					count++;
					canAddRec = true;
					for(k=0;k<colInd.size();k++)		
					{
						if(temp[colInd.get(k)].equals(joined_val.get(k)[j]))
							canAddRec = canAddRec && true;
						else 
							canAddRec = false;
					}
					if(canAddRec)
					{
						result.records.add(combine(rs1.records.get(i),rs2.records.get(j)));
					}
				}
			}	
			//System.out.println("Coutn is :"+count);
		}
		return result;
	}
	
}


class ResultSet
{
	List<String> attr= new ArrayList<String>();
	List<String[]> records = new ArrayList<String[]>();
	List<String> proj_attr = new ArrayList<String>();

	public void printResultSet()
	{
		if(records.size()==0)
		{
			System.out.println("No Rows Selected in Printing");
			return;
		}
		/*printing the col names*/
		for(int i=0;i<attr.size();i++)
		{
			System.out.print(attr.get(i)+"		");
		}
		System.out.println("");
		for(int i=0;i<records.size();i++)
		{
			for(int j=0; j<attr.size();j++)
				System.out.print(records.get(i)[j]+"		");
			System.out.println("");
		}


	}

	public int getRSColIndex(List<String> attrName, String colName)
	{
		System.out.println(colName);
		return attrName.indexOf(colName);
	}


	public void printResultSetProj()
	{
		if(records.size()==0)
		{
			System.out.println("No Rows Selected in Printing");
			return;
		}
		int no_of_proj_attr = proj_attr.size();
		for(int i=0;i<no_of_proj_attr;i++)
			System.out.print(attr.get(i)+"		");
		/*Preparing col Indices for proj_attr*/
		int[] colInd = new int[proj_attr.size()];
		for(int i=0;i<no_of_proj_attr;i++)
		{
			System.out.println(proj_attr.get(i));
			colInd[i] = attr.indexOf(proj_attr.get(i));
		}
		BufferedWriter output = null;
		try
		{
			PrintWriter pw=new PrintWriter(new FileWriter("output.txt"));
			//PrintWriter writer = new PrintWriter("result.txt", "UTF-8");
			for(int i=0;i<no_of_proj_attr;i++)
			{
				pw.print(proj_attr.get(i)+",");
			}
			pw.println("\n#"+records.size());
			for(int i=0;i<records.size();i++)
			{
				for(int j=0;j<proj_attr.size();j++)
				{
					//System.out.print(records.get(i)[colInd[j]]+"		");
					//writer.print(records.get(i)[colInd[j]]+",");
					pw.print(records.get(i)[colInd[j]]+",");
					
				}
				//System.out.println("");
				//writer.println("");
				pw.println("");
			}
			pw.close();
		}
		catch(Exception e)
		{
			System.out.println("File writing error");
			e.printStackTrace();
		}
		System.out.println(records.size()+" Records Selected");
	}


	public ResultSet projectRS()
	{
		ResultSet result = new ResultSet();
		int i,j,k;
		result.attr = this.proj_attr;
		result.proj_attr = this.proj_attr;
		/*calculating the col Inidices for the proj columns of this for attr cols of this*/
		int no_of_proj_attr = this.proj_attr.size();
		int colInd[] = new int[no_of_proj_attr];
		for(i=0;i<no_of_proj_attr;i++)
		{
			colInd[i] = this.getRSColIndex(this.attr,this.proj_attr.get(i));
		}
		int no_of_recs = this.records.size();
		String[][] helper = new String[no_of_recs][no_of_proj_attr];
		System.out.println("Records:"+no_of_recs);
		for(i=0;i<this.records.size();i++)
		{
			for(j=0;j<no_of_proj_attr;j++)
			{
				helper[i][j] = this.records.get(i)[colInd[j]];
			}
			//result.records.add(helper[i]);
			for(k=0;k<result.records.size();k++)
			{
				if(Arrays.deepEquals(helper[i],result.records.get(k)))
					break;						// might be error prone for larger helper
			}
			if(k==result.records.size())
				result.records.add(helper[i]);
		}
		return result;
	}
}









