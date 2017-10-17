package com.monetware.service.analyze;

import com.monetware.mapper.analysis.AnalysisSentimeDictionaryMapper;
import com.monetware.mapper.analysis.AnalysisSentimeWordMapper;
import com.monetware.model.analysis.AnalysisSentime;
import com.monetware.model.analysis.AnalysisSentimeDictionary;
import com.monetware.model.analysis.AnalysisSentimeWord;
import com.monetware.model.common.RtInfo;
import com.monetware.util.DataUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 *@author  venbillyu 
 *@date 创建时间：2017年2月20日 上午10:38:54 
 *@describle 情感分析-情感词典增删改查
 */
@Service
public class AnalysisSentimeDictionaryService {
	@Autowired
	private AnalysisSentimeDictionaryMapper dictionaryMapper;
	@Autowired
	private AnalysisSentimeWordMapper wordMapper;

	//查询
	public RtInfo getSentimeWords(long dictionaryId, String name, int userId, long pageNow, long pageSize){
		RtInfo rtInfo = new RtInfo();
		long pageStart = (pageNow-1)*pageSize;
		List<AnalysisSentimeWord> words = wordMapper.getDictionaryWordsByPage(dictionaryId,name,pageStart,pageSize);
		long bigTotalItems = wordMapper.getDictionaryWordsNo(dictionaryId,name);
		rtInfo.setRt_mapinfo("words", words);
		rtInfo.setRt_mapinfo("bigTotalItems", bigTotalItems);
		return rtInfo;
	}


	//根据id查询词汇
	public AnalysisSentimeWord selectWordById(long id){
		return wordMapper.selectByPrimaryKey(id);
	}


	//插入新词汇
	public RtInfo insertWord(long dictionaryId,String word , int score , int userId){
		RtInfo rtInfo = new RtInfo();
		rtInfo.setError_code(1);
		if (StringUtils.isEmpty(word)) {
			rtInfo.setError_msg("输入情感词汇为空，请重新输入");
			return rtInfo;
		}
		if (word.contains(" ")) {
			rtInfo.setError_msg("输入情感词汇不能有空格");
			return rtInfo;
		}

		if (userId<=0) {
			rtInfo.setError_msg("没有识别到合法的用户信息");
			return rtInfo;
		}

		if (wordMapper.selectByName(dictionaryId,word)!=null){
			rtInfo.setError_msg("新建词汇和原有词汇重复");
			return rtInfo;
		}




		rtInfo.setError_code(0);

		AnalysisSentimeWord sentimeWord = new AnalysisSentimeWord();

		sentimeWord.setCreateTime(new Date());
		sentimeWord.setCreateUser(userId);
		sentimeWord.setDictionaryId(dictionaryId);
		sentimeWord.setWord(word);
		sentimeWord.setScore(score);
		wordMapper.insertSelective(sentimeWord);

		rtInfo.setRt_msg("设置情感词汇成功");
		return rtInfo;

	}


	//删除词汇
	public boolean deleteWord(long id){
		return wordMapper.deleteByPrimaryKey(id)>0;
	}


	//更改词汇或者分数
	public RtInfo updateWord(AnalysisSentimeWord sentimeWord){
		RtInfo rtInfo = new RtInfo();
		int score = sentimeWord.getScore();


		int no = wordMapper.updateByPrimaryKeySelective(sentimeWord);
		if (no==0) {
			rtInfo.setError_code(1);
			rtInfo.setRt_msg("情感词汇没有更改成功，请重试");
		}else {
			rtInfo.setRt_msg("情感词汇修改成功");
		}
		return rtInfo;
	}


	//分页查询用户词典和数量
	public RtInfo selectUserDictionaryByPage(String name ,int userId,long pageNow,long pageSize){
		RtInfo rtInfo = new RtInfo();
		
		long pageStart = (pageNow-1)*pageSize;
		List<AnalysisSentimeDictionary> dictionary = dictionaryMapper.getUserDictionaryByPage(name ,userId, pageStart, pageSize);

		long bigTotalItems = dictionaryMapper.getUserDictionaryNo(name,userId);
		long thisMonthItems = dictionaryMapper.getThisMonthItems(name,userId, DataUtil.getThisMonthFirstDay());
		rtInfo.setRt_mapinfo("dictionary", dictionary);
		rtInfo.setRt_mapinfo("bigTotalItems", bigTotalItems);
		rtInfo.setRt_mapinfo("thisMonthItems",thisMonthItems);
		return rtInfo;
	}
	
	//获取所有词典
	public List<AnalysisSentimeDictionary> getUserAllDictionary(int userId){
		return dictionaryMapper.getUserAllDictionary(userId);
	}


	//根据名称获取词典
	public boolean isUsedDictioanryName(String name,int userId){
		return dictionaryMapper.getDictioanryNoByName(name,userId)>0;
	}

	//添加词典名称
	public boolean addDictionary(String name,int userId){

		AnalysisSentimeDictionary dictionary = new AnalysisSentimeDictionary();
		dictionary.setName(name);
		dictionary.setCreateUser(userId);
		dictionary.setLevel(new Integer(0).byteValue());
		dictionary.setWordsNo(0L);
		return dictionaryMapper.insertSelective(dictionary)==1?true:false;
	}

	//修改词典名称
	public boolean updateDictionaryName(long id,String name){
		AnalysisSentimeDictionary dictionary = new AnalysisSentimeDictionary();
		dictionary.setName(name);
		dictionary.setId(id);
		return dictionaryMapper.updateByPrimaryKeySelective(dictionary)>0;
	}


	//删除词典和所有词汇
	public boolean deleteDictionary(long id){
		wordMapper.deleteByDictionaryId(id);
		return dictionaryMapper.deleteByPrimaryKey(id)>0;
	}
	
	//根据主键查询词典
	public AnalysisSentimeDictionary selectDictionaryById(long id){
		return dictionaryMapper.selectByPrimaryKey(id);
	}


	//更改词汇总数
	public boolean updateDictionaryWordsNo(long id){
		long sumNo = wordMapper.getDictionaryWordsNo(id,"");
		AnalysisSentimeDictionary dictionary = new AnalysisSentimeDictionary();
		dictionary.setId(id);
		dictionary.setWordsNo(sumNo);
		return dictionaryMapper.updateByPrimaryKeySelective(dictionary)>0;
	}
}
