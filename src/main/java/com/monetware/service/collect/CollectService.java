package com.monetware.service.collect;


import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.jsoup.helper.StringUtil.isNumeric;

/**
 * @author xuantang
 */
@Component
public class CollectService {
	/**
	 * the type of rule
	 */
	public static int TYPE_CLUES_AJAX_FLIP = 0;
	public static int TYPE_CLUES = 1;
	public static int TYPE_CLUES_AJAX_CLICK = 2;
	/**
	 *
	 */
	private OnCrawleLinstener onCrawleLinstener;


	class CollectProcessor {
		private int collectType = -1;
		private OnCrawleLinstener onCrawleLinstener;
		private String ajaxXpath;
		private List<String> xpaths;
		private String url;
		private String extract_way;


		public List<String> getXpaths() {
			return xpaths;
		}

		public void setXpaths(List<String> xpaths) {
			this.xpaths = xpaths;
		}

		public String getAjaxXpath() {
			return ajaxXpath;
		}

		public void setAjaxXpath(String ajaxXpath) {
			this.ajaxXpath = ajaxXpath;
		}
		public OnCrawleLinstener getOnCrawleLinstener() {
			return onCrawleLinstener;
		}

		public void setOnCrawleLinstener(OnCrawleLinstener onCrawleLinstener) {
			this.onCrawleLinstener = onCrawleLinstener;
		}

		CollectProcessor(String url, int collectType, String extract_way) {
			this.collectType = collectType;
			this.url = url;
			this.extract_way = extract_way;
		}

		/**
		 * process the page, extract urls to fetch, extract the data and store
		 * use htmlunit ad a spider
		 * @param page page
		 */
		public void start() {
			// new webclient and initialize configure
			WebClient webClient = new WebClient(BrowserVersion.CHROME);
			webClient.getOptions().setCssEnabled(false);
			webClient.getOptions().setTimeout(35000);
			webClient.getOptions().setThrowExceptionOnScriptError(false);
			webClient.addRequestHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3192.0 Safari/537.36");
			try {
				// ajax flip and clues
				if(collectType == CollectService.TYPE_CLUES_AJAX_FLIP) {
					//　get page
					webClient.setAjaxController(new NicelyResynchronizingAjaxController());
					webClient.getOptions().setJavaScriptEnabled(true);
					HtmlPage page = webClient.getPage(url);
					webClient.waitForBackgroundJavaScript(10000);

					// note the page for share page
					List<Integer> pages = new ArrayList<>();
					Integer currentPage = 1;
					boolean isEnd = true;
					pages.add(1);
					List<String> results = new ArrayList<>();
					while(true){
						// get absolute xpath
						List<String> mXpaths = produceXpaths(page, xpaths);
						// extract content
						results.addAll(getContent(page, mXpaths, extract_way));
						// next page
						List<HtmlAnchor> htmlListItems = page.getByXPath(ajaxXpath);
						// should be one
						if (htmlListItems.size() == 0) {
							isEnd = true;
						} else if (htmlListItems.size() == 1) {
							htmlListItems.get(0).click();
						}
						// > 1
						else {
							for(HtmlAnchor htmlAnchor: htmlListItems){
								String number = htmlAnchor.asText();
								if(isNumeric(number)){
									Integer pageNumber = Integer.valueOf(number);
									if(pageNumber > currentPage){
										isEnd = false;
										pages.add(pageNumber);
										currentPage = pageNumber;
										break;
									}
								}
							}
						}
						// Crawler is over
						if(isEnd){
							if(results.size() == 0) {
								onCrawleLinstener.onFail("results is null");
								return;
							}
							onCrawleLinstener.onSuccess(results);
							break;
						}else{
							isEnd = true;
						}
					}
				}
				else if(collectType == CollectService.TYPE_CLUES) {
					webClient.getOptions().setJavaScriptEnabled(false);
					HtmlPage page = webClient.getPage(url);

					// the page is ajax page
					if (isAjaxHtml(page, xpaths.get(0))) {
						webClient.setAjaxController(new NicelyResynchronizingAjaxController());
						webClient.getOptions().setJavaScriptEnabled(false);
						page = webClient.getPage(url);
						webClient.waitForBackgroundJavaScript(10000);
					}

					List<String> mXpaths = produceXpaths(page, xpaths);
					if(mXpaths == null || mXpaths.size() == 0) {
						onCrawleLinstener.onFail("xpath is error");
						return;
					}
					// extract content
					List<String> results = getContent(page, mXpaths, extract_way);
					if(results == null || results.size() == 0) {
						onCrawleLinstener.onFail("results is null");
						return;
					}
					onCrawleLinstener.onSuccess(results);
				}
				else if(collectType == CollectService.TYPE_CLUES_AJAX_CLICK) {
					//　get page
					webClient.setAjaxController(new NicelyResynchronizingAjaxController());
					webClient.getOptions().setJavaScriptEnabled(true);
					HtmlPage page = webClient.getPage(url);
					webClient.waitForBackgroundJavaScript(10000);

					List<String> results = new ArrayList<>();
					// click
					List<HtmlAnchor> htmlListItems = page.getByXPath(ajaxXpath);

					for(HtmlAnchor htmlAnchor : htmlListItems){
						htmlAnchor.click();
						// get absolute xpath
						List<String> mXpaths = produceXpaths(page, xpaths);
						// extract content
						results.addAll(getContent(page, mXpaths, extract_way));
					}
					// Crawler is over
					if(results.size() == 0) {
						onCrawleLinstener.onFail("results is null");
						return;
					}
					onCrawleLinstener.onSuccess(results);
				}
			}catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}

		/**
		 *
		 * @param page
		 * @param xpaths
		 * @return
		 */
		List<String> produceXpaths(HtmlPage page, List<String> xpaths) {
			int size = getNumber(page, xpaths);
			List<String> mXpaths = new ArrayList<>();
			String root = getXpathListTag(xpaths);
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
				mXpaths.add(sb.toString());
			}
			return mXpaths;
		}

		/**
		 *
		 * @param page
		 * @param xpaths
		 * @return
		 */
		List<String> getContent(HtmlPage page, List<String> xpaths, String extract_way) {
			List<String> results = new ArrayList<>();
			for (int index = 0; index < xpaths.size(); index++) {
				List<Object> byXPath = page.getByXPath(xpaths.get(index));
				if (byXPath.size() > 0) {
					Object o = byXPath.get(0);
					if ("文本".equals(extract_way)) {
						if( o instanceof HtmlTableDataCell) {
							results.add(((HtmlTableDataCell) o).getTextContent().trim());
						}else if( o instanceof HtmlAnchor) {
							results.add(((HtmlAnchor) o).getTextContent().trim());
						}else if( o instanceof HtmlArticle) {
							results.add(((HtmlArticle) o).getTextContent().trim());
						}else if( o instanceof HtmlLink) {
							results.add(((HtmlLink) o).getTextContent().trim());
						}
					}else if ("链接".equals(extract_way)) {
						results.add(getAbsUrl(((HtmlAnchor) o).getBaseURI(),
								((HtmlAnchor) o).getHrefAttribute()));
					}

				}
			}
			return results;
		}

		/**
		 * 获取数量
		 * @param page
		 * @param xpaths
		 * @return
		 */
		int getNumber(HtmlPage page, List<String> xpaths) {
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
		String getXpathListTag(List<String> xpaths) {
			if(xpaths.size() >= 2) {
				char[] mainXpath = xpaths.get(0).toCharArray();
				char[] secondXpath = xpaths.get(1).toCharArray();
				int minLength = Math.min(mainXpath.length, secondXpath.length);
				int index = 0;
				while(index < minLength && mainXpath[index] == secondXpath[index]){
					index++;
				}
				return xpaths.get(0).substring(0, index - 1);

			}else {
				return null;
			}
		}
	}

	/**
	 *
	 * @param absolutePath
	 * @param relativePath
	 * @return
	 */
	public static String getAbsUrl(String absolutePath, String relativePath){
		try {
			URL absoluteUrl = new URL(absolutePath);
			URL parseUrl = new URL(absoluteUrl ,relativePath );
			return parseUrl.toString();
		}
		catch (MalformedURLException e) {
			return "";
		}
	}
	/**
	 * 测试　只需要两个xpath和一个url
	 * @param args
	 */
	public static void main(String[] args) {
		// share crawler
//		String xpath1 = "//*[@id=\"tableData_\"]/div[2]/table/tbody/tr[2]/td[2]";
//		String xpath2 = "//*[@id=\"tableData_\"]/div[2]/table/tbody/tr[3]/td[2]";
//		String url = "http://www.sse.com.cn/assortment/stock/list/share/";
//		CollectService collectByCluesService = new CollectService();
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
//		String ajaxXpath = "//*[@id=\"idStr\"]";
//		collectByCluesService.crawl(onCrawleLinstener, url, CollectService.TYPE_CLUES_AJAX_FLIP, "文本",
//				ajaxXpath, xpath1, xpath2);
		// tongji crawler
		String xpath1 = "/html/body/div[3]/div/div[3]/div/ul/li[1]/a[1]";
		String xpath2 = "/html/body/div[3]/div/div[3]/div/ul/li[2]/a[1]";
		String url = "http://sse.tongji.edu.cn/data/list/xwdt";
		CollectService collectByCluesService = new CollectService();

		OnCrawleLinstener onCrawleLinstener = new OnCrawleLinstener() {
			@Override
			public void onSuccess(List<String> result) {
				for (String str : result) {
					System.out.println(str);
				}
			}
			@Override
			public void onFail(String error) {
				System.out.println(error);
			}
		};
		String ajaxXpath = "";
		collectByCluesService.crawl(onCrawleLinstener, url, CollectService.TYPE_CLUES, "链接",
				ajaxXpath, xpath1, xpath2);

	}

	/**
	 * 线索 ＋ ajax翻页/点击
	 * @param onCrawleLinstener
	 * @param url
	 * @param ajaxXpath
	 * @param xpath1
	 * @param xpath2
	 */
	public void crawl(OnCrawleLinstener onCrawleLinstener, String url, int type, String extract_way, String ajaxXpath, String xpath1, String xpath2) {
		this.onCrawleLinstener = onCrawleLinstener;
		List<String> clues = new ArrayList<>();
		clues.add(xpath1);
		clues.add(xpath2);
		CollectProcessor clueProcessor = new CollectProcessor(url, type, extract_way);
		clueProcessor.setXpaths(clues);
		clueProcessor.setAjaxXpath(ajaxXpath);
		clueProcessor.setOnCrawleLinstener(this.onCrawleLinstener);
		clueProcessor.start();
	}
	/**
	 * 单
	 * @param onCrawleLinstener
	 * @param url
	 * @param xpath1
	 * @param xpath2
	 */
	public void crawl(OnCrawleLinstener onCrawleLinstener, String url, int type, String extract_way, String ajaxXpath, String xpath) {
		this.onCrawleLinstener = onCrawleLinstener;
		List<String> clues = new ArrayList<>();
		clues.add(xpath);
		CollectProcessor clueProcessor = new CollectProcessor(url, type, extract_way);
		clueProcessor.setXpaths(clues);
		clueProcessor.setAjaxXpath(ajaxXpath);
		clueProcessor.setOnCrawleLinstener(this.onCrawleLinstener);
		clueProcessor.start();
	}

	public OnCrawleLinstener getOnCrawleLinstener() {
		return onCrawleLinstener;
	}

	public void setOnCrawleLinstener(OnCrawleLinstener onCrawleLinstener) {
		this.onCrawleLinstener = onCrawleLinstener;
	}

	public boolean isAjaxHtml(HtmlPage page, String xpath) {
		if (page.getByXPath(xpath).size() < 1) {
			return true;
		}
		return false;
	}

	/**
	 * 使用回调获取结果
	 */
	public interface OnCrawleLinstener {
		void onSuccess(List<String> result);
		void onFail(String error);
	}
}
