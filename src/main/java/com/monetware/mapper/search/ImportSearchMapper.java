package com.monetware.mapper.search;

import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Repository;


@Repository
public interface ImportSearchMapper {
	
	void importSearchDb(HashMap<String, Object> queryMap);
	
	List<HashMap> getTableColumn(HashMap<String, Long> queryMap );
	
	void updateImportStatus(HashMap<String, Object> queryMap);
	
	List<HashMap> getProjectStatus(HashMap<String, Long> queryMap );

}
