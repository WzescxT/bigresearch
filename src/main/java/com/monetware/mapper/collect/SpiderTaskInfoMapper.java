package com.monetware.mapper.collect;

import com.monetware.model.collect.SpiderTaskInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface SpiderTaskInfoMapper {

    @Select("SELECT * FROM spider_task_info WHERE task_name = #{name}")
    SpiderTaskInfo findSpiderTaskInfoByName(@Param("name") String name);

    @Select("SELECT * FROM spider_task_info WHERE task_id = #{task_id}")
    SpiderTaskInfo findSpiderTaskInfoById(@Param("task_id") String taskId);

    @Update("UPDATE spider_task_info SET task_config_location = #{path} WHERE task_id = #{id}")
    void saveConfigPathById(@Param("path") String path, @Param("id") String id);

    @Update("UPDATE spider_task_into SET url_location = #{path} WHERE task_id = #{id}")
    void saveUrlFilePathById(@Param("path") String path, @Param("id") String id);

}
