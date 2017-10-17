package com.monetware.util;

import java.io.FileOutputStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

public class ExcelStorage {
	
	String filepath = "";
	SXSSFWorkbook wb = null;
	FileOutputStream out = null;
	Sheet sh = null;
	String [] columns;
	int currentRowIndex = 0;
	
//	static ExcelStorage storage = null;
	
	
	public ExcelStorage(String filepath, String [] columns) {

		this.filepath = filepath;
		this.columns = columns;
	}
	
	
	public void initStorage() throws Throwable{
		this.wb = new SXSSFWorkbook(100); // keep 100 rows in memory, exceeding rows will be flushed to disk
        this.sh = wb.createSheet();
        
        Row row = sh.createRow(0);
        for(int cellnum = 0; cellnum < columns.length; cellnum++){
            Cell cell = row.createCell(cellnum);
            cell.setCellValue(columns[cellnum]);
        }
	}
	
	public void appendRow(String[] columns) {
		currentRowIndex ++;
		 Row row = sh.createRow(currentRowIndex);
	        for(int cellnum = 0; cellnum < columns.length; cellnum++){
	            Cell cell = row.createCell(cellnum);
	            cell.setCellValue(columns[cellnum]);
	        }
	}
	
	
	public void finishStorage() throws Throwable{
		  out = new FileOutputStream(filepath);
	        wb.write(out);
	        out.close();

	        // dispose of temporary files backing this workbook on disk
	        wb.dispose();
	}
	
	
	
	


	public String getFilepath() {
		return filepath;
	}


	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}


	public int getCurrentRowIndex() {
		return currentRowIndex;
	}


	
	
	
	
	
	
	
	/*
	public static void main(String[] args) throws Throwable {
        SXSSFWorkbook wb = new SXSSFWorkbook(100); // keep 100 rows in memory, exceeding rows will be flushed to disk
        Sheet sh = wb.createSheet();
        for(int rownum = 0; rownum < 1000; rownum++){
            Row row = sh.createRow(rownum);
            for(int cellnum = 0; cellnum < 10; cellnum++){
                Cell cell = row.createCell(cellnum);
                String address = new CellReference(cell).formatAsString();
                cell.setCellValue(address);
            }

        }

        
        FileOutputStream out = new FileOutputStream("/Users/edward/Develop/sxssf.xlsx");
        wb.write(out);
        out.close();

        // dispose of temporary files backing this workbook on disk
        wb.dispose();
    }
*/
	
	
	
	
	
	
	
	
	
	
	
	

}


    