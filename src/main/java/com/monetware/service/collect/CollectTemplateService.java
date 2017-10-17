package com.monetware.service.collect;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.monetware.mapper.collect.CollectTemplateMapper;
import com.monetware.model.collect.CollectTemplate;
import com.monetware.model.collect.CollectTemplateField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/** 
 *@author  venbillyu 
 *@date 创建时间：2016年12月1日 下午3:52:18 
 *@describle 采集模板
 */
@Service
public class CollectTemplateService{
	private Gson gson=new Gson();
	@Autowired
	private CollectTemplateMapper collectTemplateMapper;
	public void createCollectTemplate(CollectTemplate collectTemplate) {
		//转换json，持久化数据
		collectTemplate.setCreateTime(new Timestamp(new Date().getTime()));
		String entryUrlsStr = gson.toJson(collectTemplate.getEntryUrls());
		String fieldsStr = gson.toJson(collectTemplate.getFields());
		collectTemplate.setEntryUrlsStr(entryUrlsStr);
		collectTemplate.setFieldsStr(fieldsStr);
		System.out.println("entryurls===>"+collectTemplate.getEntryUrlsStr());
		collectTemplateMapper.createCollectTemplate(collectTemplate);
		
	}

	public CollectTemplate getCollectTemplate(CollectTemplate collectTemplate) {
		CollectTemplate ct = collectTemplateMapper.getCollectTemplate(collectTemplate);
		List<String> entryurl = gson.fromJson(ct.getEntryUrlsStr(),new TypeToken<List<String>>() {
        }.getType());
		List<CollectTemplateField> fields = gson.fromJson(ct.getFieldsStr(), new TypeToken<List<CollectTemplateField>>() {  
        }.getType());
		ct.setEntryUrls(entryurl);
		ct.setFields(fields);
		return ct;
	}

	public CollectTemplate getCollectTemplateById(long id) {
		CollectTemplate ct = collectTemplateMapper.getCollectTemplateById(id);
		List<String> entryurl = gson.fromJson(ct.getEntryUrlsStr(),
				new TypeToken<List<String>>() {
				}.getType());
		List<CollectTemplateField> fields = gson.fromJson(ct.getFieldsStr(),
				new TypeToken<List<CollectTemplateField>>() {
				}.getType());
		ct.setEntryUrls(entryurl);
		ct.setFields(fields);
		return ct;
	}

	public List<CollectTemplate> getUserTemplates(HashMap<String, Object> queryMap) {
		return collectTemplateMapper.getUserTemplates(queryMap);
	}
	public long getUserTemplatesNo(int userId,String name) {
		
		return collectTemplateMapper.getUserTemplatesNo(userId,name);
	}

	public void deleteUserTemplate(long templateId,int userId) {
		
		collectTemplateMapper.deleteUserTemplate(templateId,userId);
		
	}

	public List<CollectTemplate> getUserAllTemplates(int uerId) {
		return collectTemplateMapper.getUserAllTemplates(uerId);
	}

	public long getMonthTemplatesNo(int userId,String name) {
		   //获取前月的第一天
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd"); 
        Calendar   cal_1=Calendar.getInstance();//获取当前日期 
        cal_1.add(Calendar.MONTH, -1);
        cal_1.set(Calendar.DAY_OF_MONTH,1);//设置为1号,当前日期既为本月第一天 
        Timestamp firstDay = new Timestamp(cal_1.getTime().getTime());
        //获取下个月得第一天
        Calendar cale = Calendar.getInstance();   
        cale.set(Calendar.DAY_OF_MONTH,0);//设置为1号,当前日期既为本月第一天 
        
        
        
        return collectTemplateMapper.getMonthTemplatesNo(firstDay,userId,name);
	}

	
	
	
	/*public static void main(String[] args) {
		  SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd"); 
	        
	        //获取前月的第一天
	        Calendar   cal_1=Calendar.getInstance();//获取当前日期 
	        cal_1.add(Calendar.MONTH, -1);
	        cal_1.set(Calendar.DAY_OF_MONTH,1);//设置为1号,当前日期既为本月第一天 
	        String firstDay = format.format(cal_1.getTime());
	        System.out.println("-----1------firstDay:"+firstDay);
	        //获取下个月得第一天
	        Calendar cale = Calendar.getInstance();   
	        cale.set(Calendar.DAY_OF_MONTH,1);//设置为1号,当前日期既为本月第一天 
	        String lastDay = format.format(cale.getTime());
	        
	        
	        
	        System.out.println("-----2------lastDay:"+lastDay);
	}
	*/
	
	

}
