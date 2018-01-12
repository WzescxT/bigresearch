package com.monetware.mapper.collect;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface SpiderProjectInfoMapper {

    @Select("SELECT project_name FROM spider_project_info WHERE project_id = #{project_id};")
    String getProjectNameById(@Param("project_id") Long projectId);

}
