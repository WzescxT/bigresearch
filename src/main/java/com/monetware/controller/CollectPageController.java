package com.monetware.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.monetware.mapper.collect.SpiderProjectInfoMapper;
import com.monetware.mapper.collect.SpiderTaskInfoMapper;
import com.monetware.model.collect.FilePipline;
import com.monetware.model.collect.SpiderTaskInfo;
import com.monetware.service.collect.CollectService;
import com.monetware.service.collect.XpathCollectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@RequestMapping("/collect")
@Controller
public class CollectPageController {

    private StringBuilder myResult = new StringBuilder();
    private int success = 0;

    private XpathCollectorService service = new XpathCollectorService();
    @Autowired
    CollectService collectService;
    @Autowired
    SpiderTaskInfoMapper spiderTaskInfoMapper;
    @Autowired
    SpiderProjectInfoMapper spiderProjectInfoMapper;

    @RequestMapping(value = "/CrawledData", method = RequestMethod.POST)
    @ResponseBody
    public String getProject(@RequestBody final JSONObject request) {
        String xpath = (String) request.get("xpath");
        String xpath2 = (String) request.get("xpath2");
        String nameindb = (String) request.get("nameindb");
        String crawltype = (String) request.get("crawltype");
        String ifajax = (String) request.get("ifajax");
        String ajaxtype = (String) request.get("ajaxtype");
        String ajaxxpath = (String) request.get("ajaxxpath");
        String extract_way = (String) request.get("extract_way");
        Integer project_id = request.getInteger("project_id");
        Integer task_id = request.getInteger("task_id");
        System.out.println(request.toJSONString());
        final String projectName = spiderProjectInfoMapper.getProjectNameById(Long.parseLong(String.valueOf(project_id)));
        SpiderTaskInfo task = spiderTaskInfoMapper.findSpiderTaskInfoById(String.valueOf(task_id));
        final String taskName = task.getTask_name();
        String path=task.getTask_config_location();
        File configjson=new File(path);
        String jsonString="";
        JSONObject configdata=null;
        try {
            Scanner scanner=new Scanner(new FileInputStream(configjson));
            while(scanner.hasNext()) {
                jsonString+=scanner.next();
            }
            configdata= JSONObject.parseObject(jsonString);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayList<String> urls = new ArrayList<>();
        JSONObject url_pattern = configdata.getJSONObject("url_pattern");
        com.alibaba.fastjson.JSONObject run_rule = configdata.getJSONObject("run_rule");
        String urltype=url_pattern.getString("current_selected");
        String url="";
        if(urltype.equals("single")) {
            url = url_pattern.getJSONObject("single").getString("url_path");
            urls.add(url);
        }
        else if(urltype.equals("list")) {
            List<String> tmp = collectService.generateUrls(url_pattern.getJSONObject("list"));
            urls.addAll(tmp);

            for (String ur : urls) {
                System.out.println(ur);
            }

            //add later
        }
        else if(urltype.equals("import")) {
            JSONArray importurls = url_pattern.getJSONObject("import").getJSONArray("import_urls");
            for(int i=0;i<importurls.size();i++)
            {
                System.out.println("sdfsdfdf"+(String)importurls.get(i));
                urls.add((String)(((String) importurls.get(i))).trim());
                if (i > 10) {
                    break;
                }
            }
        }
        if (crawltype.equals("单体")) {
            if(extract_way.equals("链接")) {
                xpath=xpath+"/@href";
            }
            final JSONObject result = new JSONObject();
            try {
                service.crawlSingleData(-1,true,urls, xpath, nameindb, ifajax, ajaxtype, ajaxxpath,
                        null,null,null,null,null, extract_way);
                while (!service.getiscompleted()) {
                    try {
                        Thread.sleep(10);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                result.put("testResult", FilePipline.tempResult);
                FilePipline.tempResult = "";
                return result.toString();
            } catch (Exception e) {
                result.put("testResult", "error occured");
                e.printStackTrace();
                FilePipline.tempResult = "";
                return result.toString();
            }
        } else {
            // ajax
            if (ifajax.equals("true")) {
                String ajaxPattern = ajaxtype;
                String ajaxXpath = ajaxxpath;
                final String attributeName = nameindb;
                CollectService.OnCrawlListener onCrawlListener = new
                        CollectService.OnCrawlListener() {

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
                if (ajaxPattern.equals("点击")) {
                    List<String> mUrls = new ArrayList<>();
                    urls.add(url);
                    collectService.crawl(onCrawlListener, mUrls, CollectService.TYPE_CLUES_AJAX_CLICK,
                            extract_way, ajaxXpath, xpath, xpath2);
                } else if (ajaxPattern.equals("翻页")) {
                    List<String> mUrls = new ArrayList<>();
                    urls.add(url);
                    collectService.crawl(onCrawlListener, mUrls, CollectService.TYPE_CLUES_AJAX_FLIP,
                            extract_way, ajaxXpath, xpath, xpath2);
                }
            }
            // no ajax
            else {
                final String attributeName = nameindb;
                // out
                CollectService.OnCrawlListener onCrawlListener = new
                        CollectService.OnCrawlListener() {
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
                List<String> mUrls = new ArrayList<>();
                urls.add(url);
                collectService.crawl(onCrawlListener, mUrls, CollectService.TYPE_CLUES, extract_way,
                        "", xpath, xpath2);
            }
            while (success == 0) {}
            final JSONObject res = new JSONObject();
            res.put("testResult", myResult.toString());
            System.out.println(myResult.toString());
            return res.toString();
        }
    }

}
