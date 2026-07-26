package test.saka1029.util.gomi;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.junit.Test;

import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.immutable.ImmutableCalScale;
import net.fortuna.ical4j.model.property.immutable.ImmutableVersion;
import net.fortuna.ical4j.util.Calendars;

public class TestGomi {

    static final String CALENDAR = "data/kiryu_gomi_calendar-2026-handmade.ics";

    @Test
    public void testLoad() throws IOException, ParserException {
        Calendar cal = Calendars.load(CALENDAR);
        System.out.println(cal);
        // System.out.println(cal.getComponents());
    }

    @Test
    public void testCreate() {
        Calendar calendar = new Calendar();
        calendar.add(new ProdId("Data::ICal 0.21"));
        calendar.add(ImmutableVersion.VERSION_2_0);
        calendar.add(ImmutableCalScale.GREGORIAN);
        VEvent moeru = new VEvent();
        calendar.add(moeru);
        System.out.println(calendar);
    }

    static final String TEMPLATE = """
        BEGIN:VCALENDAR
        VERSION:2.0
        PRODID:Data::ICal 0.21
        METHOD:PUBLISH
        X-WR-CALNAME:kiryu_gomi_calendar
        X-WR-TIMEZONE:Asia/Tokyo
        CALSCALE:GREGORIAN
        PREFERRED_LANGUAGE:JA
        BEGIN:VEVENT
        CONFIRM:OPAQUE
        DESCRIPTION:桐生市ごみカレンダー
        DTSTART;VALUE=DATE:${FIRST:4-1TU,4-1FR}
        LOCATION:ごみカレンダー（7区・11区・17区）
        SUMMARY:燃えるごみ
        UID:30803188-8FEA-11EC-B107-A29F2D131DD7
        RRULE:FREQ=MONTHLY;BYDAY=1TU,1FR,2TU,2FR,3TU,3FR,4TU,4FR,5TU,5FR;UNTIL=${NEXT_YEAR}0331;WKST=SU
        EXDATE:${NEXT_YEAR}0101
        SEQUENCE:0
        END:VEVENT
        BEGIN:VEVENT
        CONFIRM:OPAQUE
        DESCRIPTION:桐生市ごみカレンダー
        DTSTART;VALUE=DATE:${FIRST:4-4MO}
        LOCATION:ごみカレンダー（7区・11区・17区）
        SUMMARY:燃えないごみ
        UID:30806A22-8FEA-11EC-B107-A29F2D131DD7
        RRULE:FREQ=MONTHLY;BYDAY=4MO;UNTIL=${NEXT_YEAR}0331;WKST=SU
        SEQUENCE:0
        END:VEVENT
        BEGIN:VEVENT
        CONFIRM:OPAQUE
        DESCRIPTION:桐生市ごみカレンダー
        DTSTART;VALUE=DATE:${FIRST:4-3TH}
        LOCATION:ごみカレンダー（7区・11区・17区）
        SUMMARY:ペットボトル類・白トレイ
        UID:3080A7E4-8FEA-11EC-B107-A29F2D131DD7
        RRULE:FREQ=MONTHLY;BYDAY=3TH;UNTIL=${NEXT_YEAR}0331;WKST=SU
        SEQUENCE:0
        END:VEVENT
        BEGIN:VEVENT
        CONFIRM:OPAQUE
        DESCRIPTION:桐生市ごみカレンダー
        DTSTART;VALUE=DATE:${FIRST:5-1TH}
        LOCATION:ごみカレンダー（7区・11区・17区）
        SUMMARY:ペットボトル類・白トレイ追加
        UID:3080A7E5-8FEA-11EC-B107-A29F2D131DD7
        RRULE:FREQ=MONTHLY;BYDAY=1TH;UNTIL=${THIS_YEAR}0930;WKST=SU
        SEQUENCE:0
        END:VEVENT
        BEGIN:VEVENT
        CONFIRM:OPAQUE
        DESCRIPTION:桐生市ごみカレンダー
        DTSTART;VALUE=DATE:${FIRST:4-2TH}
        LOCATION:ごみカレンダー（7区・11区・17区）
        SUMMARY:紙類
        UID:3080D520-8FEA-11EC-B107-A29F2D131DD7
        RRULE:FREQ=MONTHLY;BYDAY=2TH,4TH;UNTIL=${NEXT_YEAR}0331;WKST=SU
        SEQUENCE:0
        END:VEVENT
        BEGIN:VEVENT
        CONFIRM:OPAQUE
        DESCRIPTION:桐生市ごみカレンダー
        DTSTART;VALUE=DATE:${FIRST:4-3MO}
        LOCATION:ごみカレンダー（7区・11区・17区）
        SUMMARY:蛍光管・スプレー類
        UID:30819A8C-8FEA-11EC-B107-A29F2D131DD7
        RRULE:FREQ=MONTHLY;BYDAY=3MO;UNTIL=${NEXT_YEAR}0331;WKST=SU
        SEQUENCE:0
        END:VEVENT
        BEGIN:VEVENT
        CONFIRM:OPAQUE
        DESCRIPTION:桐生市ごみカレンダー
        DTSTART;VALUE=DATE:${FIRST:4-2MO}
        LOCATION:ごみカレンダー（7区・11区・17区）
        SUMMARY:缶
        UID:3081CF0C-8FEA-11EC-B107-A29F2D131DD7
        RRULE:FREQ=MONTHLY;BYDAY=2MO;UNTIL=${NEXT_YEAR}0331;WKST=SU
        SEQUENCE:0
        END:VEVENT
        BEGIN:VEVENT
        CONFIRM:OPAQUE
        DESCRIPTION:桐生市ごみカレンダー
        DTSTART;VALUE=DATE:${FIRST:4-2WE}
        LOCATION:ごみカレンダー（7区・11区・17区）
        SUMMARY:びん類
        UID:308200DA-8FEA-11EC-B107-A29F2D131DD7
        RRULE:FREQ=MONTHLY;BYDAY=2WE;UNTIL=${NEXT_YEAR}0331;WKST=SU
        SEQUENCE:PAQUE0
        END:VEVENT
        END:VCALENDAR
    """;

    static Pattern VARIABLE = Pattern.compile("\\$\\{([A-Z0-9_:,-]+)\\}");
    static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    static LocalDate first(int thisYear, String monthNthWeek) {
        String[] fields = monthNthWeek.split("-");
        int month = Integer.parseInt(fields[0]);
        String nthWeek = fields[1];
        int n = Integer.parseInt(nthWeek.substring(0, 1));
        DayOfWeek week = switch (nthWeek.substring(1, 3)) {
            case "SU" -> DayOfWeek.SUNDAY;
            case "MO" -> DayOfWeek.MONDAY;
            case "TU" -> DayOfWeek.TUESDAY;
            case "WE" -> DayOfWeek.WEDNESDAY;
            case "TH" -> DayOfWeek.THURSDAY;
            case "FR" -> DayOfWeek.FRIDAY;
            case "SA" -> DayOfWeek.SATURDAY;
            default -> throw new RuntimeException("Unknown week " + nthWeek.substring(1, 3));
        };
        LocalDate first = LocalDate.of(thisYear, month, 1);
        LocalDate last = month < 12 ? LocalDate.of(thisYear, month + 1, 1) : LocalDate.of(thisYear + 1, 1, 1);
        for (LocalDate day = first; day.compareTo(last) < 0; day = day.plusDays(1)) {
            if (day.getDayOfWeek() == week)
                --n;
            if (n <= 0)
                return day;
        }
        throw new RuntimeException("Unknown month n-th week " + monthNthWeek);
    }

    static String generate(int thisYear) {
        Function<MatchResult, String> replacer = m -> {
            String variable = m.group(1);
            if (variable.equals("THIS_YEAR"))
                return "" + thisYear;
            if (variable.equals("NEXT_YEAR"))
                return "" + (thisYear + 1);
            String[] fields = variable.split(":");
            if (fields[0].equals("FIRST")) {
                String[] pats = fields[1].split(",");
                LocalDate first = Stream.of(pats).map(pat -> first(thisYear, pat)).min(LocalDate::compareTo).get();
                return first.format(DATE_FORMATTER);
            }
            throw new RuntimeException("Unknown variable %s".formatted(m.group()));
        };
        Matcher matcher = VARIABLE.matcher(TEMPLATE);
        String generated = matcher.replaceAll(replacer);
        return generated;
    }

    @Test
    public void testGenerate() {
        System.out.println(generate(2026));
    }
}
