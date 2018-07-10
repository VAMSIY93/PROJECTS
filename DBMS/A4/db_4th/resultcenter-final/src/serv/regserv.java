package serv;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import data.Data;

/**
 * Servlet implementation class regserv
 */
@WebServlet("/regserv")
public class regserv extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * 
     * Default constructor. 
     */
    public regserv() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	doPost(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	String schoolname="";
	String address="";
	String city="";
	String schoolregno="";
	String logopath=null;
	String email="";
	String pass="";
	String secque="";
	String secans="";
	String fname="";
	String lname="";
	String mobile="";
	String schid="";
	String des="";
	String loginid="";
	data.Data d = new data.Data();
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	PrintWriter out=  response.getWriter();
	String date= d.getdate();
		if(request.getParameter("submitschool")!=null)
		{
			
			out.write("Jay Sri krishna");
			schoolname=request.getParameter("strSchoolName");
			address=request.getParameter("strAddress");
			city=request.getParameter("intCityID");
			schoolregno=request.getParameter("strSchoolRegNo");
			email=request.getParameter("strEmail");
			pass=request.getParameter("strPassword");
			secque=request.getParameter("intSecquestionID");
			secans=request.getParameter("strAnswer");
			
			
			String []flds = {"strEmail","strPassword","intSecquestionID","strAnswer","dtmRegDate","dtmLastLoginDate","strStatus","strUserType"};
			String []vals = {email,pass,secque,secans,date,date,"block","school"};
			d.insert("tbllogin", flds, vals);
		
			Vector<String> v = d.getemps("tbllogin", "trim(strEmail)", "email");
			loginid=d.getfield("tbllogin", "intLoginID", "trim(strEmail)='"+email+"'");
				
			String []flds1 = {"intLoginID","strSchoolName","strSchoolAddress","intCityID","strSchoolRegNo"};
			String []vals1 = {loginid,schoolname,address,city,schoolregno};
			d.insert("tblschool", flds1, vals1);
		}
		
		
		if(request.getParameter("submitstaff")!=null)
		{
			out.write("Jay Sri krishna");
			fname=request.getParameter("strFirstName");
			lname=request.getParameter("strLastName");
			schid=request.getParameter("intSchoolID");
			mobile=request.getParameter("strMobileNo");
			des=request.getParameter("strDesignation");
			email=request.getParameter("strEmail");
			pass=request.getParameter("strPassword");
			secque=request.getParameter("intSecquestionID");
			secans=request.getParameter("strAnswer");
			
			String []flds = {"strEmail","strPassword","intSecquestionID","strAnswer","dtmRegDate","dtmLastLoginDate","strStatus","strUserType"};
			String []vals = {email,pass,secque,secans,date,date,"block","staff"};
			d.insert("tblLogIn", flds, vals);
		
			String loginid=d.getLastValue("tbllogin", "intLoginID");
			String []flds1 = {"intSchoolID","strFirstName","strLastName","intLoginID","strMobileNo","strDesignation"};
			String []vals1 = {schid,fname,lname,loginid,mobile,des};
			d.insert("tblstaff", flds1, vals1);
		}
		out.write("<script>alert('Your request for register is send. You can login after 24 hours.')</script>");
		response.sendRedirect("thanks.jsp");
	}
	}

