package com.monetware.mapper.collect;

import com.monetware.model.collect.CollectInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 
 *@author  venbillyu 
 *@date 创建时间：2016年11月29日 上午9:37:05 
 *@describle 采集信息
 */
@Repository
public interface CollectInfoMapper {
	int insertCollectInfo(@Param("projectId")long projectId, @Param("params")Map<String,Object> paramsMap);
	void createCollectInfo(CollectInfo collectInfo);
	List<Map<String,Object>> getCollectInfos(HashMap<String, Long > queryMap );
	long getCollectInfoNo(HashMap<String, Long> queryMap );
	List<HashMap> getCommonInfo(HashMap<String, Long> queryMap );
	void deleteCollectInfo(@Param("projectId")long projectId);
}
