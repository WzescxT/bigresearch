package com.monetware.model.collect;

import java.sql.Timestamp;
import java.util.Date;

public class CollectProject {
	private long id;
	private String name;
	//采集库->搜索库 导入状态
	private int importSearchStatus;
	private int createUser;
	private String group;
	private String describle;
	private String searchUrl;
	private Timestamp createTime;
	
	private String status;
	//采集时长(s)
	private long collectTime;
	private long collectNo;
	private long templateId;
	private String dataSource;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getImportSearchStatus() {
		return importSearchStatus;
	}
	public void setImportSearchStatus(int importSearchStatus) {
		this.importSearchStatus = importSearchStatus;
	}
	public int getCreateUser() {
		return createUser;
	}
	public void setCreateUser(int createUser) {
		this.createUser = createUser;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public String getDescrible() {
		return describle;
	}
	public void setDescrible(String describle) {
		this.describle = describle;
	}
	public String getSearchUrl() {
		return searchUrl;
	}
	public void setSearchUrl(String searchUrl) {
		this.searchUrl = searchUrl;
	}
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public long getCollectTime() {
		return collectTime;
	}
	public void setCollectTime(long collectTime) {
		this.collectTime = collectTime;
	}
	public long getCollectNo() {
		return collectNo;
	}
	public void setCollectNo(long collectNo) {
		this.collectNo = collectNo;
	}
	public long getTemplateId() {
		return templateId;
	}
	public void setTemplateId(long templateId) {
		this.templateId = templateId;
	}
	public String getDataSource() {
		return dataSource;
	}
	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}
	@Override
	public String toString() {
		return "CollectProject [id=" + id + ", name=" + name + ", importSearchStatus=" + importSearchStatus
				+ ", createUser=" + createUser + ", group=" + group + ", describle=" + describle + ", searchUrl="
				+ searchUrl + ", createTime=" + createTime + ", status=" + status + ", collectTime=" + collectTime
				+ ", collectNo=" + collectNo + ", templateId=" + templateId + ", dataSource=" + dataSource + "]";
	}
	
	
	
	
	
	
	
	
	
	
}
/*private int id;
private String name;
private String group;
describle
search_keyword
create_time
create_user
status
collect_no
template_id*/