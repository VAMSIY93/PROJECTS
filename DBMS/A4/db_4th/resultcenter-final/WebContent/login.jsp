<%@page import="java.util.*" import="data.Data"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%
Data d = new Data();
%>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>ResultCenter</title>
<script>	
		$(document).ready(function() {
			$("#formID").validationEngine()
		});
		
	
		</script>
</head>
<body>
<div id="wrap"><!-- wrap starts here -->
	<jsp:include page="header.jsp"></jsp:include>				
	
	<jsp:include page="sidebar.jsp"></jsp:include>	
		
	<div id="regester">
			
		<a name="Log In"></a>
		<div style="width:300px;float:left;">
		<h1>Log In Here...</h1>
		<form action="LoginServe" style="width:225px" method="post" id="formID">		
			<p>				
			<label>Email:</label>
			<input name="email" type="text" size="30" />
			<label>Password:</label>
			<input name="password" type="password" size="30" />
			<label><a href="forgetpass.jsp">Forget Password</a></label>
			<input class="button" type="submit" value="Login"/>		
			</p>		
		</form>
		<center><a href="showresult.jsp"><input type="button" value="SHOW RESULT" class="bigbutton"></a></center>
		</div>
		<div style="width:350px;float:left;">
		<a name="Log In"></a>
		<h1>Up coming Results...</h1>
		<form style="height:250px">
		<%
		String []fld = {"intExamID","strDiv","intStandard","dtmDateofResult","intLoginID"};
		String date = d.getdate();
		Vector<String> v = d.selectfields("tblresultdates", fld, "dtmDateofResult > '"+date+"'");
		
		int i = v.size();
		System.out.println("Shreyash"+i);
		for(int j=0;j<i;j=j+5)
		{
		%>
		<label>
		<%
		String examid = v.get(j).toString().trim();
		String examname = d.getfield("tblexams", "strExamTitle", "intExamID='"+examid+"'");
		String schoolid = d.getfield("tblexams", "intSchoolID", "intExamID='"+examid+"'");
		String schoolname = d.getfield("tblschool", "strSchoolName", "intSchoolID='"+schoolid+"'");
		%>
		standard :<%=v.get(j+2)%> division : <%=v.get(j+1)%>&nbsp;<%=examname %>&nbsp;result of&nbsp;
		<%=schoolname %>&nbsp;will be declared on <%=v.get(j+3) %><br/>
		---------------------------------------------------
		</label>
		<%} %>
		</form>
		</div>
							
	</div>	
	
</div><!-- wrap ends here -->	
	
	<jsp:include page="footer.jsp"></jsp:include>
</body>
</html>