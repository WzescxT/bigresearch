package com.monetware.controller;

import com.monetware.model.analysis.AnalysisSentimeDictionary;
import com.monetware.model.analysis.AnalysisSentimeWord;
import com.monetware.model.common.RtInfo;
import com.monetware.service.analyze.AnalysisProjectService;
import com.monetware.service.analyze.AnalysisSentimeDictionaryService;
import com.monetware.util.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.HashMap;
import java.util.List;


/** 
 *@author  venbillyu 
 *@date 创建时间：2017年2月20日 上午11:40:38 
 *@describle 情感词典管理
 */
@RequestMapping("/analysis")
@Controller
public class AnalysisSentimeDictionaryController {
	@Autowired
	private AnalysisSentimeDictionaryService dictionaryService;

	@Autowired
	private AnalysisProjectService projectService;

	//获取当前用户情感词典
	@RequestMapping("/getUserDictionary")
	@ResponseBody
	public RtInfo getUserDictionary(@RequestBody HashMap<String, Object> queryMap,@RequestHeader HttpHeaders headers) {
		String token = headers.getFirst("Authorization");
		int userId=AuthUtil.parseToken(token);
		long pageNow = Long.decode(queryMap.get("pageNow").toString());
		long pageSize = Long.decode(queryMap.get("pageSize").toString());
		String name = queryMap.get("name").toString();
		RtInfo rtInfo=new RtInfo();
		try {
			rtInfo = dictionaryService.selectUserDictionaryByPage(name, userId, pageNow, pageSize);
		} catch (Exception e) {
			System.out.println(e);
			rtInfo.setError_code(1);
			rtInfo.setError_msg("查询失败，请重试");
		}
		return rtInfo;
		}



	//获取情感词典中的情感词汇
	@RequestMapping("/getSentimeWords")
	@ResponseBody
	public RtInfo getSentimeWords(@RequestBody HashMap<String, Object> queryMap,@RequestHeader HttpHeaders headers) {
		String token = headers.getFirst("Authorization");
		int userId=AuthUtil.parseToken(token);
		long dictionaryId = Long.decode(queryMap.get("dictionaryId").toString());
		long pageNow = Long.decode(queryMap.get("pageNow").toString());
		long pageSize = Long.decode(queryMap.get("pageSize").toString());
		String name = queryMap.get("name").toString();
		RtInfo rtInfo=new RtInfo();
		try {
			rtInfo = dictionaryService.getSentimeWords(dictionaryId, name, userId, pageNow, pageSize);
		} catch (Exception e) {
			System.out.println(e);
			rtInfo.setError_code(1);
			rtInfo.setError_msg("查询失败，请重试");
		}
		return rtInfo;
	}




	//添加情感词汇
	@RequestMapping("/insertWord")
	@ResponseBody
	public RtInfo insertWord(@RequestBody AnalysisSentimeWord term, @RequestHeader HttpHeaders headers) {
		String token = headers.getFirst("Authorization");
		int userId=AuthUtil.parseToken(token);
		RtInfo rtInfo = new RtInfo();
		long dictionaryId = term.getDictionaryId();
		AnalysisSentimeDictionary dictionary = dictionaryService.selectDictionaryById(dictionaryId);
		if (dictionary.getCreateUser()!=userId){
			rtInfo.setError_code(1);
			rtInfo.setError_msg("没有权限修改该词典");
			return rtInfo;
		}
		try {
				rtInfo = dictionaryService.insertWord(term.getDictionaryId(),term.getWord(), term.getScore(), userId);

			dictionaryService.updateDictionaryWordsNo(term.getDictionaryId());
			} catch (Exception e) {
			System.out.println(e);
				rtInfo.setError_code(1);
				rtInfo.setError_msg("情感词汇添加失败，请重试");

			}
		
		return rtInfo;
	}
	
	//删除情感词汇
	@RequestMapping("/deleteWord")
	@ResponseBody
	public RtInfo deleteWord(@RequestBody AnalysisSentimeWord term,@RequestHeader HttpHeaders headers) {
		String token = headers.getFirst("Authorization");
		int userId=AuthUtil.parseToken(token);
		RtInfo rtInfo = new RtInfo();
		try {
			AnalysisSentimeWord word = dictionaryService.selectWordById(term.getId());
			boolean bol = dictionaryService.deleteWord(term.getId());
			dictionaryService.updateDictionaryWordsNo(word.getDictionaryId());
			rtInfo.setRt_msg("情感词汇删除成功");
		} catch (Exception e) {
			rtInfo.setError_code(1);
			rtInfo.setError_msg("情感词汇删除失败，请重试");
		}
		return rtInfo;
	}
	
	
	
	//修改情感词汇
	@RequestMapping("/updateWord")
	@ResponseBody
	public RtInfo updateWord(@RequestBody AnalysisSentimeWord term,@RequestHeader HttpHeaders headers) {
		
		RtInfo rtInfo = new RtInfo();
		try {
			rtInfo = dictionaryService.updateWord(term);
		
		} catch (Exception e) {
			rtInfo.setError_code(1);
			rtInfo.setError_msg("情感词汇修改失败，请重试");
		}

		return rtInfo;
	}
	
	
	
	//获取用户所有情感词库
	@RequestMapping("/getSentimeDictionaries")
	@ResponseBody
	public RtInfo getSentimeWords(@RequestHeader HttpHeaders headers) {
		RtInfo rtInfo= new RtInfo();
		String token = headers.getFirst("Authorization");
		int userId=AuthUtil.parseToken(token);
		List<AnalysisSentimeDictionary> dictionaries = dictionaryService.getUserAllDictionary(userId);
		rtInfo.setRt_info(dictionaries);
		return rtInfo;
		
	}

	//添加用户词典
	@RequestMapping("/addSentimeDictionary")
	@ResponseBody
	public RtInfo addSentimeDictionary(@RequestBody AnalysisSentimeDictionary dictionary,@RequestHeader HttpHeaders headers) {
		RtInfo rtInfo= new RtInfo();
		String token = headers.getFirst("Authorization");
		int userId=AuthUtil.parseToken(token);
		boolean bol = dictionaryService.isUsedDictioanryName(dictionary.getName(),userId);
		if (bol){
			rtInfo.setError_code(1);
			rtInfo.setError_msg("该情感词典名称已存在！");
			return rtInfo;
		}
		boolean bol2 = dictionaryService.addDictionary(dictionary.getName(),userId);
		if (bol2){
			rtInfo.setRt_msg("情感词典添加成功！");
		}else {
			rtInfo.setError_code(1);
			rtInfo.setRt_msg("情感词典添加失败！");
		}
		return rtInfo;

	}

	//修改词典名称
	@RequestMapping("/updateSentimeDictionary")
	@ResponseBody
	public RtInfo updateSentimeDictionary(@RequestBody AnalysisSentimeDictionary dictionary,@RequestHeader HttpHeaders headers) {
		RtInfo rtInfo= new RtInfo();
		String token = headers.getFirst("Authorization");
		int userId=AuthUtil.parseToken(token);
		boolean bol = dictionaryService.isUsedDictioanryName(dictionary.getName(),userId);

		if (bol){
			rtInfo.setError_code(1);
			rtInfo.setError_msg("该情感词典名称已存在！");
			return rtInfo;
		}
		boolean bol2 = dictionaryService.updateDictionaryName(dictionary.getId(),dictionary.getName());
		if (bol2){
			rtInfo.setRt_msg("情感词典名称修改成功！");
		}else {
			rtInfo.setError_code(1);
			rtInfo.setRt_msg("情感词典名称修改失败！");
		}
		return rtInfo;

	}



	//删除词典
	@RequestMapping("/deleteSentimeDictionary")
	@ResponseBody
	public RtInfo deleteSentimeDictionary(@RequestBody AnalysisSentimeDictionary dictionary,@RequestHeader HttpHeaders headers) {
		RtInfo rtInfo= new RtInfo();
		String token = headers.getFirst("Authorization");
		int userId=AuthUtil.parseToken(token);
		AnalysisSentimeDictionary realDictionary = dictionaryService.selectDictionaryById(dictionary.getId());
		if (realDictionary.getCreateUser() != userId){
			rtInfo.setError_code(1);
			rtInfo.setError_msg("由于权限问题，无法删除该词典！");
			return rtInfo;
		}
		boolean bol = dictionaryService.deleteDictionary(dictionary.getId());
		if (bol){
			rtInfo.setRt_msg("情感词典删除成功！");
		}else {
			rtInfo.setError_code(1);
			rtInfo.setRt_msg("情感词典删除失败！");
		}
		return rtInfo;

	}






	
}
