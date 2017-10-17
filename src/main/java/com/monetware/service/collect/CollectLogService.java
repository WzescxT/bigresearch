package com.monetware.service.collect;

import com.monetware.mapper.collect.CollectLogMapper;
import com.monetware.model.collect.CollectLog;
import org.apache.commons.lang3.StringUtils;
import org.apache.xerces.xs.datatypes.ObjectList;
import org.bouncycastle.util.Times;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by venbill on 2017/4/27.
 * 采集日志管理
 */
@Service
public class CollectLogService {
    @Autowired
    private CollectLogMapper logMapper;
    private final String LEVEL_INFO="INFO";
    private final String LEVEL_WARN="WARN";
    private final String LEVEL_ERROR="ERROR";


    public int createCollectLogTable(long projectId){
        return logMapper.createCollectLogTable(projectId);
    }

    public int dropCollectLogTable(long projectId){
        return logMapper.dropCollectLogTable(projectId);
    }

    public int insertCollectLog(long projectId,String level,String content){
        return logMapper.insertCollectLog(projectId,level,content);
    }

    public int info(long projectId,String content){
        return logMapper.insertCollectLog(projectId,LEVEL_INFO,content);
    }
    public int warn(long projectId,String content){
        return logMapper.insertCollectLog(projectId,LEVEL_WARN,content);
    }
    public int error(long projectId,String content){
        return logMapper.insertCollectLog(projectId,LEVEL_ERROR,content);
    }




    public List<CollectLog> getCollectLogs(long projectId, String level, long pageStart, long pageSize){
        return logMapper.getCollectLogs(projectId,level,pageStart,pageSize);
    }

    public long getCollectLogNo(long projectId , String level){
        return logMapper.getCollectLogNo(projectId,level);
    }

    //返回采集统计图信息
    public Map<String, List> getData(long projectId){
        //分析5小时以内的数据，30分钟设为一个时间段

        List<Long> data = new ArrayList<Long>();

        List<String>  labels = new ArrayList<String>();

        Timestamp startTime = logMapper.getStartTime(projectId);

        for (int i=0;i<10;i++){
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            if (i==0){
                labels.add(formatter.format(new Date(startTime.getTime())));
                data.add(0L);
            }
            labels.add(formatter.format(new Date(startTime.getTime()+30*60*1000*(i+1))));

            data.add(logMapper.getTimeNo(projectId,startTime.getTime()+30*60*1000*i,startTime.getTime()+30*60*1000*(i+1)));

        }
        Map<String,List> resMap = new HashMap<String,List>();
        resMap.put("labels",labels);
        resMap.put("data",data);

        return resMap;

    }


}
