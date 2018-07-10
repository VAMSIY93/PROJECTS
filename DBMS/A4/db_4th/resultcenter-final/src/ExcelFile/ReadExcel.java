package ExcelFile;


import jxl.*;
import java.io.*;
public class ReadExcel {
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
                          
                  for(int row = 1;row < rows;row=row+1) 
                  {
                	   int col=0;
                		String data = sheet.getCell(col, row).getContents();
                		 String data1 = sheet.getCell(col+1, row).getContents();
                	  System.out.print(data+ " " +data1); 
                                         
                	String []fld = {"UserName","Password"};
                	String []val = {data,data1};
                	d.insert("login", fld, val);
                          System.out.println("\n");
                  }
                }
             } catch(Exception ioe) 
             {
                       ioe.printStackTrace();
             }
             
          }
      public static void main(String arg[]){
            ReadExcel excel = new ReadExcel();
            excel.readExcelSheet("D://abc.xls");
      }
}
