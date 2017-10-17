package com.monetware.service.collect;

import com.monetware.model.collect.CollectTemplate;
import com.monetware.model.collect.CollectTemplateField;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CollectMultipageService  implements PageProcessor{
	@Autowired
	private CollectTemplateService templateService;


	@Autowired
	private ContentPageProcessService contentPageProcessService;

	@Autowired
	private CollectLogService logService;

	private long templateId;

	private long projectId;

	public void setTemplateId(long templateId) {
		this.templateId = templateId;
		//设置站点信息
		CollectTemplate collectTemplate = templateService.getCollectTemplateById(templateId);
		site.setSleepTime(collectTemplate.getInterval()==null?(int) Math.random()*3000:collectTemplate.getInterval()+(int) Math.random()*3000);
		if (collectTemplate.getDomain()!=null&&StringUtils.isNotEmpty(collectTemplate.getDomain())){
			site.setDomain(collectTemplate.getDomain());
			//只有domain生效的时候，cookie设置才有效
			if (collectTemplate.getCookie()!=null&&StringUtils.isNotEmpty(collectTemplate.getCookie())){
				site.addHeader("Cookie",collectTemplate.getCookie());
			}

		}
	}

	public void setProjectId(long projectId){
		this.projectId = projectId;
	}

	//抓取网站的相关配置，包括编码、抓取间隔、重试次数等
	private Site site = Site.me().setTimeOut(120000).setRetryTimes(50).setCycleRetryTimes(10).setSleepTime(1000)
			.setUserAgent("Mozilla/5.0 (Windows; U; Windows NT 5.2) AppleWebKit/525.13 (KHTML, like Gecko) Chrome/0.2.149.27 Safari/525.13 ");
	    /**
	     * process the page, extract urls to fetch, extract the data and store
	     *
	     * @param page page
	     */
	    @Override
	    public void process(Page page){
	    	CollectTemplate collectTemplate = templateService.getCollectTemplateById(templateId);
	    	//第一次采集深度为1
	    	int depth=1;
	    	if (page.getRequest().getExtra("depth")!=null) {
	    		depth=(int) page.getRequest().getExtra("depth");
			}
	    	
	    	int MaxDepth=collectTemplate.getDepth();
	    	//超出搜索深度
	    	if (depth>MaxDepth) {
				return ;
			}
	    	String currentUrl=page.getUrl().toString();
    		String contentUrlRegex=collectTemplate.getContentUrlRegex();
    		List<CollectTemplateField> fields=collectTemplate.getFields();
	    	
	    		
	    	Html currentHtml=page.getHtml();
	    	
	    	
	    	
	    	try {
				if (currentUrl.matches(contentUrlRegex)) {
					//处理详情页
					contentPageProcessService.progressContentPage(page,fields);

				}
				
				
				//获取所有链接
				if (depth<MaxDepth) {
					System.out.println("================》获取页面所有链接《========");
					Map<String, Object> extras=new HashMap<String, Object>();
					extras.put("depth", depth+1);
					List<String> allLinks = currentHtml.links().all();
					System.out.println("========页面链接个数=======>"+allLinks.size());
					if (allLinks.size()>0) {
						for (String link : allLinks) {
							System.out.println("=====链接======>"+link);
							Request request=new Request();
							request.setUrl(link);
							request.setExtras(extras);
							if (link.matches(contentUrlRegex)){
								request.setPriority(1);

							}else{
								request.setPriority(-1);
							}
							page.addTargetRequest(request);
						}
						logService.info(projectId,"get  "+allLinks.size()+" links");
					}
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
	    	
	    	
	    	
	    	
	        
	    }
	    	

	    /**
	     * get the site settings
	     *
	     * @return site
	     * @see Site
	     */
	    @Override
	    public Site getSite() {
	        return site;
	    }


	 

}
