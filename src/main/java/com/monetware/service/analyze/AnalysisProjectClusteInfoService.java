package com.monetware.service.analyze;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.monetware.mapper.analysis.AnalysisProjectClusteInfoMapper;
import com.monetware.mapper.analysis.TextLibraryInfoMapper;
import com.monetware.mapper.analysis.TextLibraryMapper;
import com.monetware.model.analysis.AnalysisProject;
import com.monetware.model.analysis.AnalysisProjectCluste;
import com.monetware.model.analysis.AnalysisProjectClusteInfo;
import com.monetware.model.common.RtInfo;

/** 
 *@author  venbillyu 
 *@date 创建时间：2017年2月10日 下午2:53:30 
 *@describle 
 */
@Service
public class AnalysisProjectClusteInfoService {
	@Autowired
	private AnalysisProjectClusteInfoMapper clusteInfoMapper;
	@Autowired
	private TextLibraryInfoMapper infoMapper;
	@Autowired
	private AnalysisProjectService projectService;
	@Autowired
	private AnalysisProjectClusteService clusteService;
	public List<Map<String, String>> getClusteInfo(long projectId,long clusteId,long pageNow, long pageSize){
		Map<String, Long> queryMap=new HashMap<String, Long>();
		queryMap.put("clusteId", clusteId);
	    queryMap.put("projectId", projectId);
	    queryMap.put("pageStart",(pageNow-1)*pageSize);
		queryMap.put("pageSize", pageSize);

		List<AnalysisProjectClusteInfo> clusteInfos = clusteInfoMapper.getClusteInfo(queryMap);
		
		
		
		AnalysisProject analysisProject=projectService.getAnalysisProject(projectId);
		long textLibraryId=analysisProject.getTextlibraryId();
		
		String columnSql=clusteService.getColumnSql(analysisProject.getClusteFields(),false);
		
		List<Map<String, String>> texts=new ArrayList<Map<String, String>>();
		for (AnalysisProjectClusteInfo clusteInfo : clusteInfos) {
			long textInfoId=clusteInfo.getTextInfoId();
			Map<String, String> text=new HashMap<String,String>();
			
			text=infoMapper.getOneInfo(textLibraryId, columnSql, textInfoId);
			
			texts.add(text);
					
		}
		
		return texts;
		
	}
	
	//获取该类条数
	public long getClusteInfoNo(long projectId,long clusteId){
		return clusteInfoMapper.getClusteInfoNo(projectId,clusteId);
	}
	/*
	//整理聚类文本信息
	public List<Map<String, String>> getClusteInfo(long projectId,long clusteId,long pageNow,long pageSize){
		
		AnalysisProject analysisProject=projectService.getAnalysisProject(projectId);
		long textLibraryId=analysisProject.getTextlibraryId();
		
		
		
		
		List<AnalysisProjectClusteInfo> clusteInfos=getClusteInfo(projectId, clusteId, pageNow, pageSize);
		
		String columnSql=analysisProject.getClusteFields();
		
		List<Map<String, String>> texts=new ArrayList<Map<String, String>>();
		for (AnalysisProjectClusteInfo clusteInfo : clusteInfos) {
			long textInfoId=clusteInfo.getTextInfoId();
			Map<String, String> text=new HashMap<String,String>();
			
			text=infoMapper.getOneInfo(textLibraryId, columnSql, textInfoId);
			
			texts.add(text);
					
		}
		
		return texts;
		
		
		
	}*/
		
}
