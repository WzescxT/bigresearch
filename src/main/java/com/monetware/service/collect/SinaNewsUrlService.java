package com.monetware.service.collect;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.constraints.Null;

import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.monetware.mapper.collect.ExchangeUrlMapper;
import com.monetware.model.collect.CollectSinaNewsConfig;
import com.monetware.util.httpHelper;

/**
 * 
 * @author venbill
 * @describle 新浪新闻url转换
 */
@Service
public class SinaNewsUrlService implements ExchangeUrlMapper{
	//配置信息生成url
	public String getUrl(String configBeanJson){
		CollectSinaNewsConfig configBean=new Gson().fromJson(configBeanJson, CollectSinaNewsConfig.class);
		String url = "";
		String SinaNewsSeedUrl="http://search.sina.com.cn/?c=news&q=&range=&time=&stime=&etime=&num=10&col=&source=&from=&country=&size=&a=&t=&page=1";
		String keyword = configBean.getKeyword();
		String range = configBean.getRange();
		String time = configBean.getTime();
		String stime = configBean.getStime();
		String etime = configBean.getEtime();
		String channel = configBean.getChannel();
		String source = configBean.getSource();
		String mediatype = configBean.getMideatype();
		if (source==null||source.equals("")) {
			keyword = httpHelper.URLEncode(keyword,"gbk");
		}else {
			keyword = httpHelper.URLEncode(keyword+" O:"+source,"gbk");
		}
		url = SinaNewsSeedUrl.replace("&q=", "&q="+keyword);
		url = url.replace("&range=", "&range="+range);
		url = url.replace("&time=", "&time="+time);
		if (time.equals("custom")) {
			url = url.replace("&stime=", "&stime="+stime);
			url = url.replace("&etime=", "&etime="+etime);
		}
		if (!channel.equals("")) {
			url = url.replace("&col=", "&col="+stime);
		}
		if (!mediatype.equals("")) {
			url = url.replace("&t=", "&t="+mediatype);
		}
		return url;
	}
	
	//url获取配置信息
	public String getConfigBean(String url){
		String keyword="";
		String range = "";
		String time = "";
		String stime = "";
		String etime = "";
		String channel = "";
		String source = "";
		String mediatype = "";
		
		keyword = httpHelper.URLDecode(httpHelper.regexStr(url, "&q=","&"),"gbk");
		if (keyword.contains(" O:")) {
			String spStr[] =  keyword.split(" O:");
			if (spStr.length>0) {
				keyword = spStr[0];
				source = spStr[1];
			}
		}
		range = httpHelper.regexStr(url, "&range=", "&");
		time = httpHelper.regexStr(url, "&time=", "&");
		stime = httpHelper.regexStr(url, "&stime=", "&");
		etime = httpHelper.regexStr(url, "&etime=", "&");
		channel = httpHelper.regexStr(url, "&col=", "&");
		mediatype = httpHelper.regexStr(url, "&t=", "&");
		CollectSinaNewsConfig configBean = new CollectSinaNewsConfig();
		configBean.setKeyword(keyword);
		configBean.setRange(range);
		configBean.setTime(time);
		configBean.setEtime(etime);
		configBean.setStime(stime);
		configBean.setChannel(channel);
		configBean.setSource(source);
		configBean.setMideatype(mediatype);
		return new Gson().toJson(configBean);
	}

	
}
