package com.monetware.mapper.collect;

import com.monetware.model.collect.SpiderTaskInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SpiderTaskInfoMapper {

    @Select("select * from spider_task_info where task_name = #{name}")
    SpiderTaskInfo findSpiderTaskInfoByName(@Param("name") String name);

    @Update("update spider_task_info set task_config_location = #{path} where task_id = #{id}")
    void saveConfigPathById(@Param("path") String path, @Param("id") String id);

}
