package com.monetware.service.collect;

import com.monetware.model.collect.CollectTemplate;
import com.monetware.model.collect.CollectTemplateField;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.List;
@Component
public class CollectSinglepageService implements PageProcessor{
	@Autowired
	private CollectTemplateService templateService;


	@Autowired
	private ContentPageProcessService contentPageProcessService;

	private long templateId;


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
    		String contentUrlRegex=collectTemplate.getContentUrlRegex();
    		List<CollectTemplateField> fields=collectTemplate.getFields();
	    	try {
	    		
	        if (StringUtils.isNotEmpty(contentUrlRegex)&&currentUrl.matches(contentUrlRegex)) {
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

/*
	    public void progressContentPage(Html currentHtml,long projectId,List<CollectTemplateField> fields){

				//具体内容页取值
				String columns[]=new String[fields.size()];
				String columnValues[] = new String[fields.size()];
				for (int i = 0; i < fields.size(); i++) {
					CollectTemplateField filed=fields.get(i);
					String name=filed.getName();
					String context=currentHtml.xpath(filed.getContentXpath()).toString();
					boolean b=filed.isRequire();
					if (!StringUtils.isEmpty(context)) {
						System.out.println("contextbefore====>"+context);

						try {
							context = new String(context.getBytes(), "utf8");
						} catch (UnsupportedEncodingException e) {
							context = "";
						}

						context=context.replaceAll("[\\x{10000}-\\x{10FFFF}]", "");
						System.out.println("contextafter====>"+context);
						columns[i]=name;

						columnValues[i]=context;
					}else {
						if (b) {
							//跳过
//							page.setSkip(true);
							return;
						}else {
							//存空
							context="";
							columns[i]=name;
							columnValues[i]=context;
						}
					}
				}

			try {
				jdbcUtil.insertStr("collect_info_"+projectId, columns, columnValues);
			} catch (Exception e){
				System.out.println("insert error");
			}

			System.out.println("=========》结束数据库操作");


		}*/



}
