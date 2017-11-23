package com.monetware.controller;


import com.monetware.service.collect.CollectByCluesService;
import com.monetware.service.collect.CollectByFlipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Create by xuantang
 * @date on 11/5/17
 */

@RequestMapping("/collect")
@RestController
public class CollectByFlipController {

    @Autowired
    CollectByFlipService collectByFlipService;

    private List<String> results;
    private int success = 0;

    @RequestMapping(value = "/flip", method = RequestMethod.POST)
    public List<String> crawlByClues(@RequestParam("url_path") String url_path,
                               @RequestParam("xpath1") String xpath1,
                               @RequestParam("xpath2") String xpath2,
                                     @RequestParam("ajax_xpath") String ajax_xpath) {
        success = 0;
        results = new ArrayList<>();
        System.out.println(url_path + " " + xpath1 + " " + ajax_xpath + " " + xpath2);
        CollectByFlipService.OnCrawleLinstener onCrawleLinstener = new
                CollectByFlipService.OnCrawleLinstener() {
                    @Override
                    public void onSuccess(List<String> result) {
                        results = result;
                        success = 1;
                    }

                    @Override
                    public void onFail(String error) {
                        success = 2;
                    }
                };
        collectByFlipService.crawl(onCrawleLinstener, url_path, ajax_xpath, xpath1, xpath2);
        while (success == 0) {

        }
        return results;
    }
}
