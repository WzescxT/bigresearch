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
public class CollectBaiduNewsService  implements PageProcessor{
	private long collectNo=1;
	private long projectId;
	
	
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
//				 .setHttpProxy(new HttpHost("192.168.2.49", 8080))
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
	    	CollectInfo collectInfo = new CollectInfo();
	    	String columnNames[]={"project_id","url","title","publish_time","source","content"};	
	    	List<String[]> resList=new ArrayList<String[]>();
	    	try {
				
			
	    		System.out.println("======>start<=====");
	    		String currentUrl=page.getUrl().toString();
	    		System.out.println("====>goturl<====");
	    		Html currentHtml=page.getHtml();
	    		System.out.println("====>gothtml<====");
	    		
	    		
	    		System.out.println("====>getpn<====");
	    		int pn=Integer.parseInt(httpHelper.regexStr(currentUrl, "&pn=", "&"));
	    		System.out.println("pn:"+pn);
	    		
	    		
	    		String pageLink="";
	    		if (pn!=0) {
	    			pageLink = currentHtml.xpath("//p[@id='page']/a[11]/@href").toString();        	
					
				}else {
					pageLink = currentHtml.xpath("//p[@id='page']/a[10]/@href").toString(); 
				}
	    		
	    		System.out.println("====>next pageLink:"+pageLink);
	    		if (pageLink!=null&&!pageLink.equals("")) {
	    			
	    			page.addTargetRequest(pageLink);
	    			System.out.println("====>addlist<====="+pageLink);
	    		}
	    		
	    		String newsTitle="";
	    		String newsUrl="";
	    		String newsContent="";
	    		String newsSource="";
	    		String newsDate="";
	    		Selectable mainInfo;
	    		Selectable isContainPic;
	    		
    			
	    		pn=pn+1;
//	    		String resObj[]=null;
	    		for (int i = 0; i < 10; i++) {
	    			System.out.println("====>"+(pn+i));
	    			newsTitle=currentHtml.xpath("//div[@id="+(pn+i)+"]/h3[@class='c-title']/a/text()").toString();
	    			newsUrl=currentHtml.xpath("//div[@id="+(pn+i)+"]/h3[@class='c-title']/a/@href").toString();
	    			isContainPic=currentHtml.xpath("//div[@id="+(pn+i)+"]/div/@class");
	    			if (isContainPic!=null&&isContainPic.toString().contains("c-gap-top-small")) {
						//包含图文
	    				mainInfo= currentHtml.xpath("//div[@id="+(pn+i)+"]/div/div[2]/p[@class='c-author']/text()");
	    				newsDate=mainInfo.toString().split("  ")[1];
						newsSource=mainInfo.toString().split("  ")[0];
						newsContent=currentHtml.xpath("//div[@id="+(pn+i)+"]/div/div[2]/allText()").toString();
						newsContent=newsContent.split(mainInfo.toString())[1];
						
					}else {
						//不含图片
						mainInfo= currentHtml.xpath("//div[@id="+(pn+i)+"]/div/p[@class='c-author']/text()");
						
						newsDate=mainInfo.toString().split("  ")[1];
						newsSource=mainInfo.toString().split("  ")[0];
						newsContent=currentHtml.xpath("//div[@id="+(pn+i)+"]/div/allText()").toString();
						newsContent=newsContent.split(mainInfo.toString())[1];
						
						
						
						
					}
						
	    			
	    			collectInfo.setTitle(newsTitle);
	    			collectInfo.setUrl(newsUrl);
	    			collectInfo.setContent(newsContent);
	    			collectInfo.setSource(newsSource);
	    			collectInfo.setPublishTime(newsDate);

	    			String []resObj={String.valueOf(getProjectId()),newsUrl,newsTitle,newsDate,newsSource,newsContent};
	    			resList.add(resObj);
	    			System.out.println(collectInfo.toString());
				}
	    		jdbcUtil.insertStrs("collect_info_baidu", columnNames, resList);
	        	
	    	} catch (Exception e) {
	    		try {
					jdbcUtil.insertStrs("collect_info_baidu", columnNames, resList);
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
