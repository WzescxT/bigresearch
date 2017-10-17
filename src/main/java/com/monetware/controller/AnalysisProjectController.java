package com.monetware.controller;

import com.google.gson.Gson;
import com.monetware.model.analysis.AnalysisProject;
import com.monetware.model.analysis.AnalysisProjectCluste;
import com.monetware.model.analysis.AnalysisProjectWord;
import com.monetware.model.common.RtInfo;
import com.monetware.service.analyze.*;
import com.monetware.util.AuthUtil;
import com.monetware.util.POIUtil;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

/** 
 *@author  venbillyu 
 *@date 创建时间：2016年12月29日 上午10:01:55 
 *@describle 分析项目
 */
@RequestMapping("/analysis")
@Controller
public class AnalysisProjectController {
	@Autowired
	private AnalysisProjectService analysisProjectService;
	@Autowired
	private AnalysisProjectWordService analysisProjectWordService;
	@Autowired
	private WordSegmentService wordSegmentService;
	@Autowired
	private AnalysisProjectClusteService clusteService;
	@Autowired
	private AnalysisProjectClusteInfoService clusteInfoService;
	@Autowired
	private TextLibraryService textLibraryService;
	@Autowired
	private AnalysisProjectClassifyTrainingService trainingService;
	@Autowired
	private AnalysisProjectClassifyService classifyService;
	@Autowired
	private AnalysisSentimeService sentimeService;
	
	//获取当前用户分析项目
	@RequestMapping("/getUserProjects")
	@ResponseBody
	public RtInfo getAnalysisProjects(@RequestBody HashMap<String, Object> queryMap,@RequestHeader HttpHeaders headers) {
		String token = headers.getFirst("Authorization");
		int userId=AuthUtil.parseToken(token);
		long pageNow=Long.decode(queryMap.get("pageNow").toString());
		long pageSize=Long.decode(queryMap.get("pageSize").toString());
		String name = queryMap.get("name").toString();
		System.out.println("===================>"+name);
		RtInfo rtInfo=new RtInfo();
		Map<String, Object>infoMap=new HashMap<String, Object>();
		List<AnalysisProject> analysisProjects=analysisProjectService.getUserAnalysisProjects(name,userId,pageNow,pageSize);
		infoMap.put("analysisProjects", analysisProjects);
		long thisMonthItems = analysisProjectService.getMonthProjectNo(name,userId);
		long bigTotalItems=analysisProjectService.getUserAnalysisProjectsNo(name,userId);
		infoMap.put("bigTotalItems",bigTotalItems );
		infoMap.put("thisMonthItems",thisMonthItems );

	    rtInfo.setRt_info(infoMap);
		return rtInfo;
		}
	
		//创建分析项目
		@RequestMapping("/createProject")
		@ResponseBody
		public RtInfo createAnalysisProject(@RequestBody AnalysisProject analysisProject,@RequestHeader HttpHeaders headers) {
			RtInfo rtInfo= new RtInfo();
			try {
				String token = headers.getFirst("Authorization");
				int userId=AuthUtil.parseToken(token);
				analysisProject.setCreateUser(userId);
				String columnJson=analysisProjectService.getColumnJson(analysisProject.getTextlibraryId(),true);
				analysisProject.setSegmentColumn(columnJson);
				analysisProject.setClusteFields(columnJson);
				analysisProject.setClassifyFields(columnJson);
				analysisProject.setSentimeFields(columnJson);
				analysisProjectService.createAnalysisProject(analysisProject);
				rtInfo.setRt_msg("文本分析项目创建成功");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				rtInfo.setError_code(1);
				rtInfo.setError_msg("项目创建失败");
			}
			return rtInfo;
		}	
	
	
		//删除分析项目
		@RequestMapping(value = "/deleteProject", method = RequestMethod.POST)
		@ResponseBody
		public RtInfo deleteTextLibrary(@RequestBody HashMap<String, Long> paramMap) {
			RtInfo rtInfo=new RtInfo();
			rtInfo.setRt_msg("分析项目删除成功");
			long analysisProjectId=paramMap.get("analysisProjectId");
			//删除项目
			analysisProjectService.deleteAnalysisProject(analysisProjectId);
			//删除词频统计表
			analysisProjectWordService.deleteWords(analysisProjectId);
			return rtInfo;
		}
		
		//获取项目
		@RequestMapping(value="/getProject",method=RequestMethod.POST)
		@ResponseBody
		public RtInfo getProject(@RequestBody HashMap<String, Long> paramMap){
			RtInfo rtInfo=new RtInfo();
			long analysisProjectId=paramMap.get("analysisProjectId");
			AnalysisProject ap = analysisProjectService.getAnalysisProject(analysisProjectId);
			System.out.println("====>"+ap.getClusteFields());
			rtInfo.setRt_info(ap);
			return rtInfo;
		}
		
		
		
		//开始分词
		@RequestMapping(value="/wordSegment",method=RequestMethod.POST)
		@ResponseBody
		public RtInfo wordSegment(@RequestBody HashMap<String, Object> paramMap){
			RtInfo rtInfo=new RtInfo();
			rtInfo.setRt_msg("分词成功");
			long analysisProjectId=Long.decode(paramMap.get("analysisProjectId").toString());
			String segmentType=(String) paramMap.get("segmentType");
			String segmentColumn=new Gson().toJson(paramMap.get("segmentColumn"));
			System.out.println("分词方式==========》"+segmentType);
			AnalysisProject ap=new AnalysisProject();
			ap.setId(analysisProjectId);
			ap.setSegmentStatus(1);
			ap.setSegmentType(segmentType);
			ap.setSegmentColumn(segmentColumn);
			analysisProjectService.updateAnalysisProject(ap);
			wordSegmentService.wordSegment(analysisProjectId);
			ap.setSegmentStatus(2);
			analysisProjectService.updateAnalysisProject(ap);
			return rtInfo;
		}
		
		
		//显示词频
		@RequestMapping(value="/getWordsFrequency",method=RequestMethod.POST)
		@ResponseBody
		public RtInfo getWordsFrequency(@RequestBody HashMap<String, Object> queryMap,@RequestHeader HttpHeaders headers) {
			String token = headers.getFirst("Authorization");
			long pageNow= Long.decode(queryMap.get("pageNow").toString());
			long pageSize=Long.decode(queryMap.get("pageSize").toString());
			long analysisProjectId=Long.decode(queryMap.get("analysisProjectId").toString());
			Map<String, Boolean> natureMap=(Map<String, Boolean>) queryMap.get("natures");
			String natures=analysisProjectWordService.getNatures(natureMap);
			RtInfo rtInfo=new RtInfo();
			if (natures.equals("")) {
				rtInfo.setError_code(1);
				rtInfo.setError_msg("没有选择词性，无法查询");
			}else {
				long bigTotalItems=analysisProjectWordService.getWordsFrequencyNo(analysisProjectId,natures);
				List<AnalysisProjectWord> WordsFrequency=analysisProjectWordService.getWordsFrequency(analysisProjectId,natures,pageNow,pageSize);
				rtInfo.setRt_mapinfo("WordsFrequency", WordsFrequency);
				rtInfo.setRt_mapinfo("bigTotalItems", bigTotalItems);
			}
			
			return rtInfo;
		}

		//导出词频excel文件







	@RequestMapping("/wordFrequency/exportExcel")
	@ResponseBody
	public ResponseEntity<byte[]> Export(@RequestBody HashMap<String, Object> queryMap,@RequestHeader HttpHeaders headers)  throws IOException {
		String token = headers.getFirst("Authorization");
		int userId=AuthUtil.parseToken(token);

		long analysisProjectId=Long.decode(queryMap.get("analysisProjectId").toString());
		Map<String, Boolean> natureMap=(Map<String, Boolean>) queryMap.get("natures");
		String natures=analysisProjectWordService.getNatures(natureMap);

		long bigTotalItems=analysisProjectWordService.getWordsFrequencyNo(analysisProjectId,natures);
		List<AnalysisProjectWord> WordsFrequency=analysisProjectWordService.getWordsFrequency(analysisProjectId,natures,1,bigTotalItems);


		List<Map<String, Object>> headInfoList = new ArrayList<Map<String,Object>>();

		String column[] = {"word","nature","frequency"};
		for (int i=0;i<column.length;i++){
			Map<String, Object> itemMap = new HashMap<String, Object>();

			itemMap.put("title", column[i]);
			itemMap.put("columnWidth", 50);
			itemMap.put("dataKey", column[i]);
			headInfoList.add(itemMap);
		}

		List<Map<String, Object>> dataList = new ArrayList<Map<String,Object>>();


		for (AnalysisProjectWord word:WordsFrequency) {
			Map<String, Object> dataItem = new HashedMap();
			dataItem.put("word", word.getWord());
			dataItem.put("nature",word.getNature());
			dataItem.put("frequency",word.getFrequency());
			dataList.add(dataItem);
		}

		String projectPath =  "D:/bigresearch/"+userId+"/analysis/"+analysisProjectId;
		String excelPath = projectPath+"/"+Math.random()+".xls";
		File file2 = new File(projectPath);
		if(!file2.exists()){
			file2.mkdirs();
		}

			POIUtil.exportExcelFilePath("sheet-1",excelPath, headInfoList, dataList);



		HttpHeaders fileHeaders = new HttpHeaders();
		fileHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);

	/*	String fileName = analysisProjectService.getAnalysisProject(analysisProjectId).getName()+"_词频统计.xls";

		fileName = URLEncoder.encode(fileName,"UTF8");
		System.out.println(fileName);*/

		fileHeaders.setContentDispositionFormData("attachment", "name.xls");
		return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(new File(excelPath)), fileHeaders,
				HttpStatus.CREATED);
	}










	/**
		 * 
		 * 算法问题解决：
		 * 
		 * kmeans算法：文本-->分词-->生成向量-->计算距离-->聚类结果
		 * 
		 * 
		 * 贝叶斯算法：
		 * 
		 * 
		 */
		
		
		
		
		//进行聚类分析
		@RequestMapping("/startCluste")
		@ResponseBody
		public RtInfo startCluste(@RequestBody AnalysisProject project){
			RtInfo rtInfo=new RtInfo();
			
			try {
				long projectId=project.getId();
				int algorithm=project.getClusteAlgorithm();
				String clusteField=project.getClusteFields();
				int clusteCount=project.getClusteCount();
				rtInfo = clusteService.startCluster(projectId, algorithm, clusteField, clusteCount);
			} catch (Exception e) {
				clusteService.deleteProjectCluster(project.getId());
				rtInfo.setError_code(1);
				rtInfo.setError_msg("聚类失败，请重试！");
				return rtInfo;
			}
			
			return rtInfo;
		}
		
		//获取聚类分类信息
		@RequestMapping("/getClusteResult")
		@ResponseBody
		public RtInfo getCluste(@RequestBody HashMap<String, Long> queryMap) {
			long projectId=queryMap.get("analysisProjectId");
			long pageNow=queryMap.get("pageNow");
			long pageSize=queryMap.get("pageSize");
			RtInfo rtInfo=new RtInfo();
			List<AnalysisProjectCluste> analysisProjectCluste=clusteService.getProjectCluste(projectId, pageNow, pageSize);
			long bigTotalItems=clusteService.getProjectClusteNo(projectId);
		    rtInfo.setRt_mapinfo("analysisProjectCluste", analysisProjectCluste);
		    rtInfo.setRt_mapinfo("bigTotalItems", bigTotalItems);
			return rtInfo;
		}
		
		//获取聚类详情信息
		@RequestMapping("/getClusteInfo")
		@ResponseBody
		public RtInfo getClusteInfo(@RequestBody HashMap<String, Long> queryMap) {
			//唯一
			long projectId = queryMap.get("analysisProjectId");
			//唯一
			long clusteId = queryMap.get("clusteId");
			long pageNow = queryMap.get("pageNow");
			long pageSize = queryMap.get("pageSize");
			System.out.println("====clusteId====>"+clusteId);
			RtInfo rtInfo=new RtInfo();
			List<Map<String, String>> clusteInfo=clusteInfoService.getClusteInfo(projectId, clusteId, pageNow, pageSize);
			long textNo=clusteInfoService.getClusteInfoNo(projectId, clusteId);
			System.out.println("===textNo======>"+textNo);
			/*AnalysisProject analysisProject=analysisProjectService.getAnalysisProject(projectId);
			long textLibraryId=analysisProject.getTextlibraryId();
			
			
			
			
			RtInfo rtInfo=new RtInfo();
			List<AnalysisProjectClusteInfo> clusteInfos=clusteInfoService.getClusteInfo(projectId, clusteId, pageNow, pageSize);
			long textNo=clusteInfoService.getClusteInfoNo(projectId, clusteId);
			String columnSql=analysisProject.getClusteFields();
			
			List<Map<String, String>> texts=new ArrayList<Map<String, String>>();
			for (AnalysisProjectClusteInfo clusteInfo : clusteInfos) {
				long textInfoId=clusteInfo.getTextInfoId();
				Map<String, String> text=new HashMap<String,String>();
				
				text=textLibraryService.getOneInfo(textLibraryId, columnSql, textInfoId);
				
				texts.add(text);
						
			}
			*/
			
			rtInfo.setRt_mapinfo("analysisProjectClusteInfo", clusteInfo);
			rtInfo.setRt_mapinfo("bigTotalItems", textNo);
			return rtInfo;
		}
		

	
		//进行分类分析
		@RequestMapping("/startClassify")
		@ResponseBody
		public RtInfo startClassify(@RequestBody AnalysisProject project,@RequestHeader HttpHeaders headers) {
			RtInfo rtInfo= new RtInfo();
			String token = headers.getFirst("Authorization");
			int userId=AuthUtil.parseToken(token);
			
			try {
				project.setClassifyStatus(1);
				
				analysisProjectService.updateAnalysisProject(project);
				
				classifyService.userId = userId;
				classifyService.projectId = project.getId();
				
				classifyService.runTrainAndTest();
				
				classifyService.runLoadModelAndUse();
				rtInfo.setRt_msg("分类成功");;
				project.setClassifyStatus(2);
				project.setClassifyEndTime(new Date());
				analysisProjectService.updateAnalysisProject(project);
				
				
				
			} catch (Exception e) {
				project.setClassifyStatus(0);
				analysisProjectService.updateAnalysisProject(project);
				rtInfo.setError_code(1);
				rtInfo.setError_msg("分类失败，请重试！");
				return rtInfo;
			}
			
			return rtInfo;
		}
			
		//获取分类信息
		@RequestMapping("/getClassifyResult")
		@ResponseBody
		public RtInfo getClassifyResult(@RequestBody HashMap<String, Long> queryMap) {
			long projectId=queryMap.get("analysisProjectId");
			long pageNow=queryMap.get("pageNow");
			long pageSize=queryMap.get("pageSize");
			RtInfo rtInfo=new RtInfo();
			List<Map<String, String>> classifies=classifyService.getClassifyResult(projectId, pageNow, pageSize);
			long bigTotalItems=classifyService.getClassifyResultNo(projectId);
			rtInfo.setRt_mapinfo("classifyResult", classifies);
			rtInfo.setRt_mapinfo("bigTotalItems", bigTotalItems);
			return rtInfo;
		}
		
		
		
		
		//获取某一分类中的文本信息
		@RequestMapping("/getClassifyInfo")
		@ResponseBody
		public RtInfo getClassifyInfo(@RequestBody HashMap<String, Object> queryMap) {
			long projectId=Long.decode(queryMap.get("analysisProjectId").toString());
			AnalysisProject project = analysisProjectService.getAnalysisProject(projectId);
			long textlibraryId = project.getTextlibraryId();
			String categoryName = queryMap.get("categoryName").toString();
			long pageNow=Long.decode(queryMap.get("pageNow").toString());
			long pageSize=Long.decode(queryMap.get("pageSize").toString());
			RtInfo rtInfo=new RtInfo();
			List<Long> classifiyIdList=classifyService.getInfoByCategory(projectId, categoryName, pageNow, pageSize);
			
			List<Map<String, String>> textInfos = new ArrayList<Map<String, String>>();
					
					
			Map<String, String> textInfo = new HashMap<String , String >();
			for (Long id : classifiyIdList) {
				textInfo = textLibraryService.getOneInfo(textlibraryId, "*", id);
				textInfos.add(textInfo);
			}
			long bigTotalItems=classifyService.getInfoNoByCategory(projectId, categoryName);
			rtInfo.setRt_mapinfo("classifyInfos", textInfos);
			rtInfo.setRt_mapinfo("bigTotalItems", bigTotalItems);
			rtInfo.setRt_mapinfo("classifyFields", project.getClassifyFields());
			return rtInfo;
		}
		
		
		
		
		
		
		
		//进行情感分析
		@RequestMapping("/startSentime")
		@ResponseBody
		public RtInfo startSentime(@RequestBody AnalysisProject project) {
			RtInfo rtInfo= new RtInfo();
			try {

				project.setSentimeStatus(1);
				project.setSentimeStartTime(new Date());
				analysisProjectService.updateAnalysisProject(project);
				boolean bol = sentimeService.process(project.getId());
				if (bol) {
					rtInfo.setRt_msg("情感分析成功");
				}else{
					rtInfo.setError_code(1);
					rtInfo.setError_msg("情感分析失败，请重试！");
				}
			} catch (Exception e) {
				project.setSentimeStatus(0);
				analysisProjectService.updateAnalysisProject(project);
			}
			
			project.setSentimeStatus(2);
			project.setSentimeEndTime(new Date());
			analysisProjectService.updateAnalysisProject(project);
			return rtInfo;
		}
		
		
		
		
		
		
		
		//获取情感分析结果
		@RequestMapping("/getSentimeResult")
		@ResponseBody
		public RtInfo getSentimeResult(@RequestBody Map<String, Long> paramMap,@RequestHeader HttpHeaders headers) {
			RtInfo rtInfo= new RtInfo();
			try {
				long projectId = paramMap.get("analysisProjectId");
				long pageNow = paramMap.get("pageNow");
				long pageSize = paramMap.get("pageSize");
				
				
				rtInfo = sentimeService.getResult(projectId, pageNow, pageSize);
			} catch (Exception e) {
				rtInfo.setError_code(1);
				rtInfo.setError_msg("情感分析结果查询失败，请重试！");
				return rtInfo;
			}
			
			return rtInfo;
		}
		
		
		
		

		
		
		
		
		
		
		
		
		
		
}
