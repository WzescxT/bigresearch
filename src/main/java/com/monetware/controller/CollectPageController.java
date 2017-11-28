package com.monetware.controller;

import com.monetware.model.collect.CollectProject;
import com.monetware.model.collect.CollectTemplate;
import com.monetware.model.collect.FilePipline;
import com.monetware.model.collect.MysqlPipline;
import com.monetware.model.common.RtInfo;
import com.monetware.service.collect.CollectService;
import com.monetware.service.collect.XpathCollectorService;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;



import javax.servlet.http.HttpServletRequest;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/collect")
@Controller
public class CollectPageController {

    private StringBuilder myResult = new StringBuilder();
    private int success = 0;

    private XpathCollectorService service=new XpathCollectorService();
    @Autowired
    CollectService collectService;

    @RequestMapping(value="/CrawledData",method = RequestMethod.POST)
    @ResponseBody
    public String getProject(@RequestBody final JSONObject request) {
        String xpath = (String) request.get("xpath");
        String xpath2 = (String) request.get("xpath2");
        String nameindb = (String) request.get("nameindb");
        String crawltype = (String) request.get("crawltype");
        String ifajax=(String) request.get("ifajax");
        String ajaxtype=(String) request.get("ajaxtype");
        String ajaxxpath=(String) request.get("ajaxxpath");
        String url=(String) request.get("currenturl");
        String extract_way=(String) request.get("extract_way");
        System.out.println(xpath);
        System.out.println(xpath2);
        System.out.println(nameindb);
        System.out.println(crawltype);
        System.out.println(ajaxxpath);
        System.out.println(ifajax);
        System.out.println(url);
        System.out.println(extract_way);
        if (crawltype.equals("单体")) {
            if(extract_way.equals("链接"))
            {
                xpath=xpath+"/@href";
            }
            final JSONObject result = new JSONObject();
            try {
                service.crawlSingleData(url, xpath, nameindb,ifajax,ajaxtype,ajaxxpath,null,null,null,null,null,extract_way);
                while (!service.getiscompleted()) {
                    try {
                        Thread.sleep(10);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                result.put("testResult", FilePipline.tempResult);
                FilePipline.tempResult="";
                return result.toString();
            }
            catch(Exception e)
            {
                result.put("testResult", "error occured");
                e.printStackTrace();
                FilePipline.tempResult="";
                return result.toString();
            }
        } else {
            // ajax
            if(ifajax.equals("true")) {
                String ajaxPattern = ajaxtype;
                String ajaxXpath = ajaxxpath;
                final String attributeName = nameindb;
                CollectService.OnCrawleLinstener onCrawleLinstener = new
                        CollectService.OnCrawleLinstener() {
                            @Override
                            public void onSuccess(List<String> result) {
                                for (String s : result) {
                                    myResult.append(attributeName).append(" ").append(s).append("\n");
                                    success = 1;
                                }
                            }
                            @Override
                            public void onFail(String error) {
                                System.out.println(error);
                                success = 2;
                            }
                        };
                if(ajaxPattern.equals("点击")) {
                    collectService.crawl(onCrawleLinstener, url, CollectService.TYPE_CLUES_AJAX_CLICK,
                            extract_way, ajaxXpath, xpath, xpath2);
                } else if(ajaxPattern.equals("翻页")) {
                    collectService.crawl(onCrawleLinstener, url, CollectService.TYPE_CLUES_AJAX_FLIP,
                            extract_way, ajaxXpath, xpath, xpath2);
                }
            }
            // no ajax
            else {
                final String attributeName = nameindb;
                // out
                CollectService.OnCrawleLinstener onCrawleLinstener = new
                        CollectService.OnCrawleLinstener() {
                            @Override
                            public void onSuccess(List<String> result) {
                                for (String s : result) {
                                    myResult.append(attributeName).append(" ").append(s).append("\n");
                                    success = 1;
                                }
                            }

                            @Override
                            public void onFail(String error) {
                                System.out.println(error);
                                success = 2;
                            }
                        };
                collectService.crawl(onCrawleLinstener, url, CollectService.TYPE_CLUES, extract_way, "", xpath, xpath2);
            }
            while (success == 0) {

            }
            final JSONObject res = new JSONObject();
            res.put("testResult", myResult.toString());
            System.out.println(myResult.toString());
            return res.toString();
        }
    }
}
