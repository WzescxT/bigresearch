package com.monetware.service.collect;


import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class CollectByCluesService {

	private static final String TAG_A = "a";
	private static final String TAG_LI = "li";
	class ClueProcessor implements PageProcessor {

		OnCrawleLinstener onCrawleLinstener;

		ClueProcessor(OnCrawleLinstener onCrawleLinstener) {
			this.onCrawleLinstener = onCrawleLinstener;
		}

		private Site site = Site.me()
				.setTimeOut(500000000)
				.setRetryTimes(50)
				.setCycleRetryTimes(10)
				.setSleepTime(1000)
				.setUserAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3192.0 Safari/537.36");

		public List<String> getXpaths() {
			return xpaths;
		}

		public void setXpaths(List<String> xpaths) {
			this.xpaths = xpaths;
		}

		private List<String> xpaths;

		/**
		 * process the page, extract urls to fetch, extract the data and store
		 *
		 * @param page page
		 */
		@Override
		public void process(Page page){
			int size = getNumber(page, xpaths);
			if(size == 0) {
				onCrawleLinstener.onFail("do not find xpath");
			}
			String[] mXpaths = new String[size];

			String root = getXpathListTag(xpaths);
			if(root == null) {
				onCrawleLinstener.onFail("do not find root xpath");
			}
			String tmp = xpaths.get(0);
			assert root != null;
			int indexTmp = tmp.substring(root.length()).indexOf(']');

			for(int index = 1; index <= size; index++) {
				StringBuilder sb = new StringBuilder();
				sb.append(getXpathListTag(xpaths))
						.append("[")
						.append(index)
						.append(tmp.substring(indexTmp + root.length()));
				mXpaths[index-1] = sb.toString();
				//System.out.println(mXpaths[index-1]);
			}
			// Test
			String tagType = getTagType(tmp);
			List<String> results = new ArrayList<>();
			if(TAG_A.equals(tagType)) {
				for(int index = 0; index < size; index++) {
					results.add(page.getHtml().xpath(mXpaths[index] + "/text()").toString().trim());
				}
			}else {
				for(int index = 0; index < size; index++) {
					Selectable xpath = page.getHtml().xpath(mXpaths[index]);
					if(xpath.$("a") != null) {
						results.add(xpath.xpath("a/text()").toString().trim());
					}else if(xpath.$("p") != null) {
						results.add(xpath.xpath("p/text()").toString().trim());
					}
				}
			}
			onCrawleLinstener.onSuccess(results);
		}

		/**
		 * get the site settings
		 *
		 * @return site
		 * @see Site
		 */
		@Override
		public Site getSite() {
			return site;
		}

	}


	private String getTagType(String xpath) {
		String tmp = xpath.substring(xpath.lastIndexOf('/') + 1);
		if(tmp.indexOf('[') != -1) {
			tmp = tmp.substring(0, tmp.indexOf('['));
		}
		return tmp;
	}

	private int getNumber(Page page, List<String> xpaths) {
		String xpathAll = getXpathListTag(xpaths);
		if(xpathAll != null) {
			return page.getHtml().xpath(xpathAll).all().size();
		}else {
			return 0;
		}
	}


	/**
	 * get the root tag according to xpaths
	 * @param xpaths
	 * @return the tag
	 */
	private String getXpathListTag(List<String> xpaths) {
		if(xpaths.size() >= 2) {
			char[] mainXpath = xpaths.get(0).toCharArray();
			char[] secondXpath = xpaths.get(1).toCharArray();
			int minLength = Math.min(mainXpath.length, secondXpath.length);
			int index = 0;
			while(index < minLength && mainXpath[index] == secondXpath[index]){
				index++;
			}
			return xpaths.get(0).substring(0, index-1);

		}else {
			return null;
		}
	}

	// 测试　只需要两个xpath和一个url
//	public static void main(String[] args) {
//		String xpath1 = "/html/body/div[3]/div/div[3]/div/ul/li[1]";
//		String xpath2 = "/html/body/div[3]/div/div[3]/div/ul/li[2]";
//		String url = "http://sse.tongji.edu.cn/data/list/xyxw";
//		CollectByCluesService collectByCluesService = new CollectByCluesService();
//
//		OnCrawleLinstener onCrawleLinstener = new OnCrawleLinstener() {
//			@Override
//			public void onSuccess(List<String> result) {
//				System.out.println(result.size());
//			}
//
//			@Override
//			public void onFail(String error) {
//
//			}
//		};
//		collectByCluesService.crawl(onCrawleLinstener, url, xpath1, xpath2);
//
//	}

	public void crawl(OnCrawleLinstener onCrawleLinstener, String url, String... xpath) {
		List<String> clues = new ArrayList<>();
		clues.addAll(Arrays.asList(xpath));

		ClueProcessor clueProcessor = new ClueProcessor(onCrawleLinstener);
		clueProcessor.setXpaths(clues);

		Spider.create(clueProcessor)
				.addUrl(url)
				.run();
	}

	/**
	 * 使用回调获取结果
	 */
	public interface OnCrawleLinstener {
		void onSuccess(List<String> result);
		void onFail(String error);
	}
}
