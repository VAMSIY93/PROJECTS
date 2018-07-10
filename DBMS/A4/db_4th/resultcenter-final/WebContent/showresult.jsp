<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.util.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%
	data.Data d = new data.Data();
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
		<form action="" method="post">
			<label>Select school:</label>
			<select name="school">
			<option name="">--Seclect School--</option>
			<%=d.getlist("tblschool", "intSchoolID", "strSchoolName", "1=1") %>
			</select>
			<input type="submit" id="submit" value="Search">
		</form>		
		<form action="result.jsp">
		<table border="1">
		
		<%
		String schoolid=request.getParameter("school");
		String loginid=d.getfield("tblschool", "intLoginID", "intSchoolID='"+schoolid+"'");
		String []fld = {"intExamID","strDiv","intStandard","dtmDateofResult","intLoginID"};
		String date = d.getdate();
		Vector<String> v = d.selectfields("tblresultdates", fld, "dtmDateofResult <= '"+date+"' && intLoginID='"+loginid+"'");
		
		int i = v.size();
		System.out.println("Shreyash"+i);
		for(int j=0;j<i;j=j+5)
		{
		%>
		<%
		String examid = v.get(j).toString().trim();
		String standard = v.get(j+2).toString().trim();
		String division = v.get(j+1).toString().trim();
		String examname = d.getfield("tblexams", "strExamTitle", "intExamID='"+examid+"'");
		String schoolname = d.getfield("tblschool", "strSchoolName", "intSchoolID='"+schoolid+"'");
		%>
		<tr>
		<td><%=v.get(j+3) %></td>
		<td width="400px">standard :<%=v.get(j+2)%>-<%=v.get(j+1)%>&nbsp;<%=examname %></td>
		<td><input type="submit" name="submit" value="<%=v.get(j).toString().trim()%>"></td>
		</tr>
		<input type="hidden" name="schid" value="<%=schoolid %>"> 
		<input type="hidden" name="schname" value="<%=d.getfield("tblschool", "strSchoolName", "intSchoolID='"+schoolid+"'")%>">
		<input type="hidden" name="examid" value="<%=examid %>">
		<input type="hidden" name="examname" value="<%=examname %>">
		<input type="hidden" name="division" value="<%=division %>">
		<input type="hidden" name="standard" value="<%=standard %>">
		
		<%} %>
		</table>
		
		
		</form>
		
	</div>	
	
</div><!-- wrap ends here -->	
	
	<jsp:include page="footer.jsp"></jsp:include>
</body>
</html>