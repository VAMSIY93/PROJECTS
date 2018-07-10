import java.io.*;
import java.util.*;
import javax.swing.*;

class Table
{
	String name,data[][],attrib[];
	int rows,col;
	Table(int r,int c,String tName)
	{
		name=tName;
		rows=r;
		col=c;
		attrib=new String[c];
		data=new String[r][c];
	}
}

class Relations extends JFrame
{
	boolean checkValid(String s1,String s2)
	{
		int n=0;
		double d=0.0;
		System.out.println(s1+"---"+s2);
		if(s2.equals("BOOLEAN"))
		{
			System.out.println("s1:"+s1);
			if(s1.equals("1")||s1.equals("0"))
				return true;
			else
			{
				JOptionPane.showMessageDialog(null,"INVALID....Not a boolean value.","Relations",JOptionPane.WARNING_MESSAGE);
				return false;
			}
		}
		else if(s2.equals("INTEGER"))
		{
			try
			{
				int i=Integer.parseInt(s1);
			}
			catch(Exception e)
			{
				JOptionPane.showMessageDialog(null,"INVALID....Not an Integer.","Relations",JOptionPane.WARNING_MESSAGE);
				return false;
			}
		}
		else if(s2.equals("REAL")||s2.equals("FLOAT"))
		{
			try
			{
				d=Double.parseDouble(s1);
			}
			catch(Exception e)
			{
				JOptionPane.showMessageDialog(null,"INVALID....Not a Real number.","Relations",JOptionPane.WARNING_MESSAGE);
				return false;
			}
		}
		else if(s2.equals("DATE"))
		{
			String temp[]=s1.split("/");
			try
			{
				for(int i=0;i<temp.length;i++)
					n=Integer.parseInt(temp[i]);
			}
			catch(Exception e)
			{
				JOptionPane.showMessageDialog(null,"INVALID....Not a valid date.","Relations",JOptionPane.WARNING_MESSAGE);
				return false;
			}
		}
		else if(s2.equals("TIME"))
		{
			String temp[]=s1.split(":");
			try
			{
				for(int i=0;i<temp.length;i++)
					if(i<3)
						n=Integer.parseInt(temp[i]);
					else
						d=Double.parseDouble(temp[i]);
			}
			catch(Exception e)
			{
				JOptionPane.showMessageDialog(null,"INVALID....Not a valid time.","Relations",JOptionPane.WARNING_MESSAGE);
				return false;
			}
		}

		return true;
	}

	boolean checkDataType(String line,String attrib[])
	{
		boolean result=true;
		String temp[]=line.split(",");
		for(int j=0;j<attrib.length;j++)
			result=(checkValid(temp[j],attrib[j]))&&(result);

		return result;
	}

	boolean checkPrimaryKey(int col,String data[][],int r,boolean checked)
	{
		if(checked)
		{
		try
		{
			for(int i=0;i<r;i++)
				if(data[r][col].equals(data[i][col]))
				{
					System.out.println("r:"+data[r][col]+"\ti:"+data[i][col]);
					throw new Exception();
				}
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(null,"INVALID....Primary key constraint violated.","Relations",JOptionPane.WARNING_MESSAGE);
			return false;
		}
		}
		return true;
	}

	boolean checkForeignKey(int prm,int prmatrb,int frn,int frnatrb,Table tb[])
	{
		boolean found=true;

		try
		{
		for(int i=0;i<tb[frn].rows;i++)
		{
			for(int j=0;j<tb[prm].rows;j++)
			{
				found=true;
				if(tb[prm].data[j][prmatrb].equals(tb[frn].data[i][frnatrb]))
					break;
				else
					found=false;		
			}
			if(found==false)
				throw new Exception("");
		}
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(null,"INVALID....Foreign key constraint violated.","Relations",JOptionPane.WARNING_MESSAGE);
			return false;
		}
		return true;
	}

	Table performJoin(Table tb1,int atb1,Table tb2,int atb2,boolean match)
	{
		int count=0,r1=0,r2=0;
		Vector v=new Vector(tb1.rows);
		String st="";
		Table result=tb1;
		if(tb1.name.equals(tb2.name))
			return result;
		if(match)
		{
		for(int i=0;i<tb1.rows;i++)
		{
			for(int j=0;j<tb2.rows;j++)
			{
				if(tb1.data[i][atb1].equals(tb2.data[j][atb2]))
				{
					count++;
					st=i+"-"+j;
					v.add(st);
				}
			}
		}
		
		if(count>0)
		{
			int i=0;
			result=new Table(count,(tb1.attrib.length+tb2.attrib.length-1),"Output");
			for(i=0;i<tb1.col;i++)
				result.attrib[i]=tb1.attrib[i];
			for(int j=0;j<tb2.col;j++)
				if(j!=atb2)
				{
					result.attrib[i]=tb2.attrib[j];
					i++;
				}
			
			for(i=0;i<count;i++)
			{
				st=""+v.get(i);
				String temp[]=st.split("-");
				int k=0,x=0;
				for(int j=0;j<result.col;j++)
				{
					r1=Integer.parseInt(temp[0]);
					r2=Integer.parseInt(temp[1]);
					if(j<tb1.col)
						result.data[i][j]=tb1.data[r1][j];
					else
					{
						if(k<atb2)						
							result.data[i][j]=tb2.data[r2][k];
						else
							result.data[i][j]=tb2.data[r2][k+1];
						k++;						
					}
				}
			}
		}
		}
		else
		{
			int i=0;
			result=new Table((tb1.rows*tb2.rows),(tb1.col+tb2.col),"Output");
			for(i=0;i<tb1.col;i++)
				result.attrib[i]=tb1.attrib[i];
			for(int j=0;j<tb2.col;j++)
			{
				result.attrib[i]=tb2.attrib[j];
				i++;
			}
			
			for(i=0;i<result.rows;i++)
			{
				int j=0;
				for(j=0;j<tb1.col;j++)
					result.data[i][j]=tb1.data[i/tb2.rows][j];
				for(int k=0;k<tb2.col;k++)
					result.data[i][j+k]=tb2.data[i%tb2.rows][k];
			}

		}

		return result;
	}

	boolean repeated(String data[][],int i)
	{
		for(int j=0;j<i;j++)
			if(data[i][0].equals(data[j][0]))
				return true;
		return false;
	}

	public static void main(String args[])
	{
		int not=1,r=0,c=0,nor=0,isKey=10,noat=0;
		String tName="",join[][];
		boolean gotKey=false;
		Relations rel=new Relations();

		try
		{
			BufferedReader br=new BufferedReader(new FileReader("input1.txt"));
			String line;
			if((line=br.readLine())!=null)
				not=Integer.parseInt(line);
												
			Table tb[]=new Table[not];			
			boolean valid[]=new boolean[not];

			for(int i=0;i<not;i++)
			{
				gotKey=false;
				valid[i]=true;
				isKey=10;
				if((line=br.readLine())!=null) //NAME OF TABLE
					tName=line;
					
				if((line=br.readLine())!=null) //NUMBER OF ATTRIBUTES
					c=Integer.parseInt(line);
				
				String temp[][]=new String[c][3];

				for(int j=0;j<c;j++) //READ ATTRIBUTES
				{
					if((line=br.readLine())!=null)
					{
						String tmp[]=line.split(",");
						temp[j]=tmp;						
					}
					if(temp[j][2].equals("1")&&gotKey==false)
						{gotKey=true;
						 isKey=j;}
					else if(temp[j][2].equals("1")&&gotKey==true)
					{
						JOptionPane.showMessageDialog(null,"INVALID ENTRY----Multiple keys for same Table","Relations",JOptionPane.WARNING_MESSAGE);
						valid[i]=false;
					}
				}
					
				if((line=br.readLine())!=null) //NUMBER OF RECORDS				
					r=Integer.parseInt(line);
				
				tb[i]=new Table(r,c,tName);
				for(int j=0;j<c;j++)
					tb[i].attrib[j]=temp[j][0];
				
				for(int j=0;j<tb[i].rows;j++) //READ RECORDS
				{
					if((line=br.readLine())!=null)
					{
						String tmp[]=line.split(",");
						tb[i].data[j]=tmp;
						valid[i]=valid[i]&&rel.checkDataType(line,tb[i].attrib);
						valid[i]=valid[i]&&rel.checkPrimaryKey(isKey,tb[i].data,j,valid[i]);
					}
				}		
			}
			//System.out.println("Hell");
			if((line=br.readLine())!=null)
				nor=Integer.parseInt(line);
			for(int i=0;i<nor;i++)
			{
				int prm=10,frn=10,prmatrb=10,frnatrb=10;
				if((line=br.readLine())!=null)
					System.out.print("");
				String temp[]=line.split(",");
				//System.out.println("came here");
				for(int j=0;j<not;j++)
				{
					if(temp[0].equals(tb[j].name))
						prm=j;
					if(temp[2].equals(tb[j].name))
						frn=j;					
				}
				//System.out.println("shud cum");
				for(int j=0;j<tb[prm].col;j++)
					if(temp[1].equals(tb[prm].attrib[j]))
						prmatrb=j;
				//System.out.println("shud have cum");					
				for(int j=0;j<tb[frn].col;j++)
					if(temp[3].equals(tb[frn].attrib[j]))
						frnatrb=j;

				valid[frn]=rel.checkForeignKey(prm,prmatrb,frn,frnatrb,tb);
			}
			//System.out.println("fuck");
			JTable jt[]=new JTable[not];
			JFrame jf[]=new JFrame[not];

			for(int i=0;i<not;i++)
				if(valid[i])
				{
					jt[i]=new JTable(tb[i].data,tb[i].attrib);
					jf[i]=new JFrame(tb[i].name);
					jf[i].add(new JScrollPane(jt[i]));
					jf[i].setSize(800,300);
					jf[i].setVisible(true);
					jf[i].setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				}

			if((line=br.readLine())!=null) //NUMBER OF ATTRIBUTES
				noat=Integer.parseInt(line);
			join=new String[noat][2];
			int tbno[]=new int[noat];
			Table result;

			for(int i=0;i<noat;i++)
			{
				if((line=br.readLine())!=null)
				{
					String temp[]=line.split(",");					
					join[i]=temp;
				}
				for(int j=0;j<not;j++)
					if(join[i][0].equals(tb[j].name))
						tbno[i]=j;
				
			}
			result=tb[tbno[0]];

			for(int i=1;i<noat;i++)
			{
				if(rel.repeated(join,i)==false)
				{
				boolean flag=false;
				int j=0,k=0;
				String temp="";
				Vector v=new Vector();
				for(j=0;j<tb[tbno[i]].col;j++)
				{
					for(k=0;k<result.col;k++)
					{
						if(result.attrib[k].equals(tb[tbno[i]].attrib[j]))
						{
							temp=k+"-"+j;
							v.add(temp);
						}
					}
				}
				boolean match=false;
				for(int l=0;l<v.size();l++)
				{
					String tmp=""+v.get(l);
					String arr[]=tmp.split("-");
					k=Integer.parseInt(arr[0]);
					j=Integer.parseInt(arr[1]);

					for(int m=0;m<result.rows;m++)
						for(int n=0;n<tb[tbno[i]].rows;n++)
							if(result.data[m][k].equals(tb[tbno[i]].data[n][j]))
								match=true;

					if(match)
						break;
					
				}
				result=rel.performJoin(result,k,tb[tbno[i]],j,match);	
				}
			}

			int attribno[]=new int[noat];
			int dcount=0;
			for(int j=0;j<noat;j++)
				for (int i=0;i<result.col;i++) 
				{
					boolean add=true;
					if(result.attrib[i].equals(join[j][1]))
					{
						for(int k=0;k<j;k++)
							if(join[j][1].equals(join[k][1]))
								add=false;
						if(add)
							attribno[dcount++]=i;
					}
					if(result.attrib[i].equals(join[j][1]))
						break;
				}
			Table fres=new Table(result.rows,dcount,"VIEW");
			for(int i=0;i<dcount;i++)
				fres.attrib[i]=result.attrib[attribno[i]];
			for(int i=0;i<result.rows;i++)
				for(int j=0;j<dcount;j++)
					fres.data[i][j]=result.data[i][attribno[j]];

			JTable jt1=new JTable(fres.data,fres.attrib);
			JFrame jf1=new JFrame("VIEW");
			jf1.add(new JScrollPane(jt1));
			jf1.setVisible(true);
			jf1.setSize(300,500);
			jf1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}
}