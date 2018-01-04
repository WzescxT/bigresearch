package com.monetware.model.collect;

public class SpiderProjectTaskInfo {
    private int project_id;
    private int task_id;

    public void setTask_id(int task_id) {
        this.task_id = task_id;
    }

    public void setProject_id(int project_id) {
        this.project_id = project_id;
    }

    public int getTask_id() {
        return task_id;
    }

    public int getProject_id() {
        return project_id;
    }
}
