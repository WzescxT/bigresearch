package com.monetware.service.collect;


import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.jsoup.helper.StringUtil.isNumeric;

@Component
public class CollectByFlipService {
	private static List<Integer> pages = new ArrayList<Integer>();
	private static Integer currentPage = 1;
	private static boolean isEnd = true;

	private static final String TAG_A = "a";
	private static final String TAG_LI = "li";


	class ClueProcessor implements PageProcessor {

		OnCrawleLinstener onCrawleLinstener;
		String ajaxXpath;
		ClueProcessor(OnCrawleLinstener onCrawleLinstener, String ajaxXpath) {
			this.onCrawleLinstener = onCrawleLinstener;
			this.ajaxXpath = ajaxXpath;
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
		public void process(Page rawPage) {
			String url = rawPage.getUrl().toString();
			WebClient webClient = new WebClient(BrowserVersion.CHROME);
			webClient.getOptions().setJavaScriptEnabled(true);
			webClient.getOptions().setCssEnabled(false);
			webClient.setAjaxController(new NicelyResynchronizingAjaxController());
			webClient.getOptions().setTimeout(35000);
			webClient.getOptions().setThrowExceptionOnScriptError(false);

			webClient.addRequestHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3192.0 Safari/537.36");
			try {
				HtmlPage page = webClient.getPage(url);
				webClient.waitForBackgroundJavaScript(10000);
				pages.add(1);
				List<String> results = new ArrayList<>();
				while(true){
					// extract content
					// rawPage.setRawText(page.asXml());
					// rawPage.setHtml(new Html(page.asXml()));
					// String value = rawPage.getHtml().xpath(xpath).toString();
					// System.out.println(xml);
					// System.out.println(rawPage.getHtml());
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

					// produce all xpath
					for(int index = 1; index <= size; index++) {
						StringBuilder sb = new StringBuilder();
						sb.append(getXpathListTag(xpaths))
								.append("[")
								.append(index)
								.append(tmp.substring(indexTmp + root.length()));
						mXpaths[index-1] = sb.toString();
					}
					// extract content
					for(int index = 0; index < size; index++) {
						List<Object> byXPath = page.getByXPath(mXpaths[index]);
						if(byXPath.size() > 0) {
							Object o = byXPath.get(0);
							if( o instanceof HtmlTableDataCell) {
								results.add(((HtmlTableDataCell) o).getTextContent());
							}else if( o instanceof HtmlAnchor) {
								results.add(((HtmlAnchor) o).getTextContent());
							}else if( o instanceof HtmlArticle) {
								results.add(((HtmlArticle) o).getTextContent());
							}else if( o instanceof HtmlLink) {
								results.add(((HtmlLink) o).getTextContent());
							}
						}
					}
					// next page
					List<HtmlAnchor> htmlListItems = page.getByXPath(ajaxXpath);
					for(HtmlAnchor htmlAnchor: htmlListItems){
						String number = htmlAnchor.asText();
						if(isNumeric(number)){
							Integer pageNumber = Integer.valueOf(number);
							if(pageNumber > currentPage){
								isEnd = false;
								pages.add(pageNumber);
								currentPage = pageNumber;
								htmlAnchor.click();
								break;
							}
						}
					}
					// Crawler is over
					if(isEnd){
						for(String s : results) {
							System.out.println(s);
						}
						onCrawleLinstener.onSuccess(results);
						break;
					}else{
						isEnd = true;
					}
				}

			}catch (IOException e) {
				System.out.println(e.getMessage());
			}
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


	/**
	 *
	 * @param xpath
	 * @return
	 */
	private String getTagType(String xpath) {
		String tmp = xpath.substring(xpath.lastIndexOf('/') + 1);
		if(tmp.indexOf('[') != -1) {
			tmp = tmp.substring(0, tmp.indexOf('['));
		}
		return tmp;
	}

	/**
	 *
	 * @param page
	 * @param xpaths
	 * @return
	 */
	private int getNumber(HtmlPage page, List<String> xpaths) {
		String xpathAll = getXpathListTag(xpaths);
		if(xpathAll != null) {
			return page.getByXPath(xpathAll).size();
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
//		String xpath1 = "//*[@id=\"tableData_\"]/div[2]/table/tbody/tr[2]/td[2]";
//		String xpath2 = "//*[@id=\"tableData_\"]/div[2]/table/tbody/tr[3]/td[2]";
//		String url = "http://www.sse.com.cn/assortment/stock/list/share/";
//		CollectByFlipService collectByCluesService = new CollectByFlipService();
//
//		OnCrawlListener onCrawlListener = new OnCrawlListener() {
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
//		String ajaxXpath = "//*[@id=\"idStr\"]";
//		collectByCluesService.crawl(onCrawlListener, url, ajaxXpath, xpath1, xpath2);
//
//	}

	public void crawl(OnCrawleLinstener onCrawleLinstener, String url, String ajaxXpath, String... xpath) {
		List<String> clues = new ArrayList<>();
		clues.addAll(Arrays.asList(xpath));
		ClueProcessor clueProcessor = new ClueProcessor(onCrawleLinstener, ajaxXpath);
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
