package com.monetware.service.analyze;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.monetware.mapper.analysis.AnalysisDBMapper;
import com.monetware.mapper.analysis.TextLibraryMapper;
import com.monetware.model.analysis.TextLibrary;
import com.monetware.model.common.RtInfo;

/** 
 *@author  venbillyu 
 *@date 创建时间：2017年2月27日 下午3:20:56 
 *@describle 数据库分析
 */
@Service
public class AnalysisDBService {
	
	@Autowired
	private TextLibraryMapper libraryMapper;
	@Autowired
	private AnalysisDBMapper dbMapper;
	//过滤非法sql     false 错误请求
	public boolean checkIllegal(String sql){
		sql = StringUtils.lowerCase(sql);
		if (sql.contains("drop")||sql.contains("alter")||sql.contains("create")||sql.contains("table")) {
			return false;
		}
		
		return true;
	}
	
	//检查用户权限		false 没有权限
	public boolean checkPermissions(String textLibraryId,int userId){
		TextLibrary library = libraryMapper.getTextLibrary(Long.decode(textLibraryId));
		if (library.getCreateUser()==userId) {
			return true;
		}else {
			return false;
		}
	}
	
	
	
	
	
	
	
	
	
	
/*	//执行sql
	public boolean excuteSql(Map<String, String> paramMap){
		
		try {
			String type = paramMap.get("type");
			
			String textLibraryId = paramMap.get("textLibraryId");
			//slq方式
			if (type.equals("select")) {
				String columns = paramMap.get("columns");
				String condition = paramMap.get("condition");
				String pageStart = paramMap.get("pageStart");
				String pageSize = paramMap.get("pageSize");
				dbMapper.selectSql(columns, textLibraryId, condition, pageStart, pageSize);
			
			}else if (type.equals("count")) {
				String condition = paramMap.get("condition");
				dbMapper.countSql(textLibraryId, condition);
				
			}else if (type.equals("update")) {
				String setStr = paramMap.get("setStr");
				String condition = paramMap.get("condition");
				dbMapper.updateSql(textLibraryId, setStr, condition);
				
			}else if (type.equals("delete")) {
				String condition = paramMap.get("condition");
				dbMapper.deleteSql(textLibraryId, condition);
				
			}else if (type.equals("insert")) {
				String condition = paramMap.get("condition");
				dbMapper.deleteSql(textLibraryId, condition);
				
			}
		} catch (Exception e) {
			return false;
		}
		
		
		
		return true;
	}
	
	*/
	
	
	
	
		
		public List<Map<String, String>> select(String sqlStr,long pageNow,long pageSize){
			return dbMapper.select(sqlStr, (pageNow-1)*pageSize, pageSize);
			
		}
		
		public long countSelect(String sqlStr){
			return dbMapper.countSelect(sqlStr);
		}
		
		public long count(String sqlStr){
			return dbMapper.count(sqlStr);
		}
		
		
		public long delete(String sqlStr){
			return dbMapper.delete(sqlStr);
		}
		
		public long update(String sqlStr){
			return dbMapper.update(sqlStr);
		}
	
		public long insert(String sqlStr){
			return dbMapper.insert(sqlStr);
		}
	
}
