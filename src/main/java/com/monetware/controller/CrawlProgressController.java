package com.monetware.controller;

import com.google.gson.JsonObject;
import com.monetware.mapper.collect.SpiderProgressInfoMapper;
import com.monetware.mapper.collect.SpiderProjecTaskMapper;
import com.monetware.model.collect.CollectProgress;
import com.monetware.model.collect.SpiderProgressInfo;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

@RequestMapping("/progress")
@RestController
public class CrawlProgressController {
    @Autowired
    public SpiderProjecTaskMapper spiderProjecTaskMapper;
    @Autowired
    public SpiderProgressInfoMapper spiderProgressInfoMapper;

    @RequestMapping(value="/status",method = RequestMethod.GET)
    public String getProgress()
    {
        JSONArray result=new JSONArray();
        for(Map.Entry<Integer, Integer> entry:CollectProgress.totalurls.entrySet())
        {
            int taskid=entry.getKey();
            int projectid=spiderProjecTaskMapper.findSpiderProjectIDByTaskID(taskid).getProject_id();
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("total_urls", CollectProgress.totalurls.get(taskid));
            jsonObject.put("project_id", CollectProgress.totalurls.get(projectid));
            jsonObject.put("crawled_urls",CollectProgress.crawledurls.get(taskid));
             Date starttime=CollectProgress.starttime.get(taskid);
             Date now=new Date(System.currentTimeMillis());
             SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
             double hour=(now.getTime()-starttime.getTime())/3600000.0;
             double rate=CollectProgress.crawledurls.get(taskid)/hour;
            jsonObject.put("process",CollectProgress.crawledurls.get(taskid)/(double)entry.getValue());
            jsonObject.put("starttime",sdf.format(starttime));
            jsonObject.put("nowtime",sdf.format(now));
            jsonObject.put("rate",rate);

            JSONArray timeprogresspoints=new JSONArray();
            List<SpiderProgressInfo> list=spiderProgressInfoMapper.findSpiderProgressByTaskID(taskid);
            for(int i=0;i<list.size();i++)
            {
                JSONObject point=new JSONObject();
                SpiderProgressInfo spiderProgressInfo=list.get(i);
                point.put("time",sdf.format(spiderProgressInfo.getTime()));
                point.put("crawlednumber",spiderProgressInfo.getCrawled_number());
                timeprogresspoints.add(point);
            }
            jsonObject.put("timeprogresspoints",timeprogresspoints);
            result.add(jsonObject);
        }
        return result.toString();
    }

}
