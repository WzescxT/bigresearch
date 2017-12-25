package com.monetware.model.collect;

import sun.security.provider.ConfigFile;

import java.sql.Date;

public class SpiderProgressInfo {
    private int id;
    private int task_id;
    private int crawled_number;
    private Date time;

    public SpiderProgressInfo(int task_id,int crawled_number,Date time)
    {
        this.task_id=task_id;
        this.crawled_number=crawled_number;
        this.time=time;
    }
    public SpiderProgressInfo(Integer id,Integer task_id,Integer crawled_number,Date time)
    {
        this.id=id;
        this.task_id=task_id;
        this.crawled_number=crawled_number;
        this.time=time;
    }
    public Date getTime() {
        return time;
    }

    public int getCrawled_number() {
        return crawled_number;
    }

    public int getId() {
        return id;
    }


    public int getTask_id() {
        return task_id;
    }

    public void setCrawled_number(int crawled_number) {
        this.crawled_number = crawled_number;
    }

    public void setId(int id) {
        this.id = id;
    }


    public void setTask_id(int task_id) {
        this.task_id = task_id;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
