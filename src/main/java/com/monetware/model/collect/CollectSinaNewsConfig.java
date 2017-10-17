package com.monetware.model.collect;
/**
 * @describle 新浪新闻采集配置
 * @author venbill
 *
 */
public class CollectSinaNewsConfig {
		private String name;
		private String describle;
		//搜索内容
		private String keyword;
		//频道
		private String channel;
		//采集数量
		private String no;
		//时间范围(2016 2015 2014 m w d h /custom)
		private String time;
		//初始时间(2016-11-01)
		private String stime;
		//结束时间(2016-11-25)
		private String etime;
		//范围(title/all)
		private String range;
		//新闻源
		private String source;
		//包含媒体类型(/3_5_6/2)
		private String mideatype;
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getDescrible() {
			return describle;
		}
		public void setDescrible(String describle) {
			this.describle = describle;
		}
		public String getKeyword() {
			return keyword;
		}
		public void setKeyword(String keyword) {
			this.keyword = keyword;
		}
		public String getChannel() {
			return channel;
		}
		public void setChannel(String channel) {
			this.channel = channel;
		}
		public String getNo() {
			return no;
		}
		public void setNo(String no) {
			this.no = no;
		}
		public String getTime() {
			return time;
		}
		public void setTime(String time) {
			this.time = time;
		}
		public String getStime() {
			return stime;
		}
		public void setStime(String stime) {
			this.stime = stime;
		}
		public String getEtime() {
			return etime;
		}
		public void setEtime(String etime) {
			this.etime = etime;
		}
		public String getRange() {
			return range;
		}
		public void setRange(String range) {
			this.range = range;
		}
		
		public String getSource() {
			return source;
		}
		public void setSource(String source) {
			this.source = source;
		}
		public String getMideatype() {
			return mideatype;
		}
		public void setMideatype(String mideatype) {
			this.mideatype = mideatype;
		}
		@Override
		public String toString() {
			return "CollectSinaNewsConfig [name=" + name + ", describle="
					+ describle + ", keyword=" + keyword + ", channel="
					+ channel + ", no=" + no + ", time=" + time + ", stime="
					+ stime + ", etime=" + etime + ", range=" + range
					+ ", source=" + source + ", mideatype=" + mideatype + "]";
		}
		
		
		
	}
