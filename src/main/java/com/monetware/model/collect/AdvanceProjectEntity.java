package com.monetware.model.collect;

import javax.xml.crypto.Data;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

public class AdvanceProjectEntity {

    private int project_id;

    private String project_name;

    private Timestamp create_time;

    public String getProject_name() {
        return project_name;
    }

    public void setProject_name(String project_name) {
        this.project_name = project_name;
    }


    public int getProject_id() {
        return project_id;
    }

    public void setProject_id(int project_id) {
        this.project_id = project_id;
    }


    public Timestamp getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Timestamp create_time) {
        this.create_time = create_time;
    }
}
