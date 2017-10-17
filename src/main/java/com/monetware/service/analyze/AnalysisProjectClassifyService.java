package com.monetware.service.analyze;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.ibatis.annotations.Param;
import org.apache.lucene.analysis.util.CharArrayMap.EntrySet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thunlp.text.classifiers.BasicTextClassifier;
import org.thunlp.text.classifiers.BigramChineseTextClassifier;
import org.thunlp.text.classifiers.ClassifyResult;
import org.thunlp.text.classifiers.LinearBigramChineseTextClassifier;
import org.thunlp.text.classifiers.TextClassifier;

import com.google.gson.Gson;
import com.monetware.mapper.analysis.AnalysisProjectClassifyMapper;
import com.monetware.model.analysis.AnalysisProject;
import com.monetware.model.analysis.AnalysisProjectClassify;
import com.monetware.model.analysis.AnalysisProjectClassifyTraining;

/** 
 *@author  venbillyu 
 *@date 创建时间：2017年2月15日 上午10:22:20 
 *@describle 文本分类（改写了thuctc 分词 存储和读取Mode方式）
 */
@Service
public class AnalysisProjectClassifyService {
	public int userId;
	public long projectId;
	@Autowired 
	private AnalysisProjectClassifyTrainingService trainingService;
	@Autowired 
	private AnalysisProjectService projectService;
	@Autowired
	private TextLibraryService libraryService;
	@Autowired
	private AnalysisProjectClassifyMapper classifyMapper;
	//训练文本
	public void runTrainAndTest(){
		AnalysisProject project = projectService.getAnalysisProject(projectId);
		String categories = project.getClassifyCategories();
		project.setClassifyStartTime(new Date());
		project.setClassifyStatus(1);
		projectService.updateAnalysisProject(project);
		
		// 新建分类器对象
		BasicTextClassifier classifier = new BasicTextClassifier();
		//获取分类数，初始化分类器
		
		String categoryArr[] = categories.split(" ");
		int categorySize=categoryArr.length;
		// 初始化
		classifier.loadCategoryList(categories);
		classifier.Init(categorySize);
		//设置训练文本
		List<AnalysisProjectClassifyTraining> trainings=trainingService.getTrainingListByCategories(userId, categories);
		
		
		// 运行
		String modelStr = classifier.runAsBigramChineseTextClassifier(trainings);
		
		project.setClassifyModel(modelStr);
		//保存模型
		projectService.updateAnalysisProject(project);
		
		
	}
	//使用模型进行分类
	
	/**
	 * 如果需要读取已经训练好的模型，再用其进行分类，可以按照本函数的代码调用分类器
	 * 
	 */
	public void runLoadModelAndUse() {
		//建表保存分类结果
		classifyMapper.createClassifyResultTable(projectId);
		
		// 新建分类器对象
		BasicTextClassifier classifier = new BasicTextClassifier();
		// 设置分类种类，并读取模型
		
		
		//获取分类数，初始化分类器
		AnalysisProject project = projectService.getAnalysisProject(projectId);
		
		long textLibraryId = project.getTextlibraryId(); 
		
		String categories = project.getClassifyCategories();
		
		String modelStr = project.getClassifyModel();
		
		classifier.loadCategoryList(categories);
		
		classifier.setTextClassifier(new LinearBigramChineseTextClassifier(classifier.getCategorySize()));
		classifier.getTextClassifier().loadFromString(modelStr);
		
		// 之后就可以使用分类器进行分类
		
		//获取文本库中的有效字段文本
		String fieldsJson=project.getClassifyFields();
		//有限字段sql
		String columnSql = projectService.getColumnSql(fieldsJson, false);
		
		long max = libraryService.getTextLibraryInfoNo(textLibraryId);
		Map<String, String> infoMap=new HashMap<String,String>();
		for (long i = 1; i <= max; i++) {
			String text="";
			 infoMap = libraryService.getOneInfo(textLibraryId, columnSql, i);
			 for (Entry<String, String> entry : infoMap.entrySet()) {
				text = text+" \n "+entry.getValue();
			}
			 System.out.println("分析文本====》"+text);
			 
			int topN = 1;  // 只保留一个分类结果
			ClassifyResult[] result = classifier.classifyText(text, topN);
			
			
			int categoryId = result[0].label;
			String categoryName = classifier.getCategoryName(result[0].label);
			long textInfoId = i ;
			
			classifyMapper.insert(projectId,categoryName,categoryId,textInfoId); 
			 
		}
		
		project.setClassifyStatus(2);
		project.setClassifyEndTime(new Date());
		
		
	}

	public List<Map<String, String>> getClassifyResult(long projectId,
			long pageNow, long pageSize) {
		return classifyMapper.getCategoriesByPage(projectId,
				(pageNow-1)*pageSize, pageSize);
	}

	public long getClassifyResultNo(long projectId) {
		return classifyMapper.getCategoriesNo(projectId);
	}

	
	public List<Long> getInfoByCategory(long projectId,String categoryName,long pageNow, long pageSize){
		return classifyMapper.getInfoByCategory(projectId, categoryName, (pageNow-1)*pageSize, pageSize);
	}
	
	
	public long getInfoNoByCategory(long projectId,String categoryName){
		return classifyMapper.getInfoNoByCategory(projectId, categoryName);
	}

	
	
	
	
	
	
	
}
