<%@page import="data.Data" import="java.util.*"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
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
	<form action="">
	<label>Your request for registration is send...</label>
	<a href="login.jsp"><input type="button" value="GO BACK" class="button"></a>		
	</form>			
						
	</div>	
	
</div><!-- wrap ends here -->	
	
	<jsp:include page="footer.jsp"></jsp:include>
</body>
</html>