package com.monetware.service.analyze;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.ansj.app.keyword.KeyWordComputer;
import org.ansj.app.keyword.Keyword;
import org.ansj.domain.Term;
import org.nlpcn.commons.lang.standardization.SentencesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;


/** 
 *@author  venbillyu 
 *@date 创建时间：2017年2月22日 下午2:59:39 
 *@describle 在线分析
 */
@Service
public class AnalysisOnlineService {
	@Autowired
	private WordSegmentService wordSegmentService;
	

	/**
	 * 自动生成文本摘要
	 * 
	 * 文本摘要问题
	 * 1.从原文截选：不够简练精确
	 * 2.根据关键词重造语句：很难使语句流畅
	 * 
	 * 筛选方法：
	 * 1.获取文本一定比重的关键词
	 * 2.将文本分隔成句子
	 * 3.根据关键词计算出句子得分，选出得分高的几个句子
	 * 4.根据文本句子顺序重组文本，生成摘要
	 * 
	 * @param
	 */
	
	
	
	public List<Map<String, Object>> getAbstract(String content){
		//保留关键词数量	
		int keyWordSize=20;
		//保留句子数量
		int sumNo=4;
		//文本摘要
		 String abstractStr="";
		SentencesUtil su = new SentencesUtil() ;
		List<String> sentenceList = su.toSentenceList(content) ;  
		//关键词
		 KeyWordComputer kwc = new KeyWordComputer(keyWordSize);
		    Collection<Keyword> keywords = kwc.computeArticleTfidf("", content);
		        
		if (sentenceList.size()<=sumNo) {
			//句子长度不够，返回原句
			abstractStr = content;
		}else {
			
			
			
			    Map<Integer, Double> resMap = new HashMap<Integer ,Double>();
			    double[] scores = new double[sentenceList.size()];
			    double score=0;
			    int i=0;
			    for (String sentence : sentenceList) {
			    	//获取句子得分
			        score = getSentenceScore(sentence,content.length(), keywords);
					
			        scores[i]=score;
					resMap.put(i, score);
					i+=1;
					}
			           
		         //排序
		         scores=sortArr(scores);
		         //去除得分高的前几个句子
		         
		         double [] resScore = new double[sumNo];
		         for (int j = 0; j < sumNo; j++) {
					resScore[j] = scores[j];
				}
		         //根据文本顺序生成摘要
		        
		        
		         for (int j = 0; j < sentenceList.size(); j++) {
					String sentence = sentenceList.get(j);
					score = resMap.get(j);
					for (int k = 0; k < resScore.length; k++) {
						if (score==resScore[k]) {
							if (abstractStr.indexOf(sentence)<0) {
								abstractStr = abstractStr+sentence;
								break;
								
							}
						}
					}
					
					
				}
		         
			
		}
			
	   
         
         
         
         List<Term> terms = wordSegmentService.NlpAnalysis(abstractStr);
         Map<String, Object> wordMap;
         List<Map<String, Object>> wordList = new ArrayList<>();
         
         double[] pointWords = new double[keywords.size()];
         int k=0;
         for (Keyword keyword : keywords) {
 			pointWords[k]=keyword.getScore();
 			k=k+1;
 		}
         
         pointWords = sortArr(pointWords);
         
         
         
         
       
         
         for (Term term : terms) {
        	 wordMap = new HashMap<String,Object>();
        	 wordMap.put("name",term.getName());
        	 wordMap.put("isKeyWord", false);
        	 for (Keyword keyword : keywords) {
     			if (keyword.getName().equals(term.getName())&&keyword.getScore()>pointWords[4]) {
					wordMap.put("isKeyWord", true);
					break;
				}
     		}
        	 
        	 wordList.add(wordMap);
        	 
		}
         
         
         
         
         
        return wordList;
         
		
	}
	
	
	
	
	//获取单个句子的分数
	public double getSentenceScore(String sentence,int contentLength ,Collection<Keyword> keywords){
		List<Term> terms = wordSegmentService.NlpAnalysis(sentence);
		double score=0;
		for (Term term : terms) {
			for (Keyword keyword : keywords) {
				if (term.getName().equals(keyword.getName())) {
//					score=score+keyword.getScore()*(Math.log(contentLength)/(Math.log(sentence.length())));
					score=score+keyword.getScore()*(1/(Math.log(sentence.length())));
				}
			}
		}
		
		return score;
	}
	
	//冒泡排序算法
	public double[] sortArr(double[]numbers){
		double temp; // 记录临时中间值   
	    int size = numbers.length; // 数组大小   
	    for (int i = 0; i < size - 1; i++) {   
	        for (int j = i + 1; j < size; j++) {   
	            if (numbers[i] < numbers[j]) { // 交换两数的位置   
	                temp = numbers[i];   
	                numbers[i] = numbers[j];   
	                numbers[j] = temp;   
	            }   
	        }   
	    } 
	    return numbers;
	}
	
	//转换文本繁简体（文本内容，是否切换成繁体）
	public String convertZH(String content,boolean toTraditional) throws FileNotFoundException{
		StringBuffer res=new StringBuffer();
		char[] contentArr=content.toCharArray();
		for (char c : contentArr) {
			res.append(convertChar(c, toTraditional));
		}
		return res.toString();
	}
	
	
	//切换单个汉字
	public String convertChar(char c,boolean toTraditional ) throws FileNotFoundException{
		String s = String.valueOf(c);
		String r= "";
		
/*
		//读取资源文件
		String filePath = "E:\\ZH.txt";
		  try {
			  InputStreamReader read = new InputStreamReader(ResourceLoader.getResource("classpath:dataFile/ZH.txt").getInputStream());
//			  InputStreamReader read = new InputStreamReader(this.getClass().getResourceAsStream("dataFile/ZH.txt"));


			  BufferedReader bufferedReader = new BufferedReader(read);
			  String lineTxt = null;
			  while((lineTxt = bufferedReader.readLine()) != null){
				  if (lineTxt.indexOf(s)>=0) {
					  r = lineTxt;
					  break;
				  }
			  }
			  read.close();
*/






//读取资源文件
		String filePath = "D:/bigresearch/ZH.txt";

//			  File file = ResourceUtils.getFile("classpath:dataFile/ZH.txt");
//			  File file = new ResourceLoader().getResource("classpath:dataFile/ZH.txt").getInputStream();
              String encoding="utf-8";
              File file=new File(filePath);


		try {


              if(file.isFile() && file.exists()){ //判断文件是否存在
                  InputStreamReader read = new InputStreamReader(
                  new FileInputStream(file),encoding);//考虑到编码格式


                  BufferedReader bufferedReader = new BufferedReader(read);
                  String lineTxt = null;
                  while((lineTxt = bufferedReader.readLine()) != null){
                      if (lineTxt.indexOf(s)>=0) {
						r = lineTxt;
						break;
					}
                  }
                  read.close();
      }else{
          System.out.println("找不到指定的文件");
      }
      } catch (Exception e) {
          System.out.println("读取文件内容出错");
          e.printStackTrace();
      }
		
		  if (r.equals("")) {
			return s;
		}
		
		String [] arr=r.split("　　");
		for (String str : arr) {
			if (str.indexOf(s)!=-1) {
				r = str;
				return toTraditional ? r.split("=")[1]:r.split("=")[0] ;
			}
		}
		return r;
	}
	
	
	
	//拼音切换  依赖于pinyin4j.jar
	public String getPinyin(String content){
		 String pinyinName = "";  
	        char[] nameChar = content.toCharArray();  
	        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();  
//	        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);  
	        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);  
//	        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);  
	        defaultFormat.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);
	        defaultFormat.setToneType(HanyuPinyinToneType.WITH_TONE_MARK);  
	        for (int i = 0; i < nameChar.length; i++) {  
	            String s = String.valueOf(nameChar[i]);  
	            if (s.matches("[\\u4e00-\\u9fa5]")) {  
	                try {  
	                    String[] mPinyinArray = PinyinHelper.toHanyuPinyinStringArray(nameChar[i], defaultFormat);  
	                    pinyinName += " "+mPinyinArray[0];  
	                } catch (BadHanyuPinyinOutputFormatCombination e) {  
	                    e.printStackTrace();  
	                }  
	            } else {  
	                pinyinName += " "+nameChar[i];  
	            }  
	        }  
	        return pinyinName;  
	}
	
	
	
	/*
	public static void main(String[] args) {
		AnalysisOnlineService service=new AnalysisOnlineService();
		String pinyin = service.getPinyin("苏志燮");
		System.out.println(pinyin);
	}
	
	*/
	/*
	
	public static void main(String[] args) {
		String s = "韓";
		boolean toTraditional = false;
		String r= "";
		
		//读取资源文件
		
		
		String filePath = "E:\\ZH.txt";
		  try {
              String encoding="utf-8";
              File file=new File(filePath);
              if(file.isFile() && file.exists()){ //判断文件是否存在
                  InputStreamReader read = new InputStreamReader(
                  new FileInputStream(file),encoding);//考虑到编码格式
                  BufferedReader bufferedReader = new BufferedReader(read);
                  String lineTxt = null;
                  while((lineTxt = bufferedReader.readLine()) != null){
                      if (lineTxt.indexOf(s)>=0) {
						r = lineTxt;
						break;
					}
                  }
                  read.close();
      }else{
          System.out.println("找不到指定的文件");
      }
      } catch (Exception e) {
          System.out.println("读取文件内容出错");
          e.printStackTrace();
      }
		
		  if (r.equals("")) {
			System.out.println("");
		}
		
		String [] arr=r.split("　　");
		for (String str : arr) {
			if (str.indexOf(s)!=-1) {
				System.out.println(str);
				r = str;
				System.out.println(toTraditional ? r.split("=")[1]:r.split("=")[0]); ;
			}
		}
		
		
		
		
		
		
		
		
		
		
		
	}
	
	*/
/*	public static void main(String[] args) {
		AnalysisOnlineService service = new AnalysisOnlineService();
		String res = service.convertZH("中国", true);
		System.out.println(res);
	}
	
	
	*/
	/*public static void main(String[] args) {
		String [] strArrStrings = "锅=鍋　　蝈=蟈　　国=囯　　国=國　　帼=幗　　掴=摑　　果=菓　　椁=槨　　".split("　　");
		for (String string : strArrStrings) {
			System.out.println(string);
		}
	}*/
	
	
}
