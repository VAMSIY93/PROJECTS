<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<%
	data.Data d = new data.Data();
%>
<link rel="stylesheet" type="text/css" href="css/jscal2.css" />
<link rel="stylesheet" href="css/main.css" type="text/css" />
<link rel="stylesheet" href="css/validationEngine.jquery.css" type="text/css" media="screen" title="no title" charset="utf-8" />
<script src="js/jscal2.js" type="text/javascript"></script>
<script src="js/en.js "type="text/javascript"></script>
<script src="js/jquery-1.4.min.js" type="text/javascript"></script>
<script src="js/jquery.validationEngine-en.js" type="text/javascript"></script>
<script src="js/jquery.validationEngine.js" type="text/javascript"></script>
	
			<script>	
		$(document).ready(function() {
			 	$("#form1").validationEngine()	
		});</script>
<title>Registration Page</title>

</head>
<body>
<div id="wrap"><!-- wrap starts here -->
	<jsp:include page="header.jsp"></jsp:include>				
	
	<jsp:include page="sidebar.jsp"></jsp:include>	
		
	<div id="regester">
			
		<a name="Registration Form"></a>
		<h1>Registration Form</h1>
		<form name="form1" id="form1" style="width:400px" method="post" action="regserv">
			<p>				
			<label>School Name:</label>
			<select name="intSchoolID" id="intSchoolID" class="validate[required]"><%=d.getlist("tblschool", "intSchoolID", "strSchoolName", "1=1") %></select>
			<label>First Name:</label>
			<input type="text" name="strFirstName" id="strfirstName" class="validate[required,length[2,20]]"/>
			<label>Last Name:</label>
			<input type="text" name="strLastName" id="strLastName" class="validate[required,length[2,20]]" />
			<label>Mobile No.:</label>
			<input type="text" name="strMobileNo" id="strMobileNo" class="validate[required,custom[onlyNumber],length[10,10]]" />
			<label>Designation:</label>
			<input type="text" name="strDesignation" id="strDesignation" class="validate[required]" />
			<label>Email:</label>
			<input type="text" name="strEmail" id="strEmail" class="validate[required,custom[email]]"/>
			<label>Password:</label>
			<input type="password" name="strPassword" id="strPassword" class="validate[required,length[6,20]] text-input" />
			<label>Confirm Password:</label>
			<input type="password" name="strConfirmPass" id="strConfirmPass" class="validate[required,confirm[strPassword]] text-input" />
			<label>Security Question:</label>
			<select name="intSecquestionID" id="intSecquestionID" class="validate[required]"><%=d.getlist("tblsecquestion", "intSecquestionID", "strSecquestion", "1=1") %></select>
			<label>Answer:</label>
			<input type="text" name="strAnswer" id="strAnswer" class="validate[required]" /><br/><br/>
			<input type="checkbox" name="terms" id="terms" class="validate[required] checkbox"/>&nbsp;&nbsp;I accept <a href="terms.jsp">terms & condition</a>.<br/><br/>
			<input type="submit" name="submitstaff" id="submitstaff" value="Register" class="button">
			</p>		
		</form>
							
	</div>	
	
</div><!-- wrap ends here -->	
	
	<jsp:include page="footer.jsp"></jsp:include>
</body>
</html>