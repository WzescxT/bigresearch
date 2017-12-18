package com.monetware.controller;

import com.monetware.model.collect.CollectProgress;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping("/progress")
@RestController
public class CrawlProgressController {

    @RequestMapping(value="/status",method = RequestMethod.POST)
    public String getProgress(@RequestBody final JSONObject request)
    {
        JSONArray result=new JSONArray();
        for(Map.Entry<Integer, Integer> entry:CollectProgress.totalurls.entrySet())
        {
            int taskid=entry.getKey();
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("task_id", CollectProgress.totalurls.get(taskid));
            jsonObject.put("process",CollectProgress.crawledurls.get(taskid)/(double)entry.getValue());
            result.add(jsonObject);
        }
        return result.toString();
    }

}
