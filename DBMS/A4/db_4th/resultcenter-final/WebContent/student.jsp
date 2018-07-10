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
<link rel="stylesheet" href="css/main.css" type="text/css" />
<%if(session.getAttribute("emailid")==null)
{
	response.sendRedirect("login.jsp");
}
%>
<%
	data.Data d = new data.Data();
%>
<title>Manage Student</title>
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
		<h1>Add Student Detail...</h1>
					
					<form action="addStudentDetail" method="post">
					<input type="hidden" name="schoolname" id="schoolname" value="<%=session.getAttribute("schoolname") %>">
					<input type="hidden" name="schoolid" id="schoolid" value="<%=session.getAttribute("schoolid") %>">
					<label>
					Note : Select your standard and division in which you want to insert student detail
					and download file to insert student detail. Add detail in this file and upload this
					same file. 
					</label>
					<br/>
					Standard : 	<input type="text" name="standard" id="standard" style="width:35px;">
					Division : <input type="text" name="division" id="division" style="width:35px;">
					No. of Student you want to add : <input type="text" name="no" id="no" style="width:35px;">
					<label><p>*Do not change any default detail.</p></label>
					<input type="submit" name="getfile" id="getfile" value="Create File" class="button">
					</form>
					
	
	<a name="TemplateInfo"></a>
		<h1>Show Student Detail...</h1>
		<form name="form1" id="form1" action="">
		Standard : 	<select name="standard" id="standard" style="width:35px;">
		<option value=""></option>
		<%=d.getlist("tblstandard", "intstandard", "intstandard", "1=1") %>
		</select>
		Division : <select name="division" id="division" style="width:35px;">
		<option value=""></option>
		<%=d.getlist("tbldivision", "strDiv", "strDiv", "1=1") %>
		</select>
		<input type="submit" name="submit" value="Show" class="button"/>
		</form>
		<%if(request.getParameter("mode")!=null && request.getParameter("mode").equals("update")) { %>
<%=d.geteditgrid("tblschoolstudents","student.jsp",request.getParameter("id"),"intSchoolID='"+session.getAttribute("schoolid")+"' && intStandard='"+request.getParameter("standard")+"' && trim(strDivision)='"+request.getParameter("division")+"'")%>
<%} else { %>
<%=d.getdelgrid("tblschoolstudents","student.jsp","intSchoolID='"+session.getAttribute("schoolid")+"' && intStandard='"+request.getParameter("standard")+"' && trim(strDivision)='"+request.getParameter("division")+"'")%>
<% } %>
	</div>	
	
	
</div><!-- wrap ends here -->	
	
	<jsp:include page="footer.jsp"></jsp:include>
	
</body>
</html>
