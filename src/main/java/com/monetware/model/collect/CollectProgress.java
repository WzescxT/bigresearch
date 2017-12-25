package com.monetware.model.collect;

import com.monetware.mapper.collect.SpiderProgressInfoMapper;
import com.monetware.mapper.collect.SpiderProjecTaskMapper;
import com.monetware.mapper.collect.SpiderTaskInfoMapper;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;

//temp implemention
//when several users use this website at the same time,use database to record their total urls and current urls
@Component
public class CollectProgress {
    public static HashMap<Integer,Integer> totalurls=new HashMap<>();
    public static HashMap<Integer,Integer> crawledurls=new HashMap<>();
    public static HashMap<Integer,Date> starttime=new HashMap<>();
    public static HashSet<Integer> finishtaskid=new HashSet<>();
    @Resource
    private SpiderProgressInfoMapper spiderProgressInfoMapper;

    public static SpiderProgressInfoMapper SspiderProgressInfoMapper;

    @PostConstruct
    public void init() {
        this.SspiderProgressInfoMapper = spiderProgressInfoMapper;
        runTimeCount();
    }

    public static void runTimeCount()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {

                        Thread.sleep(5000);

                        for (Iterator<Map.Entry<Integer, Integer>> it = CollectProgress.totalurls.entrySet().iterator(); it.hasNext();)
                        {

                            Map.Entry<Integer, Integer> entry = it.next();
                            int taskid=entry.getKey();
                            int total=entry.getValue();
                            int crawledurls=CollectProgress.crawledurls.get(taskid);
                            Date now=new Date(System.currentTimeMillis());
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            if(total==crawledurls && finishtaskid.contains(taskid))
                            {

                            }
                            else if(total==crawledurls && !finishtaskid.contains(taskid))
                            {
                                SspiderProgressInfoMapper.insertSpiderProgressInfo(new SpiderProgressInfo(taskid,crawledurls,now));
                                finishtaskid.add(taskid);
                            }
                            else
                            {
                                SspiderProgressInfoMapper.insertSpiderProgressInfo(new SpiderProgressInfo(taskid,crawledurls,now));
                            }




                        }
                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    
}
