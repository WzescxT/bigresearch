package com.monetware.service.analyze;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ansj.app.keyword.KeyWordComputer;
import org.ansj.app.keyword.Keyword;
import org.ansj.domain.Term;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.monetware.mapper.analysis.AnalysisProjectWordsMapper;
import com.monetware.model.analysis.AnalysisProjectWord;

/** 
 *@author  venbillyu 
 *@date 创建时间：2016年12月30日 上午11:47:56 
 *@describle 
 */
@Service
public class AnalysisProjectWordService {
	@Autowired
	AnalysisProjectWordsMapper wordsMapper;
	public List<AnalysisProjectWord> getWords(List<Term> terms){
		List<AnalysisProjectWord> words=new ArrayList<AnalysisProjectWord>();
		for (Term term : terms) {
			AnalysisProjectWord word=new AnalysisProjectWord();
			word.setWord(term.getName());
			word.setNature(term.getNatureStr());
			words.add(word);
		}
		return words;
	}
	
	public void deleteWords(long analysisProjectId){
		wordsMapper.deleteWords(analysisProjectId);
		
		
	}

	public List<AnalysisProjectWord> getWordsFrequency(long analysisProjectId,String natures,
			long pageNow, long pageSize) {
		Map<String, Object> queryMap=new HashMap<String, Object>();
		queryMap.put("natures", natures);
	    queryMap.put("analysisProjectId",analysisProjectId);
	    queryMap.put("pageStart",(pageNow-1)*pageSize);
		queryMap.put("pageSize", pageSize);
		return wordsMapper.getWordsFrequency(queryMap);
	}

	public long getWordsFrequencyNo(long analysisProjectId,String natures) {
		Map<String, Object> queryMap=new HashMap<String, Object>();
		queryMap.put("natures", natures);
	    queryMap.put("analysisProjectId",analysisProjectId);
	    return wordsMapper.getWordsFrequencyNo(queryMap);
	}
	
	public String getNatures(Map<String , Boolean> natureMap){
		String natures="";
		String key="";
		Boolean value=false;
		String append="";
		 for (Map.Entry<String, Boolean> entry : natureMap.entrySet()) {
			 key=entry.getKey();
			 value=entry.getValue();
			if (value.equals(true)) {
				if (key.equals("n")) {
					append="'n','nr','nr1','nr2','nrj','nrf','ns','nsf','nt','nz','nl','ng','nw',";
				}else if(key.equals("t")){
					append="'t','tg',";
				}else if(key.equals("v")){
					append="'v','vd','vn','vshi','vyou','vf','vx','vi','vl','vg',";
				}else if(key.equals("a")){
					append="'a','ad','an','ag','al',";
				}else if(key.equals("b")){
					append="'b','bl',";
				}else if(key.equals("r")){
					append="'r','rr','rz','rzt','rzs','rzv','ry','ryt','rys','ryv','rg',";
				}else if(key.equals("m")){
					append="'m','mq',";
				}else if(key.equals("q")){
					append="'q','qv','qt',";
				}else if(key.equals("p")){
					append="'p','pba','pbei',";
				}else if(key.equals("u")){
					append="'u','uzhe','ule','uguo','ude1','ude2','ude3','usuo','udeng','uyy','udh','uls','uzhi','ulian',";
				}else if(key.equals("x")){
					append="'x','xx','xu',";
				}else if(key.equals("w")){
					append="'w','wkz','wky','wyz','wyy','wj','ww','wt','wd','wf','wn','wm','ws','wp','wb','wh',";
				}else {
					append="'"+key+"',";
					
				}
				natures=natures+append;
			} 
		 }
		 if (natures.length()<=1) {
			 //没有选择词性
			natures="";
		}else {
			
			natures=natures.substring(0,natures.length()-1);
		}
		return natures;
	}
	
	
	
	public List<Keyword> getKeyWords(String content){
		KeyWordComputer kwc = new KeyWordComputer(10);
		kwc.computeArticleTfidf(content);
	    List<Keyword> result = kwc.computeArticleTfidf( content);
	            return result;
	}
	
	
	
	
	
	
	
	
}
