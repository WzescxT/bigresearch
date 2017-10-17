package com.monetware.service.collect;

import com.monetware.mapper.collect.CollectInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.Map;

/**
 * Created by venbill on 2017/3/20.
 * 处理爬虫采集结果
 */
@Component
public class DataBasePipeline implements Pipeline{
    @Autowired
    private CollectInfoMapper infoMapper;
    @Autowired
    private CollectLogService logService;

    private long projectId;


    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    @Override
    public void process(ResultItems resultItems, Task task) {

        Map<String, Object> fields = resultItems.getAll();

        if (!fields.isEmpty()){

            infoMapper.insertCollectInfo(projectId,fields);
            logService.info(projectId,"store the data to the database successfully");
        }

    }



}
