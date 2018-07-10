package serv;

import java.io.IOException;  
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import data.Data;

import ExcelFile.writeResultExcel;

/**
 * Servlet implementation class addStudentResult
 */
@WebServlet("/addStudentResult")
public class addStudentResult extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private HttpSession S;
	Data d = new Data();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public addStudentResult() {
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
    		
    		String schoolid=request.getParameter("schoolid");
    		System.out.println("school id:"+schoolid);
    		String schoolname=d.getfield("tblschool", "strSchoolName", "intSchoolID="+schoolid);
    		System.out.println("schoolname:"+schoolname);
    		
    		String standard=request.getParameter("standard");
    		System.out.println("standard:"+standard);
    		String division=request.getParameter("division");
    		System.out.println("division:"+division);
    		String subjectid=request.getParameter("subjectid");
    		System.out.println("subject id:"+subjectid);
    		String subjectname=d.getfield("tblsubject", "strSubjectName", "intSubID="+subjectid);
    		System.out.println("subject name :"+subjectname);
    		String examid=request.getParameter("examid");
    		System.out.println("division:"+examid);
    		String examname=d.getfield("tblexams", "strExamTitle", "intExamID="+examid);
    		System.out.println("subject name :"+examname);
    		    		
    		
    		String path=request.getServletContext().getRealPath("getresultfile.jsp");
            path=path.substring(0, path.length()-18);
            		
    		path = path+"\\resultfile\\"+schoolname+""+standard+""+division+".xls";
    		System.out.println("path :"+path);
    		//String path="E:\\JAVA\\JAVA\\Workspace\\resultcenter\\WebContent\\file\\"+schoolname+""+standard+""+division+".xls";
    		String divid = d.getfield("tbldivision", "intDivID", "intSchoolID="+Integer.parseInt(schoolid)+" && intStandard="+Integer.parseInt(standard)+" && strDiv='"+division+"'");
    		System.out.println("division ID:"+divid);
    		
    		writeResultExcel w = new writeResultExcel();
    		w.setOutputFile(path);
    	    w.setexam(examid, examname);
    		w.setsubject(subjectid, subjectname);
    		w.setschoolname(schoolname,schoolid);
    	    w.setstandarddivision(standard, division);
    	    w.write();
    	    
    	    String path1=schoolname+""+standard+""+division+".xls";
    	    
    	    S.setAttribute("filepath1", path1);
    		
    	    
    	    response.sendRedirect("downloadresult.jsp");
        	   		
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
