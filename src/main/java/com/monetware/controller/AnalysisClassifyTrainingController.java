package com.monetware.controller;

import com.monetware.model.analysis.AnalysisProject;
import com.monetware.model.analysis.AnalysisProjectClassifyTraining;
import com.monetware.model.common.RtInfo;
import com.monetware.service.analyze.AnalysisProjectClassifyTrainingService;
import com.monetware.service.analyze.AnalysisProjectService;
import com.monetware.util.AuthUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 
 *@author  venbillyu 
 *@date 创建时间：2017年2月16日 上午11:13:57 
 *@describle 分类训练集
 */
@RequestMapping("/analysis")
@Controller
public class AnalysisClassifyTrainingController {
	@Autowired
	private AnalysisProjectClassifyTrainingService trainingService;
	@Autowired
	private AnalysisProjectService projectService;
	
	
//查询分类训练集
	@RequestMapping("/getClassifyTraining")
	@ResponseBody
	public RtInfo getClassifyTraining(@RequestBody HashMap<String, Object> queryMap,@RequestHeader HttpHeaders headers) {
		RtInfo rtInfo= new RtInfo();
		try {
			String token = headers.getFirst("Authorization");
			int userId=AuthUtil.parseToken(token);
			
			long pageNow = Long.decode(queryMap.get("pageNow").toString());
			long pageSize = Long.decode(queryMap.get("pageSize").toString());
			String name = queryMap.get("name").toString();


			List<Map<String, String>> trainingList = trainingService.getCategoriesByPage(name,userId, pageNow, pageSize);
			List<String> categories= trainingService.getCategories(name,userId);
			long thisMonthItems = trainingService.getThisMonthItems(name,userId);

			long bigTotalItems = categories.size();

			rtInfo.setRt_mapinfo("thisMonthItems",thisMonthItems);
			rtInfo.setRt_mapinfo("analysisProjectClassifyTraining", trainingList);
		    rtInfo.setRt_mapinfo("bigTotalItems", bigTotalItems);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			rtInfo.setError_code(1);
			rtInfo.setError_msg("查询失败");
		}
		
		return rtInfo;
	
	}



	//添加分类
	@RequestMapping(value = "/addCategory", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public RtInfo addCategory(@RequestBody AnalysisProjectClassifyTraining training,@RequestHeader HttpHeaders headers) {
		RtInfo rtInfo= new RtInfo();
		String category=training.getCategoryName();
		if (category.contains(" ")||category.contains(",")||category.contains("'")) {
			rtInfo.setError_code(1);
			rtInfo.setError_msg("分类名称含有非法字符");
			return rtInfo;
		}




		if (StringUtils.isEmpty(category)) {
			rtInfo.setError_code(1);
			rtInfo.setError_msg("没有输入分类");
			return rtInfo;
		}

		try {
			String token = headers.getFirst("Authorization");
			int userId=AuthUtil.parseToken(token);
			training.setCreateUser(userId);
			if (trainingService.getCategories(category,userId).size()>0){
				rtInfo.setError_code(1);
				rtInfo.setError_msg("已有改分类名称，请重新输入！");
				return rtInfo;
			}

			training.setCreateTime(new Date());
			trainingService.insertTrainingData(training);
			rtInfo.setRt_msg("添加成功");

		} catch (Exception e) {
			rtInfo.setError_code(1);
			rtInfo.setError_msg("添加失败");
		}

		return rtInfo;

	}



	//添加训练文本
	@RequestMapping(value = "/addClassifyTraining", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public RtInfo addTrainingCategory(@RequestBody AnalysisProjectClassifyTraining training,@RequestHeader HttpHeaders headers) {
		RtInfo rtInfo= new RtInfo();
		String category=training.getCategoryName();
		if (category.contains(" ")||category.contains(",")||category.contains("'")) {
			rtInfo.setError_code(1);
			rtInfo.setError_msg("分类名称含有非法字符");
			return rtInfo;
		}
		
		
		
		String text=training.getText();
		if (StringUtils.isEmpty(category)) {
			rtInfo.setError_code(1);
			rtInfo.setError_msg("没有输入分类");
			return rtInfo;
		}
		if (StringUtils.isEmpty(text)) {
			rtInfo.setError_code(1);
			rtInfo.setError_msg("没有输入文本");
			return rtInfo;
		}
		try {
			String token = headers.getFirst("Authorization");
			int userId=AuthUtil.parseToken(token);
			training.setCreateUser(userId);


			training.setCreateTime(new Date());
			trainingService.insertTrainingData(training);
			rtInfo.setRt_msg("添加成功");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			rtInfo.setError_code(1);
			rtInfo.setError_msg("添加失败");
		}
		
		return rtInfo;
	
	}		
	
	
	//删除分类和该分类的所有文本
		@RequestMapping(value = "/delTrainingCategory", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
		@ResponseBody
		public RtInfo delTrainingCategory(@RequestBody AnalysisProjectClassifyTraining training,@RequestHeader HttpHeaders headers) {
			RtInfo rtInfo= new RtInfo();
			String category=training.getCategoryName();
			if (StringUtils.isEmpty(category)) {
				rtInfo.setError_code(1);
				rtInfo.setError_msg("没有选择分类");
				return rtInfo;
			}
			
			try {
				String token = headers.getFirst("Authorization");
				int userId=AuthUtil.parseToken(token);
				training.setCreateUser(userId);
				trainingService.deleteTrainingByCategory(category, userId);
				rtInfo.setRt_msg("删除成功");
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				rtInfo.setError_code(1);
				rtInfo.setError_msg("删除失败");
			}
			
			return rtInfo;
		
		}		
	
	
		//修改分类名称
		@RequestMapping(value = "/updateCategoryName", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
		@ResponseBody
		public RtInfo updateCategoryName(@RequestBody Map<String, String> paramMap,@RequestHeader HttpHeaders headers) {
			RtInfo rtInfo= new RtInfo();
			String token = headers.getFirst("Authorization");
			int userId=AuthUtil.parseToken(token);

			String oldCategoryName=paramMap.get("oldCategoryName");
			String newCategoryName=paramMap.get("newCategoryName");
			if (StringUtils.isEmpty(oldCategoryName)||StringUtils.isEmpty(newCategoryName)) {
				rtInfo.setError_code(1);
				rtInfo.setError_msg("输入分类为空");
				return rtInfo;
			}else{

				if (trainingService.getCategories(newCategoryName,userId).size()>0){
					rtInfo.setError_code(1);
					rtInfo.setError_msg("已有改分类名称，请重新输入！");
					return rtInfo;
				}

			}
			
			try {

				trainingService.updateCategoryName(oldCategoryName,newCategoryName, userId);
				rtInfo.setRt_msg("成功修改分类名称");
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				rtInfo.setError_code(1);
				rtInfo.setError_msg("修改分类名称失败");
			}
			
			return rtInfo;
		
		}		
		
		

		//分页查询分类文本信息
		@RequestMapping("/getPageTrainingText")
		@ResponseBody
		public RtInfo getPageTraingingText(@RequestBody Map<String, Object> queryMap,@RequestHeader HttpHeaders headers) {
			RtInfo rtInfo= new RtInfo();
			try {
				String token = headers.getFirst("Authorization");
				int userId=AuthUtil.parseToken(token);
				String categoryName = queryMap.get("categoryName").toString();
				long pageNow = Long.decode(queryMap.get("pageNow").toString());
				long pageSize = Long.decode(queryMap.get("pageSize").toString());
				List<AnalysisProjectClassifyTraining> trainingList = trainingService.getTrainingByPage(userId, categoryName, pageNow, pageSize);
				
				long bigTotalItems = trainingService.getTrainingNoByCategory(userId, categoryName);
				
				rtInfo.setRt_mapinfo("classifyTraining", trainingList);
			    rtInfo.setRt_mapinfo("bigTotalItems", bigTotalItems);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				rtInfo.setError_code(1);
				rtInfo.setError_msg("查询失败");
			}
			
			return rtInfo;
		
		}	
		
		//删除训练文本
		@RequestMapping("/delTraining")
		@ResponseBody
		public RtInfo delTraining(@RequestBody AnalysisProjectClassifyTraining training) {
			RtInfo rtInfo= new RtInfo();
			long id = training.getId();
			try {
				trainingService.delTraining(training.getId());
				rtInfo.setRt_msg("训练文本删除成功");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				rtInfo.setError_code(1);
				rtInfo.setError_msg("删除失败");
			}
			
			return rtInfo;
			
		}			
		
		//将数据库中的已选分类和全部类型进行处理 ，返回 {str:boolean}
		@RequestMapping("/getCategories")
		@ResponseBody
		public RtInfo getCategories(@RequestBody AnalysisProject project,@RequestHeader HttpHeaders headers) {
			RtInfo rtInfo= new RtInfo();
			try {
				String token = headers.getFirst("Authorization");
				int userId=AuthUtil.parseToken(token);
				long projectId = project.getId();
				String selectedCategories=projectService.getAnalysisProject(projectId).getClassifyCategories();
				String selectedCategoriesArr[]={};
				if (selectedCategories!=null&!StringUtils.isEmpty(selectedCategories)) {
					selectedCategoriesArr=selectedCategories.split(" ");
				}
				
				List<String> categories = trainingService.getCategories("",userId);
				
				Map<String , Boolean> map = new HashMap<String , Boolean> (); 
				for (String category : categories) {
					map.put(category, false);
					for (int i = 0; i < selectedCategoriesArr.length; i++) {
						if (category.equals(selectedCategoriesArr[i])) {
							map.put(category, true);
							break;
						}
						
					}
				}
				
				
				rtInfo.setRt_info(map);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				rtInfo.setError_code(1);
				rtInfo.setError_msg("查询失败");
			}
			
			return rtInfo;
			
		}			
		
		
	
		
}
