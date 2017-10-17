package com.monetware.service.analyze;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.proto.GetACLRequest;
import org.carrot2.clustering.kmeans.BisectingKMeansClusteringAlgorithm;
import org.carrot2.clustering.lingo.LingoClusteringAlgorithm;
import org.carrot2.clustering.stc.STCClusteringAlgorithm;
import org.carrot2.core.Cluster;
import org.carrot2.core.Document;
import org.carrot2.core.LanguageCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.monetware.mapper.analysis.AnalysisProjectClusteInfoMapper;
import com.monetware.mapper.analysis.TextLibraryInfoMapper;
import com.monetware.mapper.analysis.AnalysisProjectClusteMapper;
import com.monetware.model.analysis.AnalysisProject;
import com.monetware.model.analysis.AnalysisProjectCluste;
import com.monetware.model.analysis.TextLibrary;
import com.monetware.model.common.RtInfo;
import com.monetware.service.analyze.AnalysisProjectService;
import com.monetware.service.analyze.TextLibraryService;


/** 
 *@author  venbillyu 
 *@date 创建时间：2017年2月8日 下午6:02:29 
 *@describle 聚类分析算法(使用carrot2开源jar包)
 */
@Service
public class AnalysisProjectClusteService {
	
	@Autowired
	private AnalysisProjectService projectService;
	@Autowired
	private TextLibraryService textLibraryService;
	@Autowired
	private TextLibraryInfoMapper infoMapper;
	@Autowired
	private AnalysisProjectClusteMapper clusteMapper;
	@Autowired
	private AnalysisProjectClusteInfoMapper clusteInfoMapper;
	
	
	
	
	//开始采集（分析项目id,使用聚类的算法，进行聚类的字段）
	public RtInfo startCluster(long projectId, int algorithm, String clusteField , int clusteCount) {
		RtInfo rtInfo=new RtInfo();
		try {
			deleteProjectCluster(projectId);
			updateProjectCluster(projectId, 1, algorithm, clusteField, clusteCount);
			AnalysisProject project = projectService.getAnalysisProject(projectId);
			clusteInfoMapper.createAnalysisProjectClusteInfoTable(projectId);
			new ClusterThread(projectId, project.getTextlibraryId(), algorithm, clusteField, clusteCount).start();
			rtInfo.setRt_msg("请求成功，正在聚类！");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return rtInfo;
	}
	
	public void deleteProjectCluster(long projectId) {
		//删除聚类信息    项目主表状态  聚类主表  聚类副表
		AnalysisProject analysisProject=new AnalysisProject();
		analysisProject.setId(projectId);;
		analysisProject.setClusteStatus(0);
		projectService.updateAnalysisProject(analysisProject);
		clusteMapper.deleteByProjectId(projectId);
		clusteInfoMapper.dropAnalysisProjectClusteInfoTable(projectId);
	}
	//更新聚类 	状态 算法  字段
	private void updateProjectCluster(long projectId, int clusteStatus, int clusteAlgorithm, String clusteField, int clusteCount){
		AnalysisProject project = new AnalysisProject();
		project.setId(projectId);
		project.setClusteStatus(clusteStatus);
		project.setClusteAlgorithm(clusteAlgorithm);
		project.setClusteFields(clusteField);
		project.setClusteCount(clusteCount);
		if(clusteStatus==1){
			project.setClusteStartTime(new Date());
			project.setClusteEndTime(null);
		} else if(clusteStatus==2){
			project.setClusteEndTime(new Date());
		}
		
		projectService.updateAnalysisProject(project);
	}
	
	private List<Document> getDocuments(long textlibraryId, String clusterField){
		final ArrayList<Document> documents = new ArrayList<Document>();
		
		String columnSql=getColumnSql(clusterField, true);
		long maxNo=infoMapper.getTextLibraryInfoNo(textlibraryId);
		List<Map<String, Object>> datas = infoMapper.getAllInfoByColumns(textlibraryId,columnSql);
		for(Map<String, Object> dataMap: datas){
			String id = dataMap.get("id").toString();
			
			Document d = new Document();
			d.setField("id", id);
			String summary = "";
			
			for (Entry<String, Object> mapSet : dataMap.entrySet()) {
				String key=mapSet.getKey();
				String value=mapSet.getValue().toString();
				if (!key.equals("id")&&!StringUtils.isEmpty(value)) {
					summary=summary+"\n"+value;
				}
						
			}
			
			
			d.setSummary(summary);
			d.setLanguage(LanguageCode.CHINESE_SIMPLIFIED);
			documents.add(d);
		}
		return documents;
	}
	
	private boolean processClusterResult(long projectId, Collection<Cluster> clusters){
		
		AnalysisProjectCluste projectCluste=new AnalysisProjectCluste();
		projectCluste.setProjectId(projectId);
		for(Cluster c: clusters){
			String clusterName = c.getLabel();
			long textNo = c.getAllDocuments().size();
			projectCluste.setTextNo(textNo);
			projectCluste.setCreateTime(new Date());
			projectCluste.setClusterName(clusterName);
			clusteMapper.insert(projectCluste);
			long clusterId = projectCluste.getId();
			
			
			for(Document d: c.getAllDocuments()){
				clusteInfoMapper.insert(projectId, clusterId, Long.decode(d.getField("id").toString()));
			}
		}
		AnalysisProject analysisProject=new AnalysisProject();
		analysisProject.setId(projectId);
		analysisProject.setClusteStatus(2);
		analysisProject.setClusteEndTime(new Date());
		projectService.updateAnalysisProject(analysisProject);
		return true;
	}
	
	class ClusterThread extends Thread{
		private long projectId;
		private long textlibraryId;
		private int algorithm;
		private String clusterField;
		private int clusteCount;
		
		public ClusterThread(long projectId, long textlibraryId, int algorithm, String clusterField, int clusteCount){
			this.projectId = projectId;
			this.textlibraryId = textlibraryId;
			this.algorithm = algorithm;
			this.clusterField = clusterField;
			this.clusteCount = clusteCount;
		}
		
		public void run() {
			System.out.println("cluster begin!");
			List<Document> documents = getDocuments(textlibraryId, clusterField);
			Collection<Cluster> clusters = null;
			if(algorithm==1){
				BisectingKMeansClusteringAlgorithm alg = new BisectingKMeansClusteringAlgorithm();
				alg.documents = documents;
				//设置聚类数量
				if (clusteCount!=0) {
					alg.clusterCount=clusteCount;
				}
				alg.process();
				clusters = alg.clusters;
			} else if(algorithm==2){
				STCClusteringAlgorithm alg = new STCClusteringAlgorithm();
				alg.documents = documents;
				if (clusteCount!=0) {
					alg.maxClusters=clusteCount;
				}
				alg.process();
				clusters = alg.clusters;
			} else if(algorithm==3){
				LingoClusteringAlgorithm alg = new LingoClusteringAlgorithm();
				alg.documents = documents;
				if (clusteCount!=0) {
					alg.desiredClusterCountBase=clusteCount;
				}
				alg.process();
				clusters = alg.clusters;
			} 
			processClusterResult(projectId, clusters);
			System.out.println("cluster end!");
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	//获取有效列 sql 
	//column1,column2,column3
	//传入json字符串 是否额外添加id    输出有效列
	public String getColumnSql(String clusterField,boolean addId){
		Map<String, Boolean> columnMap=new HashMap<String, Boolean>();
		System.out.println("===>"+clusterField);
		columnMap=new Gson().fromJson(clusterField, columnMap.getClass());
		String columnSql="";
		if (addId) {
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
	
	
	
	
	
	
	
	
	
	
	
	
	//查询显示方法
	
	//获取聚类主要信息
	public List<AnalysisProjectCluste> getProjectCluste(long projectId, long pageNow,long pageSize ){
		Map<String, Long> queryMap=new HashMap<String, Long>();
	    queryMap.put("projectId", projectId);
	    queryMap.put("pageStart",(pageNow-1)*pageSize);
		queryMap.put("pageSize", pageSize);
		return clusteMapper.getProjectCluste(queryMap);
	}
	
	//获取聚类条数
	public long getProjectClusteNo(long projectId){
		return clusteMapper.getProjectClusteNo(projectId);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
