package com.monetware.service.collect;


import com.alibaba.fastjson.JSONObject;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.monetware.mapper.collect.SpiderTaskInfoMapper;
import com.monetware.model.collect.SpiderTaskInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileWriter;
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
	private OnCrawlListener onCrawlListener;

	class CollectProcessor {
		private int collectType = -1;
		private OnCrawlListener onCrawlListener;
		private String ajaxXpath;
		private List<String> xpaths;
		private List<String> urls;
		private String extract_way;

		public void setXpaths(List<String> xpaths) {
			this.xpaths = xpaths;
		}

		public void setAjaxXpath(String ajaxXpath) {
			this.ajaxXpath = ajaxXpath;
		}
		public OnCrawlListener getOnCrawlListener() {
			return onCrawlListener;
		}

		public void setOnCrawlListener(OnCrawlListener onCrawlListener) {
			this.onCrawlListener = onCrawlListener;
		}

		CollectProcessor(List<String> urls, int collectType, String extract_way) {
			this.collectType = collectType;
			this.urls = urls;
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
					HtmlPage page = webClient.getPage(urls.get(0));
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
								onCrawlListener.onFail("results is null");
								return;
							}
							onCrawlListener.onSuccess(results);
							break;
						}else{
							isEnd = true;
						}
					}
				}
				else if(collectType == CollectService.TYPE_CLUES) {
					List<String> results = new ArrayList<>();
					boolean isAjax = false;
					for (String url : urls) {
						webClient.getOptions().setJavaScriptEnabled(false);
						HtmlPage page = webClient.getPage(url);
						// the page is ajax page
						if (!isAjax && isAjaxHtml(page, xpaths.get(0))) {
							webClient.setAjaxController(new NicelyResynchronizingAjaxController());
							webClient.getOptions().setJavaScriptEnabled(false);
							page = webClient.getPage(url);
							webClient.waitForBackgroundJavaScript(10000);
							isAjax = true;
						}
						// extract content
						try {
							List<String> mXpaths = produceXpaths(page, xpaths);
							results.addAll(getContent(page, mXpaths, extract_way));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					onCrawlListener.onSuccess(results);
				}
				else if(collectType == CollectService.TYPE_CLUES_AJAX_CLICK) {
					List<String> results = new ArrayList<>();
					boolean isAjax = false;
					for (String url : urls) {
						//　get page
						webClient.setAjaxController(new NicelyResynchronizingAjaxController());
						webClient.getOptions().setJavaScriptEnabled(true);
						HtmlPage page = webClient.getPage(url);
						webClient.waitForBackgroundJavaScript(10000);
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
							onCrawlListener.onFail("results is null");
							return;
						}
					}
					onCrawlListener.onSuccess(results);
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
						}else if( o instanceof HtmlSpan) {
							results.add(((HtmlSpan) o).getTextContent().trim());
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
		 * @param xpaths the extracted path
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

        /**
         * Check if the page is using ajax
         * @param page the page of html
         * @param xpath the xpath of page
         * @return
         */
        boolean isAjaxHtml(HtmlPage page, String xpath) {
            if (page.getByXPath(xpath).size() < 1) {
                return true;
            }
            return false;
        }
        /**
         *
         * @param basePath the base url
         * @param relativePath the relative url
         * @return the absolute url
         */
        String getAbsUrl(String basePath, String relativePath){
            try {
                URL baseUrl = new URL(basePath);
                URL parseUrl = new URL(baseUrl ,relativePath);
                return parseUrl.toString();
            }
            catch (MalformedURLException e) {
                return "";
            }
        }

    }


    /**
     * Generate Urls
     * @param jsonObject the json data
     * @return
     */

    public List<String> generateUrls(JSONObject jsonObject) {
        String template = jsonObject.get("url_wildcard").toString();
        int start = Integer.valueOf(jsonObject.get("init_value").toString());
        int interval = Integer.valueOf(jsonObject.get("gap").toString());
        int num = Integer.valueOf(jsonObject.get("pages_num").toString());

        List<String> urls = new ArrayList<>();
        // Generate the urls
        for (int i = 0; i < num; i++) {
            urls.add(template.replaceAll("\\{[^}]*\\}",
                    String.valueOf(start + i * interval)));
        }
        return urls;
    }

    /**
     *
     * @param filename
     * @param content
     * @param append
     */
    public void saveToFile(String filename, String content, boolean append) {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(
                    new FileWriter(filename, append));
            bufferedWriter.write(content + "\n");
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	/**
	 * 线索 ＋ ajax翻页/点击
	 * @param onCrawlListener
	 * @param urls
	 * @param ajaxXpath
	 * @param xpath1
	 * @param xpath2
	 */
	public void crawl(OnCrawlListener onCrawlListener, List<String> urls, int type, String extract_way, String ajaxXpath, String xpath1, String xpath2) {
		this.onCrawlListener = onCrawlListener;
		List<String> clues = new ArrayList<>();
		clues.add(xpath1);
		clues.add(xpath2);
		CollectProcessor clueProcessor = new CollectProcessor(urls, type, extract_way);
		clueProcessor.setXpaths(clues);
		clueProcessor.setAjaxXpath(ajaxXpath);
		clueProcessor.setOnCrawlListener(this.onCrawlListener);
		clueProcessor.start();
	}

	/**
	 *
	 * @param onCrawlListener
	 * @param urls
	 * @param type
	 * @param extract_way
	 * @param ajaxXpath
	 * @param xpath
	 */
	public void crawl(OnCrawlListener onCrawlListener, List<String> urls, int type, String extract_way, String ajaxXpath, String xpath) {
		this.onCrawlListener = onCrawlListener;
		List<String> clues = new ArrayList<>();
		clues.add(xpath);
		CollectProcessor clueProcessor = new CollectProcessor(urls, type, extract_way);
		clueProcessor.setXpaths(clues);
		clueProcessor.setAjaxXpath(ajaxXpath);
		clueProcessor.setOnCrawlListener(this.onCrawlListener);
		clueProcessor.start();
	}

    /**
     *
     * @param onCrawlListener
     */
	public void setOnCrawlListener(OnCrawlListener onCrawlListener) {
		this.onCrawlListener = onCrawlListener;
	}

	/**
	 * 使用回调获取结果
	 */
	public interface OnCrawlListener {
		void onSuccess(List<String> result);
		void onFail(String error);
	}
}
