package com.monetware.service.collect;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.downloader.AbstractDownloader;
import us.codecraft.webmagic.selector.PlainText;

/**
 * Created by venbill on 2017/3/25.
 */
public class MyHtmlUnitDownloader extends AbstractDownloader{
    private int threadNum;
    @Override
    public Page download(Request request, Task task) {
        //todo  设置cookie
        //todo  从代理池中获取代理
        String url = request.getUrl();

        final WebClient webClient = new WebClient(BrowserVersion.CHROME);

        // 1 启动JS
        webClient.getOptions().setJavaScriptEnabled(true);
        // 2 禁用Css，可避免自动二次请求CSS进行渲染
        webClient.getOptions().setCssEnabled(false);
        // 3 启动客户端重定向
        webClient.getOptions().setRedirectEnabled(true);
        // 4 js运行错误时，是否抛出异常
        webClient.getOptions().setThrowExceptionOnScriptError(false);


        // 5 设置超时
        webClient.getOptions().setTimeout(50000);

        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);

        String content="";

        try {

            HtmlPage page = webClient.getPage(url);
            webClient.waitForBackgroundJavaScript(20000);


            content = page.asXml();


        }catch (Exception e){
            System.out.println(e);
            webClient.close();
        }

        webClient.close();
        Page page = new Page();
        page.setRawText(content);
        page.setUrl(new PlainText(request.getUrl()));
        page.setRequest(request);
        page.setStatusCode(200);
        return page;

    }



    @Override
    public void setThread(int threadNum) {
            this.threadNum = threadNum;
    }


}

