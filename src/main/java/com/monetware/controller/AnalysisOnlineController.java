package com.monetware.controller;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ansj.app.keyword.KeyWordComputer;
import org.ansj.app.keyword.Keyword;
import org.ansj.domain.Term;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.monetware.model.common.RtInfo;
import com.monetware.service.analyze.AnalysisOnlineService;
import com.monetware.service.analyze.WordSegmentService;

/** 
 *@author  venbillyu 
 *@date 创建时间：2017年2月22日 上午10:08:14 
 *@describle 在线分析
 */
@RequestMapping("/analysis/online")
@Controller
public class AnalysisOnlineController {
	@Autowired
	private AnalysisOnlineService onlineService;
	@Autowired
	private WordSegmentService segmentService;
	
	@RequestMapping("/getKeyWords")
	@ResponseBody
	public RtInfo getKeyWords(@RequestBody Map<String, String> paramMap,@RequestHeader HttpHeaders headers) {
		RtInfo rtInfo= new RtInfo();
		//提取20个关键词
		KeyWordComputer kwc = new KeyWordComputer(40);
		String content = paramMap.get("content");
		
		Collection<Keyword> result = kwc.computeArticleTfidf("", content);
		List<Map<String, Object>> resList = new ArrayList<Map<String, Object>>();
		Map<String, Object> resMap;
		for (Keyword keyword : result) {
			resMap = new HashMap<String, Object>();
			resMap.put("text", keyword.getName());
			resMap.put("weight", keyword.getScore());
//			resMap.put("link", "");
			resList.add(resMap);
		}
        rtInfo.setRt_info(resList);
		return rtInfo;
		
	}
	
	
	/**
	 * 文本摘要问题
	 * 1.从原文截选：不够简练精确
	 * 2.根据关键词重造语句：很难使语句流畅
	 * 
	 * 筛选方法：
	 * 1.获取文本一定比重的关键词
	 * 2.将文本分隔成句子
	 * 3.找到含有这些关键词的句子
	 * 4.根据文本句子顺序重组文本，生成摘要
	 * 
	 * @param args
	 */
	@RequestMapping("/getAbstract")
	@ResponseBody
	public RtInfo getAbstract(@RequestBody Map<String, String> paramMap) {
		RtInfo rtInfo= new RtInfo();
		String content = paramMap.get("content");
		List<Map<String, Object>> wordList= onlineService.getAbstract(content);
		rtInfo.setRt_info(wordList);
		return rtInfo;
	}
	
	//切换繁简体
	@RequestMapping("/ZHConvert")
	@ResponseBody
	public RtInfo ZHConvert(@RequestBody Map<String, Object> paramMap) throws FileNotFoundException {
		RtInfo rtInfo= new RtInfo();
		String content = paramMap.get("content").toString();
		boolean toTraditional = (boolean) paramMap.get("toTraditional");
		content = onlineService.convertZH(content, toTraditional);
		rtInfo.setRt_info(content);
		return rtInfo;
		
	}
	
	
	//获取文本拼音
	@RequestMapping("/getPinyin")
	@ResponseBody
	public RtInfo getPinyin(@RequestBody Map<String, String> paramMap) {
		RtInfo rtInfo= new RtInfo();
		String content = paramMap.get("content");
		content = onlineService.getPinyin(content);
		rtInfo.setRt_info(content);
		return rtInfo;
	}
	
	
	//四种分词方式
	@RequestMapping("/getSegmentContent")
	@ResponseBody
	public RtInfo getSegmentContent(@RequestBody Map<String, String> paramMap) {
		RtInfo rtInfo= new RtInfo();
		String toParsStr = paramMap.get("content");
		int type = Integer.parseInt(paramMap.get("segmentType").toString());
		List<Term> terms = new ArrayList<Term>();
		switch (type) {
		case 1:terms=segmentService.BaseAnalysis(toParsStr);
			
			break;
		case 2:terms=segmentService.IndexAnalysis(toParsStr);
			
			break;
		case 3:terms=segmentService.NlpAnalysis(toParsStr);
			
			break;
		case 4:terms=segmentService.ToAnalysis(toParsStr);
			break;

		default:
			break;
		}
		rtInfo.setRt_info(terms);
		return rtInfo;
	}
	
	
	
	
	
	
	
	
	
	
	
	
}
