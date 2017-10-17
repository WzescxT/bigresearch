package com.monetware.mapper.analysis;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/** 
 *@author  venbillyu 
 *@date 创建时间：2017年2月27日 下午4:27:04 
 *@describle 数据库分析
 */
@Repository
public interface AnalysisDBMapper {
	
//	List<Map<String, String>> selectSql(@Param("columns")String columns,@Param("textLibraryId")String textLibraryId,@Param("condition")String condition,@Param("pageStart") String pageStart,@Param("pageSize")String pageSize);
	
//	long countSql(@Param("textLibraryId")String textLibraryId,@Param("condition") String condition);
	
/*	
	void updateSql(@Param("textLibraryId")String textLibraryId,@Param("setStr")String setStr,@Param("condition")String condition);
	
	
	void deleteSql(@Param("textLibraryId")String textLibraryId,@Param("condition")String condition);
	
	
	void insertSql(@Param("textLibraryId")String textLibraryId,@Param("columns")String columns,@Param("columnsValue")String columnsValue);
	
	
	*/
	
	
	
	
	List<Map<String, String>> select(@Param("selectSql")String selectSql,@Param("pageStart") long pageStart,@Param("pageSize")long pageSize);
	
	long countSelect(@Param("countSql")String countSql);
	
	long count(@Param("countSql")String countSql);
	
	
	long delete(@Param("deleteSql")String deleteSql);
	
	
	long update(@Param("updateSql")String updateSql);
	

	
	long insert(@Param("insertSql")String insertSql);
	
	
	
	
}
