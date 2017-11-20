package com.monetware.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.monetware.mapper.collect.AdvanceCollectMapper;
import com.monetware.mapper.collect.SpiderTaskInfoMapper;
import com.monetware.model.collect.FilePipline;
import com.monetware.model.collect.SpiderTaskInfo;
import com.monetware.service.collect.XpathCollectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.FileInputStream;
import java.util.Scanner;

@RequestMapping("/CrawlPlan")
@Controller
public class CrawlPlanController {
    @Autowired
    private SpiderTaskInfoMapper spiderTaskInfoMapper;
    private XpathCollectorService service=new XpathCollectorService();
    @RequestMapping(value="/Plan",method = RequestMethod.POST)
    @ResponseBody
    public String getProject(@RequestBody JSONObject request) {
        int task_id = (int) request.get("task_id");
        SpiderTaskInfo task= spiderTaskInfoMapper.findSpiderTaskInfoById(String.valueOf(task_id));
        String path=task.getTask_config_location();
        File configjson=new File(path);
        String jsonString="";
        JSONObject configdata=null;
        try {
            Scanner scanner=new Scanner(new FileInputStream(configjson));
            while(scanner.hasNext())
            {
                jsonString+=scanner.next();

            }
            configdata=JSONObject.parseObject(jsonString);
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        JSONObject assistant_rule=configdata.getJSONObject("assistant_rule");
        JSONObject url_pattern=configdata.getJSONObject("url_pattern");
        JSONObject store_rule=configdata.getJSONObject("store_rule");
        JSONArray creep_rule=configdata.getJSONArray("creep_rule");
        JSONObject run_rule=configdata.getJSONObject("run_rule");

        if(assistant_rule.getBoolean("open"))
        {
            //登录
        }
        else
        {
            //不配置登录
            String urltype=url_pattern.getString("current_selected");
            if(urltype.equals("single"))
            {
                String url=url_pattern.getJSONObject("single").getString("url_path");
                String store_pattern=store_rule.getString("store_pattern");
                String proxy_id=run_rule.getString("proxy_id");
                JSONObject time=run_rule.getJSONObject("time");
                String header=run_rule.getString("headers");
                String custom_config=run_rule.getString("custom_config");


                for(Object eachtask:creep_rule)
                {
                    JSONObject eachtaskJSON=(JSONObject)eachtask;
                    String creep_pattern=eachtaskJSON.getString("creep_pattern");
                    if(creep_pattern.equals("单体"))
                    {
                        JSONObject ajax=eachtaskJSON.getJSONObject("ajax");
                        if(ajax.getBoolean("open"))
                        {
                            String ajax_pattern=ajax.getString("ajax_pattern");
                            if(ajax_pattern.equals("点击"))
                            {
                                String button_xpath=ajax.getString("button_xpath");
                                String xpath1=((JSONObject) eachtask).getString("attribute_xpath");
                                String attribute_name=eachtaskJSON.getString("attribute_name");
                                service.crawlSingleData(url,xpath1,attribute_name,ajax.getBoolean("open").toString(),ajax_pattern,button_xpath,proxy_id,time.getString("start_time"),time.getString("end_time"),header,store_pattern);


                            }
                            else
                            {

                            }
                        }
                        else
                        {
                            String xpath1=((JSONObject) eachtask).getString("attribute_xpath");
                            String attribute_name=eachtaskJSON.getString("attribute_name");
                            service.crawlSingleData(url,xpath1,attribute_name,ajax.getBoolean("open").toString(),null,null,proxy_id,time.getString("start_time"),time.getString("end_time"),header,store_pattern);
                        }
                    }
                    else
                    {
                    //xuantang here
                    }

                }

            }
            else if(urltype.equals("list"))
            {

            }
            else if(urltype.equals("click"))
            {

            }
            else if(urltype.equals("import"))
            {

            }
        }



        return "";

    }
}
