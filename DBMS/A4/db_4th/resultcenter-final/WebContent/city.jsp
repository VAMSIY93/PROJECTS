<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.util.Vector"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

<meta name="Description" content="Information architecture, Web Design, Web Standards." />
<meta name="Keywords" content="your, keywords" />
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="Distribution" content="Global" />
<meta name="Author" content="Erwin Aligam - ealigam@gmail.com" />
<meta name="Robots" content="index,follow" />		
<%if(session.getAttribute("emailid")==null)
{
	response.sendRedirect("login.jsp");
}
%>
<link rel="stylesheet" href="css/main.css" type="text/css" />
<%
	data.Data d = new data.Data();
%>
<title>Manage City</title>
	<link rel="shortcut icon" href="images/rc.jpg" type="image/x-icon"/>	
</head>

<body>
<%
if(request.getParameter("mode")!=null && request.getParameter("mode").equals("del"))
{
	String id=request.getParameter("id");
	d.delete("tblcity","intCityId="+id);	
}
%>
<div id="wrap"><!-- wrap starts here -->
	<jsp:include page="adminheader.jsp"></jsp:include>				
	
	<jsp:include page="adminsidebar.jsp"></jsp:include>	
		
	<div id="main">
	<a name="TemplateInfo"></a>
		<h1>Manage City...</h1>
		<form name="city" id="city" method="post" action="InsertsData">
<input type="hidden" name="tbl" id="tbl" value="tblcity">
<table>
<tr><td>City Name:</td><td><input type="text" name="strCityName" id="CityName" ></td></tr>
<tr><td><input type="submit" id="submit" value="Submit"></td>
</tr>
</table>
</form>
<%if(request.getParameter("mode")!=null && request.getParameter("mode").equals("update")) { %>
<%=d.geteditgrid("tblcity","city.jsp",request.getParameter("id"))%>
<%} else { %>
<%=d.getdelgrid("tblcity","city.jsp") %>
<% } %>
					
	</div>	
	
	
</div><!-- wrap ends here -->	
	
	<jsp:include page="footer.jsp"></jsp:include>
	
</body>
</html>
