package com.monetware.mapper.collect;

import com.monetware.model.collect.CollectSinaNewsConfig;
import com.monetware.util.httpHelper;

/** 
 *@author  venbillyu 
 *@date 创建时间：2016年11月28日 下午2:53:02 
 *@describle 
 */
public interface ExchangeUrlMapper {
	//根据配置类生成url
	String getUrl(String configBeanJson);
	
	//url获取配置信息
	String getConfigBean(String url);
}