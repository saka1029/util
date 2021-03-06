package saka1029.util.main;

import java.io.IOException;
import java.net.MalformedURLException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Jav {

    static final int DEFAULT_MAX = 20;

    static void usage() {
        System.err.println("usage: java saka1029.util.main.Jav [-m max] KEYWORD...");
        System.exit(1);
    }

    public static void main(String[] args) throws MalformedURLException, IOException {
        int max = DEFAULT_MAX;
        int len = args.length;
        int i = 0;
        for ( ; i < len; ++i)
            if (args[i].equals("-m"))
                if (++i < len)
                    max = Integer.parseInt(args[i]);
                else
                    usage();
            else
                break;
        if (i >= len)
            usage();
        for ( ; i < len; ++i)
            get(args[i], max);
    }

    public void test() throws MalformedURLException, IOException {
        get("山井すず");
        get("百葉花音");
        get("春咲りょう");
//        get("海藤みずほ");
//        get("岡本真憂");
//        get("佐々木れい");
//        get("乙都さきの");
//        get("朝陽そら");
//        get("大橋優子");
//        get("今浪そな");
//        get("今村加奈子");
//        get("陽向さえか");
        get("碧しの");
        get("夏目彩春");
        get("藍芽みずき");
        get("石原莉奈");
//        get("平手まな");
        get("藍澤りく");
//        get("山口葉瑠");
        get("東條なつ");
        get("葵つかさ");
        get("辻本杏");
//        get("湊莉久");
        get("高千穂すず");
//        get("翼");
        get("竹田ゆめ");
        get("市来まひろ");
        get("輝月あんり");
        get("お貸しします");
        get("破壊版");
//        get("NHDTB-533");
//        get("無修正");
//        get("ナンパ連れ込みSEX隠し撮り");
//        get("終電逃した女性を駅前でナンパ");
    }

    static void get(String name) throws MalformedURLException, IOException {
        get(name, DEFAULT_MAX);
    }

    static void get(String name, int max) throws MalformedURLException, IOException {
        System.out.println("#### " + name);
        String base = "https://sukebei.nyaa.si/?f=0&c=0_0&q=";
        // int max = 20;
        query(base + name, max);
        System.out.println();
    }

    static void query(String url, int max) throws IOException {
        Document doc = Jsoup.connect(url)
            .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
//            .referrer("http://www.google.com")
            .execute()
            .parse();
        Elements table = doc.select("table.torrent-list tr");
        System.out.println("サイズ,日付,シード数,URL,タイトル");
        int i = 0;
        for (Element e : table) {
            if (e.select("td:eq(1)").text().equals("")) continue;
            if (i++ >= max) break;
            System.out.printf("%s,%s,%s,%s,%s%n",
                e.select("td:eq(3)").text(),
                e.select("td:eq(4)").text(),
                e.select("td:eq(5)").text(),
                e.select("td:eq(2) a:eq(0)").attr("abs:href"), // abs:は絶対URLの取得
                e.select("td:eq(1) > a").text().replaceAll("^\\W+|\\]", ""));
        }
    }
}
