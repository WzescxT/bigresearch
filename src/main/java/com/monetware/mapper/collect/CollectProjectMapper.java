package com.monetware.mapper.collect;

import com.monetware.model.collect.CollectProject;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

/** 
 *@author  venbillyu 
 *@date 创建时间�?2016�?11�?25�? 下午5:03:56 
 *@describle 采集项目
 */
@Repository
public interface CollectProjectMapper {
	
	int updateByPrimaryKey(CollectProject record);
	void createCollectProject(CollectProject collectProject);
	void updateCollectProject(CollectProject CollectProject);
	void deleteCollectProject(@Param("id")long projectId);
	CollectProject getCollectProject(long projectId);
	CollectProject getCollectProjectByName(CollectProject CollectProject);
	List<CollectProject> getCollectProjects(int userId);
	List<CollectProject> getCollectProjects1(int userId);
	List<CollectProject> getPartCollectProjects(HashMap<String, Object> queryMap);
	long getCollectProjectsNo(@Param("userId")int userId,@Param("name")String name);
	long getMonthProjectsNo(@Param("userId")int userId,@Param("name")String name,@Param("firstDay") Timestamp firstDay);

	List<CollectProject> getSearchProjects(@Param("userId")int userId);
	
}
