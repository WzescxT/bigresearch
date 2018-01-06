package com.monetware.service.collect;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author Create by xuantang
 * @date on 11/12/17
 */
@Component
public class DownloadPageService {

    class DownloadProcessor implements PageProcessor {

        OnCrawlListener onCrawlListener;

        DownloadProcessor(OnCrawlListener onCrawlListener) {
            this.onCrawlListener = onCrawlListener;
        }

        private Site site = Site.me()
                .setTimeOut(500000000)
                .setRetryTimes(50)
                .setCycleRetryTimes(10)
                .setSleepTime(1000)
                .setUserAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3192.0 Safari/537.36");

        @Override
        public void process(Page page) {
            onCrawlListener.onSuccess(page.getHtml().toString());
        }

        @Override
        public Site getSite() {
            return site;
        }
    }

    /**
     * Download page
     * @param onCrawlListener
     * @param url
     */
    public void crawl(OnCrawlListener onCrawlListener, String url) {
        try {
            Document doc = Jsoup.connect(url).get();
            System.out.println(doc.outerHtml());
            String xml = doc.outerHtml();
            onCrawlListener.onSuccess(replaceAll(url, xml));
        } catch (Exception e) {
            try {
                WebClient webClient = new WebClient(BrowserVersion.CHROME);
                webClient.getOptions().setJavaScriptEnabled(true);
                webClient.getOptions().setCssEnabled(false);
                webClient.getOptions().setTimeout(35000);
                webClient.setAjaxController(new NicelyResynchronizingAjaxController());
                webClient.addRequestHeader("User-Agent",
                        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3192.0 Safari/537.36");
                HtmlPage page = webClient.getPage(url);
                webClient.waitForBackgroundJavaScript(10000);
                String xml = page.asXml();
                onCrawlListener.onSuccess(replaceAll(url, xml));
            } catch (IOException e1) {
                onCrawlListener.onFail("DownloadFail");
                e1.printStackTrace();
            }
        }
    }
    /**
     * 使用回调获取结果
     */
    public interface OnCrawlListener {
        void onSuccess(String result);
        void onFail(String error);
    }

    /**
     * Replace src and script
     * @param urlPath
     * @param xml
     * @return
     */
    public static String replaceAll(String urlPath, String xml) {

        String url_path = urlPath;
        String root_path = null;
        try {
            URL url = new URL(url_path);
            root_path = url.getProtocol() + "://" + url.getHost();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        String regEx1="href=\"/";
        Pattern pattern1 = Pattern.compile(regEx1);
        Matcher matcher1 = pattern1.matcher(xml);
        String r1 = matcher1.replaceAll("href=\"" + root_path + "/");
        String regEx2="src=\"/";
        Pattern pattern2 = Pattern.compile(regEx2);
        Matcher matcher2 = pattern2.matcher(r1);
        String r2 = matcher2.replaceAll("src=\"" + root_path + "/");
        return r2;
    }
}
