package com.monetware.service.collect;

import com.monetware.model.collect.CollectTemplateField;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Xpath2Selector;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by venbill on 2017/3/20.
 * 处理内容页
 */
@Service
public class ContentPageProcessService {
    private long projectId;
    public void setProjectId(long projectId){
        this.projectId = projectId;
    }
    @Autowired
    private CollectLogService logService;


    public void progressContentPage(Page page, List<CollectTemplateField> fields){
        Html currentHtml = page.getHtml();
        //具体内容页取值
        for (int i = 0; i < fields.size(); i++) {
            CollectTemplateField field=fields.get(i);
            String name=field.getName();
            String context="";
            try{
                context=currentHtml.xpath(field.getContentXpath()).toString();
            }catch (Exception e){

                logService.warn(projectId,"cannot parse html by using xpath1,try to use xpath2");
                try {
                    Xpath2Selector xpath2Selector = new Xpath2Selector(field.getContentXpath());
                    context=xpath2Selector.select(currentHtml.getDocument().outerHtml());
                }catch (Exception e2){
                    logService.error(projectId,"cannot parse html by using xpath2,please change xpath selector");
                }


            }

            boolean b=field.isRequire();
            if (context!=null&&StringUtils.isNotEmpty(context)) {

                try {
                    context = new String(context.getBytes(), "utf8");
                } catch (UnsupportedEncodingException e) {
                    context = "";
                }

                context=context.replaceAll("[\\x{10000}-\\x{10FFFF}]", "");

                page.putField(name,context);

            }else {
                if (b) {
                    //跳过

                    page.setSkip(true);
                }else {
                    //存空

                    context="";
                    page.putField(name,context);
                }
            }
        }

        logService.info(projectId,"success to parse a web page");


    }



}
