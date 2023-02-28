package saka1029.util.passmark;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class TestPassMark {

    /*
     * <li id="pk3876"> <span class="more_details"
     * onclick="pa(event, '3,010', 1855, 35, 2, 2, 'NA', null, null);"></span> <a
     * href="cpu.php?cpu=AMD+Athlon+Silver+3050e&amp;id=3876"> <span
     * class="prdname">AMD Athlon Silver 3050e</span> <div> <span class="index red"
     * style="width: 0%">(0%)</span> </div> <span class="count">NA</span><span
     * class="mark-neww">3,010</span> <span class="price-neww">NA</span></a> </li>
     */
    public static void main(String[] args) throws IOException {
        String[] urls = {"https://www.cpubenchmark.net/high_end_cpus.html",
            "https://www.cpubenchmark.net/mid_range_cpus.html",
            "https://www.cpubenchmark.net/midlow_range_cpus.html",
            "https://www.cpubenchmark.net/low_end_cpus.html",};
        String[] ranges = {"high", "mid", "midlow", "low" };
        try (PrintWriter w = new PrintWriter(new FileWriter("data/passmaprk.csv"))) {
            w.printf("%s,%s,%s,%s,%s%n", "range", "url", "name", "passmark", "price");
            Set<String> names = new HashSet<>();
            for (int i = 0; i < urls.length; i++) {
                Document doc = Jsoup.parse(new URL(urls[i]), 5000);
                Elements list = doc.select("ul.chartlist > li");
                for (Element e : list) {
                    String name = e.select("span.prdname").text().replace(",", " ");
                    if (names.contains(name))
                        continue;
                    names.add(name);
                    String href = e.select("a").attr("abs:href");
                    String count = e.select("span.count").text().replace(",", "");
                    String mark = e.select("span.mark-neww").text().replace(",", "");
                    if (!mark.isEmpty())
                        count = mark;
                    String price = e.select("span.price-neww").text().replace(",", "");
//                    if (count.contains("."))
//                        System.out.println("###" + e);
                    w.printf("%s,%s,%s,%s,%s%n", ranges[i], href, name, count, price);
                }
            }
        }
    }

}
