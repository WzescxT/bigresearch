package com.monetware.service.analyze;

import com.monetware.mapper.analysis.AnalysisProjectClassifyTrainingMapper;
import com.monetware.model.analysis.AnalysisProjectClassifyTraining;
import com.monetware.util.DataUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/** 
 *@author  venbillyu 
 *@date 创建时间：2017年2月14日 下午5:12:36 
 *@describle 分类训练集增删改查
 */
@Service
public class AnalysisProjectClassifyTrainingService {
	@Autowired
	private AnalysisProjectClassifyTrainingMapper trainingMapper;
	@Autowired
	private AnalysisProjectService projectService;
	
	public boolean insertTrainingData(AnalysisProjectClassifyTraining trainingData){
		int res = trainingMapper.insertSelective(trainingData);
		return res==0?true : false;
	} 
	 
	public boolean deleteTrainingData(long id){
		int res = trainingMapper.deleteByPrimaryKey(id);
		return res==0?true : false;
	} 
	
	public boolean deleteTrainingByCategory(String category,int userId){
		int res = trainingMapper.deleteTrainingByCategory(category,userId);
		return res==0?true : false;
	}
	
	public boolean updateTrainingData(AnalysisProjectClassifyTraining trainingData){
		int res = trainingMapper.updateByPrimaryKeySelective(trainingData);
		return res==0?true : false;
	} 
	
	
	
	//根据用户和分类查询查询训练文本集合(一个分类)
	public List<AnalysisProjectClassifyTraining> getTrainingListByCategory(int userId,String category){
		return trainingMapper.getTrainingListByCategory(userId,category);
	}
	//根据用户和分类查询查询训练文本集合(一个分类)
	public long getTrainingNoByCategory(int userId,String category){
		return trainingMapper.getTrainingNoByCategory(userId,category);
	}
	
	
	
	//根据用户和分类查询查询训练文本集合(一个分类)-分页
	public List<AnalysisProjectClassifyTraining> getTrainingByPage(int userId,String category,long pageNow,long pageSize){
		return trainingMapper.getTrainingByPage(userId,category, (pageNow-1)*pageSize, pageSize);
	}
	
	
	
	
	
	
	//根据用户和分类查询查询训练文本集合(多个分类)
	public List<AnalysisProjectClassifyTraining> getTrainingListByCategories(int userId,String categories){
		String categoriesSql="";
		String categoryArr[]=categories.split(" ");
		for (String category : categoryArr) {
			categoriesSql=categoriesSql+" '"+category+"' ,";
		}
		if (!categoriesSql.equals("")) {
			categoriesSql=categoriesSql.substring(0, categoriesSql.length()-1);
		}
		return trainingMapper.getTrainingListByCategories(userId,categoriesSql);
	}
	
	
	
	
	//整理用户训练集信息  （类名  训练集）
	public Map<String, List<AnalysisProjectClassifyTraining>> getTrainingMap(int userId){
		Map<String, List<AnalysisProjectClassifyTraining>> trainingMap=new HashMap<String, List<AnalysisProjectClassifyTraining>>();
		List<AnalysisProjectClassifyTraining> trainingList=trainingMapper.getTrainingByUser(userId);
		//对list进行分类
		for (AnalysisProjectClassifyTraining training : trainingList) {
			String categoryName=training.getCategoryName();
			if (trainingMap.containsKey(categoryName)) {
				trainingMap.get(categoryName).add(training);
			}else {
				List<AnalysisProjectClassifyTraining> list=new ArrayList<AnalysisProjectClassifyTraining>();
				list.add(training);
				trainingMap.put(categoryName, list);
			}
		}
		return trainingMap;
		
	}
		
	
	
	//根据用户查询分类
	public List<AnalysisProjectClassifyTraining> getTrainingByUser(int userId){
			return trainingMapper.getTrainingByUser(userId);
			
		}
	
	//根据用户查询分类集合 
	public List<String> getCategories(String name,int userId){
		return trainingMapper.getCategories(name,userId);
		
	}

	//获取当月分类总量
	public long getThisMonthItems(String name,int userId){
		return trainingMapper.getThisMonthItems(name,userId, DataUtil.getThisMonthFirstDay());
	}

	
	//查询用户分析项目表 ，获取categories ，并将其进行编号
	public Hashtable<String, Integer> getHashCategories(long projectId){
		Hashtable<String, Integer> hashCategories=new Hashtable<String, Integer>();
		//获取空格分隔的分类
		String categories = projectService.getAnalysisProject(projectId).getClassifyCategories();
		String categorieArr[] = categories.split(" ");
		int i=0;
		for (String str : categorieArr) {
			hashCategories.put(str, i);
			i=i+1;
		}
		return hashCategories;
		
	}
	
	
	
	public List<Map<String, String>> getCategoriesByPage(String name,int userId,long pageNow,long pageSize){
		return trainingMapper.getCategoriesByPage(name, userId, (pageNow-1)*pageSize, pageSize);
	}

	public void updateCategoryName(String oldCategoryName, String newCategoryName, int userId) {
		trainingMapper.updateCategoryName(oldCategoryName,newCategoryName,userId);
		
	}
	
	
	
	public void delTraining(long id){
		trainingMapper.deleteByPrimaryKey(id);
	}
	
	
	
}
