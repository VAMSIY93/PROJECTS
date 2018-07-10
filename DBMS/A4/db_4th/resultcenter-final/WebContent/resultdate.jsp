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
<link rel="stylesheet" href="css/main.css" type="text/css" />
<link rel="stylesheet" type="text/css" href="css/jscal2.css" />
<script src="js/jscal2.js"></script>
<script src="js/en.js"></script>
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
	d.delete("tblresultdates","intDateID="+id);	
}
%>
<div id="wrap"><!-- wrap starts here -->
	<jsp:include page="schoolheader.jsp"></jsp:include>				
	
	<jsp:include page="schoolsidebar.jsp"></jsp:include>	
		
	<div id="main">
	<a name="TemplateInfo"></a>
		<h1>Manage Result Date...</h1>
<form action="InsertsData" method="post" id="form1" name="form1">

<table>
<tr><td><input type="hidden" id="intLoginID" name="intLoginID" value="<%=session.getAttribute("loginid")%>"></td></tr>
<tr><td><input type="hidden" id="dtmUpdatedDate" name="dtmUpdatedDate" value="<%=d.getdate()%>"></td></tr>
<tr><td>Exam Name :</td><td><select id="intExamID" name="intExamID"><%=d.getlist("tblexams", "intExamID", "strExamTitle", "1=1") %></select></td></tr>
<tr><td>Division :</td><td><select id="strDiv" name="strDiv"><%=d.getlist("tbldivision", "strDiv", "strDiv", "1=1") %></select></td></tr>
<tr><td>Standard :</td><td><select id="intStandard" name="intStandard"><%=d.getlist("tblstandard", "intStandard", "intStandard", "1=1") %></select></td></tr>
<tr><td>Date of Result :</td><td><input type="text" placeholder="YYYY-MM-DD" id="dtmDateofResult" name="dtmDateofResult">
<button id="f_btn1">Date</button>&nbsp;<br />
  <script type="text/javascript">//<![CDATA[
      Calendar.setup({
        inputField : "dtmDateofResult",
        trigger    : "f_btn1",
        onSelect   : function() { this.hide() },
        showTime   : 12,
        dateFormat : "%Y-%m-%d"
      });
    //]]></script></td>
</tr>
<tr><td><input type="submit" id="submit" value="Submit"></td></tr>
</table>
<input type="hidden" name="tbl" value="tblresultdates"/> </form>
<%if(request.getParameter("mode")!=null && request.getParameter("mode").equals("update")) { %>
<%=d.geteditgrid("tblresultdates","resultdate.jsp",request.getParameter("id"))%>
<%} else { %>
<%=d.getdelgrid("tblresultdates","resultdate.jsp","intLoginID="+session.getAttribute("loginid")) %>
<% } %>
					
	</div>	
	
	
</div><!-- wrap ends here -->	
	
	<jsp:include page="footer.jsp"></jsp:include>
	
</body>
</html>
