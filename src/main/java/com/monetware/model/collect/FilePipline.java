package com.monetware.model.collect;

import org.springframework.beans.factory.annotation.Value;
import redis.clients.jedis.Pipeline;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;

import java.io.File;
import java.io.FileWriter;
import java.util.Map;

public class FilePipline implements us.codecraft.webmagic.pipeline.Pipeline {
    public static String tempResult="";

    @Value("${generate.spider.result}")
    private String generate_spider_result;

    @Override
    public void process(ResultItems resultItems, Task task) {

        Map<String,Object> infos=resultItems.getAll();
        String result="";
        if(infos==null||infos.size()==0) {
            result="nothing to show";
        }
        else {
            for (Map.Entry<String, Object> info : infos.entrySet()) {
                result += info.getKey() + ":" + info.getValue() == null ? "no data crawled\n" : info.getValue()+"\n";
            }

        }
        tempResult += result;
        try {
            File direc = new File("data/single");
            if (direc.isDirectory()) {
                File[] files = direc.listFiles();
                if (files.length == 0) {
                    File file = new File("data/single/1.txt");
                    // System.out.println(file.getAbsolutePath());
                    file.createNewFile();
                    FileWriter writer=new FileWriter(file,true);
                    writer.write(result);
                    writer.flush();
                }
                else
                {
                    int Maxnumber=Integer.parseInt(files[0].getName().replace(".txt",""));
                    File lastfile=files[0];
                    for(File file:files)
                    {
                        int temp=Integer.parseInt(file.getName().replace(".txt",""));
                        if(Maxnumber<temp)
                        {
                            Maxnumber=temp;
                            lastfile=file;
                        }
                    }
                    if(lastfile.length()>52428800)
                    {
                        int num=Maxnumber+1;
                        File newfile=new File("data/single/" + num+".txt");
                        newfile.createNewFile();
                        FileWriter writer=new FileWriter(newfile,true);
                        writer.write(result);
                        writer.flush();
                        //写入resulnewt
                    }
                    else
                    {
                        FileWriter writer=new FileWriter(lastfile,true);
                        writer.write(result);
                        writer.flush();
                    }

                }
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        /*if(news!=null) {
            System.out.println("insert in news");
            Dao.getInstance().inertNews(news.getTitle(),news.getBody(),news.getPublish_time(),news.getWriter(),news.getDatasource(),news.getUrl(),news.isYangzhou(),news.isZhenjiang(),news.isBeijing(),news.isShanghai(),news.isHangzhou(),news.isChengdu(),news.isChangsha(),news.isZhangbei(),news.isZhoushan(),news.isCaomei(),news.isMidi(),news.isNanjing());
        }*/

    }
}
