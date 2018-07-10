<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.util.Vector" import="java.util.Enumeration"%>
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
	int cnt;
%>
<title>Manage Staff</title>
	<link rel="shortcut icon" href="images/rc.jpg" type="image/x-icon"/>	
</head>

<body>
<%
if(request.getParameter("mode")!=null && request.getParameter("mode").equals("del"))
{
	String id=request.getParameter("id");
	Vector<String> v = d.getemps("tblstaff","intStaffID", Integer.parseInt(id));
	d.delete("tbllogin","intLoginId="+Integer.parseInt(v.get(4).toString()));
	d.delete("tblstaff","intStaffId="+id);
}
%>
<div id="wrap"><!-- wrap starts here -->
	<jsp:include page="schoolheader.jsp"></jsp:include>				
	
	<jsp:include page="schoolsidebar.jsp"></jsp:include>	
		
	<div id="main">
	<a name="TemplateInfo"></a>
		<h1>Delete Staff...</h1><br/>
		
		<%=d.getOnlyDel("staff", "staff.jsp", "intSchoolID='"+session.getAttribute("schoolid")+"' && trim(strStatus)='active'") %>
					
		<h1>Confirm Staff Request...</h1><br/>
		
			
	<%
	String fld[]={"intLoginID","strFirstName","strLastName","strEmail"};
	Vector<String> v = d.selectfields("staff", fld, "intSchoolID='"+session.getAttribute("schoolid")+"' &&trim(strStatus)='block'");
	
	int i=v.size();
	for(int j=0;j<i;j=j+4)
	{
	%>
	<form action="confirm">
	<input type="hidden" name="table" id="table" value="tblstaff">
	<input type="hidden" name="loginid" id="loginid" value="<%=v.get(j).toString().trim()%>">
	<label>Name : <%=v.get(j+1).toString().trim() %>&nbsp;<%=v.get(j+2).toString().trim() %></label>
	<label>Email : <%=v.get(j+3).toString().trim()%></label>
	<input type="submit" name="confirm1" id="confirm1" value="Confirm">
	<input type="submit" name="cancel" id="cancel" value="Cancel">
	</form>
	<%} %>
	</div>	
	
	
</div><!-- wrap ends here -->	
	
	<jsp:include page="footer.jsp"></jsp:include>
	
</body>
</html>
