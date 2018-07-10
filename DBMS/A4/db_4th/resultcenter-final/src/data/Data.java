package data;

import java.sql.*;
import java.util.Calendar;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.omg.CORBA_2_3.portable.OutputStream;

public class Data
{
	Connection con;
	Statement S;
	ResultSet R;
	Statement S1;
	ResultSet R1;

	public String msg;
    private PreparedStatement P;
	
    public Data()
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			con=DriverManager.getConnection("jdbc:mysql://localhost:3306/dbresultcenter","root","");
			S=con.createStatement();
			msg="Connected";
		}
		catch(Exception E)
		{
			msg=E.getMessage();
		}
	}

    public String getdate()
	{
		String d=null; 
    	try
        {                                             
			P = con.prepareStatement("SELECT CURDATE()");
			R= P.executeQuery();
			
			if(R.next())
            {
					d=R.getDate("CURDATE()").toString();
            }
        }catch(Exception E)
        {
        	msg=E.getMessage();        	 
        }
		return d;	
	}
    
   
    
    public Vector getColumnName(String tbl)
    {
    	Vector V= new Vector(1,1);
		try
        {                                             
			String query="SELECT column_name FROM information_schema.columns WHERE table_name = '"+tbl+"' ORDER BY ordinal_position";
			System.out.println(query);
			P = con.prepareStatement(query);
			R= P.executeQuery();
			
			if(R.next())
            {
					V.add(R.getString("column_name"));
            }
        }catch(Exception E)
        {
        	msg=E.getMessage();        	 
        }
		return V;
    }
    
    public String getLastValue(String tbl,String field)
    {
            String query="Select * from "+tbl;
            String val="";
           try{
            S=con.createStatement();
             R=S.executeQuery(query);
             if(R.last())
             {
                 val=R.getString(field);
             }
            
           }catch(Exception E)
           {
        	   E.getMessage();
           }
           return val;
     }
    
	public Vector getusers(String tbl,String fld)
	{
		Vector V= new Vector(1,1);
		try
        {                                             
			String query="Select * from  "+tbl ;
			System.out.println(query);
			P = con.prepareStatement(query);
			R= P.executeQuery();
			
			if(R.next())
            {
					V.add(R.getString(fld));
            }
        }catch(Exception E)
        {
        	msg=E.getMessage();        	 
        }
		return V;	
	}

	public Vector getemps(String tbl,String fld,int userid)
	{
		Vector V= new Vector(1,1);
		try
        {                                             
			String query="Select * from  "+tbl+" where "+fld+" ="+ userid;
			System.out.println(query);
			P = con.prepareStatement(query);
			R= P.executeQuery();
			
			if(R.next())
            {
				for(int i=1;i<=16;i++)
					V.add(R.getString(i));
            }
        }catch(Exception E)
        {
        	msg=E.getMessage();        	 
        }
		return V;
	}
	
	public Vector getemps(String tbl,String cond)
	{
		Vector V= new Vector(1,1);
		try
        {                                             
			String query="Select * from  "+tbl+" where "+ cond;
			System.out.println(query);
			P = con.prepareStatement(query);
			R= P.executeQuery();
			
			if(R.next())
            {
				for(int i=1;i<=16;i++)
					V.add(R.getString(i));
            }
        }catch(Exception E)
        {
        	msg=E.getMessage();        	 
        }
		return V;
	}
	
	public String getfield(String tbl,String fld,String cond)
	{
		String fld1 = "";
		try
        {                                             
			String query="Select "+fld+" from  "+tbl+" where "+ cond;
			System.out.println(query);
			P = con.prepareStatement(query);
			R= P.executeQuery();
			
			if(R.next())
            {
				fld1=R.getString(fld);
            }
        }catch(Exception E)
        {
        	msg=E.getMessage();        	 
        }
		return fld1;
	}
	public Vector getemps(String tbl,String fld,String userid)
	{
		Vector V= new Vector(1,1);
		try
        {                                             
			String query="Select * from  "+tbl+" where "+fld+" ="+ userid;
			System.out.println(query);
			P = con.prepareStatement(query);
			R= P.executeQuery();
			
			if(R.next())
            {
				for(int i=1;i<=16;i++)
					V.add(R.getString(i));
            }
        }catch(Exception E)
        {
        	msg=E.getMessage();        	 
        }
		return V;
	}

	
	public String getdelgrid(String tbl,String url)
                       {
            String ans="<div style=\"height:300px;width:1000px;overflow:auto;\"><table border='1'>";
            try
                                {                                             
             P = con.prepareStatement("Select * from " + tbl);
                 
                 R= P.executeQuery();
            ResultSetMetaData RM= R.getMetaData();
            ans+="<tr><th>Delete</th><th>Edit</th>";
            for(int i=0;i<RM.getColumnCount();i++)
                ans+="<td>"+RM.getColumnLabel(i+1)+"</td>";
            ans+="</tr>";
                   while(R.next())
                                        {
                       String id=R.getString(1);
                       String conf="confirm(\'Sure Want to Delete?\')";
                       String del="<a href ='"+ url+"?id="+id+"&mode=del' onclick=\" return "+ conf+"\">Delete</a>"   ;
                       String upd="<a href ='" + url+ "?id="+id+"&mode=update'>Edit</a>";
                        ans+="<tr>";
                        ans+="<td>"+ del+" </td>";
                        ans+="<td>"+ upd+" </td>";
                        
                  for(int i=1;i<= R.getMetaData().getColumnCount();i++)
                        ans+="<td>"+ R.getString(i)+" </td>";
                                          }
                             }catch(Exception E)
                                 {
                                 msg=E.getMessage();
                                 }
            ans+="</table></div>";
            return ans;
                       }
   
	
   
        public String []getFields(String tbl)
        {
        	String clm[]=null;
        	try
        	{
        	P = con.prepareStatement("Select * from " + tbl);
        	R= P.executeQuery();
        	ResultSetMetaData RM= R.getMetaData();
        	clm= new String[RM.getColumnCount()];
        	for(int i=0;i<RM.getColumnCount();i++)
        	{
        		clm[i]=RM.getColumnLabel(i+1);
        	}
        	}catch(Exception E)
        	{
        		
        	}
        	return clm;
        }
        
        public String geteditgrid(String tbl,String url,String key)
        {
String ans="<div style=\"height:300px;width:1000px;overflow:auto;\"><table border='1'>";
try
                 {                                             
P = con.prepareStatement("Select * from " + tbl);
  
  R= P.executeQuery();
ResultSetMetaData RM= R.getMetaData();
ans+="<tr><th>Delete</th><th>Edit</th>";
String clm[]= new String[RM.getColumnCount()];
for(int i=0;i<RM.getColumnCount();i++)
{
	clm[i]=RM.getColumnLabel(i+1);
	ans+="<td>"+RM.getColumnLabel(i+1)+"</td>";
 
}
ans+="</tr>";
    while(R.next())
                         {
        String id=R.getString(1);
        String conf="confirm(\'Sure Want to Delete?\')";
        String del="<a href ='"+ url+"?id="+id+"&mode=del' onclick=\" return "+ conf+"\">Delete</a>"   ;
        String upd="<a href ='" + url+ "?id="+id+"&mode=update'>Edit</a>";
        if(id.equals(key))
        {
        	upd="<input type='Submit' value='Save' name='Submit'>";
        ans+="<form name='form1' action='UpdateData' method='post'>";
        ans+="<input type='hidden' name='tbl' value='"+ tbl+"'>";
        ans+="<input type='hidden' name='pg' value='"+ url+"'>";
        }
         ans+="<tr>";
         ans+="<td>"+ del+" </td>";
         ans+="<td>"+ upd+" </td>";
         
   for(int i=1;i<= R.getMetaData().getColumnCount();i++)
   {
	   if(id.equals(key))
       {
		   		ans+="<td><input type='text' name='"+ clm[i-1]+"' value='"+R.getString(i)+"'></td>";
       }
	   else
	   ans+="<td>"+ R.getString(i)+" </td>";
   }    
   ans+="</form>";
   ans+="</tr>";
   }
              }catch(Exception E)
                  {
                  msg=E.getMessage();
                  }
ans+="</table></div>";
return ans;
        }
        
        
        public String geteditgrid(String tbl,String url,String key,String cond)
        {
String ans="<div style=\"height:300px;width:650px;overflow:auto;\"><table border='1'>";
try
                 {       
	String q="Select * from " + tbl +" where "+cond;
	System.out.println("Shreysh "+q);
P = con.prepareStatement(q);
  
  R= P.executeQuery();
ResultSetMetaData RM= R.getMetaData();
ans+="<tr><th>Delete</th><th>Edit</th>";
String clm[]= new String[RM.getColumnCount()];
for(int i=0;i<RM.getColumnCount();i++)
{
	clm[i]=RM.getColumnLabel(i+1);
	ans+="<td>"+RM.getColumnLabel(i+1)+"</td>";
 
}
ans+="</tr>";
    while(R.next())
                         {
        String id=R.getString(1);
        String conf="confirm(\'Sure Want to Delete?\')";
        String del="<a href ='"+ url+"?id="+id+"&mode=del' onclick=\" return "+ conf+"\">Delete</a>"   ;
        String upd="<a href ='" + url+ "?id="+id+"&mode=update'>Edit</a>";
        if(id.equals(key))
        {
        	upd="<input type='Submit' value='Save' name='Submit'>";
        ans+="<form name='form1' action='UpdateData' method='post'>";
        ans+="<input type='hidden' name='tbl' value='"+ tbl+"'>";
        ans+="<input type='hidden' name='pg' value='"+ url+"'>";
        }
         ans+="<tr>";
         ans+="<td>"+ del+" </td>";
         ans+="<td>"+ upd+" </td>";
         
   for(int i=1;i<= R.getMetaData().getColumnCount();i++)
   {
	   if(id.equals(key))
       {
		   		ans+="<td><input type='text' name='"+ clm[i-1]+"' value='"+R.getString(i)+"'></td>";
       }
	   else
	   ans+="<td>"+ R.getString(i)+" </td>";
   }    
   ans+="</form>";
   ans+="</tr>";
   }
              }catch(Exception E)
                  {
                  msg=E.getMessage();
                  }
ans+="</table></div>";
return ans;
        }

        
        
        public String getform(String tbl,String url)
        {
String ans="<div style=\"height:300px;width:700px;overflow:auto;\"><table border='1'>";
ans+="<form name='frm"+tbl+"' action='"+url+"'>";
ans +="<input type='hidden' value='"+ tbl + "' name='tbl'>";
ans +="<table>";
try
                 {                                             
P = con.prepareStatement("Select * from " + tbl);
  
  R= P.executeQuery();
ResultSetMetaData RM= R.getMetaData();

for(int i=0;i<RM.getColumnCount();i++)
{
if(!RM.isAutoIncrement(i+1))
{
	String fld=RM.getColumnLabel(i+1);
 ans+="<tr><th>"+fld+"</th>";
ans +="<td><input type='text' name='"+ fld+"'></td>";
ans+="</tr>";
}
}
ans+="<tr><td><input type='reset' value='Cancel'></td>";
ans+="<td><input type='Submit' value='Save' name='Submit'></td></tr>";
                 }catch(Exception E)
                  {
                  msg=E.getMessage();
                  }
ans+="</form>";
ans+="</table></div>";
return ans;
        }

public String getdelgrid(String tbl,String url,String cond)
                       {
            String ans="<div style=\"height:300px;width:1000px;overflow:auto;\"><table border='1'>";
            try
                                {                                             
                                    msg+="Select * from " + tbl+" where "+cond;
                                    System.out.println(msg);
             P = con.prepareStatement("Select * from " + tbl+" where "+cond);
                 
                 R= P.executeQuery();
            ResultSetMetaData RM= R.getMetaData();
            ans+="<tr><th>Delete</th><th>Edit</th>";
            for(int i=0;i<RM.getColumnCount();i++)
                ans+="<td>"+RM.getColumnLabel(i+1)+"</td>";
            ans+="</tr>";
                   while(R.next())
                                        {
                       String id=R.getString(1);
                       String conf="confirm(\'Sure Want to Delete?\')";
                       String del="<a href ='"+ url+"?id="+id+"&mode=del' onclick=\" return "+ conf+"\">Delete</a>"   ;
                       String upd="<a href ='"+ url+ "?id="+id+"&mode=update'>Edit</a>";
                        ans+="<tr>";
                        ans+="<td>"+ del+" </td>";
                        ans+="<td>"+ upd+" </td>";
                  for(int i=1;i<= R.getMetaData().getColumnCount();i++)
                        ans+="<td>"+ R.getString(i)+" </td>";
                                          }
                             }catch(Exception E)
                                 {
                                 msg=E.getMessage();
                                 }
            ans+="</table></div>";
            return ans;
                       }
        
public String getOnlygrid(String tbl,String url,String cond)
       {
            String ans="<div style=\"height:300px;width:500px;overflow:auto;\"><table border='1'>";
            try
            {                                             
                msg+="Select * from " + tbl+" where "+cond;
                P = con.prepareStatement("Select * from " + tbl+" where "+cond);
                 
                R= P.executeQuery();
                ResultSetMetaData RM= R.getMetaData();
                ans+="<tr>";
                for(int i=0;i<RM.getColumnCount();i++)
                ans+="<td>"+RM.getColumnLabel(i+1)+"</td>";
                ans+="</tr>";
                while(R.next())
                {
                       String id=R.getString(1);
                       String conf="confirm(\'Sure Want to Delete?\')";
                       ans+="<tr>";
                       
                       for(int i=1;i<= R.getMetaData().getColumnCount();i++)
                       ans+="<td>"+ R.getString(i)+" </td>";
                }
            }catch(Exception E)
            {
                msg=E.getMessage();
            }
            ans+="</table></div>";
            return ans;
       }


public String getOnlyUp(String tbl,String url,String cond)
                       {
            String ans="<div style=\"height:300px;width:500px;overflow:auto;\"><table border='1'>";
            try
                                {                                             
                                    msg+="Select * from " + tbl+" where "+cond;
             P = con.prepareStatement("Select * from " + tbl+" where "+cond);
                 
                 R= P.executeQuery();
            ResultSetMetaData RM= R.getMetaData();
            ans+="<tr><th></th><th></th>";
            for(int i=0;i<RM.getColumnCount();i++)
                ans+="<td>"+RM.getColumnLabel(i+1)+"</td>";
            ans+="</tr>";
                   while(R.next())
                                        {
                       String id=R.getString(1);
                       String conf="confirm(\'Sure Want to Delete?\')";
                       String del="<a href ='"+ url+"?id="+id+"&mode=del' onclick=\" return "+ conf+"\">Delete</a>"   ;
                       String upd="<a href ='up" + url+ "?id="+id+"&mode=update'>Edit</a>";
                        ans+="<tr>";
                       
                        ans+="<td>"+ upd+" </td>";
                  for(int i=1;i<= R.getMetaData().getColumnCount();i++)
                        ans+="<td>"+ R.getString(i)+" </td>";
                                          }
                             }catch(Exception E)
                                 {
                                 msg=E.getMessage();
                                 }
            ans+="</table></div>";
            return ans;
                       }


public String getOnlyDel(String tbl,String url,String cond)
                       {
            String ans="<div style=\"height:auto;width:1000px;overflow:auto;\"><table border='1'>";
            try
                                {                                             
                                    msg+="Select * from " + tbl+" where "+cond;
             P = con.prepareStatement("Select * from " + tbl+" where "+cond);
                 
                 R= P.executeQuery();
            ResultSetMetaData RM= R.getMetaData();
            ans+="<tr><th>Delete</th>";
            for(int i=0;i<RM.getColumnCount();i++)
                ans+="<td>"+RM.getColumnLabel(i+1)+"</td>";
            ans+="</tr>";
                   while(R.next())
                                        {
                       String id=R.getString(1);
                       String conf="confirm(\'Sure Want to Delete?\')";
                       String del="<a href ='"+ url+"?id="+id+"&mode=del' onclick=\" return "+ conf+"\">Delete</a>"   ;
                       ans+="<tr>";
                       ans+="<td>"+ del+" </td>";
                       
                  for(int i=1;i<= R.getMetaData().getColumnCount();i++)
                        ans+="<td>"+ R.getString(i)+" </td>";
                                          }
                             }catch(Exception E)
                                 {
                                 msg=E.getMessage();
                                 }
            ans+="</table></div>";
            return ans;
                       }


public String insert(String tbl,String []fld,String []val)
{
int i;
String q="Insert into "+ tbl +"(";
	for(i=0;i<fld.length;i++)
	{
		if(i!=fld.length-1)
		q+=fld[i]+",";
		else
		q+=fld[i]+") values (";
		
	}
	
	for(i=0;i<fld.length;i++)
	{
		if(i!=fld.length-1)
		q+=" ' "+val[i]+" ' ,";
		else
		q+="'"+val[i]+"')";
		
	}
	msg=q;
	try
	{
	System.out.println(q);
	S=con.createStatement();
	S.executeUpdate(q);
	System.out.println("Inserted...");
	msg="Inserted";
	}
	catch(Exception E)
	{
	msg=E.toString();
	return q;//E.getMessage();
	}
	return msg;
}

public int getMax(String tbl,String fld)
{
    int x=0;
        try {
            
            String query="Select max("+ fld +") from " + tbl;
            R= S.executeQuery(query);
            if(R.next())
                x=Integer.parseInt(R.getString(1));
        } catch (SQLException ex) {
            msg=ex.toString();
        }
        return x;
}
public int delete(String tbl,String cond)
{
try
{
msg="Delete from "+ tbl +" Where " + cond;
return S.executeUpdate("Delete from "+ tbl +" Where " + cond);
}catch(Exception E){}
return 0;
}


public String update(String tbl,String []fld,String []val,String cond)
{
int i;
String q="update "+ tbl +" set ";
	for(i=0;i<fld.length;i++)
	{
		if(i!=fld.length-1)
		q+=fld[i]+"='" +val[i] +"',";
		else
		q+=fld[i]+"='" +val[i] +"' Where "+ cond;
	}
	
	msg=q;
	try
	{
	S.executeUpdate(q);
	}
	catch(Exception E)
	{
	msg=E.getMessage();
	return E.getMessage();
	}
	return q;
	
}


public String update(String tbl,String []fld,String []val)
{
int i;
String cond=fld[0]+" ='"+ val[0]+"'";
String q="update "+ tbl +" set ";
	for(i=1;i<fld.length;i++)
	{
		if(i!=fld.length-1)
		q+=fld[i]+"='" +val[i] +"',";
		else
		q+=fld[i]+"='" +val[i] +"' Where "+ cond;
	}
	
	msg=q;
	try
	{
	S.executeUpdate(q);
	}
	catch(Exception E)
	{
	msg=E.getMessage();
	return E.getMessage();
	}
	return q;
	
}

public String update1(String tbl,String []fld,String val,String cond)
{
int i;
String q="update "+ tbl +" set ";
	for(i=0;i<fld.length;i++)
	{
		if(i!=fld.length-1)
		q+=fld[i]+"='" +val+"',";
		else
		q+=fld[i]+"='" +val+"' Where "+ cond;
	}
	
	msg=q;
	try
	{
	S.executeQuery(q);
	}
	catch(Exception E)
	{
	//msg=E.getMessage();
	return E.getMessage();
	}
	return q;
}

public String update2(String tbl,String fld,String val,String cond)
{
int i;
String q="update "+ tbl +" set "+fld+"='" +val+"' where "+cond;
System.out.println(q);	
	msg=q;
	try
	{
	S.executeUpdate(q);
	System.out.println("dsfhksdh");
	}
	
	catch(Exception E)
	{
	//msg=E.getMessage();
	return E.getMessage();
	}
	return q;
}

String updatepay(String tbl,String []fld,int []val,String cond)
{
int i;
String q="update "+ tbl +" set ";
	for(i=0;i<fld.length;i++)
	{
		if(i!=fld.length-1)
		q+=fld[i]+"='" +val[i] +"',";
		else
		q+=fld[i]+"='" +val[i] +"' Where "+ cond;
	}
	
	msg=q;
	try
	{
	S.executeQuery(q);
	}
	catch(Exception E)
	{
	msg=E.getMessage();
	return E.getMessage();
	}
	return q;
}

public Vector<String> select(String tbl,String cond)
{
Vector<String> V=null;
try
{
R=S.executeQuery("Select * from "+ tbl +" Where "+ cond);
ResultSetMetaData meta= R.getMetaData();
int c=meta.getColumnCount();
V= new Vector<String>(c*R.getFetchSize());
while(R.next())
{
	for(int i=1;i<=c;i++)
	{
	V.add(R.getString(i));
	}
}
}catch(Exception e)
{
}
return V;
}



public Vector<String> selectedit(String tbl)
{
Vector<String> V=null;
try
{
R=S.executeQuery("Select * from "+ tbl );
ResultSetMetaData meta= R.getMetaData();
int c=meta.getColumnCount();
V= new Vector<String>(c*R.getFetchSize());
while(R.next())
{
	for(int i=1;i<=c;i++)
	{
	V.add(R.getString(i));
	}
}
}catch(Exception e)
{
}
return V;
}


public Vector<String> selectfields(String tbl,String fld[],String cond)
{
//msg="Callede";
Vector<String> V=new Vector<String>(1,1);

int i;
String q=" Select ";
String m=null;
for(i=0;i<fld.length;i++)
	{
		if(i!=fld.length-1)
		q+=fld[i]+",";
		else
		q+=fld[i]+ " from " + tbl + " Where "+ cond;	
	}
try
{
msg=q;
System.out.println(q);
R=S.executeQuery(q);
ResultSetMetaData meta= R.getMetaData();
int c=meta.getColumnCount();

while(R.next())
{
    msg="Data Foudned";
	for( i=1;i<=c;i++)
	{
	V.add(R.getString(i));
        System.out.println(R.getString(i));
	}
}
}catch(Exception e)
{
msg="Error" +e.getMessage();

}
return V;
}

public Vector<String> selectfields1(String tbl,String fld[],boolean cond)
{
Vector<String> V=new Vector<String>(1,1);
int i;
String q=" Select ";

for(i=0;i<fld.length;i++)
	{
		if(i!=fld.length-1)
		q+=fld[i]+",";
		else
		q+=fld[i]+ " from " + tbl + " Where "+ cond;	
	}
try
{
R=S.executeQuery(q);
ResultSetMetaData meta= R.getMetaData();
int c=meta.getColumnCount();
//V= new Vector(c*R.getFetchSize());
while(R.next())
{
	for( i=1;i<=c;i++)
	{
	V.add(R.getString(i));
	}
}
}catch(Exception e)
{
}
return V;
}

public String  getlist(String tbl,String f1,String f2,String cond)
{
String op="";
try
{
msg="Select "+f1+","+f2+" from "+ tbl +" Where "+ cond;
R=S.executeQuery("Select trim("+f1+"),trim("+f2+") from "+ tbl +" Where "+ cond);

while(R.next())
{
op+="<option value ='"+ R.getString(1)+"'> "+ R.getString(2) +"</option>"; 
}
}catch(Exception e)
{
op=e.getMessage();
}
return op;
}



public int cnt (String tbl)
{
int c=0;
try
{

R=S.executeQuery ("Select count(*) from "+ tbl );
if(R.next())
	c=R.getInt(1);
	msg=" Founded"+ c;
}
catch(Exception e)
{
msg=e.getMessage();
}
return c;
}

public String convertdate(String date)
{
    String datearray[]=date.split("-");
    String cdate[]={datearray[2],datearray[1],datearray[0]};
    String month[]={"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
    String converteddate=datearray[2]+" "+month[Integer.parseInt(datearray[1])-1]+" "+datearray[0];
    System.out.println("new date"+converteddate);
    return converteddate;
}

public String insertdate(String date)
{
    String datearray[]=date.split("/");
    String month[]={"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
    String converteddate=datearray[1]+"-"+month[Integer.parseInt(datearray[0])-1]+"-"+datearray[2];
    return converteddate;
}
public String gettoday()
{
	Calendar C= Calendar.getInstance();
	String dt= C.get(Calendar.YEAR)+"-"+C.get(Calendar.MONTH) +"-"+C.get(Calendar.DAY_OF_MONTH);
	return dt;
}


public String formatt()
{
	int i=0;
	String ans="";
	String query="Select * from tblemppersonal";
ans+="<form name='attend' method='post' action='AttendServe'>";
ans +="<table>";
ans+="<tr><th>Employee ID </th<th>Name of Employee</th><th>Status</th><th>Notes</th></tr>";
	try
	{
		
	R=S.executeQuery(query);

	while(R.next())
	{
		ans+="<tr>";
		ans+="<td><input type='text' readonly='readonly' name='txtid"+i+"' value='"+ R.getString(1)+"'></td>";
		ans+="<td><input type='text' readonly='readonly' name='txtnm"+i+"' value='"+ R.getString(3)+" "+R.getString(5)+"'></td>";
		ans+="<td><input type='text'  name='txtnt"+i+"' ></td>";
		ans+="<td><select name='sel"+i+"'><option value='1'>Present</option><option value='0'>Leave</option><option value='0.5'>Half Leave</option></select></td>";
	i++;
	}
	}catch(Exception E)
	{
		
	}
	ans+="<tr><td></td><td colspan='3'><input type='Submit' name='Submit' value='Save'></td></tr>";
	ans+="</table>";
	ans+="<input type='hidden' name='count' value='"+i+"'>";
	ans+="<form>";
	return ans;
}



public String formsal()
{
	int i=0;

	String ans="";
	String query="Select * from tblemppersonal";

	ans+="<div style=\"height:300px;width:700px;overflow:auto;\">";
ans +="<table>";
ans+="<tr><th>Employee ID </th><th>Name of Employee</th><th>Basic</th><th>DA</th>";
ans+="<th>HRA </th><th>Other Allowance</th><th>Total Allowances</th><th>PF</th>";
ans+="<th>Other Deduction </th><th>Total Deduction</th><th>Net Salary</th><th>Notes</th></tr>";

	try
	{
		Statement S1= con.createStatement();		
	R=S.executeQuery(query);

	while(R.next())
	{
		double basic=0,da=0,hra=0,pf=0,all=0,ded=0;
		double basic1=0,da1=0,hra1=0,pf1=0,all1=0,ded1=0,totalall=0,totalded=0,net=0;
		String query1="Select * from tblemplsalary where EmpId ='"+ R.getString(1)+"'";
		ResultSet R1= S1.executeQuery(query1);
		if(R1.next())
		{
			basic= R1.getDouble(4);
			da= R1.getDouble(5);
			hra= R1.getDouble(6);
			pf=R1.getDouble(7);
			all=R1.getDouble(8);
			ded=R1.getDouble(9);
			
			da1= basic * da/100;
			hra1=basic * hra/100;
			pf1= basic *pf/100;
			
			totalall= basic + da1+ hra1+all;
			totalded= pf1+ded;
			
			net=totalall-totalded;
		}
		R1.close();
		ans+="<tr>";
		ans+="<td><input type='text' readonly='readonly' name='txtid"+i+"' value='"+ R.getString(1)+"'></td>";
		ans+="<td><input type='text' readonly='readonly' name='txtnm"+i+"' value='"+ R.getString(3)+" "+R.getString(5)+"'></td>";
		ans+="<td><input type='text'  name='txtbasic"+i+"' value='"+ basic+"'></td>";
		ans+="<td><input type='text'  name='txtda"+i+"' value='"+ da1+"'></td>";
		ans+="<td><input type='text'  name='txthra"+i+"' value='"+ hra1+"'></td>";
		ans+="<td><input type='text'  name='txtall"+i+"' value='"+ all+"'></td>";
		ans+="<td><input type='text'  name='txtttlall"+i+"' value='"+ totalall+"'></td>";
		ans+="<td><input type='text'  name='txtpf"+i+"' value='"+ pf1+"'></td>";
		ans+="<td><input type='text'  name='txtded"+i+"' value='"+ ded+"'></td>";
		ans+="<td><input type='text'  name='txtttlded"+i+"' value='"+ totalded+"'></td>";
		ans+="<td><input type='text'  name='txtnet"+i+"' value='"+ net+"'></td>";
		ans+="<td><input type='text'  name='txtnotes"+i+"' ></td>";
	i++;
	}
	}catch(Exception E)
	{
		msg=E.toString();
	}
	ans+="<tr><td></td><td colspan='3'><input type='Submit' name='Submit' value='Save'></td></tr>";
	ans+="</table>";
	ans+="<input type='hidden' name='count' value='"+i+"'>";
ans+="</div>";
	return ans;
}


}
    

