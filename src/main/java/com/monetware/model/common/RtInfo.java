package com.monetware.model.common;

import java.util.HashMap;
import java.util.Map;

/** 
 *@author  venbillyu 
 *@date 创建时间：2016年12月8日 下午4:20:03 
 *@describle 返回信息
 */
public class RtInfo {
	//错误提示码
	private int error_code;
	//错误提示信息
	private String error_msg;
	//返回主信息
	private Object rt_info;
	//返回提示信息
	private String rt_msg;
	//返回map主信息
	private Map<String, Object>rt_mapinfo;
	public String getRt_msg() {
		return rt_msg;
	}
	public void setRt_msg(String rt_msg) {
		this.rt_msg = rt_msg;
	}
	public RtInfo() {
		super();
		Map<String, Object> mapinfo=new HashMap<String, Object>();
		this.setError_code(0);
		this.setError_msg("");
		this.setRt_info("{}");
		this.setRt_msg("");
		this.rt_mapinfo=mapinfo;
		// TODO Auto-generated constructor stub
	}
	public int getError_code() {
		return error_code;
	}
	public void setError_code(int error_code) {
		this.error_code = error_code;
	}
	public String getError_msg() {
		return error_msg;
	}
	public void setError_msg(String error_msg) {
		this.error_msg = error_msg;
	}
	public Object getRt_info() {
		return rt_info;
	}
	public void setRt_info(Object rt_info) {
		this.rt_info = rt_info;
	}
	public Map<String, Object> getRt_mapinfo() {
		return rt_mapinfo;
	}
	public void setRt_mapinfo(String key,Object value) {
		this.rt_mapinfo.put(key, value);
	}
	
}
