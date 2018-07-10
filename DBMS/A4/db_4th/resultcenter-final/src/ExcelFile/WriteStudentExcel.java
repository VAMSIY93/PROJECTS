package ExcelFile;



import java.io.File;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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


public class WriteStudentExcel {

  private WritableCellFormat timesBoldUnderline;
  private WritableCellFormat times;
  private String inputFile;
  private String schoolname;
  private String standard;
  private String division;
  private int number;
  private String schoolid;
  private String divid;
  
public void setOutputFile(String inputFile) {
  this.inputFile = inputFile;
  }

public void setschoolname(String schoolname,String schoolid) {
	  this.schoolname=schoolname;
	  this.schoolid=schoolid;
	  }

public void setstandarddivision(String standard, String division, String divid) {
	  this.standard=standard;
	  this.division=division;
	  this.divid=divid;
	  }

public void setnoofstudent(int number) {
	  this.number=number;
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
  
	  Connection con= null;
	  PreparedStatement p = null;
	  ResultSet r = null;
	
	  int a=1;
	  int i=1;
	  int srno=1;
	  
	  addCaption(sheet, 0,0,"SchoolID");
	  addCaption(sheet, 1,0,"SchoolName");
	  addCaption(sheet, 2,0,"Standard");
	  addCaption(sheet, 3,0,"Division");
	  addCaption(sheet, 4,0,"StudentGrNo");
	  addCaption(sheet, 5,0,"FirstName");
	  addCaption(sheet, 6,0,"LastName");
	  addCaption(sheet, 7,0,"Year");
	  
	  
	  
		
	for(int j=0;j<number;j++)
	{
		  
			  addLabel(sheet, 0, i, schoolid);
			  addLabel(sheet, 1, i, schoolname);
			  addLabel(sheet, 2, i, standard);
			  addLabel(sheet, 3, i, division);
		  	
		  		System.out.println("in 4 loop");
		  	i++;
	
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
    WriteStudentExcel test = new WriteStudentExcel();
    test.setOutputFile("d:/abc.xls");
    test.setschoolname("shree k o shah adarsh school","2");
    test.setstandarddivision("8", "A","2");
    test.setnoofstudent(5);
    test.write();
    System.out.println("Please check the result file under d:/abc.xls ");
    
  }
} 