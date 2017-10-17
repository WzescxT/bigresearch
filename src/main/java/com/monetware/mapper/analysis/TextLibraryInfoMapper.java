package com.monetware.mapper.analysis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/** 
 *@author  venbillyu 
 *@date 创建时间：2017年1月13日 下午5:13:06 
 *@describle 分析文本库
 */
@Repository
public interface TextLibraryInfoMapper {
	
	Map<String, String> getOneInfo(@Param("textLibraryId")long textLibraryId,@Param("columns")String colmns,@Param("id")long id);
	List<Map<String, Object>> getAllInfoByColumns(@Param("textLibraryId")long textLibraryId,@Param("columns")String colmns);
	List<Map<String, String>> getInfoByPage(@Param("textLibraryId")long projectId,@Param("pageStart")long pageStart,@Param("pageSize")long pageSize);
	
	void createTextLibraryInfoTable(@Param("textLibraryId")long textLibraryId, @Param("sql")String sql);
	void dropTextLibraryInfoTable(@Param("textLibraryId")long textLibraryId);
	long getTextLibraryInfoNo(@Param("textLibraryId")long textLibraryId);
	
	
	
	
}
