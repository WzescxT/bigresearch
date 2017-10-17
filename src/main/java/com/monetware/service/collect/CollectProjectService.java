package com.monetware.service.collect;

import com.monetware.mapper.collect.CollectProjectMapper;
import com.monetware.model.collect.CollectProject;
import com.monetware.model.common.RtInfo;
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
 *@date 创建时间：2016年11月25日 下午5:31:52 
 *@describle 采集项目service
 */
@Service
public class CollectProjectService{
	 @Autowired
	 private CollectProjectMapper collectProjectMapper;

	

	public RtInfo createCollectProject(CollectProject collectProject) {
		
		RtInfo rtInfo=new RtInfo();
		//判断用户项目名有没有重复
		boolean isExit=false;
		List<CollectProject> collectProjects=getCollectProjects(collectProject.getCreateUser());
		for (CollectProject exitProject : collectProjects) {
			if (exitProject.getName().equals(collectProject.getName())) {
				rtInfo.setError_code(1);
				rtInfo.setError_msg("项目名已经存在");
				return rtInfo;
			}
		}
		Timestamp nowTime = new Timestamp(new Date().getTime());
		collectProject.setCreateTime(nowTime);
		collectProject.setStatus("准备采集");
		collectProject.setCollectNo(0);
		collectProjectMapper.createCollectProject(collectProject);
		rtInfo.setRt_msg("创建成功");
		return rtInfo;
	}

	public void updateCollectProject(CollectProject collectProject) {
		collectProjectMapper.updateCollectProject(collectProject);
	}

	public void deleteCollectProject(long projectId) {
		collectProjectMapper.deleteCollectProject(projectId);
		
	}

	public CollectProject getCollectProject(long projectId) {
		return collectProjectMapper.getCollectProject(projectId);
	}

	public CollectProject getCollectProjectByName(CollectProject collectProject) {
		
		return collectProjectMapper.getCollectProjectByName(collectProject);
	}

	public List<CollectProject> getCollectProjects(int userId) {
		return collectProjectMapper.getCollectProjects(userId);
	}
	
	public List<CollectProject> getCollectProjects1(int userId) {
		return collectProjectMapper.getCollectProjects1(userId);
	}
	
	
	public List<CollectProject> getPartCollectProjects(HashMap<String, Object> queryMap) {
		return collectProjectMapper.getPartCollectProjects(queryMap);
	}
	
	
	
	public long getCollectProjectsNo(int userId,String name) {
		return collectProjectMapper.getCollectProjectsNo(userId,name);
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
     	return collectProjectMapper.getMonthProjectsNo(userId,name,firstDay);
	}




	public List<CollectProject> getSearchProjects(int userId){
		return collectProjectMapper.getSearchProjects(userId);
	}
	 
}
