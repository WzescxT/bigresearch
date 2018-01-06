package com.monetware.mapper.collect;

import com.monetware.model.collect.SpiderProgressInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface SpiderProgressInfoMapper {
    @Select("SELECT * FROM spider_progress_info WHERE task_id = #{task_id}")
    List<SpiderProgressInfo> findSpiderProgressByTaskID(@Param("task_id") int task_id);

    @Insert("INSERT INTO spider_progress_info(task_id,crawled_number,time) VALUES(#{task_id}, #{crawled_number}, #{time})")
    void insertSpiderProgressInfo(SpiderProgressInfo spiderProgressInfo);
}
