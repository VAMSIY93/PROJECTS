package serv;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import data.Data;

/**
 * Servlet implementation class UpdateData
 */
@WebServlet("/UpdateData")
public class UpdateData extends HttpServlet {
	private static final long serialVersionUID = 1L;
       Data D= new Data();
       PrintWriter out=null;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UpdateData() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String tbl= request.getParameter("tbl");
		String pg=request.getParameter("pg");
		System.out.println(tbl+" "+pg);
		String clm[]= D.getFields(tbl);
		String val[]=new String[clm.length];
		out=response.getWriter();
		for(int i=0;i<clm.length;i++)
		{
			String vl=request.getParameter(clm[i]);
			val[i]=vl;
			out.write(clm[i]+" "+vl);
		}
		D.update(tbl, clm, val);
	out.write(D.msg);
		response.sendRedirect(pg);
		
	}

}
