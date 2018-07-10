package ExcelFile;



import java.io.File; 
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Locale;

import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.UnderlineStyle;
import jxl.write.Formula;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import data.Data;


public class writeResultExcel {

  private WritableCellFormat timesBoldUnderline;
  private WritableCellFormat times;
  private String inputFile;
 
  private String schoolid;
  private String schoolname;
  
  private String standard;
  private String division;
  
  private String examid;
  private String examname;
  
  private String subjectid;
  private String subjectname;
 
  
public void setOutputFile(String inputFile) {
  this.inputFile = inputFile;
  }

public void setschoolname(String schoolname,String schoolid) {
	  this.schoolname=schoolname;
	  this.schoolid=schoolid;
	  }

public void setstandarddivision(String standard, String division) {
	  this.standard=standard;
	  this.division=division;
	  }

public void setexam(String examid, String examname) {
	  this.examid=examid;
	  this.examname=examname;
	  System.out.println("Examid:"+examid);
	  System.out.println("Examname:"+examname);
		
	  }
public void setsubject(String subjectid, String subjectname) {
	  this.subjectid=subjectid;
	  this.subjectname=subjectname;
	  }



  public void write() throws IOException, WriteException {
    File file = new File(inputFile);
    WorkbookSettings wbSettings = new WorkbookSettings();

    wbSettings.setLocale(new Locale("en", "EN"));

    WritableWorkbook workbook = Workbook.createWorkbook(file, wbSettings);
    workbook.createSheet("Report", 0);
    WritableSheet excelSheet = workbook.getSheet(0);
    createLabel(excelSheet);
    createContent(excelSheet);

    workbook.write();
    workbook.close();
  }

  private void createLabel(WritableSheet sheet)
      throws WriteException {
    // Lets create a times font
    WritableFont times10pt = new WritableFont(WritableFont.TIMES, 10);
    // Define the cell format
    times = new WritableCellFormat(times10pt);
    // Lets automatically wrap the cells
    times.setWrap(true);

    // Create create a bold font with unterlines
    WritableFont times10ptBoldUnderline = new WritableFont(WritableFont.TIMES, 10, WritableFont.BOLD, false,
        UnderlineStyle.SINGLE);
    timesBoldUnderline = new WritableCellFormat(times10ptBoldUnderline);
    // Lets automatically wrap the cells
    timesBoldUnderline.setWrap(true);

    CellView cv = new CellView();
    cv.setFormat(times);
    cv.setFormat(timesBoldUnderline);
   // cv.setAutosize(true);

    // Write a few headers
    

  }

  
  
  private void createContent(WritableSheet sheet) throws WriteException,
      RowsExceededException {
  
	  
	  int a=1;
	  int srno=1;
	  
	  addCaption(sheet, 0,0,"ExamID");
	  addCaption(sheet, 1,0,"ExamName");
	  addCaption(sheet, 2,0,"SubjectID");
	  addCaption(sheet, 3,0,"SunjectName");
	  addCaption(sheet, 4,0,"Standard");
	  addCaption(sheet, 5,0,"Division");
	  addCaption(sheet, 6,0,"StudentId");
	  addCaption(sheet, 7,0,"FirstName");
	  addCaption(sheet, 8,0,"LastName");
	  addCaption(sheet, 9,0,"Marks");
	  addCaption(sheet, 10,0,"Status");
	  
	  Connection con= null;
	  Statement S=null;
	  PreparedStatement p = null;
	  ResultSet r = null;
	
	  System.out.println("Shreyash Kalaria");
	  try{
		  
	  	Class.forName("com.mysql.jdbc.Driver");
		con=DriverManager.getConnection("jdbc:mysql://localhost:3306/dbresultcenter","root","");
		String q="select intGrNo, strFirstName, strLastName from tblschoolstudents where intSchoolID="+schoolid+" && intStandard="+standard+" && trim(strDivision)='"+division+"'";
		System.out.println("Query:"+q);
		S=con.createStatement();
		r=S.executeQuery(q);
		System.out.println("Hi...");
		int i=1;
		while(r.next())
		{
			System.out.println("Hi...");

			addLabel(sheet, 0, i, examid);
			addLabel(sheet, 1, i, examname);
			addLabel(sheet, 2, i, subjectid);
			addLabel(sheet, 3, i, subjectname);
			addLabel(sheet, 4, i, standard);
			addLabel(sheet, 5, i, division);
			addLabel(sheet, 6, i, r.getString(1));
			addLabel(sheet, 7, i, r.getString(2));
			addLabel(sheet, 8, i, r.getString(3));
			i++;
			System.out.println(r.getString(1));
			System.out.println(r.getString(2));
			System.out.println(r.getString(3));
			
			
		}
		System.out.println("Jay shree krishna");
	  }catch(Exception e)
	  {
		  e.printStackTrace();
	  }
		
	  
	  }
	
	  


  private void addLabel(WritableSheet sheet, int column, int row, String s)
      throws WriteException, RowsExceededException {
    Label label;
    label = new Label(column, row, s, times);
    sheet.addCell(label);
  }
  
  private void addCaption(WritableSheet sheet, int column, int row, String s)
	      throws RowsExceededException, WriteException {
	    Label label;
	    label = new Label(column, row, s, timesBoldUnderline);
	    sheet.addCell(label);
	  }

  public static void main(String[] args) throws WriteException, IOException {
    writeResultExcel test = new writeResultExcel();
    test.setOutputFile("d:/abc.xls");
    test.setexam("4", "finalexam");
    test.setschoolname("asdfghj", "1");
    test.setstandarddivision("8", "A");
    test.setsubject("1", "Maths");
    test.write();
    System.out.println("Please check the result file under d:/abc.xls ");
    
  }
} 