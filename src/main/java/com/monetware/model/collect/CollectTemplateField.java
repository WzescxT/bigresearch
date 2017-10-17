package com.monetware.model.collect;
/** 
 *@author  venbillyu 
 *@date 创建时间：2016年12月1日 下午3:21:46 
 *@describle 
 */
public class CollectTemplateField {
	private String name;
	private String contentXpath;
	private boolean require;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContentXpath() {
		return contentXpath;
	}

	public void setContentXpath(String contentXpath) {
		this.contentXpath = contentXpath;
	}

	public boolean isRequire() {
		return require;
	}

	public void setRequire(boolean require) {
		this.require = require;
	}
}
