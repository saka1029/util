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
