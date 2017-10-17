package com.monetware.service.analyze;

import com.google.gson.Gson;
import com.monetware.mapper.analysis.AnalysisProjectWordsMapper;
import com.monetware.mapper.analysis.TextLibraryInfoMapper;
import com.monetware.model.analysis.AnalysisProject;
import com.monetware.model.analysis.AnalysisProjectWord;
import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.BaseAnalysis;
import org.ansj.splitWord.analysis.IndexAnalysis;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/** 
 *@author  venbillyu 
 *@date 创建时间：2016年12月14日 上午11:32:23 
 *@describle 采用Ansj分词算法
 *各种分词特性详情见：http://nlpchina.github.io/ansj_seg/
 */
@Service
public class WordSegmentService {
	@Autowired
	AnalysisProjectService analysisProjectService;
	@Autowired
	AnalysisProjectWordsMapper wordsMapper;
	@Autowired
	AnalysisProjectWordService wordService;
	@Autowired
	TextLibraryInfoMapper textLibraryInfoMapper;
	//基本分词
	public List<Term> BaseAnalysis(String toParsStr){
		Result parse = BaseAnalysis.parse(toParsStr);
		List<Term> terms=parse.getTerms();
		/*for (Term term : terms) {
			//词组
			System.out.println(term.getName());
			//词性
			System.out.println(term.getNatureStr());
		}*/
		return terms;
	}
	
	//精准分词
	public List<Term> ToAnalysis(String toParsStr){
		Result parse = ToAnalysis.parse(toParsStr);
		List<Term> terms=parse.getTerms();
		return terms;
	}
	
	//nlp分词
	public List<Term> NlpAnalysis(String toParsStr){
		Result parse = NlpAnalysis.parse(toParsStr);
		List<Term> terms=parse.getTerms();
		return terms;
		
		
	}
	
	//面向索引
	public List<Term> IndexAnalysis(String toParsStr){
		Result parse = IndexAnalysis.parse(toParsStr);
		List<Term> terms=parse.getTerms();
		return terms;
	}
	
	
	
	
	//分词入库
	@SuppressWarnings("unchecked")
	public void wordSegment(long analysisProjectId){
		//建表
		
		wordsMapper.createAnalysisProjectWordTable(analysisProjectId);
		
		//获取文本库路径
		AnalysisProject project=analysisProjectService.getAnalysisProject(analysisProjectId);
		String type=project.getSegmentType();
		long textLibraryId=project.getTextlibraryId();
		String segmentColumn=project.getSegmentColumn();
		Map<String, Boolean> columnMap=new HashMap<String, Boolean>();
		columnMap=new Gson().fromJson(segmentColumn, columnMap.getClass());
		String columnSql="";
		for (Entry<String, Boolean> entry : columnMap.entrySet()) {
			if (entry.getValue().equals(true)) {
				
				columnSql=columnSql+entry.getKey()+",";
			}
		}
		if (!columnSql.equals("")) {
			
			columnSql=columnSql.substring(0, columnSql.length()-1);
		}
		long maxLineNo=textLibraryInfoMapper.getTextLibraryInfoNo(textLibraryId);
		Map<String, String> queryMap=new HashMap<String, String>();
		
		List<Term> terms=new ArrayList<Term>();
		List<AnalysisProjectWord> words=new ArrayList<AnalysisProjectWord>();
		String content="";
		for (int i = 1; i <= maxLineNo; i++) {
			content="";
			//获取文本
			queryMap=textLibraryInfoMapper.getOneInfo(textLibraryId, columnSql, i);
			for (Entry<String, String> entry : queryMap.entrySet()) {
				
				if (!StringUtils.isEmpty(entry.getValue())) {
					content=content+" "+entry.getValue();
					
				}
			}
			//分词
			if (type.equals("BaseAnalysis")) {
				terms=BaseAnalysis(content);
			}else if(type.equals("ToAnalysis")){
				terms=ToAnalysis(content);
			}else if(type.equals("NlpAnalysis")){
				terms=NlpAnalysis(content);
			}else if(type.endsWith("IndexAnalysis")){
				terms=IndexAnalysis(content);
			}else{
				//base
				terms=BaseAnalysis(content);
			}			
			//入库
			
			if (terms!=null&&terms.size()!=0) {
				words=wordService.getWords(terms);
			}
			wordsMapper.batchInsert(analysisProjectId, words);
				
		}
		
		
		wordsMapper.sumWordFrequency(analysisProjectId);
		
		
	}
	
	

}
