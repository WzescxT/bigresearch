package com.monetware.controller;

import com.monetware.service.collect.DownloadPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

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

    private String response = "fail";
    @RequestMapping(value = "/download", method = RequestMethod.POST)
    public String crawlByClues(@RequestParam("url_path") String url_path) {
        success = 0;
        DownloadPageService.OnCrawlListener onCrawlListener = new
                DownloadPageService.OnCrawlListener() {
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
        downloadPageService.crawl(onCrawlListener, url_path);
        while (success == 0) {

        }
        if (success == 1) {
            // download page to local
            String host = null;
            String filename;
            host = hashCode(url_path);
            // save to file path
            filename = "src/main/webapp/view/download/" + host + ".html";
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
                response =  "success";
            } catch (IOException e) {
                e.printStackTrace();
                response = "fail";
            }
        } else if (success == 2) {
            response =  "fail";
        } else {
            response =  "fail";
        }
        return response;
    }

    /**
     * Check file exist
     * @param filename
     * @return
     */
    @RequestMapping(value = "/file/exist", method = RequestMethod.POST)
    public boolean checkFileExist(@RequestParam("filename") String filename) {
        File file = new File("src/main/webapp/view/download/" + filename);
        if (file.exists()) {
            return true;
        }
        return false;
    }

    private String hashCode(String str) {
        return String.valueOf(str.hashCode());
    }
}
