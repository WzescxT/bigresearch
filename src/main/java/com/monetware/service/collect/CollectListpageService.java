package com.monetware.service.collect;

import com.monetware.model.collect.CollectTemplate;
import com.monetware.model.collect.CollectTemplateField;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Xpath2Selector;

import java.util.List;

@Component
public class CollectListpageService  implements PageProcessor{

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
	private Site site = Site.me().setTimeOut(120000).setRetryTimes(5).setCycleRetryTimes(5)
		.setUserAgent("Mozilla/5.0 (Windows; U; Windows NT 5.2) AppleWebKit/525.13 (KHTML, like Gecko) Chrome/0.2.149.27 Safari/525.13 ");

	    /**
	     * process the page, extract urls to fetch, extract the data and store
	     *
	     * @param page page
	     */
	    @Override
	public void process(Page page){

	    CollectTemplate collectTemplate = templateService.getCollectTemplateById(templateId);
	    String currentUrl=page.getUrl().toString();
	    String nextUrlXpath=collectTemplate.getNextUrlXpath();
    	String contentUrlXpath=collectTemplate.getContentUrlXpath();
    	String listUrlRegex=collectTemplate.getListUrlRegex();
    	String contentUrlRegex=collectTemplate.getContentUrlRegex();
    	List<CollectTemplateField> fields=collectTemplate.getFields();
    	try {
	    		
	    	Html currentHtml= page.getHtml();

			//判断是否是列表页
	        if (currentUrl.matches(listUrlRegex)) {

				//处理列表页
				String nextPageUrl="";
				try{
					nextPageUrl=currentHtml.xpath(nextUrlXpath).toString();
					if (nextPageUrl==null||StringUtils.isEmpty(nextPageUrl)){
						throw  new Exception();
					}
				}catch (Exception e){
					Xpath2Selector xpath2Selector = new Xpath2Selector(nextUrlXpath);
					nextPageUrl = xpath2Selector.select(page.getRawText());

					if (nextPageUrl!=null&&nextPageUrl.startsWith("/")){
						nextPageUrl = "http://"+site.getDomain()+nextPageUrl;
					}


				}

	        	if (StringUtils.isNotEmpty(nextPageUrl)) {

	        		Request request=new Request();
	        		request.setUrl(nextPageUrl);
	        		request.setPriority(-1);
	        		
	        		page.addTargetRequest(request);

	        		logService.info(projectId,"get the next page link "+nextPageUrl+" and add it to the queue");

				}else{
					logService.warn(projectId,"fail to get the next page link");

				}
	        	List<String> detailPagesUrl;
	        	try{
					detailPagesUrl = currentHtml.xpath(contentUrlXpath).all();

				}catch (Exception e){

					Xpath2Selector xpath2Selector2 = new Xpath2Selector(contentUrlXpath);
					detailPagesUrl = xpath2Selector2.selectList(currentHtml.getDocument().text());
				}



	        	if (detailPagesUrl!=null&&detailPagesUrl.size()!=0) {
					//添加详情页
					page.addTargetRequests(detailPagesUrl, 1);

					logService.info(projectId,"get "+detailPagesUrl.size()+" detail pages");
				}else{
					logService.warn(projectId,"fail to get detail page");

				}
			}else if (StringUtils.isEmpty(contentUrlRegex)||currentUrl.matches(contentUrlRegex)) {
	        	//处理详情页
				contentPageProcessService.setProjectId(projectId);
				contentPageProcessService.progressContentPage(page,fields);
			}
	    	} catch (Exception e) {
				System.out.println(e);
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
