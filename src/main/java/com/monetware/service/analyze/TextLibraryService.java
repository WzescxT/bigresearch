package com.monetware.service.analyze;

import com.monetware.mapper.analysis.TextLibraryInfoMapper;
import com.monetware.mapper.analysis.TextLibraryMapper;
import com.monetware.model.analysis.TextLibrary;
import com.monetware.util.DataUtil;
import com.monetware.util.ExcelUtil;
import com.monetware.util.jdbcUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

/** 
 *@author  venbillyu 
 *@date 创建时间：2016年12月27日 下午3:00:59 
 *@describle 文本库操作
 */
@Service
public class TextLibraryService {
	@Autowired
	private TextLibraryMapper textLibraryMapper;
	@Autowired
	private TextLibraryInfoMapper textLibraryInfoMapper;
	
	
	public List<TextLibrary> getUserTextLibraries(String name,int userId, long pageNow,long pageSize ){
	    Map<String, Object> queryMap=new HashMap<String, Object>();
	    queryMap.put("name",name);
	    queryMap.put("userId", Long.decode(String.valueOf(userId)));
	    queryMap.put("pageStart",(pageNow-1)*pageSize);
		queryMap.put("pageSize", pageSize);
		return textLibraryMapper.getUsertTextLibraries(queryMap);
		
	}

	public long getMonthTextLibraryNo(int userId,String name){
		Timestamp firstDay = DataUtil.getThisMonthFirstDay();
		return textLibraryMapper.getMonthTextLibraryNo(firstDay,userId,name);
	}


	public List<TextLibrary> getUserAllTextLibraries(int userId){
		return textLibraryMapper.getUserAllTextLibraries(userId);
	}
	
	
	
	public TextLibrary getTextLibrary(long textLibraryId){
		return textLibraryMapper.selectByPrimaryKey(textLibraryId);
		
	}
	public long getUserTextLibrariesNo(int userId){
		return textLibraryMapper.getUsertTextLibrariesNo(userId);
	}
	public void createTextLibrary(TextLibrary textLibrary) {
		textLibraryMapper.insertSelective(textLibrary);
		
	}
	
	public void updateTextLibrary(TextLibrary textLibrary){
		textLibraryMapper.updateByPrimaryKeySelective(textLibrary);
		
		
		
	}
	//删除文本表
	public void deleteText(long textLibraryId){
		textLibraryInfoMapper.dropTextLibraryInfoTable(textLibraryId);
		TextLibrary tl=new TextLibrary();
		tl.setId(textLibraryId);
		tl.setLineNo(0L);
		tl.setImportStatus(0);
		textLibraryMapper.updateByPrimaryKeySelective(tl);
	}
	//删除文本库表中的记录
	public void deleteTextLibrary(long textLibraryId){
		TextLibrary textLibrary = getTextLibrary(textLibraryId);
		if (textLibrary.getLineNo()!=null&&textLibrary.getLineNo()>0) {
			deleteText(textLibraryId);
		}
		textLibraryMapper.deleteByPrimaryKey(textLibraryId);
		
	}
	
	
	
	
	
	
	public String[] getColumns(ExcelUtil excelUtil,String fields){
		System.out.println("=====>get header");
		String header[]={};
		try {
			
			String title[]=excelUtil.readExcelTitle();
			if (fields.equals("*")) {
				header=title;
			}else {
				String field[]=fields.split(" ");
				int m=0;
				for (int i = 0; i < title.length; i++) {
					for (int j = 0; j < field.length; j++) {
						if (title[i]==field[j]) {
							header[m]=title[i];
							m=m+1;
						}
					}
				}
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("====>遍历header");
		for (String str : header) {
			System.out.println("=====header====>"+str);
		}
		return header;
	}
	
	
	
	
	
	public void importExcel(ExcelUtil excelUtil,Long textlibraryId,String fields) {
		try {
			System.out.println("=====>import");
			String header[]=getColumns(excelUtil, fields);
			
			int maxLineNo=excelUtil.getLineNo();
			
			String tableName="analysis_textLibrary_info_"+textlibraryId;
			List<String[]> objs=new ArrayList<String[]>();
			int pageNo=(int) Math.ceil((double)maxLineNo/1000);
			for (int i = 0; i < pageNo; i++) {
				objs = excelUtil.readExcelContent( i, header);
				jdbcUtil.insertStrs(tableName, header, objs);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e);
			e.printStackTrace();
		}
		
		
		
		
	}

	public void createTextLibraryInfoTable(long textLibraryId, String sql) {
		textLibraryInfoMapper.createTextLibraryInfoTable(textLibraryId, sql);
		
	}
	
	public void dropTextLibraryInfoTable(long textLibraryId) {
		textLibraryInfoMapper.dropTextLibraryInfoTable(textLibraryId);
		
	}
	
	
	
	
	public long getTextLibraryInfoNo(long textLibraryId){
		return textLibraryInfoMapper.getTextLibraryInfoNo(textLibraryId);
		
	}
	
	
	
	
	public Map<String, String> getOneInfo(long textLibraryId,String columns,long id){
		return textLibraryInfoMapper.getOneInfo(textLibraryId, columns, id);
	}
	
	
	
	

}
