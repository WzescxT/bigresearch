package com.monetware.service.collect;

import com.monetware.mapper.collect.CollectInfoMapper;
import com.monetware.model.collect.CollectInfo;
import com.monetware.model.collect.CollectTemplate;
import com.monetware.model.collect.CollectTemplateField;
import com.monetware.util.jdbcUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 
 *@author  venbillyu 
 *@date 创建时间：2016年11月29日 上午9:41:31 
 *@describle 信息采集
 */
@Service
public class CollectInfoService{
	
	@Autowired
	private CollectInfoMapper collectInfoMapper;
	@Autowired
	private CollectTemplateService collectTemplateService;
	@Autowired
	private CollectProjectService collectProjectService;
	//	创建采集信息表
	public void createInfoTable(long projectId)
			throws SQLException {
		long templateId = collectProjectService.getCollectProject(projectId).getTemplateId();
		CollectTemplate collectTemplate=collectTemplateService.getCollectTemplateById(templateId);

		List<CollectTemplateField> fields = collectTemplate.getFields();
		String columns[] = new String[fields.size()];
		for (int i = 0; i < fields.size(); i++) {
			columns[i] = fields.get(i).getName();
		}
			jdbcUtil.createTable("collect_info_" + projectId, columns);
		}


	public void createCollectInfo(CollectInfo collectInfo) {
		collectInfoMapper.createCollectInfo(collectInfo);
	}
	
	

	public List<Map<String,Object>> getCollectInfos(HashMap<String, Long> queryMap) {
		return collectInfoMapper.getCollectInfos(queryMap);
	}


	public long getCollectInfoNo(long projectId) {
		HashMap<String,Long> queryMap=new HashMap<String, Long>();
		queryMap.put("projectId",projectId );
		return collectInfoMapper.getCollectInfoNo(queryMap);
	}
	
	public void deleteCollectInfo(long projectId){
		collectInfoMapper.deleteCollectInfo(projectId);
	}
	
	
	public List<HashMap> getCommonInfo(HashMap<String, Long> queryMap){
		return collectInfoMapper.getCommonInfo(queryMap);
	}

}
