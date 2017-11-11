package com.monetware.controller;

import com.google.gson.Gson;
import com.monetware.model.collect.CollectBaiduNewsConfig;
import com.monetware.model.collect.CollectSinaNewsConfig;
import com.monetware.model.collect.CollectTemplate;
import com.monetware.model.collect.CollectTemplateField;
import com.monetware.model.common.RtInfo;
import com.monetware.service.collect.*;
import com.monetware.util.AuthUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *@author  venbillyu 
 *@date 创建时间：2016年11月25日 下午4:58:08 
 *@describle 采集配置
 */
@RequestMapping("/template")
@Controller
public class CollectTemplateController {

	@Autowired
	private CollectTemplateService collectTemplateService;

	@Autowired
	private SinaNewsUrlService sinaNewsUrlService;

	@Autowired
	private BaiduNewsUrlService baiduNewsUrlService;





	//创建模板
	@RequestMapping("/createCustom")
    @ResponseBody
    public RtInfo createTemplate(@RequestBody CollectTemplate collectTemplate, @RequestHeader HttpHeaders headers) {
		RtInfo rtInfo=new RtInfo();
		try {
	    String token = headers.getFirst("Authorization");
	    int usrId=AuthUtil.parseToken(token);
	    collectTemplate.setCreateUser(usrId);
//    	System.out.println(collectTemplate.toString());
    	
    		System.out.println(collectTemplate.toString());
    		collectTemplateService.createCollectTemplate(collectTemplate);
    		
    		rtInfo.setRt_msg("模板创建成功");
    		
    		
		} catch (Exception e) {
			System.out.println(e);
			rtInfo.setError_code(1);
			rtInfo.setError_msg("模板创建失败");
		}
        
        return rtInfo;
    }
    
	
	 //获取模板
		@RequestMapping("/getTemplate")
	    @ResponseBody
	    public HashMap<String, Object> getTemplate(@RequestBody CollectTemplate collectTemplate) {
	    	System.out.println(collectTemplate.toString());
	    	HashMap<String, Object> resMap = new HashMap<String, Object>();
	    	resMap.put("error_code", "0");
	    	try {
	    		CollectTemplate ct=collectTemplateService.getCollectTemplate(collectTemplate);
	    		resMap.put("info", ct);
	    		
			} catch (Exception e) {
				System.out.println(e);
				resMap.put("error_code", "1");
				resMap.put("error_msg", "创建失败");
			}
	        
	        return resMap;
	    }
		
		
		//删除项目
		@RequestMapping("/deleteOne")
		@ResponseBody
		public RtInfo deleteProject(@RequestBody HashMap<String, Long> queryMap,@RequestHeader HttpHeaders headers) {
			RtInfo rtInfo=new RtInfo();
			rtInfo.setRt_msg("模板删除成功");
			try {
				long templateId=queryMap.get("templateId");
				String token = headers.getFirst("Authorization");
				int userId=AuthUtil.parseToken(token);
				collectTemplateService.deleteUserTemplate(templateId, userId);
			} catch (Exception e) {
				rtInfo.setError_code(1);
				rtInfo.setError_msg("操作错误，无法删除项目");
			}
			        
			return rtInfo;
		}
		
				
				
				
				
				
		@RequestMapping("/getUserTemplates")
		@ResponseBody
		public RtInfo getUserTemplates(@RequestBody HashMap<String, Object> queryMap,@RequestHeader HttpHeaders headers) {
			String token = headers.getFirst("Authorization");
		    int userId=AuthUtil.parseToken(token);
		    queryMap.put("userId", Long.decode(String.valueOf(userId)));
		    queryMap.put("pageStart",(Long.decode(String.valueOf(queryMap.get("pageNow")))-1)*Long.decode(String.valueOf(queryMap.get("pageSize"))));
		    String name = (String)queryMap.get("name")==null?"":(String)queryMap.get("name");
			RtInfo rtInfo=new RtInfo();
			Map<String, Object>infoMap=new HashMap<String, Object>();
			List<CollectTemplate> templates=collectTemplateService.getUserTemplates(queryMap);
			infoMap.put("templates", templates);
			
			long bigTotalItems=collectTemplateService.getUserTemplatesNo(userId,name);
			
			
			long thisMonthItems=collectTemplateService.getMonthTemplatesNo(userId,name);
			
			
			infoMap.put("bigTotalItems",bigTotalItems );
			infoMap.put("thisMonthItems",thisMonthItems );
	    	rtInfo.setRt_info(infoMap);
			return rtInfo;
			
		}


		@RequestMapping("/getTemplateById")
		@ResponseBody
		public RtInfo getTemplateById(@RequestBody HashMap<String, Long> queryMap) {

			RtInfo rtInfo=new RtInfo();
			try {
				long templateId = queryMap.get("templateId");
				CollectTemplate template = collectTemplateService.getCollectTemplateById(templateId);

				rtInfo.setRt_info(template);
			} catch (Exception e) {
				rtInfo.setError_code(1);
				rtInfo.setError_msg("没有查询到结果！");
			}


			return rtInfo;

		}

	
		
		
		
		
		
		@RequestMapping("/getUserAllTemplates")
		@ResponseBody
		public RtInfo getUserAllTemplates(@RequestHeader HttpHeaders headers) {
			String token = headers.getFirst("Authorization");
		    int userId=AuthUtil.parseToken(token);
			RtInfo rtInfo=new RtInfo();
			List<CollectTemplate> templates=collectTemplateService.getUserAllTemplates(userId);
	    	rtInfo.setRt_info(templates);
			return rtInfo;
			
		}
		
		
		
		
		
		
		
		
		
		
		
		
	
	
	
	//新浪新闻采集配置
    @RequestMapping("/sinaNewsConfig")
    @ResponseBody
    public RtInfo sinaNewsConfig(@RequestBody CollectSinaNewsConfig sinaNewsConfig,@RequestHeader HttpHeaders headers) {
    	RtInfo rtInfo=new RtInfo();
    	rtInfo.setError_code(0);
    	rtInfo.setRt_msg("配置创建成功");
    	try {
    		String token = headers.getFirst("Authorization");
    	    int usrId=AuthUtil.parseToken(token);
    	    CollectTemplate ct=new CollectTemplate();
    	    ct.setCreateUser(usrId);
        	
    		String configBeanJson=new Gson().toJson(sinaNewsConfig);
    		String url=sinaNewsUrlService.getUrl(configBeanJson);
    		List<String> urls=new ArrayList<String>();
    		urls.add(url);
    		List<CollectTemplateField> fields=new ArrayList<CollectTemplateField>();
    		String columns[]={"title","publish_time","source","content"};
    		
    		for (int i = 0; i < columns.length; i++) {
    			CollectTemplateField field=new CollectTemplateField();
				field.setName(columns[i]);
				if(i==0){
					field.setContentXpath("//h1/text()");
				}else if (i==1){
					field.setContentXpath("//span[@class='time-source']/allText()");
				}else if (i==2){
					field.setContentXpath("//span[@class='time-source']//span/allText()");
				}else if (i==3){
					field.setContentXpath("//div[@id='artibody']/allText()");
				}
				if (i==0) {
					field.setRequire(true);
					
				}else {
					field.setRequire(false);
				}
				fields.add(field);
			}
    		//添加采集内容和正则语法
    		ct.setName(sinaNewsConfig.getName());
    		ct.setEntryUrls(urls);
    		ct.setDepth(2);
    		ct.setDescription(sinaNewsConfig.getDescrible());
    		ct.setContentUrlXpath("//h2/a/@href");
    		ct.setContentUrlRegex(".*");
    		ct.setFields(fields);
    		ct.setListUrlRegex(".*search.sina.com.cn.*");
    		ct.setNextUrlXpath("//a[@title='下一页']/@href");
    		ct.setType("user-listpage");
    		
    		collectTemplateService.createCollectTemplate(ct);
			} catch (Exception e) {
				rtInfo.setError_code(1);
				rtInfo.setError_msg("配置创建失败");
				e.printStackTrace();
			}
        return rtInfo;
    }
    
    
       
  //百度新闻采集配置
    @RequestMapping("/baiduNewsConfig")
    @ResponseBody
    public RtInfo baiduNewsConfig(@RequestBody CollectBaiduNewsConfig baiduNewsConfig,@RequestHeader HttpHeaders headers) {
    	RtInfo rtInfo=new RtInfo();
    	rtInfo.setError_code(0);
    	rtInfo.setRt_msg("配置创建成功");
    		try {
				String token = headers.getFirst("Authorization");
				int usrId=AuthUtil.parseToken(token);
				String configBeanJson=new Gson().toJson(baiduNewsConfig);
				String url=baiduNewsUrlService.getUrl(configBeanJson);
				System.out.println(url);
				long bt=Long.decode(StringUtils.substringBetween(url, "&bt=", "&"));
				long et=Long.decode(StringUtils.substringAfter(url, "&et="));
				
				long start = System.currentTimeMillis(); 
				//初始化起止时间
				url=url.replace(Long.toString(bt), "");
				url=url.replace(Long.toString(et), "");
				List<String> urls=new ArrayList<String>();
				
				long oneDayTime=3600*24;
					for (long i = bt; i < et; i=i+oneDayTime) {
						urls.add(url.replace("&bt=", "&bt="+i).replace("&et=", "&et="+(i+oneDayTime-1)));
					}
				CollectTemplate ct = new CollectTemplate();
				ct.setName(baiduNewsConfig.getName());
				ct.setDescription(baiduNewsConfig.getDescrible());
				ct.setType("system-baidunews");
				ct.setCreateUser(usrId);
				ct.setEntryUrls(urls);
				ct.setDepth(1);
				ct.setContentUrlRegex(".*");
				List<CollectTemplateField> fields=new ArrayList<CollectTemplateField>();
				String names[]={"title","publish_time","source","url"};
				for (int i = 0; i < names.length; i++) {
					CollectTemplateField ctf=new CollectTemplateField();
					ctf.setName(names[i]);
					ctf.setContentXpath("xpath by system");
					ctf.setRequire(false);
					fields.add(ctf);
				}
				ct.setFields(fields);
				collectTemplateService.createCollectTemplate(ct);
			} catch (Exception e) {
				System.out.println(e);
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				return rtInfo;
    
    } 
    
}
