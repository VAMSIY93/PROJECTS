package serv;
import java.sql.*; 
import java.lang.*;
import java.util.zip.*;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.DriverManager;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.omg.CORBA.portable.InputStream;

import ExcelFile.readStudentExcel;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

/**
 * Servlet implementation class ProdImg1
 */
@WebServlet("/ProdImg1")
public class ProdImg1 extends HttpServlet {
	private static final long serialVersionUID = 1L;
    data.Data d = new data.Data();
    int schid;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ProdImg1() {
        super();
        // TODO Auto-generated constructor stub
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
response.setContentType("text/html;charset=UTF-8");
PrintWriter out = response.getWriter();
HttpSession S=request.getSession();   
String schoolname=S.getAttribute("schoolname").toString();
try
{
	
			
String saveFile="";
String contentType = request.getContentType();
if((contentType != null)&&(contentType.indexOf("multipart/form-data") >= 0)){
DataInputStream in = new DataInputStream(request.getInputStream());
int formDataLength = request.getContentLength();
byte dataBytes[] = new byte[formDataLength];
int byteRead = 0;
int totalBytesRead = 0;
	while(totalBytesRead < formDataLength)
	{
	byteRead = in.read(dataBytes, totalBytesRead,formDataLength);
	totalBytesRead += byteRead;
	}
	String file = new String(dataBytes);
	saveFile = file.substring(file.indexOf("filename=\"") + 10);
	saveFile = saveFile.substring(0, saveFile.indexOf("\n"));
	saveFile = saveFile.substring(saveFile.lastIndexOf("\\") + 1,saveFile.indexOf("\""));
	int lastIndex = contentType.lastIndexOf("=");
	String boundary = contentType.substring(lastIndex + 1,contentType.length());
	int pos;
	pos = file.indexOf("filename=\"");
	pos = file.indexOf("\n", pos) + 1;
	pos = file.indexOf("\n", pos) + 1;
	pos = file.indexOf("\n", pos) + 1;
	int boundaryLocation = file.indexOf(boundary, pos) - 4;
	int startPos = ((file.substring(0, pos)).getBytes()).length;
	int endPos = ((file.substring(0, boundaryLocation)).getBytes()).length;
	
	String path=request.getServletContext().getRealPath("schoolhome.jsp");
    path=path.substring(0, path.length()-15);
    		
	path = path+"\\image\\"+schoolname+".jpg";
	System.out.println("path :"+path);
	
	
	File ff = new File(path);
	path = ff.getPath();
	FileOutputStream fileOut = new FileOutputStream(ff);
	fileOut.write(dataBytes, startPos, (endPos - startPos));
	fileOut.flush();
	fileOut.close();
	
	
	int i = Integer.parseInt(S.getAttribute("schoolid").toString());
	System.out.println("schoolid:"+i);
	Connection connection = null;
	String connectionURL = "jdbc:mysql://localhost:3306/dbresultcenter";
	ResultSet rs = null;
	PreparedStatement psmnt = null;
	FileInputStream fis;

	
		try{
			
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		connection = (Connection) DriverManager.getConnection(connectionURL, "root", "");
		File f = new File(saveFile);
		
		psmnt = (PreparedStatement) connection.prepareStatement("insert into tblschoollogo (intSchoolID,strLogo) values (?,?)");
		//fis = new FileInputStream(f);
		psmnt.setString(2,path);
		psmnt.setInt(1,i);
		int s = psmnt.executeUpdate();
	    response.sendRedirect("schoolhome.jsp");
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	    fileOut.flush();
	    fileOut.close();
	    
	
	
}

}finally {            
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
