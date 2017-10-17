package com.monetware.service.collect;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.catalina.core.ApplicationContext;
import org.apache.http.HttpHost;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.monetware.mapper.collect.CollectInfoMapper;
import com.monetware.model.collect.CollectInfo;
import com.monetware.model.collect.CollectSinaNewsConfig;
import com.monetware.util.httpHelper;
import com.monetware.util.jdbcUtil;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

@Component
public class CollectBaiduNewsTitleService  implements PageProcessor{
	private long projectId;
	
	@Autowired
	private CollectLogService logService;
	@Autowired
	private CollectInfoMapper collectInfoMapper;
	
	private Request request=new Request();
	
	public long getProjectId() {
		return projectId;
	}


	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}


		//抓取网站的相关配置，包括编码、抓取间隔、重试次数等
		 private Site site = Site.me().setTimeOut(500000000).setRetryTimes(50).setCycleRetryTimes(10).setSleepTime(1000)
				 .setUserAgent("Mozilla/5.0 (Windows; U; Windows NT 5.2) AppleWebKit/525.13 (KHTML, like Gecko) Chrome/0.2.149.27 Safari/525.13 ");
		 		
//				 .setHttpProxy(new HttpHost("111.13.7.42", 81));
//		            .setHttpProxy(new HttpHost("192.168.2.49", 8080))
//		            .setUserAgent(
//                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");
//		            .setUsernamePasswordCredentials(new UsernamePasswordCredentials("hepan", "abcdef"));
	    /**
	     * process the page, extract urls to fetch, extract the data and store
	     *
	     * @param page page
	     */
	    @Override
	    public void process(Page page){
	    	String columnNames[]={"url","title","publish_time","source"};	
	    	List<String[]> resList=new ArrayList<String[]>();
	    	try {
				
	    		System.out.println("======>start<=====");
	    		String currentUrl=page.getUrl().toString();
	    		System.out.println("====>goturl<====");
	    		Html currentHtml=page.getHtml();
	    		System.out.println("====>gothtml<====");
	    		System.out.println("====currentUrl"+currentUrl);
	    		
	    		System.out.println("====>getpn<====");
	    		int pn=Integer.parseInt(httpHelper.regexStr(currentUrl, "&pn=", "&"));
	    		System.out.println("pn:"+pn);
	    		
	    		
	    		String pageLink="";
	    		
	    		Selectable pageLinkSelectable=currentHtml.$("#page > a:last-child");
	    		
	    		System.out.println("====>next pageLink:"+pageLink);
	    		if (pageLinkSelectable!=null&&!pageLinkSelectable.equals("")&&!(pageLinkSelectable+"").equals("null")) {
	    			pageLink=pageLinkSelectable.regex("href=\"(.*)\"\\s").toString().replaceAll("&amp;", "&");
	    			page.addTargetRequest(pageLink);
					logService.info(projectId,"get the next page link "+pageLink+" and add it to the queue");
	    		}

	    		
	    		String newsTitle="";
	    		String newsUrl="";
	    		String newsSource="";
	    		String newsDate="";
	    		Selectable mainInfo;
	    		
    			
	    		pn=pn+1;
	    		for (int i = 0; i < 10; i++) {
	    			System.out.println("====>"+(pn+i));
	    			newsTitle=currentHtml.xpath("//div[@id="+(pn+i)+"]/h3[@class='c-title']/a/allText()").toString();
	    			newsUrl=currentHtml.xpath("//div[@id="+(pn+i)+"]/h3[@class='c-title']/a/@href").toString();
	    				mainInfo= currentHtml.xpath("//div[@id="+(pn+i)+"]/div[@class='c-title-author']/text()");
	    				if (mainInfo.toString().split("  ").length>1) {
	    					newsDate=mainInfo.toString().split("  ")[1];
	    					newsSource=mainInfo.toString().split("  ")[0];
						}else {
							newsDate=mainInfo.toString();
						}
	    			String []resObj={newsUrl,newsTitle,newsDate,newsSource};
	    			resList.add(resObj);
//	    			System.out.println(collectInfo.toString());
					logService.info(projectId,"got a news");
				}
	    		jdbcUtil.insertStrs("collect_info_"+projectId, columnNames, resList);
				logService.info(projectId,"store the data to the database successfully");
	    	} catch (Exception e) {
				logService.error(projectId,"there are some errors");
	    		try {
	    			jdbcUtil.insertStrs("collect_info_"+projectId, columnNames, resList);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
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
