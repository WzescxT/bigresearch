import com.monetware.service.collect.CollectService;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Create by xuantang
 * @date on 1/6/18
 */
public class SpiderTest {

    @Test
    public void SpiderClueTest() {
        // share crawler
        String xpath1 = "/html/body/div[3]/div[1]/div[2]/div[1]/div[1]/div[1]/div[1]/a[1]/span[1]";
        String xpath2 = "/html/body/div[3]/div[1]/div[2]/div[1]/div[1]/div[2]/div[1]/a[1]/span[1]";
        String url = "http://daily.zhihu.com/";
        CollectService collectByCluesService = new CollectService();

        CollectService.OnCrawlListener onCrawlListener = new CollectService.OnCrawlListener() {
            @Override
            public void onSuccess(List<String> result) {
                for (String string : result) {
                    System.out.println(string);
                }
            }

            @Override
            public void onFail(String error) {

            }
        };
        List<String> urls = new ArrayList<>();
        urls.add(url);
        String ajaxXpath = "//*[@id=\"idStr\"]";
        collectByCluesService.crawl(onCrawlListener, urls, CollectService.TYPE_CLUES, "文本",
                ajaxXpath, xpath1, xpath2);
    }

    @Test
    public void SpiderClueFlipTest() {
        // share crawler
        String xpath1 = "//*[@id=\"tableData_\"]/div[2]/table[1]/tbody[1]/tr[3]/td[2]";
        String xpath2 = "//*[@id=\"tableData_\"]/div[2]/table[1]/tbody[1]/tr[5]/td[2]";
        String url = "http://www.sse.com.cn/assortment/stock/list/share/";
        CollectService collectByCluesService = new CollectService();

        CollectService.OnCrawlListener onCrawlListener = new CollectService.OnCrawlListener() {
            @Override
            public void onSuccess(List<String> result) {
                for (String string : result) {
                    System.out.println(string);
                }
            }

            @Override
            public void onFail(String error) {

            }
        };
        List<String> urls = new ArrayList<>();
        urls.add(url);
        String ajaxXpath = "//*[@id=\"idStr\"]";
        collectByCluesService.crawl(onCrawlListener, urls, CollectService.TYPE_CLUES_AJAX_FLIP, "文本",
                ajaxXpath, xpath1, xpath2);
    }
}
