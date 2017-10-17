package com.monetware.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.monetware.model.analysis.TextLibrary;
import com.monetware.model.common.RtInfo;
import com.monetware.service.analyze.AnalysisDBService;
import com.monetware.service.analyze.AnalysisProjectService;
import com.monetware.service.analyze.TextLibraryService;
import com.monetware.util.AuthUtil;

/** 
 *@author  venbillyu 
 *@date 创建时间：2017年2月27日 下午3:11:31 
 *@describle 数据库可视化工具
 */
@RequestMapping("/analysis/db")
@Controller
public class AnalysisDBController {

	@Autowired
	private TextLibraryService libraryService;
	@Autowired
	private AnalysisDBService dbService;
	@Autowired
	private AnalysisProjectService projectService;
	
	//获取当前用户文本库
	@RequestMapping("/getUserLibraries")
	@ResponseBody
	public RtInfo getUserLibraries(@RequestHeader HttpHeaders headers) {
		RtInfo rtInfo = new RtInfo();
		String token = headers.getFirst("Authorization");
		int userId=AuthUtil.parseToken(token);
		List<TextLibrary> textLibraries = libraryService.getUserAllTextLibraries(userId);
		rtInfo.setRt_info(textLibraries);
		return rtInfo;
	
	}
	
	
	//获取当前用户文本库
	@RequestMapping("/getLibraryColumns")
	@ResponseBody
	public RtInfo getLibraryColumns(@RequestBody Map<String, Long> queryMap,@RequestHeader HttpHeaders headers) {
		System.out.println("get columns");
		RtInfo rtInfo = new RtInfo();
		String token = headers.getFirst("Authorization");
		int userId=AuthUtil.parseToken(token);
		//獲取文本庫id
		long textlibraryId= queryMap.get("id");
//		System.out.println(textlibraryId);
		String columns = projectService.getColumnJson(textlibraryId,false);
//		System.out.println(columns);
		rtInfo.setRt_info(columns);
		return rtInfo;
	}
	
	
	
	//生成sql
	@RequestMapping("/createSql")
	@ResponseBody
	public RtInfo createSql(@RequestBody Map<String, String> paramMap,@RequestHeader HttpHeaders headers) {
		RtInfo rtInfo = new RtInfo();
		String token = headers.getFirst("Authorization");
		int userId=AuthUtil.parseToken(token);
		
		String type = paramMap.get("type");		
		//slq方式
		if (type.equals("select")) {
			
		}else if (type.equals("count")) {
			
		}else if (type.equals("update")) {
			
		}else if (type.equals("delete")) {
			
		}else if (type.equals("insert")) {
			
		}
		
		
		return rtInfo;
	}
	
	
	

	
	
	//执行sql语句
	@RequestMapping("/excuteSql")
	@ResponseBody
	public RtInfo excuteSql(@RequestBody Map<String, String> paramMap,@RequestHeader HttpHeaders headers) {
		RtInfo rtInfo = new RtInfo();
		try {
			String token = headers.getFirst("Authorization");
			int userId=AuthUtil.parseToken(token);
			
			String textLibraryId = paramMap.get("textLibraryId");
			//用户是否有权限操作该表
			boolean bol = dbService.checkPermissions(textLibraryId, userId);
			String sqlStr = paramMap.get("sqlStr");
			if (!bol) {
				rtInfo.setError_code(1);
				rtInfo.setError_msg("您没有权限操作这个数据表！");
			}
			long type = Long.decode(paramMap.get("type"));
			if (type==1) {
				//查询
				long pageNow = Long.decode(paramMap.get("pageNow"));
				long pageSize = Long.decode(paramMap.get("pageSize"));
				List<Map<String, String>> mapList = dbService.select(sqlStr, pageNow, pageSize);
				long bigTotalItems = dbService.countSelect(sqlStr);
				rtInfo.setRt_mapinfo("bigTotalItems", bigTotalItems);
				rtInfo.setRt_mapinfo("results", mapList);
				
				
			}else if(type==2){
				//统计数量
				
				long bigTotalItems = dbService.count(sqlStr);
				System.out.println("No===>"+bigTotalItems);
				rtInfo.setRt_mapinfo("bigTotalItems", bigTotalItems);
				rtInfo.setRt_msg("共查到"+bigTotalItems+"条数据！");
				if (bigTotalItems==0) {
					rtInfo.setRt_msg("没有查到数据！");
					
				}
				
			}else if(type==3){
				//修改数据
				
				long bigTotalItems = dbService.update(sqlStr);
				rtInfo.setRt_mapinfo("bigTotalItems", bigTotalItems);
				rtInfo.setRt_msg("成功修改"+bigTotalItems+"条数据！");
				if (bigTotalItems==0) {
					rtInfo.setRt_msg("没有成功修改数据！");
					
				}
			}else if(type==4){
				//删除
				long bigTotalItems = dbService.delete(sqlStr);			
				rtInfo.setRt_mapinfo("bigTotalItems", bigTotalItems);
				rtInfo.setRt_msg("成功删除"+bigTotalItems+"条数据！");
				if (bigTotalItems==0) {
					rtInfo.setRt_msg("没有成功删除数据！");
					
				}
				
			}else if(type==5){
				long bigTotalItems = dbService.insert(sqlStr);
				rtInfo.setRt_mapinfo("bigTotalItems", bigTotalItems);
				rtInfo.setRt_msg("成功插入"+bigTotalItems+"条数据！");
				if (bigTotalItems==0) {
					rtInfo.setRt_msg("没有成功插入数据！");
					
				}
			}
		} catch (Exception e) {
			rtInfo.setError_code(1);
			rtInfo.setError_msg("SQL执行异常，请重试！");
		}
		

		
		return rtInfo;
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
