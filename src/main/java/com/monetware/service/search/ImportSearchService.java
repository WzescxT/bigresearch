package com.monetware.service.search;



import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;



import com.monetware.mapper.search.ImportSearchMapper;
import com.monetware.model.search.ImportSearchModel;
import com.monetware.model.search.SearchModel;


public class ImportSearchService {
	
	@Autowired
	private ImportSearchMapper importSearchMapper;

	
	public void importSearchDb(HashMap<String, Object> queryMap){
		importSearchMapper.importSearchDb(queryMap);
	}
	
	
	public List<HashMap> getTableColumn(HashMap<String, Long> queryMap){
		return importSearchMapper.getTableColumn(queryMap);
	}

	public void updateImportStatus(HashMap<String, Object> queryMap){
		importSearchMapper.updateImportStatus(queryMap);
	}
	
	
	public List<HashMap> getProjectStatus(HashMap<String, Long> queryMap){
		return importSearchMapper.getProjectStatus(queryMap);
	}
	
	

}
