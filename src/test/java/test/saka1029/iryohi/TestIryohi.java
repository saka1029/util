package test.saka1029.iryohi;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.junit.Test;
import saka1029.util.Common;

public class TestIryohi {

    static final Logger logger = Common.logger(TestIryohi.class);

    record Iryohi(LocalDate 受診日, String 医療機関, String 診療科, int 金額) {
        public Iryohi(int y, int m, int d, String 医療機関, String 診療科, int 金額) {
            this(LocalDate.of(y, m, d), 医療機関, 診療科, 金額);
        }
        static final String FORMAT = "%s %s %s %s%n";
        static String pad(Object obj, int width) {
            String s = obj.toString();
            int size = s.chars().map(c -> c < 256 ? 1 : 2).sum();
            return s + (size >= width ? "" : " ".repeat(width - size));
        }
        static String padr(Object obj, int width) {
            String s = obj.toString();
            int size = s.chars().map(c -> c < 256 ? 1 : 2).sum();
            return (size >= width ? "" : " ".repeat(width - size)) + s;
        }
        static String f(Object... o) {
            return pad(o[0], 12) + pad(o[1], 10) + pad(o[2], 8) + padr(o[3], 8);
        }
        public static void print(List<Iryohi> list) {
            int total = 0;
            System.out.println(f("受診日", "医療機関", "診療科", "金額"));
            for (Iryohi i : list) {
                System.out.println(f(i.受診日, i.医療機関, i.診療科, i.金額));
                total += i.金額;
            }
            System.out.println(f("合計", "", "", total));
        }
    }

    @Test
    public void testIryohi() {
        List<Iryohi> list = new ArrayList<>();
        list.add(new Iryohi(2023, 11, 10, "上毛病院", "精神科", 1500));
        list.add(new Iryohi(2023, 11, 10, "石川内科", "内科", 30));
        Iryohi.print(list);
    }

}
