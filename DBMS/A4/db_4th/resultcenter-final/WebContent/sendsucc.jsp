<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>ResultCeneter</title>
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
		<form action="login.jsp" style="width:225px" method="post" id="formID">		
			<p>				
			<label>Password successfully send to your email address...</label>
			<input type="Submit" name="Submit" value="next>>"/>		
			</p>
		</form>		
							
	</div>	
	
</div><!-- wrap ends here -->	
	
	<jsp:include page="footer.jsp"></jsp:include>
</body>
</html>