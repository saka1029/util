package test.saka1029.util.calendar;

import java.io.IOException;
import java.time.LocalDate;

import org.junit.Test;

import saka1029.util.calendar.CalendarImage;

public class TestCalendarImage {

    @Test
    public void testCalendarImage() throws IOException {
        CalendarImage ci = new CalendarImage();
        ci.draw(LocalDate.of(2026, 7, 1), "calendar.png");
    }

}
