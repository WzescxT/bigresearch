package com.monetware.controller;

import com.monetware.model.collect.CollectProgress;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Map;

@RequestMapping("/progress")
@RestController
public class CrawlProgressController {

    @RequestMapping(value="/status",method = RequestMethod.GET)
    public String getProgress(@RequestBody final JSONObject request)
    {
        JSONArray result=new JSONArray();
        for(Map.Entry<Integer, Integer> entry:CollectProgress.totalurls.entrySet())
        {
            int taskid=entry.getKey();
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("task_id", CollectProgress.totalurls.get(taskid));
             Date starttime=CollectProgress.starttime.get(taskid);
             Date now=new Date(System.currentTimeMillis());
             SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
             double hour=(now.getTime()-starttime.getTime())/3600000.0;
             double rate=CollectProgress.crawledurls.get(taskid)/hour;
            jsonObject.put("process",CollectProgress.crawledurls.get(taskid)/(double)entry.getValue());
            jsonObject.put("starttime",sdf.format(starttime));
            jsonObject.put("nowtime",sdf.format(now));
            jsonObject.put("rate",rate);
            result.add(jsonObject);
        }
        return result.toString();
    }

}
