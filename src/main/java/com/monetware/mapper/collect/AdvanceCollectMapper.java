package com.monetware.mapper.collect;

import com.monetware.model.collect.AdvanceProjectEntity;
import com.monetware.model.collect.AdvanceTaskEntity;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Mapper
@Component
public interface AdvanceCollectMapper {

    @Select("select * from spider_project_info")
    @Results({
            @Result(column = "project_id", property = "project_id")
    })
    ArrayList<AdvanceProjectEntity> getProjects();

    @Insert("insert into spider_project_info(project_name, create_time) values(#{advanceProjectEntity.project_name}, #{advanceProjectEntity.create_time})")
    @Options(useGeneratedKeys = true, keyProperty = "advanceProjectEntity.project_id")
    boolean createProject(@Param("advanceProjectEntity") AdvanceProjectEntity advanceProjectEntity);


    @Select("SELECT spider_task_info.task_id, spider_task_info.task_name"
            + " FROM spider_project_task,spider_task_info"
            + " WHERE spider_task_info.task_id = spider_project_task.task_id"
            + " AND spider_project_task.project_id = #{project_id}")
    ArrayList<AdvanceTaskEntity> getTasks(@Param("project_id") int project_id);

    @Insert("INSERT INTO spider_task_info(task_name, create_time) VALUES (#{advanceTaskEntity.task_name},#{advanceTaskEntity.create_time})")
    @Options(useGeneratedKeys = true, keyProperty = "advanceTaskEntity.task_id")
    boolean createTask(@Param("advanceTaskEntity") AdvanceTaskEntity advanceTaskEntity);

    @Insert("INSERT INTO spider_project_task VALUES (#{project_id},#{task_id})")
    boolean setRelation(@Param("project_id") int project_id, @Param("task_id") int task_id);
}
