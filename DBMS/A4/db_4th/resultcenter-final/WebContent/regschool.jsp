<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

<link rel="stylesheet" href="css/main.css" type="text/css" />
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
		<form name="form1" id="form1" style="width:400px" action="regserv" method="post">
			<p>				
			<label>School Name:</label>
			<input type="text" name="strSchoolName" id="strSchoolName" class="validate[required]"/>
			<label>Address:</label>
			<textarea name="strAddress" id="strAddress" rows="5" cols="5" class="validate[required]"></textarea>
			<label>City:</label>
			<select name="intCityID" id="intCityID" class="validate[required]"><%=d.getlist("tblcity", "intCityID", "strCityName", "1=1") %></select>
			<label>School Registration No.:</label>
			<input type="text" name="strSchoolRegNo" id="strSchoolRegNo" class="validate[required]"/>
			<label>Email:</label>
			<input type="text" name="strEmail" id="strEmail" class="validate[required,custom[email]]"/>
			<label>Password:</label>
			<input type="password" name="strPassword" id="strPassword" class="validate[required,length[6,20]] text-input"/>
			<label>Confirm Password:</label>
			<input type="password" name="strConfirmPass" id="strConfirmPass" class="validate[required,confirm[strPassword]] text-input"/>
			<label>Security Question:</label>
			<select name="intSecquestionID" id="intSecquestionID"><%=d.getlist("tblsecquestion", "intSecquestionID", "strSecquestion", "1=1") %></select>
			<label>Answer:</label>
			<input type="text" name="strAnswer" id="strAnswer" class="validate[required]"/><br/><br/>
			<input type="checkbox" name="terms" id="checkbox" class="validate[required]"/>&nbsp;&nbsp;I accept <a href="terms.jsp">terms & condition</a>.<br/><br/>
			<input type="submit" name="submitschool" id="submitschool" value="Register" class="button">
			</p>		
		</form>
							
	</div>	
	
</div><!-- wrap ends here -->	
	
	<jsp:include page="footer.jsp"></jsp:include>
</body>
</html>