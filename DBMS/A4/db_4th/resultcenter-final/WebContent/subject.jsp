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
<title>Manage Subject</title>
	<link rel="shortcut icon" href="images/rc.jpg" type="image/x-icon"/>	
</head>

<body>
<%
if(request.getParameter("mode")!=null && request.getParameter("mode").equals("del"))
{
	String id=request.getParameter("id");
	d.delete("tblsubject","intSubId="+id);	
}
%>
<div id="wrap"><!-- wrap starts here -->
	<jsp:include page="schoolheader.jsp"></jsp:include>				
	
	<jsp:include page="schoolsidebar.jsp"></jsp:include>	
		
	<div id="main">
	<a name="TemplateInfo"></a>
		<h1>Manage Subject...</h1>
		<form name="subject" id="subject" method="post" action="InsertsData">
<input type="hidden" name="tbl" id="tbl" value="tblsubject">

<table>
<tr><td></td><td><input type="hidden" name="intSchoolID" id="intSchoolID" value="<%=session.getAttribute("schoolid") %>"></td></tr>
<tr><td>Subject Name:</td><td><input type="text" name="strSubjectName" id="strSujectName" ></td></tr>
<tr><td>Subject Type:</td><td>
<select name="strSubjectType" id="strSubjectType" >
<option value="compalsary">compalsary</option>
<option value="optional">optional</option>
</select></td></tr>
<tr><td>Standard:</td><td><select name="intStandard" id="intStandard"><%=d.getlist("tblstandard", "intStandard", "intStandard","1=1") %></select></td></tr>
<tr><td><input type="submit" id="submit" value="Submit"></td>	
</tr>
</table>
</form>
<%if(request.getParameter("mode")!=null && request.getParameter("mode").equals("update")) { %>
<%=d.geteditgrid("tblsubject","subject.jsp",request.getParameter("id"),"intSchoolID="+session.getAttribute("schoolid"))%>
<%} else { %>
<%=d.getdelgrid("tblsubject","subject.jsp","intSchoolID="+session.getAttribute("schoolid")) %>
<% } %>
					
	</div>	
	
	
</div><!-- wrap ends here -->	
	
	<jsp:include page="footer.jsp"></jsp:include>
	
</body>
</html>
