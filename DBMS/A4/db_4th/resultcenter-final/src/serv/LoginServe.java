package serv;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import data.Data;

/**
 * Servlet implementation class LoginServe
 */
@WebServlet("/LoginServe")
public class LoginServe extends HttpServlet {
	private static final long serialVersionUID = 1L;
	 Data D=new Data();
	    private HttpSession S;
	    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
	            throws ServletException, IOException {
	     
	    	response.setContentType("text/html;charset=UTF-8");
	        PrintWriter out = response.getWriter();
	        String userid=request.getParameter("email");
	        String password=request.getParameter("password");
	        
	        try {
	            String fld[]={"intLoginID","strEmail","strPassword","StrUserType","dtmLastLoginDate","strStatus"};
	            Vector<String> v=D.selectfields("tbllogin",fld, "trim(strEmail)='"+userid.trim()+"'");
	           // Vector v = D.selectedit("tbllogin");
	           // out.println(D.msg);   
	            if(!v.isEmpty())
	                
	            {
	            	
	                 String dpassword=v.get(2).toString().trim();
	                 String drole=v.get(3).toString().trim().toLowerCase();
	                 String status=v.get(5).toString().trim().toLowerCase();
	                 String status1="active";
	                 String status2="block";
	           
	                if(password.equals(dpassword) && status.equals(status1))
	                   
	                {
	                   S= request.getSession();
	                    S.setAttribute("emailid", userid);
	                    S.setAttribute("role", drole);
	                   S.setAttribute("loginid", v.get(0).toString().trim());
	                   S.setAttribute("lastlogindate", v.get(4).toString().trim()); 
	                   if(drole.equalsIgnoreCase("admin"))
	                   {
	                         response.sendRedirect("adminhome.jsp");
	     	           }
                  	  
	                   else if(drole.equalsIgnoreCase("school"))
	                   {
	                    	 Vector V= D.getemps("tblschool","intLoginID",Integer.parseInt(S.getAttribute("loginid").toString()));
	                    	 out.print(userid);
	                    	 if(V!=null)
	     	                {
	     	                S.setAttribute("schoolid", V.get(0));
	     	                S.setAttribute("schoolname", V.get(2));
	     	                S.setAttribute("logo1", V.get(3).toString());
	     	               response.sendRedirect("schoolhome.jsp");
	     	                }
	                    }
	                        
	                      else if(drole.equalsIgnoreCase("staff"))
	                      {
	                    	  Vector V= D.getemps("tblstaff","intLoginID",Integer.parseInt(S.getAttribute("loginid").toString()));
	                    	  if(V!=null)
	                    	  {
	                    		  S.setAttribute("staffid", V.get(0));
	                    		  S.setAttribute("firstname", V.get(2));
	                    		  S.setAttribute("lastname", V.get(3));
	                    		  response.sendRedirect("staffhome.jsp");
	                    	  }
	                      }
	             
	                }
	                else
	                {
	                	if(status.equals(status2))
	                	{
	                		out.write("<script>alert('Your username is block.')</script>");
		                	out.write("<script>history.go(-1);</script>");
	                	}else{
	                	out.write("<script>alert('Invalid Username or Password')</script>");
	                	out.write("<script>history.go(-1);</script>");
	                	}
	                }
	            }
	           
	            	else
	                {
	            		
	            		out.write("<script>alert('Invalid Username or Password')</script>");
	                	out.write("<script>history.go(-1);</script>");
	                }
	        
	        }
	        catch(Exception E)
	        {
	        	out.write("E"+E.toString());
	        	E.printStackTrace();
	        }
	        finally {            
	            out.close();
	        }
	    }

       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServe() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
this.processRequest(request,response);
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		this.processRequest(request,response);
	}

}
