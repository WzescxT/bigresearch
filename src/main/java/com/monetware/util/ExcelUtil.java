package com.monetware.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.POIXMLDocument;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/** 
 *@author  venbillyu 
 *@date 创建时间：2017年1月3日 下午4:12:19 
 *@describle 
 */
public class ExcelUtil {
    private Workbook wb;  
    private Sheet sheet;  
    private Row row;  
    

	public ExcelUtil(InputStream is)  {
		super();
		
			try {
				if (!is.markSupported()) {
				    is = new PushbackInputStream(is, 8);
				}
				if(POIFSFileSystem.hasPOIFSHeader(is)) {//2003版
					  wb = new HSSFWorkbook(is);
				}  
				if(POIXMLDocument.hasOOXMLHeader(is)) {//2007版
					wb = new XSSFWorkbook(OPCPackage.open(is));//OPCPackage.open(is)取得一个文件的读写权限
				}
			
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("exception===>"+e);
			}
		
	}

	/** 
     * 读取Excel表格表头的内容 
     * @param InputStream 
     * @return String 表头内容的数组 
     */  
    public String[] readExcelTitle() {
        sheet = wb.getSheetAt(0);  
        row = sheet.getRow(0);  
        // 标题总列数  
        int colNum = row.getPhysicalNumberOfCells();  
        System.out.println("colNum:" + colNum);  
        String[] title = new String[colNum];  
        for (int i = 0; i < colNum; i++) {  
            title[i] = row.getCell((short) i).getStringCellValue();  
        }  
        for (int i = 0; i < title.length; i++) {
			System.out.println("=======>标题"+i+title[i]);
		}
        return title;  
    }  
  
   /* *//** 
     * 读取Excel数据内容 
     * @param InputStream 
     * @return Map 包含单元格数据内容的Map对象 
     * 每次读1000行
     *//*  
    public List<Map<String, Object>> readExcelContent(InputStream is,int pageNo,String fields[]) {  
        List<Map<String, Object>> mapObjs=new ArrayList<Map<String, Object>>(); 
        String str = "";  
        //获取列名
        String title[]=readExcelTitle(is);
        //获取列名和相应的列数
        Map<String, Integer> cellMap=new HashMap<String, Integer>();
        for (int i = 0; i < title.length; i++) {
        	
        	for (int j = 0; j < fields.length; j++) {
        		if (title[i].equals(fields[j])) {
					cellMap.put(title[i],i );
				}
             	
             }
        	
        }
        Map<String, Object> mapObj=new HashMap<String, Object>();
        sheet = wb.getSheetAt(0);  
        // 得到总行数  
        int rowNum = sheet.getLastRowNum();  
        System.out.println("最大行数=========="+rowNum);
        if (((pageNo+1)*1000+1)<rowNum) {
			rowNum=(pageNo+1)*1000;
		}
        
        
        row = sheet.getRow(0);  
        Cell c=row.getCell(0);
        System.out.println("(0,0)=========>"+c.getStringCellValue());
        int colNum = row.getPhysicalNumberOfCells();  
        // 正文内容应该从第二行开始,第一行为表头的标题  
        for (int i = pageNo*1000+1; i <= rowNum; i++) { 
        	if (i==0) {
				continue;
			}
            row = sheet.getRow(i);
            for (Entry<String, Integer> entry : cellMap.entrySet()) {
            	System.out.println(entry.getKey()+"===========>"+entry.getValue());
            	System.out.println("=======坐标===========>"+i+","+entry.getValue());
            	Object cellValue="";
            	if (row.getCell( entry.getValue())!=null) {
//					cellValue=row.getCell( entry.getValue()).getStringCellValue();
            		cellValue=getCellFormatValue(row.getCell(entry.getValue()));
				}
            	mapObj.put(entry.getKey(), cellValue);
            	}
            mapObjs.add(mapObj);
            
        }  
        
        
        return mapObjs;  
    }  
    */
    
    public List<String[]> readExcelContent(int pageNo,String fields[]) {  
        List<String[]> resObjs=new ArrayList<String[]>(); 
        String str = "";  
        //获取列名
        String title[]=readExcelTitle();
        //获取列名和相应的列数
        Map<String, Integer> cellMap=new HashMap<String, Integer>();
        for (int i = 0; i < title.length; i++) {
        	for (int j = 0; j < fields.length; j++) {
        		if (title[i].equals(fields[j])) {
					cellMap.put(title[i],i );
					System.out.println(title[i]+"================>"+i);
				}
             }
        }
        sheet = wb.getSheetAt(0);  
        // 得到总行数  
        int rowNum = sheet.getLastRowNum();  
        System.out.println("最大行数=========="+rowNum);
        if (((pageNo+1)*1000+1)<rowNum) {
			rowNum=(pageNo+1)*1000;
		}
        row = sheet.getRow(0);  
        Cell c=row.getCell(0);
        int colNum = row.getPhysicalNumberOfCells();  
        // 正文内容应该从第二行开始,第一行为表头的标题  
        for (int i = pageNo*1000+1; i <= rowNum; i++) { 
        	if (i==0) {
				continue;
			}
            row = sheet.getRow(i);
            String[] strObj=new String[fields.length];
            for (Entry<String, Integer> entry : cellMap.entrySet()) {
            	int columnNo=entry.getValue();
            	String cellValue="";
            	if (row.getCell( columnNo)!=null) {
            		//将表格数据类型设置成String
            		row.getCell(columnNo).setCellType(Cell.CELL_TYPE_STRING);
            		cellValue=row.getCell(columnNo).getStringCellValue();
				}
            	if (i==10) {
            		System.out.println("========>("+i+","+columnNo+")"+cellValue);
				}
            	strObj[columnNo]=cellValue;
            	}
            resObjs.add(strObj);
            
        }  
        
        
        return resObjs;  
    }  
    
    public int getLineNo(){
       
         sheet = wb.getSheetAt(0);  
         // 得到总行数  
         int rowNum = sheet.getLastRowNum();  
        return rowNum;
    }
    
    
    
    
    private Object getCellFormatValue(Cell cell) {  
        Object cellvalue = "";  
        if (cell != null) {  
            // 判断当前Cell的Type  
            switch (cell.getCellType()) {  
            // 如果当前Cell的Type为NUMERIC  
            case Cell.CELL_TYPE_NUMERIC:  
            	cellvalue=cell.getNumericCellValue();
            case Cell.CELL_TYPE_FORMULA: {  
                // 判断当前的cell是否为Date  
                if (DateUtil.isCellDateFormatted(cell)) {  
                    // 如果是Date类型则，转化为Data格式  
                    //方法1：这样子的data格式是带时分秒的：2011-10-12 0:00:00  
                    //cellvalue = cell.getDateCellValue().toLocaleString();  
                    //方法2：这样子的data格式是不带带时分秒的：2011-10-12  
                    Date date = cell.getDateCellValue();  
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");  
                    cellvalue = sdf.format(date);  
                }  
                // 如果是纯数字  
                else {  
                    // 取得当前Cell的数值  
                    cellvalue = cell.getNumericCellValue();  
                }  
                break;  
            }  
            // 如果当前Cell的Type为STRIN  
            case Cell.CELL_TYPE_STRING:  
                // 取得当前的Cell字符串  
                cellvalue = cell.getStringCellValue();  
                break;  
            // 默认的Cell值  
            default:  
                cellvalue = " ";  
            }  
        } else {  
            cellvalue = "";  
        }  
        return cellvalue;  
  
    }  
  
}
