package saka1029.util.main;

import java.io.IOException;
import java.time.LocalDate;

import saka1029.util.calendar.CalendarImage;

public class Calendar {

    static void usage() {
        System.err.printf("java %s [-n 月数] YYYYMM %n", Calendar.class.getName());
        System.err.printf("    YYYYMM  : 作成するカレンダーの年月を指定する。%n");
        System.err.printf("    -n 月数 : 複数月分作成するとき指定する。%n");
        throw new IllegalArgumentException();
    }

    public static void main(String[] args) throws IOException {
        int n = 1;
        String yyyymm = null;
        for (int i = 0, size = args.length; i < size; ++i)
            switch (args[i]) {
                case "-n":
                    if (++i < size)
                        n = Integer.parseInt(args[i]);
                    else
                        usage();
                    break;
                default:
                    yyyymm = args[i];
                    break;
            }
        if (yyyymm == null || yyyymm.length() != 6)
            usage();
        LocalDate month = LocalDate.of(
            Integer.parseInt(yyyymm.substring(0, 4)),
            Integer.parseInt(yyyymm.substring(4, 6)),
            1);
        new CalendarImage().draw(month, n, "calendar-%04d-%02d.png");
    }

}