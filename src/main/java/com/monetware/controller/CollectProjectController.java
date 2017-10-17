package com.monetware.controller;

import com.monetware.model.collect.CollectInfo;
import com.monetware.model.collect.CollectLog;
import com.monetware.model.collect.CollectProject;
import com.monetware.model.collect.CollectTemplate;
import com.monetware.model.common.RtInfo;
import com.monetware.service.collect.*;
import com.monetware.util.AuthUtil;
import com.monetware.util.POIUtil;
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
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.*;

/** 
 *@author  venbillyu 
 *@date 创建时间：2016年11月25日 下午4:58:08 
 *@describle 采集项目
 */
@RequestMapping("/collect")
@Controller
public class CollectProjectController {
	@Autowired
    private CollectProjectService collectProjectService;
	@Autowired
	private CollectTemplateService collectTemplateService;
	@Autowired
	private CollectListpageService collectCommonService;
	@Autowired
	private SinaNewsUrlService sinaNewsUrlService;
	@Autowired
	private CollectInfoService collectInfoService;
	@Autowired
	private CoreCollectService coreCollectService;
	@Autowired
	private CollectLogService logService;
	
	
	@RequestMapping("/getProject")
    @ResponseBody
    public RtInfo getProject(@RequestBody HashMap<String, Long> queryMap) {
		long projectId=queryMap.get("projectId");
    	RtInfo rtInfo=new RtInfo();
    	try {
    		Map<String, Object> infoMap=new HashMap<String, Object>();
    		CollectProject project = collectProjectService.getCollectProject(projectId);
    		if (project==null) {
    			rtInfo.setError_code(1);
    			rtInfo.setError_msg("查询异常，没有此项目");
    			return rtInfo;
    		}
    		infoMap.put("project", project);
    		long templateId=project.getTemplateId();
    		CollectTemplate ct;

    		if (templateId>0) {

				try {
					ct = collectTemplateService.getCollectTemplateById(templateId)==null?null:collectTemplateService.getCollectTemplateById(templateId);
					infoMap.put("template", ct);
				} catch (Exception e) {
					Map<String,String> tempMap = new HashMap<String,String>();
					tempMap.put("error","模板已被删除，无法展示！");
					infoMap.put("template",tempMap);
				}


			}else {
				infoMap.put("template", "'error_msg':'尚未选择模板'");
			}
    		rtInfo.setRt_info(infoMap);
		} catch (Exception e) {

			rtInfo.setError_code(1);
			rtInfo.setError_msg("查询失败");
		}
        
        return rtInfo;
    }
	
	
	@RequestMapping("/createProject")
    @ResponseBody
    public RtInfo createProject(@RequestBody CollectProject collectProject,@RequestHeader HttpHeaders headers) {
		RtInfo rtInfo = new RtInfo();
		String token = headers.getFirst("Authorization");
		int userId=AuthUtil.parseToken(token);
		collectProject.setCreateUser(userId);
    	try {
    		rtInfo = collectProjectService.createCollectProject(collectProject);
    		if (rtInfo.getError_code()==1) {
				return rtInfo;
			}
    		CollectProject cp=collectProjectService.getCollectProjectByName(collectProject);
    		if (cp.getTemplateId()>0) {
    			collectInfoService.createInfoTable(cp.getId());
			}
			logService.createCollectLogTable(cp.getId());
    		rtInfo.setRt_info(cp);
		} catch (Exception e) {
			System.out.println(e);
			rtInfo.setError_code(1);
			rtInfo.setError_msg("创建失败");
		}
        
        return rtInfo;
    }
    
	
	
	
	
	
	//删除项目
		@RequestMapping("/deleteProject")
	    @ResponseBody
	    public RtInfo deleteProject(@RequestBody HashMap<String, Long> queryMap,@RequestHeader HttpHeaders headers) {
			long projectId=queryMap.get("projectId");
			String token = headers.getFirst("Authorization");
		    int usrId=AuthUtil.parseToken(token);
		    List<CollectProject> projects=collectProjectService.getCollectProjects(usrId);
			boolean exist=false;
		    if (projects!=null&&projects.size()>0) {
				for (CollectProject project : projects) {
					if (projectId==project.getId()) {
						exist=true;
						break;
					}
				}
			}
	    	RtInfo rtInfo=new RtInfo();
	    	if (exist) {
	    		logService.dropCollectLogTable(projectId);
				collectProjectService.deleteCollectProject(projectId);

				rtInfo.setRt_msg("项目删除成功");
			}else {
				rtInfo.setError_code(1);
				rtInfo.setError_msg("操作错误，无法删除项目");
			}
	        
	        return rtInfo;
	    }
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@RequestMapping("/getUserProjects")
    @ResponseBody
    public RtInfo getUserProjects(@RequestBody HashMap<String, Object> queryMap,@RequestHeader HttpHeaders headers) {
		String token = headers.getFirst("Authorization");
	    int userId=AuthUtil.parseToken(token);
	    queryMap.put("userId", Long.decode(String.valueOf(userId)));
	    queryMap.put("pageStart",(Long.decode(String.valueOf(queryMap.get("pageNow")))-1)*Long.decode(String.valueOf(queryMap.get("pageSize"))));
	    String name = (String)queryMap.get("name")==null?"":(String)queryMap.get("name");
		
		RtInfo rtInfo=new RtInfo();
		Map<String, Object>infoMap=new HashMap<String, Object>();
		List<CollectProject> projects=new ArrayList<CollectProject>();
		projects=collectProjectService.getPartCollectProjects(queryMap);
		infoMap.put("projects", projects);
		long bigTotalItems=collectProjectService.getCollectProjectsNo(userId,name);
		
		

		long thisMonthItems=collectProjectService.getMonthTemplatesNo(userId,name);
		



		infoMap.put("bigTotalItems", bigTotalItems);
		infoMap.put("thisMonthItems", thisMonthItems);
		rtInfo.setRt_info(infoMap);
		return rtInfo;
		
	}
	
    
	
		//启动公共配置项目采集
		@RequestMapping("/startCommonCollect")
	    @ResponseBody
	    public HashMap<String, Object> startCommonCollect(@RequestBody CollectProject collectProject) {
	    	System.out.println("=======>start collect");
	    	long projectId = collectProject.getId();
	    	System.out.println(collectProject.toString());
	    	HashMap<String, Object> resMap = new HashMap<String, Object>();
	    	CollectProject cp = collectProjectService.getCollectProject(collectProject.getId());
	    	resMap.put("error_code", "0");

	    	return resMap;
	    }
	
	
		
    
    @RequestMapping("/createInfo")
    @ResponseBody
    public HashMap<String, Object> createInfo(@RequestBody CollectInfo collectInfo) {
    	System.out.println(collectInfo.toString());
    	HashMap<String, Object> resMap = new HashMap<String, Object>();
    	resMap.put("error_code", "0");
    	collectInfoService.createCollectInfo(collectInfo);
    	return resMap;
    }
    
    
    
    @RequestMapping("/getCollectInfo")
    @ResponseBody
    public RtInfo getCollectInfo(@RequestBody HashMap<String, Long> queryMap) {
    	HashMap<String, Object> infoMap=new HashMap<String, Object>();
    	long projectId=queryMap.get("projectId");
    	System.out.println("参数传递："+queryMap.toString());
    	RtInfo rtInfo=new RtInfo();
    	queryMap.put("pageStart",(queryMap.get("pageNow")-1)*queryMap.get("pageSize"));
    	List<HashMap> collectInfos=collectInfoService.getCommonInfo(queryMap);
    	
    	infoMap.put("collectInfos", collectInfos);

    	long bigTotalItems=collectInfoService.getCollectInfoNo(projectId);
    	infoMap.put("bigTotalItems",bigTotalItems );
    	rtInfo.setRt_info(infoMap);
    	return rtInfo;
    }


    @RequestMapping("/deleteCollectInfo")
    @ResponseBody
    public RtInfo deleteCollectInfo(@RequestBody HashMap<String, Long> queryMap) {
    	long projectId=queryMap.get("projectId");
    	RtInfo rtInfo=new RtInfo();
    	collectInfoService.deleteCollectInfo(projectId);
    	CollectProject collectProject=new CollectProject();
    	collectProject.setId(projectId);
    	collectProject.setCollectNo(0);
    	collectProject.setStatus("准备采集");
    	collectProjectService.updateCollectProject(collectProject);
    	logService.dropCollectLogTable(projectId);
    	rtInfo.setRt_msg("数据删除成功");
    	return rtInfo;
    }
    
    @RequestMapping("/startCollect")
    @ResponseBody
    public RtInfo startCollect(@RequestBody CollectProject collectProject) {
    	RtInfo rtInfo=new RtInfo();
    	long start=0;
    	long projectId = 0;
    	long templateId = 0;
		try {
			projectId = collectProject.getId();
			logService.createCollectLogTable(projectId);
			CollectProject cp = collectProjectService.getCollectProject(projectId);
			//采集状态更改
			collectProject.setStatus("正在采集");
			collectProjectService.updateCollectProject(collectProject);
			templateId=cp.getTemplateId();

			CollectTemplate ct= null;
			try {
				ct = collectTemplateService.getCollectTemplateById(templateId);
			} catch (Exception e) {
				rtInfo.setError_code(1);
				rtInfo.setError_msg("该项目的采集模板失效，不再提供采集功能！");
				return rtInfo;
			}

			start = System.currentTimeMillis();
			coreCollectService.controlCollect(projectId, templateId);
		} catch (Exception e) {
			System.out.println("项目"+collectProject.getId()+"采集失败");
			e.printStackTrace();
		}
        long end = System.currentTimeMillis();
        long collectTime=end-start;
        //采集状态更改
        collectProject.setCollectTime(collectTime);
        collectProject.setStatus("完成采集");
        collectProject.setCollectNo(collectInfoService.getCollectInfoNo(collectProject.getId()));
        collectProjectService.updateCollectProject(collectProject);
        //保留两位小数
		DecimalFormat df = new DecimalFormat("#0.00");
        rtInfo.setRt_msg("成功采集信息，本次采集使用"+df.format(((double)end-start)/60000)+"分钟");
        return  rtInfo;
    }



	//采集情况展示
	@RequestMapping("/getCollectCondition")
	@ResponseBody
	public RtInfo getCollect(@RequestBody CollectProject collectProject) {
		RtInfo rtInfo = new RtInfo();
		rtInfo.setRt_info("");
		return rtInfo;
	}











	//导出excel表格
	/**
	 * 导出用户信息为excel表格
	 *
	 * @throws IOException
	 */

	@RequestMapping("/exportExcel/{userId}/{projectId}")
	@ResponseBody
	public ResponseEntity<byte[]> Export(@PathVariable("userId") Integer userId,@PathVariable("projectId") Long projectId)  throws IOException {
		HashMap<String, Object> infoMap=new HashMap<String, Object>();
		HashMap<String,Long>  queryMap= new HashMap<String,Long>();
		queryMap.put("projectId",projectId);

		long bigTotalItems=collectInfoService.getCollectInfoNo(projectId);

		queryMap.put("pageSize", bigTotalItems);

		queryMap.put("pageStart",0L);
		queryMap.put("bigTotalItems", bigTotalItems);
		List<HashMap> collectInfos=collectInfoService.getCommonInfo(queryMap);


		infoMap.put("collectInfos", collectInfos);
		infoMap.put("bigTotalItems",bigTotalItems );

		List<Map<String, Object>> headInfoList = new ArrayList<Map<String,Object>>();

		Iterator ite = collectInfos.get(0).entrySet().iterator();
		while (ite.hasNext()) {
			Map<String, Object> itemMap = new HashMap<String, Object>();

			Map.Entry entry = (Map.Entry) ite.next();
			Object key = entry.getKey();
			Object val = entry.getValue();
			itemMap.put("title", key);
			itemMap.put("columnWidth", 50);
			itemMap.put("dataKey", key);
			if(key.equals("id")==false && key.equals("Id")==false && key.equals("ID")==false){

				headInfoList.add(itemMap);

			}

		}



		List<Map<String, Object>> dataList = new ArrayList<Map<String,Object>>();




		Map<String, Object> dataItem = null;


		for(int i=0; i <collectInfos.size(); i++){
			dataItem = new HashMap<String, Object>();


			Iterator iter = collectInfos.get(i).entrySet().iterator();
			while (iter.hasNext()) {

				Map.Entry entry = (Map.Entry) iter.next();
				String key = entry.getKey().toString();
				String val = entry.getValue().toString();

				if(key.equals("id")==false && key.equals("Id")==false && key.equals("ID")==false){
					dataItem.put(key, val);
				}
			}




			dataList.add(dataItem);
		}


		String projectPath =  "D:/bigresearch/"+userId+"/collect/"+projectId;
		String excelPath = projectPath+"/"+Math.random()+".xls";
		File file2 = new File(projectPath);
		if(!file2.exists()){
			file2.mkdirs();
		}

		try {
			POIUtil.exportExcelFilePath("sheet-1",excelPath, headInfoList, dataList);

		}catch (Exception e){
			System.out.println(e);
		}

		HttpHeaders fileHeaders = new HttpHeaders();
		fileHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		String fileName = collectProjectService.getCollectProject(projectId).getName()+".xls";
		fileName = URLEncoder.encode(fileName,"UTF8");
		System.out.println(fileName);

		fileHeaders.setContentDispositionFormData("attachment", fileName);
		return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(new File(excelPath)), fileHeaders,
				HttpStatus.CREATED);
	}








	@RequestMapping("/getSearchProjects")
	@ResponseBody
	public RtInfo getSearchProjects(@RequestHeader HttpHeaders headers) {
		String token = headers.getFirst("Authorization");
		int userId=AuthUtil.parseToken(token);
		RtInfo rtInfo = new RtInfo();
		List<CollectProject> projects = collectProjectService.getSearchProjects(userId);
		rtInfo.setRt_info(projects);
		return rtInfo;

	}




	//获取采集日志信息
	@RequestMapping("/getCollectLogs")
	@ResponseBody
	public RtInfo getCollectLogs(@RequestBody HashMap<String,String> paramMap) {

		RtInfo rtInfo = new RtInfo();
		try {
			long projectId = Long.decode(paramMap.get("projectId"));
			String level = paramMap.get("level");
			long pageSize = 10;
			long pageStart = (Long.decode(paramMap.get("pageNow"))-1)*pageSize;

			List<CollectLog> collectLogs = logService.getCollectLogs(projectId , level, pageStart, pageSize);
			long maxPage = (long) Math.ceil((double)logService.getCollectLogNo(projectId , level)/(double)pageSize);
			rtInfo.setRt_mapinfo("collectLogs",collectLogs);
			rtInfo.setRt_mapinfo("maxPage",maxPage);
		}catch (Exception e){
			rtInfo.setRt_mapinfo("collectLogs",new ArrayList<CollectLog>());
			rtInfo.setRt_mapinfo("maxPage",0);
		}
		return rtInfo;

	}





	//获取采集日志信息
	@RequestMapping("/getLogData")
	@ResponseBody
	public RtInfo getLogData(@RequestBody HashMap<String,String> paramMap) {

		RtInfo rtInfo = new RtInfo();
		try {
			long projectId = Long.decode(paramMap.get("projectId"));
			Map<String,List> resMap = logService.getData(projectId);
			rtInfo.setRt_mapinfo("data",resMap.get("data"));
			rtInfo.setRt_mapinfo("labels",resMap.get("labels"));
		}catch (Exception e){
			rtInfo.setRt_mapinfo("data",new ArrayList<String>());
			rtInfo.setRt_mapinfo("labels",new ArrayList<String>());
		}

		return rtInfo;

	}











}
