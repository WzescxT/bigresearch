package com.monetware.service.collect;

import com.google.gson.Gson;
import com.monetware.mapper.collect.ExchangeUrlMapper;
import com.monetware.model.collect.CollectBaiduNewsConfig;
import com.monetware.util.httpHelper;
import org.springframework.stereotype.Service;

import java.util.Date;

/** 
 *@author  venbillyu 
 *@date 创建时间：2016年12月10日 上午11:50:12 
 *@describle 
 */
@Service
public class BaiduNewsUrlService implements ExchangeUrlMapper{

	@Override
	public String getUrl(String configBeanJson) {
		CollectBaiduNewsConfig configBean=new Gson().fromJson(configBeanJson, CollectBaiduNewsConfig.class);
		String mustKeyword=configBean.getMustKeyword();
		String anyoneWord=configBean.getAnyoneWord();
		String bt=configBean.getBt();
		String et=configBean.getEt();
		String tn=configBean.getTn();
		String word="";
		//拼接关键字
		if (tn.equals("newstitledy")) {
			word="intitle";
			if (anyoneWord.equals("")) {
				word=word+":("+mustKeyword+")";
			}else {
				word=word+":("+mustKeyword+"(";
				String [] anyoneWords=anyoneWord.split("\\s+");
				for (String str : anyoneWords) {
					word=word+str+" | ";
				}
				word=word.substring(0, word.length()-3)+"))";
				word=httpHelper.URLEncode(word, "utf-8");
			}
		}else {
			word=mustKeyword;
		}
		//拼接日期
		Date date=new Date(1969, 12, 30, 8, 0);
		Date bDate=new Date(Integer.parseInt(bt.split("-")[0]),Integer.parseInt(bt.split("-")[1]),Integer.parseInt(bt.split("-")[2]));
		Date eDate=new Date(Integer.parseInt(et.split("-")[0]), Integer.parseInt(et.split("-")[1]), Integer.parseInt(et.split("-")[2])+1);
		bt=Long.toString((bDate.getTime()-date.getTime())/1000);
		et=Long.toString((eDate.getTime()-date.getTime())/1000-1);
		String seedUrl="http://news.baidu.com/ns?word=&pn=0&cl=2&ct=0&tn=&ie=utf-8&bt=&et=";
		seedUrl=seedUrl.replace("?word=", "?word="+word);
		seedUrl=seedUrl.replace("&tn=", "&tn="+tn);
		seedUrl=seedUrl.replace("&bt=","&bt="+bt );
		seedUrl=seedUrl.replace("&et=","&et="+et );
		return seedUrl;
	}

	@Override
	public String getConfigBean(String url) {
		// TODO Auto-generated method stub
		return null;
	}

	

}
