<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.util.Vector"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
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
		});
</script>
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
%>

<title>Change Password</title>
	<link rel="shortcut icon" href="images/rc.jpg" type="image/x-icon"/>	
</head>

<body>
<div id="wrap"><!-- wrap starts here -->
	<jsp:include page="schoolheader.jsp"></jsp:include>				
	
	<jsp:include page="schoolsidebar.jsp"></jsp:include>	
		
	<div id="main">
	<form id="form1">
     <table border="0">
        <tr>
            <th>Enter Old Password</th>
            <td><input type="password" name="oldpassword" id="oldpassword" class="validate[required,length[6,20]] text-input"></input></td>
            <td></td>
        </tr>
         <tr>
            <th>Enter New Password</th>
            <td><input type="password" name="newpassword1" id="newpassword1" class="validate[required,length[6,20]] text-input"></input></td>
            <td></td>
        </tr>
         <tr>
            <th>Enter Confirm Password</th>
            <td><input type="password" name="newpassword2" id="newpassword1" class="validate[required,confirm[newpassword1]] text-input"></input></td>
            <td></td>
        </tr>
         <tr>
            <th></th>
            <td><input type="Submit" value="OK" name="ok"></input></td>
            <td></td>
        </tr>
    </table>
	</form>
	<% 
    String email=session.getAttribute("emailid").toString();
	System.out.println("change : "+email);
    if(request.getParameter("ok")!=null)
    {
        String fld[]={"strPassword"};
        String cond="trim(strEmail)='"+email.trim()+"'";
        Vector<String> v=d.selectfields("tbllogin", fld,  "trim(strEmail)='"+email.trim()+"'");
        String newpass[]={request.getParameter("newpassword1")};
        String oldpass=request.getParameter("oldpassword");
        String selpassword=v.get(0).toString().trim();
        System.out.println("change : "+selpassword+","+newpass[0]+","+oldpass);
        if(oldpass.equals(selpassword))
        {
        	System.out.println("Hi... : "+selpassword+","+newpass[0]);
            
            d.update("tbllogin", fld, newpass, cond);
            response.sendRedirect("login.jsp");
        }
        
    }
%>
	</div>	
	
	
</div><!-- wrap ends here -->	
	
	<jsp:include page="footer.jsp"></jsp:include>
	
</body>
</html>