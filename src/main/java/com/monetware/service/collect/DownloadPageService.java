package com.monetware.service.collect;

import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;


/**
 * @author Create by xuantang
 * @date on 11/12/17
 */
@Component
public class DownloadPageService {

    class DownloadProcessor implements PageProcessor {

        OnCrawleLinstener onCrawleLinstener;

        DownloadProcessor(OnCrawleLinstener onCrawleLinstener) {
            this.onCrawleLinstener = onCrawleLinstener;
        }

        private Site site = Site.me()
                .setTimeOut(500000000)
                .setRetryTimes(50)
                .setCycleRetryTimes(10)
                .setSleepTime(1000)
                .setUserAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3192.0 Safari/537.36");

        @Override
        public void process(Page page) {
            onCrawleLinstener.onSuccess(page.getHtml().toString());
        }

        @Override
        public Site getSite() {
            return site;
        }
    }

    public void crawl(OnCrawleLinstener onCrawleLinstener, String url) {

        DownloadProcessor downloadProcessor = new DownloadProcessor(onCrawleLinstener);

        Spider.create(downloadProcessor)
                .addUrl(url)
                .run();
    }
    /**
     * 使用回调获取结果
     */
    public interface OnCrawleLinstener {
        void onSuccess(String result);
        void onFail(String error);
    }
}
