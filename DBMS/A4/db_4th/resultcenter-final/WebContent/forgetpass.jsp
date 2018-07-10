<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" href="css/main.css" type="text/css" />
<link rel="stylesheet" href="css/validationEngine.jquery.css" type="text/css" media="screen" title="no title" charset="utf-8" />
<script src="js/jquery-1.4.min.js" type="text/javascript"></script>
<script src="js/jquery.validationEngine-en.js" type="text/javascript"></script>
<script src="js/jquery.validationEngine.js" type="text/javascript"></script>
<title>Forget Password</title>
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
			<br/>
		<form action="SendPass" style="width:225px" method="post" id="formID">		
			<p>				
			<label>Enter Your Email:</label>
			<input name="txtMail" type="text" size="30" id="txtEmail" class="validate[required,custom[email]]"/>
			<br/>
			<br/>
			<input class="button" type="Submit" name="Submit" id="Submit" value="Submit"/>		
			</p>
		</form>		
							
	</div>	
	
</div><!-- wrap ends here -->	
	
	<jsp:include page="footer.jsp"></jsp:include>
</body>
</html>