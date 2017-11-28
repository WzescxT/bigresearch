package com.monetware.service.collect;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.monetware.model.collect.FilePipline;
import com.monetware.model.collect.MysqlPipline;
import com.monetware.util.Useragnets;
import org.apache.http.HttpHost;
import us.codecraft.webmagic.*;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.pipeline.FilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.jsoup.helper.StringUtil.isNumeric;

public class XpathCollectorService {
    private String xpath;
    private String url;
    private String nameinDB;
    private String ifajax;
    private String ajaxtype;
    private String ajaxXpath;
    private PropertyChangeSupport propertySupport;
    private boolean isCompleted;
    private String proxy_id;
    private String starttime;
    private String endtime;
    private String header;
    private String extract_way;
    public XpathCollectorService()
    {
        propertySupport = new PropertyChangeSupport(this);
        isCompleted=false;
    }
    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        propertySupport.addPropertyChangeListener(listener);
    }
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }
    public void crawlSingleData(String url,String xpath,String nameinDB,String ifajax,String ajaxtype,String ajaxXpath,String proxy_id,String starttime,String endtime,String header,String storetype,String extract_way)
    {
        this.xpath=xpath;
        this.url=url;
        this.nameinDB=nameinDB;
        this.ifajax=ifajax;
        this.ajaxtype=ajaxtype;
        this.ajaxXpath=ajaxXpath;
        this.proxy_id=proxy_id;
        this.starttime=starttime;
        this.endtime=endtime;
        this.header=header;
        this.extract_way=extract_way;
        if(extract_way.equals("链接"))
        {
            this.xpath=xpath+"/@href";
        }
        if(this.header==null||this.header.equals(""))
        {

            this.header=Useragnets.getuseragent();
        }

            List<SpiderListener> spiderlisteners = new ArrayList<>();
            spiderlisteners.add(new SpiderListener() {
                @Override
                public void onSuccess(Request request) {
                    isCompleted = true;
                    System.out.println("onsuccess");
                    propertySupport.firePropertyChange("isCompleted", false, true);

                }

                @Override
                public void onError(Request request) {
                    isCompleted = true;
                    FilePipline.tempResult = "error";
                    propertySupport.firePropertyChange("isCompleted", false, true);

                }
            });
            if(ifajax.equals("false")) {
                Spider.create(new SingleCrawler()).setSpiderListeners(spiderlisteners).addUrl(url).addPipeline(new FilePipline()).addPipeline(new ConsolePipeline()).thread(5).run();
            }
            else if(ifajax.equals("true")) {
                Spider.create(new AjaxCrawler()).setSpiderListeners(spiderlisteners).addUrl(url).addPipeline(new FilePipline()).addPipeline(new ConsolePipeline()).thread(5).run();
            }


    }
    class SingleCrawler implements PageProcessor{
        private Site site = Site.me().setRetryTimes(3).setSleepTime(1000)
                //.setDomain("www.douban.com")
                .setUserAgent(header)
                ;

        @Override
        public void process(Page page) {
            String value=page.getHtml().xpath(xpath).toString();
            page.putField(nameinDB,value);

        }

        @Override
        public Site getSite() {
            return site;
        }

    }
    class MultiCrawler implements PageProcessor{
        private Site site = Site.me().setRetryTimes(3).setSleepTime(1000)
               // .setDomain("www.douban.com")
                .setUserAgent(header)
                ;

        @Override
        public void process(Page page) {
            List<String> allValues=page.getHtml().xpath(xpath).all();
            page.putField(nameinDB,allValues);
        }

        @Override
        public Site getSite() {
            return site;
        }

    }
    class AjaxCrawler implements PageProcessor {
        private Site site = Site.me().setRetryTimes(3).setSleepTime(1000).setUserAgent(Useragnets.getuseragent());

        @SuppressWarnings("deprecation")
        @Override
        public void process(Page page) {
            if(ajaxtype.equals("点击")) {
                try {
                    WebClient webClient = new WebClient(BrowserVersion.CHROME);
                    webClient.getOptions().setCssEnabled(false);
                    webClient.getOptions().setUseInsecureSSL(true);
                    webClient.getOptions().setJavaScriptEnabled(true);
                    webClient.setAjaxController(new NicelyResynchronizingAjaxController());
                    HtmlPage htmlPage = webClient.getPage(url);
                    webClient.waitForBackgroundJavaScript(10000);
                    List<HtmlAnchor> htmlListItems = htmlPage.getByXPath(ajaxXpath);
                    int i = 0;
                    for (HtmlAnchor htmlAnchor : htmlListItems) {
                        System.out.println("afbkasbdk");
                        htmlPage = (HtmlPage) htmlAnchor.click();
                        webClient.waitForBackgroundJavaScript(10000);
                        i++;
                        if (i == 3) {
                            break;
                        }
                    }
                    page.setRawText(htmlPage.asXml());
                    page.setHtml(new Html(htmlPage.asXml()));
                    String value = page.getHtml().xpath(xpath).toString();
                    if (extract_way.equals("链接")) {
                        if (!value.contains("://")) {
                            String root_path = null;
                            try {
                                URL tempurl = new URL(url);
                                root_path = tempurl.getProtocol() + "://" + tempurl.getHost();
                                System.out.println(root_path);
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            }
                            value = root_path + value;
                        }
                    }
                    page.putField(nameinDB, value);
                    webClient.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if(ajaxtype.equals("翻页"))
            {
                try {
                    WebClient webClient = new WebClient(BrowserVersion.CHROME);
                    webClient.getOptions().setCssEnabled(false);
                    webClient.getOptions().setUseInsecureSSL(true);
                    webClient.getOptions().setJavaScriptEnabled(true);
                    webClient.setAjaxController(new NicelyResynchronizingAjaxController());
                    HtmlPage htmlPage = webClient.getPage(url);
                    webClient.waitForBackgroundJavaScript(10000);

                    int pages = 0;
                    String result="";
                    while (pages<10) {
                        List<HtmlAnchor> htmlListItems = htmlPage.getByXPath(ajaxXpath);
                        System.out.println("afbkasbdk");
                        htmlPage = (HtmlPage) htmlListItems.get(7).click();
                        webClient.waitForBackgroundJavaScript(1000);
                        page.setRawText(htmlPage.asXml());
                        page.setHtml(new Html(htmlPage.asXml()));
                        String value = page.getHtml().xpath(xpath).toString();
                        if (extract_way.equals("链接")) {
                            if (!value.contains("://")) {
                                String root_path = null;
                                try {
                                    URL tempurl = new URL(url);
                                    root_path = tempurl.getProtocol() + "://" + tempurl.getHost();
                                    System.out.println(root_path);
                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                }
                                value = root_path + value;
                            }
                        }
                        result=result+value;
                        pages++;
                    }
                    page.putField(nameinDB, result);
                    webClient.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

        @Override
        public Site getSite() {
            return site;
        }

    }
    public boolean getiscompleted() {
        return isCompleted;
    }
}
