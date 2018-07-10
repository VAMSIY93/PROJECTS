<%@ page language="java" contentType="text/html; charset=windows-1256"
        pageEncoding="windows-1256"%>
        <%@ page import="java.sql.*" %>
         <%@ page import="java.io.*" %>
    <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
    <html>
    <head>
    <meta http-equiv="Content-Type" content="text/html; charset=windows-1256">
    <title>Insert title here</title>
    </head>
    <body>
     <H1>Fetching Data From a Database</H1>
    <%
    String schid=session.getAttribute("schoolid").toString();
	Blob image = null;  
    byte[] imgData = null;  
    Connection con;
    String url="jdbc:mysql://localhost:3306/dbresultcenter";
    String uName="root";
    String pwd="";
    Class.forName("com.mysql.jdbc.Driver").newInstance();
        con=DriverManager.getConnection(url,uName,pwd);
        String sql="Select * from tblschoollogo where intSchoolID=(?)";
        PreparedStatement stmt=con.prepareStatement(sql);
        stmt.setString(1, schid);
        ResultSet resultset=stmt.executeQuery();
    while(resultset.next())
    {
        Blob bl = resultset.getBlob("file_path");
        byte[] pict = bl.getBytes(1,(int)bl.length());
        response.setContentType("image/jpg");
        OutputStream o = response.getOutputStream();
    	o.write(pict);
        o.flush();
        o.close();
    }
    
    
    %>
    </body>
    </html>