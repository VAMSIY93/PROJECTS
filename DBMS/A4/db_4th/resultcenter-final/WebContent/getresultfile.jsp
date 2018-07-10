<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.util.*"%>
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
	<link rel="shortcut icon" href="images/rc.jpg" type="image/x-icon"/>	
</head>

<body>
<div id="wrap"><!-- wrap starts here -->
	<jsp:include page="staffheader.jsp"></jsp:include>				
	
	<jsp:include page="staffsidebar.jsp"></jsp:include>	
		
		<div id="main">
		<%
		String schoolid = d.getfield("tblstaff", "intSchoolID", "intStaffID="+session.getAttribute("staffid")); 
		String standard = d.getfield("tblallocatedstaff", "intStandard","intAllocatedStaffID="+session.getAttribute("staffid"));
		String division = d.getfield("tblallocatedstaff", "strDiv","intAllocatedStaffID="+session.getAttribute("staffid")).trim();
		%>
	<a name="TemplateInfo"></a>
		<h1>Get result file...</h1>
			
					<form action="addStudentResult" method="post">
					<input type="hidden" name="schoolid" id="schoolid" value="<%=d.getfield("tblstaff", "intSchoolID", "intStaffID="+session.getAttribute("staffid"))%>">
					<input type="hidden" name="standard" id="standard" value="<%=standard%>">
					<input type="hidden" name="division" id="division" value="<%=division%>">
					<label>
					Note : Select your standard, division and subject in which you want to insert student result detail
					and download file to insert result detail. Add detail in this file and upload this
					same file. 
					</label>
					<br/>
					<table>
					<tr><td>Standard : </td><td><%=standard %></td></tr>	
					<tr><td>Division : </td><td><%=division %></td></tr>
					<tr><td>Subject : </td><td><select name="subjectid" id="subjectid"><%=d.getlist("tblsubject", "intSubID", "strSubjectName", "intSchoolID='"+schoolid+"' && intStandard='"+standard+"'") %></select></td></tr>
					<tr><td>Exam : </td><td><select name="examid" id="examid"><%=d.getlist("tblexams", "intExamID", "strExamTitle", "intSchoolID="+schoolid) %></select></td></tr>
					</table>
					<label><p>*Do not change any default detail.</p></label>
					<input type="submit" name="getfile" id="getfile" value="Create File" class="button">
					</form>
							
	</div>	
						
	</div>	
	
	
</div><!-- wrap ends here -->	
	
	<jsp:include page="footer.jsp"></jsp:include>
	
</body>
</html>
