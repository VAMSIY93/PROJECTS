package ExcelFile;

import java.io.File;

import jxl.Sheet;
import jxl.Workbook;

public class readStudentExcel {
	public void readExcelSheet(String destFile){ 
	      File excelSheet = null;
	      Workbook workbook = null;
	     
			data.Data d = new data.Data();
		  
	        try {
	        	 
	             Workbook wb = Workbook.getWorkbook(new File(destFile));
	             System.out.println(wb.getNumberOfSheets());
	                for(int sheetNo=0; sheetNo<wb.getNumberOfSheets();sheetNo++)
	                {                
	                	Sheet sheet = wb.getSheet(sheetNo);
	                	int columns = sheet.getColumns();
	                	int rows = sheet.getRows();
	                	// String data = null;
	                	//String data1 = null;
	                	
	               
	                	System.out.println("Sheet Name\t"+wb.getSheet(sheetNo).getName());
	                	
	                    String[] cols=new String[columns];
	                   
	                    for(int row = 1;row < rows;row=row+1) 
	                    {
	                  	   int col=0;
	                  		String grno = sheet.getCell(4, row).getContents();
	                  		 String schoolid = sheet.getCell(0, row).getContents();
	                  		String firstname = sheet.getCell(5, row).getContents();
	                  		String lastname = sheet.getCell(6, row).getContents();
	                  		String div = sheet.getCell(3, row).getContents();
	                  		String standard = sheet.getCell(2, row).getContents();
	                  		String year = sheet.getCell(7, row).getContents();
	                  		 
	                  	   
	                                           
	                  	String []fld = {"intGrNo","intSchoolID","strFirstName","strLastName","strDivision","intStandard","strYear"};
	                  	String []val = {grno,schoolid,firstname,lastname,div,standard,year};
	                  	d.insert("tblschoolstudents", fld, val);
	                    
	                    }
	                    
	                	}
	             } catch(Exception ioe) 
	             {
	                       ioe.printStackTrace();
	             }
	             
	          }
	      public static void main(String arg[]){
	            readStudentExcel excel = new readStudentExcel();
	            excel.readExcelSheet("D://abc.xls");
	      }
}
