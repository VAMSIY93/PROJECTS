package ExcelFile;

import java.io.File;

import jxl.Sheet;
import jxl.Workbook;

public class readResultExcel {
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
	                  		String examid = sheet.getCell(0, row).getContents();
	                  		 String studentid = sheet.getCell(6, row).getContents();
	                  		String subjectid = sheet.getCell(2, row).getContents();
	                  		String marks = sheet.getCell(9, row).getContents();
	                  		String status = sheet.getCell(10, row).getContents();
	                  		                  	   
	                                           
	                  	String []fld = {"intExamID","intStudentID","intSubjectID","intMarks","strStatus"};
	                  	String []val = {examid,studentid,subjectid,marks,status};
	                  	d.insert("tblconvertedresult", fld, val);
	                    
	                    }
	                    
	                	}
	             } catch(Exception ioe) 
	             {
	                       ioe.printStackTrace();
	             }
	             
	          }
	      public static void main(String arg[]){
	            readResultExcel excel = new readResultExcel();
	            excel.readExcelSheet("D://abc.xls");
	      }
}
