package com.monetware.service.analyze;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.monetware.mapper.analysis.AnalysisProjectMapper;
import com.monetware.mapper.analysis.TextLibraryInfoMapper;
import com.monetware.mapper.analysis.TextLibraryMapper;
import com.monetware.model.analysis.AnalysisProject;
import com.monetware.util.DataUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/** 
 *@author  venbillyu 
 *@date 创建时间：2016年12月29日 上午9:49:01 
 *@describle 分析项目处理
 */
@Service
public class AnalysisProjectService {
	@Autowired
	private AnalysisProjectMapper analysisProjectMapper;
	@Autowired
	private TextLibraryMapper textLibraryMapper;
	@Autowired
	private TextLibraryInfoMapper textLibraryInfoMapper;
	public List<AnalysisProject> getUserAnalysisProjects(String name,int userId, long pageNow,long pageSize){
		Map<String, Object> queryMap=new HashMap<String, Object>();
		queryMap.put("name",name);
	    queryMap.put("userId", Long.decode(String.valueOf(userId)));
	    queryMap.put("pageStart",(pageNow-1)*pageSize);
		queryMap.put("pageSize", pageSize);
		return analysisProjectMapper.getUserAnalysisProjects(queryMap);
	}

	public long getMonthProjectNo(String name,int userId){
		return analysisProjectMapper.getMonthProjectNo(name,userId,DataUtil.getThisMonthFirstDay());

	}
	
	public long getUserAnalysisProjectsNo(String name,int userId){
		return analysisProjectMapper.getUserAnalysisProjectsNo(name,userId);
	}
	
	public void deleteAnalysisProject(long id){
		analysisProjectMapper.deleteByPrimaryKey(id);
	}
	
	public void createAnalysisProject(AnalysisProject analysisProject){
		analysisProjectMapper.insertSelective(analysisProject);
	}
	
	public AnalysisProject getAnalysisProject(long id){
		return analysisProjectMapper.selectByPrimaryKey(id);
	}
	
	public void updateAnalysisProject(AnalysisProject ap){
		analysisProjectMapper.updateByPrimaryKeySelective(ap);
	}
	
	
	public String getColumnJson(long textlibraryId,boolean bol){
		//获取列名
		JsonObject columnJson=new JsonObject();
		List<Map<String, String>> infoList=textLibraryInfoMapper.getInfoByPage(textlibraryId, 1, 1);
		for (Map<String, String> map : infoList) {
			for (Map.Entry<String, String> entry : map.entrySet()) {
				if (!entry.getKey().equals("id")) {
					columnJson.addProperty(entry.getKey(), bol);
				}
			}
		}
		return new Gson().toJson(columnJson);
	
	}
	
	//获取有效列 sql 
		//column1,column2,column3
		//传入json字符串 是否额外添加id    输出有效列
		public String getColumnSql(String fieldsJson,boolean addIdBol){
			Map<String, Boolean> columnMap=new HashMap<String, Boolean>();
			columnMap=new Gson().fromJson(fieldsJson, columnMap.getClass());
			String columnSql="";
			if (addIdBol) {
				columnSql="id,";
			}
			for (Entry<String, Boolean> entry : columnMap.entrySet()) {
				if (entry.getValue().equals(true)) {
					
					columnSql=columnSql+entry.getKey()+",";
				}
			}
			if (!columnSql.equals("")) {
				
				columnSql=columnSql.substring(0, columnSql.length()-1);
			}
			return columnSql;
			
		}
	
		
		
	
	
}