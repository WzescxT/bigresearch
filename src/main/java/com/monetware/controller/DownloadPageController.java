package com.monetware.controller;

import com.monetware.service.collect.CollectByCluesService;
import com.monetware.service.collect.DownloadPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.jnlp.DownloadService;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Create by xuantang
 * @date on 11/12/17
 */

@RequestMapping("/collect")
@RestController
public class DownloadPageController {

    @Autowired
    DownloadPageService downloadPageService;

    private String result;
    private int success = 0;

    @RequestMapping(value = "/download", method = RequestMethod.POST)
    public String crawlByClues(@RequestBody Map<String,Object> requests) {
        String url_path = requests.get("url_path").toString();
        success = 0;
        DownloadPageService.OnCrawleLinstener onCrawleLinstener = new
                DownloadPageService.OnCrawleLinstener() {
                    @Override
                    public void onSuccess(String res) {
                        result = res;
                        success = 1;
                    }

                    @Override
                    public void onFail(String error) {
                        success = 2;
                    }
                };
        downloadPageService.crawl(onCrawleLinstener, url_path);
        while (success == 0) {

        }
        // download page to local
        String host = null;
        String filename;
        String s = "([a-zA-Z0-9-]+)(.com\\b|.net\\b|.edu\\b|.miz\\b|.biz\\b|.cn\\b|.cc\\b|.org\\b){1,}";
        Pattern pattern1 = Pattern.compile(s);
        Matcher matcher1 = pattern1.matcher(url_path);

        if(matcher1.find()) {
            host = matcher1.group();
        }
        filename = "src/main/webapp/view/" + host + ".html";
        File file = new File(filename);
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(result);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "success";
    }

}
