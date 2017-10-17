package com.monetware.controller;
import com.monetware.mapper.search.ImportSearchMapper;
import com.monetware.model.collect.CollectProject;
import com.monetware.model.collect.CollectTemplateField;
import com.monetware.model.common.RtInfo;
import com.monetware.service.collect.CollectProjectService;
import com.monetware.service.collect.CollectTemplateService;
import com.monetware.service.search.ImportSolrService;
import com.monetware.util.AuthUtil;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@RequestMapping("/search")
@Controller
public class SearchController {	
	
	@Autowired
	private ImportSearchMapper importSearchMapper;
	
	@Autowired
    private CollectProjectService collectProjectService;

	@Autowired
	private CollectTemplateService templateService;

	@Autowired
	private ImportSolrService importSolrService;



//vvvvvv venbill
	//数据导入solr，创建索引
	@RequestMapping("/importSolr")
	@ResponseBody
	public RtInfo importSolr(@RequestBody HashMap<String, Long> queryMap) {
		long projectId=queryMap.get("id");
		RtInfo rtInfo=new RtInfo();
		boolean bol=false;
		bol = importSolrService.importDocs(projectId);

		if (bol){
			CollectProject project =  collectProjectService.getCollectProject(projectId);
			project.setImportSearchStatus(2);
			collectProjectService.updateCollectProject(project);

			rtInfo.setRt_msg("导入成功,可以进行搜索了！");
		}else {
			rtInfo.setError_code(1);
			rtInfo.setError_msg("导入失败，请重试！");
		}

		return rtInfo;
	}




























	@RequestMapping("/listSearchModel")
	@ResponseBody
	public RtInfo Searh(@RequestBody HashMap<String, String> queryMap,@RequestHeader HttpHeaders headers) throws Exception{
		
		RtInfo rtInfo=new RtInfo();

		HashMap<String, Object> infoMap=new HashMap<String, Object>();
		
		String token = headers.getFirst("Authorization");
	    int userId=AuthUtil.parseToken(token);
		System.out.println("userId==>"+userId);
		queryMap.put("userId", String.valueOf(userId));


	    
	    System.out.println(userId+"wwwwwwwwwwwwwwwwwwwwwwwww");
		
		String searchStr=queryMap.get("searchStr");
		String act=queryMap.get("act");
		if (StringUtils.isEmpty(act)){
			rtInfo.setError_code(1);
			rtInfo.setError_msg("请先选择项目进行搜索！");
			return rtInfo;
		}

		long projectId = Long.decode(act);
		CollectProject searchProject = collectProjectService.getCollectProject(projectId);
		List<CollectTemplateField> fields = templateService.getCollectTemplateById(searchProject.getTemplateId()).getFields();
		String [] columns = new String[fields.size()];
		int i=0;
		for (CollectTemplateField field:fields) {

			columns[i]=field.getName();
			i++;
		}

		rtInfo.setRt_mapinfo("columns",columns);
		rtInfo.setRt_mapinfo("searchProject",searchProject);


		int pageSize = Integer.parseInt(queryMap.get("pageSize"));
		int currentPage = Integer.parseInt(queryMap.get("pageNow"));
		
		
		  String url="http://localhost:9090/solr/search_node";
		   HttpSolrClient httpSolrClient = new HttpSolrClient(url);
		   httpSolrClient.setParser(new XMLResponseParser()); // 设置响应解析器
		   httpSolrClient.setConnectionTimeout(500); // 建立连接的最长时间
		    SolrQuery solrQuery = new SolrQuery(); // 构造搜索条件
//		    if(act==null || searchStr == null){
		    	
//		    }else if (act==null || searchStr != null) {
//	    	solrQuery.setQuery("*:"+searchStr); 
//		    }
//		    else if (act !=null && act.equals("CONTENT")) {
//		    	solrQuery.setQuery(act + ":" + searchStr);
//		    }else{
//		    	solrQuery.setQuery("TITLE:" + searchStr + " " + "OR" + " " + "CONTENT:" + searchStr);
//		    }
		    
		    //solrQuery.addFilterQuery("CREATE_USER:"+userId);
		   
		    if(act == "" && searchStr == ""){
		    	solrQuery.setQuery("*:*");
		    	solrQuery.addFilterQuery("solr_userId:"+userId);
		    	
		    }else if(act == "" && searchStr != ""){
		    	solrQuery.setQuery("COLUMN1:"+searchStr+" OR "+ "COLUMN1:"+searchStr+" OR "+ "COLUMN2:"+searchStr+" OR "+ "COLUMN3:"+searchStr+" OR "+ "COLUMN4:"+searchStr+" OR "+ "COLUMN5:"+searchStr+" OR "+ "COLUMN6:"+searchStr+" OR "+ "COLUMN7:"+searchStr+" OR "+ "COLUMN8:"+searchStr+" OR "+ "COLUMN9:"+searchStr+" OR "+ "COLUMN10:"+searchStr);

		    	solrQuery.addFilterQuery("solr_userId:"+userId);

		    }else if(act != "" && searchStr == ""){
		    	solrQuery.setQuery("*:*");
		    	solrQuery.addFilterQuery("solr_userId:"+userId);
		    	solrQuery.addFilterQuery("solr_projectId:"+act);
		    	
		    }else if(act != "" && searchStr != ""){
		    	solrQuery.setQuery(searchStr);
//		    	solrQuery.setQuery("COLUMN1:"+searchStr+" OR "+ "COLUMN1:"+searchStr+" OR "+ "COLUMN2:"+searchStr+" OR "+ "COLUMN3:"+searchStr+" OR "+ "COLUMN4:"+searchStr+" OR "+ "COLUMN5:"+searchStr+" OR "+ "COLUMN6:"+searchStr+" OR "+ "COLUMN7:"+searchStr+" OR "+ "COLUMN8:"+searchStr+" OR "+ "COLUMN9:"+searchStr+" OR "+ "COLUMN10:"+searchStr);

		    	solrQuery.addFilterQuery("solr_userId:"+userId);
		    	solrQuery.addFilterQuery("solr_projectId:"+act);
		    }
		    
		    solrQuery.setStart(pageSize*(currentPage-1));
		    solrQuery.setRows(pageSize);
		    
		    
		        QueryResponse queryResponse =  httpSolrClient.query(solrQuery);
		        SolrDocumentList results = queryResponse.getResults();
				   
				    
				   
					
				    long bigTotalItems = results.getNumFound();


		infoMap.put("bigTotalItems",bigTotalItems );

				/*    List<SearchModel> SearchModels  =  new ArrayList<SearchModel>();
				    
				    for(SolrDocument rs : results ){
				    	SearchModel    SearchModel =  new SearchModel ();
				    	SearchModel.setId((String)rs.get("id"));
				    	SearchModel.setCreateUser((String)rs.get("CREATE_USER"));
				    	SearchModel.setProjectId((String)rs.get("PROJECT_ID"));
				    	SearchModel.setColumn1((String)rs.get("COLUMN1"));
				    	SearchModel.setColumn2((String)rs.get("COLUMN2"));
				    	SearchModel.setColumn3((String)rs.get("COLUMN3"));
				    	SearchModel.setColumn4((String)rs.get("COLUMN4"));
				    	SearchModel.setColumn5((String)rs.get("COLUMN5"));
				    	SearchModel.setColumn6((String)rs.get("COLUMN6"));
				    	SearchModel.setColumn7((String)rs.get("COLUMN7"));
				    	SearchModel.setColumn8((String)rs.get("COLUMN8"));	
				    	SearchModel.setColumn9((String)rs.get("COLUMN9"));
				    	SearchModel.setColumn10((String)rs.get("COLUMN10"));
						SearchModels.add(SearchModel);
				   
				    	
				    }*/
				    
				    infoMap.put("results", results);

//				    rtInfo.setRt_info(infoMap);
				    
				    rtInfo.setRt_mapinfo("results", results);
				    rtInfo.setRt_mapinfo("bigTotalItems",bigTotalItems);


				    
				    System.out.println("======>OVER");
				    
				    
		  return rtInfo;
	}
	
	
	
	//采集库信息导入搜索库

	
	
    @RequestMapping("/getTableColumn")
    @ResponseBody
    public RtInfo getTableColumn(@RequestBody HashMap<String, Long> queryMap) {
    	HashMap<String, Object> infoMap=new HashMap<String, Object>();
    	
    	System.out.println("参数传递："+queryMap.toString());
    	RtInfo rtInfo=new RtInfo();
    	
    	
    	
    	List<HashMap> projectStatus = importSearchMapper.getProjectStatus(queryMap);
    	
    	String status = projectStatus.get(0).get("import_search_status").toString();
    	
    	System.out.println(projectStatus.get(0).get("import_search_status"));
    	
    	List<HashMap> tableColumn = null;
    	
//    	if(status.equals("0")){
    		 tableColumn = importSearchMapper.getTableColumn(queryMap);
//    	}else{
//    		
//    	}
    	
    	
    	
    	
    	infoMap.put("tableColumn", tableColumn);
    	
    	
    	
    	 List<String> headInfoList = new ArrayList<String>();
    	
    	 Iterator ite = tableColumn.get(0).entrySet().iterator();  
	       while (ite.hasNext()) {  
	    	 
	      	 
	           Map.Entry entry = (Map.Entry) ite.next();  
	           String key = entry.getKey().toString();  
	           Object val = entry.getValue();
	           
	           System.out.println(key+"============================================");
	           if(key.equals("id")==false && key.equals("Id")==false && key.equals("ID")==false){
	        	   
	        	   headInfoList.add(key); 
	        	   
	           }
	       
	       }
	       System.out.println("============================================");
	       
	       infoMap.put("headInfoList", headInfoList);
    	
    	
	       for(int i=0; i<headInfoList.size(); i++){
	    	   	System.out.println(headInfoList.get(i)+"================================");
	       }
	       System.out.println(headInfoList.size()+"================================");
	       
	       HashMap<String, Boolean> tableKey = new HashMap<String, Boolean>();
	       
	       
	       
	       for(int i=0; i<headInfoList.size(); i++){
	    	   
	    	   tableKey.put(headInfoList.get(i), true);
	       }
	       
	       
	       
	       infoMap.put("tableKey", tableKey);
	       
	       
	       
	       
	       
	       Iterator iter = tableKey.entrySet().iterator();  
	       while (iter.hasNext()) {  
	    	 
	      	 
	           Map.Entry entry = (Map.Entry) iter.next();  
	           String key = entry.getKey().toString();  
	           Object val = entry.getValue();
	           
	           System.out.println(key+"============================================");
	           System.out.println(val+"============================================");
	           
	       
	       }
	       
	       
	       
	       
	       
	       
    	rtInfo.setRt_info(infoMap);
    	
    	
    	return rtInfo;
    }
    
    
    
    
    
    @RequestMapping("/importSearchDB")
	@ResponseBody
	public RtInfo importSearchDB(@RequestBody HashMap<String, Object> queryMap,@RequestHeader HttpHeaders headers) throws Exception{
    	System.out.println("参数传递："+queryMap.toString());
    	CollectProject collectProject = new CollectProject();
    	collectProject.setImportSearchStatus(1);
    	String token = headers.getFirst("Authorization");
	    int userId=AuthUtil.parseToken(token);
	    
	    System.out.println(userId+"wwwwwwwwwwwwwwwwwwwwwwwww");
	    
	    queryMap.put("userId", userId);
    	
    	System.out.println(queryMap.get("id")+"================================="); 
    	
    	HashMap numMap = (HashMap) queryMap.get("id");
    	
    	System.out.println(numMap.get("id")+"=============<<<<<<<<<<<<<<<<<<<<<<<"); 
    	
    	Object num = numMap.get("id");
    	
    	System.out.println(queryMap.get("tableKey")+"================================="); 
//    	long projectId = importSearchModel.getId();
//    	System.out.println(importSearchModel.toString());
		
    	//importSearchMapper.importSearchDb(importSearchModel);
    	HashMap<String, String> tableKeySelect = (HashMap<String, String>) queryMap.get("tableKey");
    	List<String> selectKey = new ArrayList<String>();
    	Iterator iter = tableKeySelect.entrySet().iterator();  
	       while (iter.hasNext()) {  
	    	 
	      	 
	           Map.Entry entry = (Map.Entry) iter.next();  
	           String key = entry.getKey().toString();  
	           String val = entry.getValue().toString();
	           
	           System.out.println(key+"============================================");
	           System.out.println(val+"============================================");
	           
		       if(val.equals("true")){
		    	   
		    	   selectKey.add(key);
		       	}
	           
	       }
    	
	    String sqlSelect = "";
    	
		for(int i=0; i<selectKey.size(); i++){
			System.out.println(selectKey.get(i)+"++++++++++++++++++++++++++++++++");
			sqlSelect = "`"+selectKey.get(i)+"`,"+sqlSelect;
			
		}
		
		String sqlStr = sqlSelect.substring(0,sqlSelect.length()-1);
		
		System.out.println(sqlStr+"VVVVVVVVVVVVVVVVVVVVVVVV");
		
		String insertColumn = "";
		
		for(int i=selectKey.size(); i>0; i--){
			
			insertColumn = "`column"+i+"`,"+insertColumn;
			
		}
		
		String column = insertColumn.substring(0,insertColumn.length()-1);
		
		System.out.println(column+"VVVVVVVVVVVVVVVVVVVVVVVV");
		
		
		
		queryMap.put("sqlStr", sqlStr);
		
		queryMap.put("column", column);
		
		queryMap.put("num", num);
		
		System.out.println(collectProject.getImportSearchStatus()+"xxxxxxxxxxxxx");
		
		System.out.println(collectProject.getImportSearchStatus()+"feng-----------------------------------------------");
		
		importSearchMapper.importSearchDb(queryMap);
		
		collectProject.setImportSearchStatus(2);
		String status = collectProject.getImportSearchStatus() + "";
		
		
		queryMap.put("status", status);
		
		importSearchMapper.updateImportStatus(queryMap);
		RtInfo rtInfo=new RtInfo();
		
		return rtInfo;
		
	}
	
	
	
	

}
