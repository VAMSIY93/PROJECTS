<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="java.util.Vector" import="java.sql.*"%>
<%
	data.Data d = new data.Data();
Connection con=null;
Statement S=null;
PreparedStatement P=null;
PreparedStatement P1=null;
ResultSet R;

String date=d.getdate();
d.update2("tbllogin", "dtmLastLoginDate", d.getdate(), "intLoginID="+session.getAttribute("loginid"));

session.removeAttribute("emailid");
session.removeAttribute("loginid");
session.removeAttribute("role");
response.sendRedirect("login.jsp");
%>
