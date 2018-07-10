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
<link rel="stylesheet" type="text/css" href="css/jscal2.css" />
<script src="js/jscal2.js"></script>
<script src="js/en.js"></script>
<%
	data.Data d = new data.Data();
%>
<title>ResultCenter</title>
	<link rel="shortcut icon" href="images/rc.jpg" type="image/x-icon"/>	
</head>

<body>
<%
if(request.getParameter("mode")!=null && request.getParameter("mode").equals("del"))
{
	String id=request.getParameter("id");
	d.delete("tblexams","intExamId="+id);	
}
%>
<div id="wrap"><!-- wrap starts here -->
	<jsp:include page="schoolheader.jsp"></jsp:include>				
	
	<jsp:include page="schoolsidebar.jsp"></jsp:include>	
		
	<div id="main">
	<a name="TemplateInfo"></a>
		<h1>Manage Exams...</h1>
		<form name="form1" id="form1" method="post" action="InsertsData">
		<table>
<tr>
<td>Exam Title :</td><td><input type="text" id="strExamTitle" name="strExamTitle"></td>
</tr>

<tr>
<td>Start Date :</td><td><input type="text" placeholder="YYYY-MM-DD" id="dtmStartDate" name="dtmStartDate" readonly >
<button id="f_btn1">Date</button>&nbsp;<br />
  <script type="text/javascript">//<![CDATA[
      Calendar.setup({
        inputField : "dtmStartDate",
        trigger    : "f_btn1",
        onSelect   : function() { this.hide() },
        showTime   : 12,
        dateFormat : "%Y-%m-%d"
      });
    //]]></script>
</td>
</tr>

<tr>
<td>End Date :</td><td><input type="text" placeholder="YYYY-MM-DD" id="dtmEndDate" name="dtmEndDate" readonly>
<button id="f_btn2">Date</button>&nbsp;<br />
  <script type="text/javascript">//<![CDATA[
      Calendar.setup({
        inputField : "dtmEndDate",
        trigger    : "f_btn2",
        onSelect   : function() { this.hide() },
        showTime   : 12,
        dateFormat : "%Y-%m-%d"
      });
    //]]></script>
</td>
</tr>

<tr>
<td></td><td><input type="hidden" id="intSchoolID" name="intSchoolID" value="<%=session.getAttribute("schoolid")%>"></td>
</tr>
<tr><td><input type="submit" name="Submit" value="Submit"></td>
</tr>
</table>
<input type="hidden" name="tbl" value="tblexams"/> </form>
<%if(request.getParameter("mode")!=null && request.getParameter("mode").equals("update")) { %>
<%=d.geteditgrid("tblexams","exam.jsp",request.getParameter("id"))%>
<%} else { %>
<%=d.getdelgrid("tblexams","exam.jsp","intSchoolID="+session.getAttribute("schoolid")) %>
<% } %>
	</div>	
	
	
</div><!-- wrap ends here -->	
	
	<jsp:include page="footer.jsp"></jsp:include>
	
</body>
</html>
