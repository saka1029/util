package test.saka1029.util.calendar;

import java.io.IOException;
import java.time.LocalDate;

import org.junit.Test;

import saka1029.util.calendar.CalendarImage;

public class TestCalendarImage {

    static String 祝日CSV = "https://www8.cao.go.jp/chosei/shukujitsu/syukujitsu.csv";

    @Test
    public void testCalendarImage() throws IOException {
        CalendarImage ci = new CalendarImage();
        ci.draw(LocalDate.of(2026, 7, 1), "calendar.png");
    }

}
