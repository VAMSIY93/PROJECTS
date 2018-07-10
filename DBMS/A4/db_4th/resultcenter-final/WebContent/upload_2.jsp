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
<link rel="stylesheet" href="css/main.css" type="text/css" />
<%if(session.getAttribute("emailid")==null)
{
	response.sendRedirect("login.jsp");
}
%>
<%
	data.Data d = new data.Data();
%>
<title>Manage Student</title>
	<link rel="shortcut icon" href="images/rc.jpg" type="image/x-icon"/>	
</head>

<body>
<div id="wrap"><!-- wrap starts here -->
	<jsp:include page="staffheader.jsp"></jsp:include>				
	
	<jsp:include page="staffsidebar.jsp"></jsp:include>	
		
	<div id="main">
	<a name="TemplateInfo"></a>
		<h1>Add Student Result Detail...</h1>
					
					<form id="ContactForm" action="upload_studena.jsp" method="post" enctype="multipart/form-data">
                          <div class="wrapper"> <strong>Upload A File*</strong>
                              <div class="bg">
                                <input type="file" name="file" id="myFile"/><span id="fileerror" style="color:red"></span>
                              </div>
                          </div>
                            
                            <p>
                            <input type="submit" value="upload" />
                            </p>
                          
                    </form>
</div>	
	
	
</div><!-- wrap ends here -->	
	
	<jsp:include page="footer.jsp"></jsp:include>
	
</body>
</html>
