package com.monetware.controller;

import com.monetware.model.collect.CollectProject;
import com.monetware.model.collect.CollectTemplate;
import com.monetware.model.collect.MysqlPipline;
import com.monetware.model.common.RtInfo;
import com.monetware.service.collect.XpathCollectorService;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

@RequestMapping("/collect")
@Controller
public class CollectPageController {

    private XpathCollectorService service=new XpathCollectorService();

    @RequestMapping(value="/CrawledData",method = RequestMethod.POST)
    @ResponseBody
    public String getProject(@RequestBody JSONObject request) {
        String xpath = (String) request.get("xpath");
        String nameindb = (String) request.get("nameindb");
        String crawltype = (String) request.get("crawltype");
        String ifajax=(String) request.get("ifajax");
        String ajaxtype=(String) request.get("ajaxtype");
        String ajaxxpath=(String) request.get("ajaxxpath");
        System.out.println(xpath);
        System.out.println(nameindb);
        System.out.println(crawltype);
        System.out.println(ajaxxpath);
        System.out.println(ifajax);
        final JSONObject result = new JSONObject();

        try {
            service.crawlSingleData("http://www.sse.com.cn/assortment/stock/list/share/"/*"https://www.douban.com/"*/, xpath, nameindb,crawltype,ifajax,ajaxtype,ajaxxpath);
            while (!service.getiscompleted()) {
                try {
                    Thread.sleep(10);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            result.put("testResult", MysqlPipline.tempResult);
            MysqlPipline.tempResult="";
            return result.toString();
        }
        catch(Exception e)
        {
            result.put("testResult", "error occured");
            e.printStackTrace();
            MysqlPipline.tempResult="";
            return result.toString();
        }


    }
}
