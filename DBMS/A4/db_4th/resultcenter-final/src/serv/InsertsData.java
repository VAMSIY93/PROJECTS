package serv;

import java.io.IOException;

import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import data.Data;

/**
 * Servlet implementation class InsertsData
 */
@WebServlet("/InsertsData")
public class InsertsData extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public InsertsData() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter out= response.getWriter();
		Enumeration <String>flds=request.getParameterNames();
		Data D = new Data();
		
		int cnt=0;
		String tbl= request.getParameter("tbl");
		while(flds.hasMoreElements())
		{

		String fldname= flds.nextElement();
	if(!(fldname.equals("Submit") || fldname.equals("Reset") || fldname.equals("tbl")))
	{
		//out.write(fldname+"<br>");
		cnt++;
	}
		}
		String []flds1= new String[cnt];
		String []vals = new String[cnt];
		cnt=0;
		Enumeration <String>flds11=request.getParameterNames();
		while(flds11.hasMoreElements())
		{
	String fldname= flds11.nextElement();
	if(!(fldname.equals("Submit") || fldname.equals("Reset") || fldname.equals("tbl")))
	{

		flds1[cnt]=fldname;
		vals[cnt]=request.getParameter(fldname);
		System.out.println(flds1[cnt]+":"+vals[cnt]);
		cnt++;
	}
		}
	D.insert(tbl, flds1, vals);
	out.write("<script>alert('"+D.msg+"')</script>");

	if(tbl.equals("tblstaff"))
	{
		response.sendRedirect("staff.jsp");
	}
	if(tbl.equals("tblsubject"))
	{
		response.sendRedirect("subject.jsp");
	}
	if(tbl.equals("tblallocatedstaff"))
	{
		response.sendRedirect("standard.jsp");
	}
	if(tbl.equals("tblcity"))
	{
		response.sendRedirect("city.jsp");
	}
	if(tbl.equals("tblexams"))
	{
		response.sendRedirect("exam.jsp");
	}
	if(tbl.equals("tblsecquestion"))
	{
		response.sendRedirect("secquestion.jsp");
	}
	if(tbl.equals("tblresultdates"))
	{
		response.sendRedirect("resultdate.jsp");
	}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	doGet(request,response);
	}

}
