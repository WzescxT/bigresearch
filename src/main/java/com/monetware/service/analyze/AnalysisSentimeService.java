package com.monetware.service.analyze;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.monetware.mapper.analysis.AnalysisSentimeDictionaryMapper;
import com.monetware.mapper.analysis.AnalysisSentimeWordMapper;
import com.monetware.model.analysis.AnalysisSentimeWord;
import org.ansj.domain.Term;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.monetware.mapper.analysis.AnalysisSentimeMapper;
import com.monetware.model.analysis.AnalysisProject;
import com.monetware.model.analysis.AnalysisSentime;
import com.monetware.model.common.RtInfo;

/** 
 *@author  venbillyu 
 *@date 创建时间：2017年2月20日 下午4:41:32 
 *@describle 情感分析处理
 */
@Service
public class AnalysisSentimeService {
	@Autowired
	private TextLibraryService textLibraryService;
	@Autowired
	private AnalysisProjectService projectService;
	@Autowired
	private WordSegmentService segmentService;
	@Autowired
	private AnalysisSentimeDictionaryMapper dictionaryMapper;
	@Autowired
	private AnalysisSentimeMapper sentimeMapper;
	@Autowired
	private AnalysisSentimeWordMapper sentimeWordMapper;
	
	//加载文本数据
	public boolean process(long projectId){
		try {
			AnalysisProject project = projectService.getAnalysisProject(projectId);
			String sentimeFieldsJson = project.getSentimeFields();
			String columns = projectService.getColumnSql(sentimeFieldsJson, false);
			long libraryId = project.getTextlibraryId();
			long sumNo = textLibraryService.getTextLibraryInfoNo(libraryId);
			Map<String, String> infoMap = new HashMap<String,String>();
			String text = "";

			Long dictionaryId = Long.decode(project.getSentimeWords());
			//情感词汇数组

			List<AnalysisSentimeWord> words = sentimeWordMapper.getDictionaryAllWords(dictionaryId);

			String word = "";

			//建表
			sentimeMapper.createAnalysisSentimeTable(projectId);
			for (int i = 1; i <= sumNo ; i++) {

				infoMap = textLibraryService.getOneInfo(libraryId, columns, i);
				for (Entry<String, String> entry : infoMap.entrySet()) {
					text = text + "\n" +entry.getValue();
				}
				List<Term> terms = segmentService.NlpAnalysis(text);

				int negativeScore=0;
				int positiveScore=0;
				//遍历分词结果集
				for (Term term : terms) {
					word = term.getName();
					//遍历情感词汇数组
					for (AnalysisSentimeWord wordObj : words) {
						if (word.equals(wordObj.getWord())) {
							//情感词汇得分
							if (wordObj.getScore()>0){
								positiveScore = positiveScore + wordObj.getScore();
							}else {
								negativeScore = negativeScore + wordObj.getScore();
							}


							continue;
						}
					}


					word="";
				}

				//持久化保存结果
				
				sentimeMapper.insert(projectId, i, positiveScore, negativeScore, text);
				
				text = "";
				
				
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	
		
		
	}
	
	
	
	public RtInfo getResult(long projectId,long pageNow,long pageSize){
		RtInfo rtInfo = new RtInfo();
		long pageStart = (pageNow-1)*pageSize ;
		long bigTotalItems = sentimeMapper.getSentimeResultNo(projectId);
		List<AnalysisSentime> sentimeResults = sentimeMapper.getSentimeResult(projectId, pageStart, pageSize);
		rtInfo.setRt_mapinfo("sentimeResults", sentimeResults);
		rtInfo.setRt_mapinfo("bigTotalItems", bigTotalItems);
		return rtInfo;
		
	}
	


	
	
	
	
	
	
	
}
