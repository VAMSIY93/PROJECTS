package serv;


import java.io.IOException; 
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import data.email;
import data.sendmails;

import java.io.PrintWriter;
import java.sql.*;
import java.util.Random;

/**
 * Servlet implementation class SendPass
 */
@WebServlet("/SendPass")
public class SendPass extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public SendPass() {
        // TODO Auto-generated constructor stub
    }

    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try 
        {
            if(request.getParameter("Submit")!=null)
            {
                String un= request.getParameter("txtMail");
                un=un.trim();
                
                String query="Select * from tbllogin";
                Connection con=null;
                PreparedStatement P=null;
                Statement S = null;
                ResultSet R= null;
                
                Class.forName("com.mysql.jdbc.Driver");
                con= DriverManager.getConnection("jdbc:mysql://localhost:3306/dbresultcenter","root","");
                S=con.createStatement();
                R=S.executeQuery(query);
                System.out.println("hi..");
                if(R.next())
                {
                	if(un.equals(R.getString("strEmail")))
                	{
	                    Random Rn= new Random();
	                    String ps="";
	                    for(int i=1;i<=6;i++)
	                        ps=ps+ Rn.nextInt(9);
	                    R.close();
		                System.out.println(un+" : "+ps);
		                query="Update tbllogin set strPassword= ? where strEmail =?";
		                P= con.prepareStatement(query);
		                P.setString(1, ps);
		                P.setString(2, un );
		                System.out.println("Password Updated...");
		                if(P.executeUpdate()>0)
		                {
		                    //send mail
		                  sendmails s= new sendmails();
		                  s.send(un, ps);
		                }
		                response.sendRedirect("sendsucc.jsp");
                	}
                	
                }  
            }
        }
        catch(Exception E)
        {
            //out.write("Error :"+E);
            
        }
        finally {            
            out.close();
        }
    }
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
        processRequest(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
        processRequest(request, response);
	}

}
