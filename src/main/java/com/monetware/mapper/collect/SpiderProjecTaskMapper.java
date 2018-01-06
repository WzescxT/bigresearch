package com.monetware.mapper.collect;

import com.monetware.model.collect.SpiderProjectTaskInfo;
import com.monetware.model.collect.SpiderTaskInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface SpiderProjecTaskMapper {

    @Select("SELECT * FROM spider_project_task WHERE task_id = #{task_id}")
    SpiderProjectTaskInfo findSpiderProjectIDByTaskID(@Param("task_id") int task_id);

}
