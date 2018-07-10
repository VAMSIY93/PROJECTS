package serv;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class confirm
 */
@WebServlet("/confirm")
public class confirm extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public confirm() {
        super();
        // TODO Auto-generated constructor stub
    }
    data.Data d= new data.Data();
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		if(request.getParameter("confirm1")!=null)
		{
			String table=request.getParameter("table");
			String login=request.getParameter("loginid");
			d.update2("tbllogin", "strStatus", "active", "intLoginID="+login);
			if(table.equals("tblstaff"))
			{
				response.sendRedirect("staff.jsp");
			}
			if(table.equals("tblschool"))
			{
				response.sendRedirect("school.jsp");
			}
		}
		if(request.getParameter("cancel")!=null)
		{
			String table=request.getParameter("table");
			String login=request.getParameter("loginid");
			d.delete("tbllogin", "intLoginID="+login);
			if(table.equals("tblstaff"))
			{
				d.delete(table, "intLoginID="+login);
				response.sendRedirect("staff.jsp");
			}
			if(table.equals("tblschool"))
			{
				d.delete(table, "intLoginID="+login);
				response.sendRedirect("school.jsp");
			}
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
