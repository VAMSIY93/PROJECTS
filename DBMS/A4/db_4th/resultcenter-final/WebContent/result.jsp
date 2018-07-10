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
	
		
	<div id="regester" style="width:800px">
	<%
	String schid=request.getParameter("schid");
	String schoolname=request.getParameter("schname");
	String examname=request.getParameter("examname");
	String examid=request.getParameter("examid");
	String standard=request.getParameter("standard");
	String division=request.getParameter("division");
	%>
			<center>
			<form action="" method="post">
			<input type="hidden" name="schid" value="<%=schid %>"> 
			<input type="hidden" name="division" value="<%=division %>">
			<input type="hidden" name="standard" value="<%=standard %>">
			<input type="hidden" name="examid" value="<%=examid %>">
		
				Enter Your Gr. No. : <input type="text" name="grno" id="grno">
				<input type="submit" name="submit" value="Search" class="button">
			</form>
			<%
			String grno=request.getParameter("grno");
						
			String firstname=d.getfield("tblschoolstudents", "strFirstName", "trim(intGrNo)="+grno+" && trim(intSchoolID)="+schid+" && trim(intStandard)="+standard+" && trim(strDivision)='"+division+"'");
			String lastname=d.getfield("tblschoolstudents", "strLastName", "trim(intGrNo)="+grno+" && trim(intSchoolID)="+schid+" && trim(intStandard)="+standard+" && trim(strDivision)='"+division+"'");
			
			%>
				<table border=1 width="500px">
				<tr><td rowspan="4"><img src="image/<%=schoolname %>.jpg" alt="<%=schoolname %>"width="100px" height="100px">
				</td><td>School Name :</td><td> <%=schoolname %></td></tr>
				<tr><td>Exam Name :</td><td> <%=examname %></td></tr>
				<tr><td>Standard :</td><td> <%=standard %></td></tr>
				<tr><td>Division :</td><td> <%=division %></td></tr>
				<tr><td>Gr. No. :</td><td colspan="2"> <%=grno %></td></tr>
				<tr><td>Student Name :</td><td colspan="2"> <%=firstname %>&nbsp;<%=lastname %></td></tr>
				<tr><th>Subject Name</th><th>Marks</th><th></th></tr>		
				<%
				String []fld={"intSubjectID","intMarks","strStatus"};
				Vector<String> v = d.selectfields("tblconvertedresult", fld, "trim(intStudentID)='"+grno+"' && trim(intExamID)='"+examid+"'");
				int i=v.size();
				String subjectid;
				int a=0,b=0;
				String status="";
				for(int j=0;j<i;j=j+3)
				{
					String subject=d.getfield("tblsubject", "strSubjectName", "intSubID="+v.get(j).trim());
					System.out.println("subject:"+subject);
					%><tr><td><%=subject%></td><% 
					%><td><%=v.get(j+1)%></td><% 
					%><td><%=v.get(j+2)%></td></tr><% 
					if(v.get(j+2).toString().trim().matches("fail"))
					{
						status="fail";
						
					}
					else
					{
						if(status!="fail")
						{
						status="pass";
						}
					}
					a=a+100;
					int marks=Integer.parseInt(v.get(j+1).toString().trim());
					b=b+marks;
				}
				double per=0;
				if(a!=0)
				{
				double c = b / a;
				per = 100 * b / a;
				}
				
				%>
				<tr><th colspan="2">Total</th><th><%=b %></th></tr>
				<tr><th colspan="2">Percentage</th><th><%=per %></th></tr>
				<tr><th colspan="3">
				<%
				if(status=="fail")
				{
					%><label style="color:#f00">Sorry, You are fail.</label><%
				}
				if(status=="pass")
				{
					%><label style="color:#1B992A">Congratulation, You are pass.</label><%
				}
				%>	
				</table>
</center>
<br/>
	</div>	
	
</div><!-- wrap ends here -->	
	
	<jsp:include page="footer.jsp"></jsp:include>
</body>
</html>