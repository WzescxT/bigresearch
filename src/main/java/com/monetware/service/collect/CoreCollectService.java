package com.monetware.service.collect;

import com.monetware.mapper.collect.CollectProjectMapper;
import com.monetware.model.collect.CollectTemplate;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.scheduler.PriorityScheduler;

import java.util.HashMap;
import java.util.Map;

/** 
 *@author  venbillyu 
 *@date 创建时间：2016年12月6日 下午3:33:17 
 *@describle 控制调用采集程序(统一调度)
 */
@Service
public class CoreCollectService {
	@Autowired
	private CollectTemplateService templateService;


	@Autowired
	private  CollectSinglepageService collectSinglepageService;
	@Autowired
	private DataBasePipeline dataBasePipeline;

	@Autowired
	private CollectListpageService collectListpageService;

	@Autowired
	private CollectMultipageService collectMultipageService;

	@Autowired
	private CollectLogService logService;


	
	public Map<String, Object> controlCollect(long projectId,long templateId){
		logService.info(projectId,"Start collecting");
		CollectTemplate ct = templateService.getCollectTemplateById(templateId);
		dataBasePipeline.setProjectId(projectId);
		Map<String, Object> resMap=new HashMap<String, Object>();
		String type=ct.getType();
    	if (type.startsWith("system")) {
    		if (type.equals("system-baidunews")) {
    			long start = System.currentTimeMillis();
				//初始化起止时间
		    	CollectBaiduNewsTitleService collectBaiduNewsTitleService=new CollectBaiduNewsTitleService();
		    	collectBaiduNewsTitleService.setProjectId(projectId);
		        try {
		        	for (String url : ct.getEntryUrls()) {
						if (StringUtils.isNotEmpty(url)&&url.matches(ct.getContentUrlRegex())) {
							Spider.create(collectBaiduNewsTitleService)
							.setDownloader(new MyHttpClientDownloader(projectId,logService))
			                .addUrl(url)
			                .thread(50).run();
						}
					}
				} catch (Exception e) {

				}
		        long end = System.currentTimeMillis();
		        System.out.println((end-start)/1000);
			}
			
		}else{
			//自定义模板
			if (type.equals("user-singlepage")) {
				//单页采集
				for (String url : ct.getEntryUrls()) {
					if (StringUtils.isNotEmpty(url)&&url.matches(ct.getContentUrlRegex())) {
						collectSinglepageService.setTemplateId(templateId);
						dataBasePipeline.setProjectId(projectId);
						Spider.create(collectSinglepageService)
								.addPipeline(dataBasePipeline)
								.setDownloader(new MyHttpClientDownloader(projectId,logService))
//								.setDownloader(new SeleniumDownloader("E:/tool/chrome/chromedriver.exe"))
		                		.addUrl(url)
		                		.thread(20).run();
					}
				}
			}else if (type.equals("user-listpage")) {
				System.out.println("int user-listpage");
				//分页模板
				collectListpageService.setTemplateId(templateId);
				collectListpageService.setProjectId(projectId);

				for (String url : ct.getEntryUrls()) {
					if (StringUtils.isNotEmpty(url)) {
						Spider.create(collectListpageService)
								.addPipeline(dataBasePipeline)
								.setScheduler(new PriorityScheduler())
								.setDownloader(new MyHttpClientDownloader(projectId,logService))
//								.setDownloader(new SeleniumDownloader("E:/tool/chrome/chromedriver.exe"))
//								.setDownloader(new HttpClientDownloader())
//								.setDownloader(new PhantomJSDownloader("E:/tool/webmagictool/phantomjs/bin/phantomjs.exe","E:/tool/webmagictool/phantomjs/bin/crawl.js"))
//								.setDownloader(new MyHtmlUnitDownloader())
		                		.addUrl(url)
		                		.thread(10).run();
					}
				}
			}else if (type.equals("user-multipage")) {
				//分层模板(广度搜索)
				collectMultipageService.setTemplateId(templateId);
				for (String url : ct.getEntryUrls()) {
					if (StringUtils.isNotEmpty(url)) {
						Spider.create(collectMultipageService)
								.addPipeline(dataBasePipeline)
								.setScheduler(new PriorityScheduler())
								.setDownloader(new MyHttpClientDownloader(projectId,logService))
		                		.addUrl(url)
		                		.thread(20).run();
					}
				}
			}
		}
		logService.info(projectId,"Spider is finished");
		return resMap;
	}
	
	
	
	
}
