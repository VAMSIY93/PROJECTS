<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.util.*"%>
     <%@ page import="java.sql.*" %>
         <%@ page import="java.io.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>

<meta name="Description" content="Information architecture, Web Design, Web Standards." />
<meta name="Keywords" content="your, keywords" />
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="Distribution" content="Global" />
<meta name="Author" content="Erwin Aligam - ealigam@gmail.com" />
<meta name="Robots" content="index,follow" />		

<link rel="stylesheet" href="css/main.css" type="text/css" />
<%if(session.getAttribute("emailid")==null)
{
	response.sendRedirect("login.jsp");
}
%>
<title>ResultCenter</title>
	<link rel="shortcut icon" href="images/rc.jpg" type="image/x-icon"/>	
</head>
<%
	data.Data d = new data.Data();
%>
<body>
<div id="wrap"><!-- wrap starts here -->
	<jsp:include page="schoolheader.jsp"></jsp:include>				
	
	<jsp:include page="schoolsidebar.jsp"></jsp:include>	
		
	<div id="main">
	<a name="TemplateInfo"></a>
		<h1>Welcome...</h1>
			<table border="1">
			<%
			String path=d.getfield("tblschoollogo", "strLogo", "intSchoolID="+session.getAttribute("schoolid")).trim();
			%>
			<tr>
			<td rowspan="5">
			<img src="image/<%=session.getAttribute("schoolname") %>.jpg" alt="<%=session.getAttribute("schoolname") %>"width="250px" height="250px">
			</td>
			<td>School ID  :</td><td><%=session.getAttribute("schoolid") %></td></tr>
			<tr><td>School Name  :</td><td><%=session.getAttribute("schoolname") %></td></tr>
			<tr><td>Email ID  :</td><td><%=session.getAttribute("emailid") %></td></tr>
			<tr><td>Role  :</td><td><%=session.getAttribute("role") %></td></tr>
			<tr><td>Last Login Date  :</td><td><%=session.getAttribute("lastlogindate") %></td></tr>
			</table>
			<%
			if(path.isEmpty())
			{
			%>
			<form action="ProdImg1" method="post" enctype="multipart/form-data">
			<label>School Logo : </label>
			<input type="hidden" name="schid" value="<%=session.getAttribute("schoolid") %>">
			<input type="file" name="logo">
			<input type="submit" name="addimg" id="addimg" value="Upload">
			</form>
			<%} %>		
	</div>	
	
	
</div><!-- wrap ends here -->	
	
	<jsp:include page="footer.jsp"></jsp:include>
	
</body>
</html>
