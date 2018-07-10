package serv;

import java.io.DataInputStream;   
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import ExcelFile.WriteStudentExcel;
import ExcelFile.readStudentExcel;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

import data.Data;



/**
 * Servlet implementation class addStudentDetail
 */
@WebServlet("/addStudentDetail")
public class addStudentDetail extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private HttpSession S;
	Data d = new Data();
    /**
     * @see HttpServlet#HttpServlet()
     */
    public addStudentDetail() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    	    throws ServletException, IOException {
    	response.setContentType("text/html;charset=UTF-8");
    	S=request.getSession();
    	PrintWriter out = response.getWriter();
    	if(request.getParameter("getfile")!=null)
    	{
    	try
    	{
    		
    		String schoolname=request.getParameter("schoolname");
    		System.out.println("schoolname:"+schoolname);
    		String schoolid=request.getParameter("schoolid");
    		System.out.println("schoolid:"+schoolid);
    		String standard=request.getParameter("standard");
    		System.out.println("standard:"+standard);
    		String division=request.getParameter("division");
    		System.out.println("division:"+division);
    		int no=Integer.parseInt(request.getParameter("no"));
    		System.out.println("no of student:"+no);
    		
    		String path=request.getServletContext().getRealPath("student.jsp");
            path=path.substring(0, path.length()-12);
            		
    		path = path+"\\file\\"+schoolname+""+standard+""+division+"student.xls";
    		System.out.println("path :"+path);
    		//String path="E:\\JAVA\\JAVA\\Workspace\\resultcenter\\WebContent\\file\\"+schoolname+""+standard+""+division+".xls";
    		String divid = d.getfield("tbldivision", "intDivID", "intSchoolID="+Integer.parseInt(schoolid)+" && intStandard="+Integer.parseInt(standard)+" && strDiv='"+division+"'");
    		System.out.println("division ID:"+divid);
    		
    		WriteStudentExcel w = new WriteStudentExcel();
    		w.setOutputFile(path);
    	    w.setschoolname(schoolname,schoolid);
    	    w.setstandarddivision(standard, division,divid);
    	    w.setnoofstudent(no);
    	    w.write();
    	    
    	    String path1=schoolname+""+standard+""+division+"student.xls";
    	    
    	    S.setAttribute("filepath1", path1);
    		
    	    
    	    response.sendRedirect("downloadstudent.jsp");
        	   		
    		}
    		catch (Exception e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}finally {            
    	out.close();
    	}
    	}
    	
    	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		processRequest(request,response);
	}

}
