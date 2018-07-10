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
<title>Manage Security Question</title>
	<link rel="shortcut icon" href="images/rc.jpg" type="image/x-icon"/>	
</head>

<body>
<%
if(request.getParameter("mode")!=null && request.getParameter("mode").equals("del"))
{
	String id=request.getParameter("id");
	d.delete("tblsecquestion","intSecquestionId="+id);	
}
%>
<div id="wrap"><!-- wrap starts here -->
	<jsp:include page="adminheader.jsp"></jsp:include>				
	
	<jsp:include page="adminsidebar.jsp"></jsp:include>	
		
	<div id="main">
	<a name="TemplateInfo"></a>
		<h1>Manage Security Question...</h1>
		<form name="form1" id="form1" method="post" action="InsertsData">
<table>
<tr>
<td>SecQuestion:</td>
<td><input type="text" name="strSecquestion" id="strSecquestion" ></td>
</tr>
<tr>
<td><input type="submit" value="Submit"></td>
</tr>
</table>
<input type="hidden" name="tbl" value="tblsecquestion"/> </form>
<%if(request.getParameter("mode")!=null && request.getParameter("mode").equals("update")) { %>
<%=d.geteditgrid("tblsecquestion","secquestion.jsp",request.getParameter("id"))%>
<%} else { %>
<%=d.getdelgrid("tblsecquestion","secquestion.jsp") %>
<% } %>
	</div>	
	
	
</div><!-- wrap ends here -->	
	
	<jsp:include page="footer.jsp"></jsp:include>
	
</body>
</html>
