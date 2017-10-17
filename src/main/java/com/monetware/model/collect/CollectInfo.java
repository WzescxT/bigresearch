package com.monetware.model.collect;

import java.sql.Timestamp;

/** 
 *@author  venbillyu 
 *@date 创建时间：2016年11月29日 上午9:27:08 
 *@describle 采集controller
 */
public class CollectInfo {
	private long id;
	private long projectId;
	private long collectNo;
	private String url;
	private String title;
	private String Abstract;
	private String keywords;
	private String author;
	private String publishTime;
	private String source;
	private String heads;
	private String content;
	private String channel;
	private String province;
	private String city;
	private int polarity;
	private int strength;
	private int commentnum;
	private int clicknum;
	private int likenum;
	private int dislikenum;
	private int downloadnum;
	private Timestamp createTime;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	public long getProjectId() {
		return projectId;
	}
	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}
	
	public long getCollectNo() {
		return collectNo;
	}
	public void setCollectNo(long collectNo) {
		this.collectNo = collectNo;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAbstract() {
		return Abstract;
	}
	public void setAbstract(String abstract1) {
		Abstract = abstract1;
	}
	public String getKeywords() {
		return keywords;
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	
	public String getPublishTime() {
		return publishTime;
	}
	public void setPublishTime(String publishTime) {
		this.publishTime = publishTime;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getHeads() {
		return heads;
	}
	public void setHeads(String heads) {
		this.heads = heads;
	}
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public int getPolarity() {
		return polarity;
	}
	public void setPolarity(int polarity) {
		this.polarity = polarity;
	}
	public int getStrength() {
		return strength;
	}
	public void setStrength(int strength) {
		this.strength = strength;
	}
	
	public int getCommentnum() {
		return commentnum;
	}
	public void setCommentnum(int commentnum) {
		this.commentnum = commentnum;
	}
	public int getClicknum() {
		return clicknum;
	}
	
	public int getLikenum() {
		return likenum;
	}
	public void setLikenum(int likenum) {
		this.likenum = likenum;
	}
	public void setClicknum(int clicknum) {
		this.clicknum = clicknum;
	}
	public int getDislikenum() {
		return dislikenum;
	}
	public void setDislikenum(int dislikenum) {
		this.dislikenum = dislikenum;
	}
	public int getDownloadnum() {
		return downloadnum;
	}
	public void setDownloadnum(int downloadnum) {
		this.downloadnum = downloadnum;
	}
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	@Override
	public String toString() {
		return "CollectInfo [id=" + id + ", projectId=" + projectId
				+ ", collectNo=" + collectNo + ", url=" + url + ", title="
				+ title + ", Abstract=" + Abstract + ", keywords=" + keywords
				+ ", author=" + author + ", publishTime=" + publishTime
				+ ", source=" + source + ", heads=" + heads + ", content="
				+ content + ", channel=" + channel + ", province=" + province
				+ ", city=" + city + ", polarity=" + polarity + ", strength="
				+ strength + ", commentnum=" + commentnum + ", clicknum="
				+ clicknum + ", likenum=" + likenum + ", dislikenum="
				+ dislikenum + ", downloadnum=" + downloadnum + ", createTime="
				+ createTime + "]";
	}
	
	
}
