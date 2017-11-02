package com.monetware.model.collect;


import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;

public class MysqlPipline implements Pipeline {

    public static String tempResult="";
    @Override
    public void process(ResultItems resultItems, Task task) {

        Map<String,Object> infos=resultItems.getAll();
        if(infos==null||infos.size()==0)
        {
            tempResult="nothing to show";
        }
        else {
            for (Map.Entry<String, Object> info : infos.entrySet()) {
                tempResult += info.getKey() + ":" + info.getValue()==null?"no data crawled":info.getValue();
            }
        }

        /*if(news!=null) {
            System.out.println("insert in news");
            Dao.getInstance().inertNews(news.getTitle(),news.getBody(),news.getPublish_time(),news.getWriter(),news.getDatasource(),news.getUrl(),news.isYangzhou(),news.isZhenjiang(),news.isBeijing(),news.isShanghai(),news.isHangzhou(),news.isChengdu(),news.isChangsha(),news.isZhangbei(),news.isZhoushan(),news.isCaomei(),news.isMidi(),news.isNanjing());
        }*/

    }
}
