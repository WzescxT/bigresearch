package com.monetware.model.collect;

import java.sql.Timestamp;

public class SpiderTaskInfo {

    private String task_id;
    private String task_name;
    private String task_leader;
    private String task_description;
    private String task_config_location;
    private String url_location;
    private String content_location;
    private Timestamp create_time;

    public String getTask_id() {
        return task_id;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }

    public String getTask_name() {
        return task_name;
    }

    public void setTask_name(String task_name) {
        this.task_name = task_name;
    }

    public String getTask_leader() {
        return task_leader;
    }

    public void setTask_leader(String task_leader) {
        this.task_leader = task_leader;
    }

    public String getTask_description() {
        return task_description;
    }

    public void setTask_description(String task_description) {
        this.task_description = task_description;
    }

    public String getTask_config_location() {
        return task_config_location;
    }

    public void setTask_config_location(String task_config_location) {
        this.task_config_location = task_config_location;
    }

    public String getUrl_location() {
        return url_location;
    }

    public void setUrl_location(String url_location) {
        this.url_location = url_location;
    }

    public String getContent_location() {
        return content_location;
    }

    public void setContent_location(String content_location) {
        this.content_location = content_location;
    }

    public Timestamp getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Timestamp create_time) {
        this.create_time = create_time;
    }

}
