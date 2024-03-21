package test.saka1029.dentaku;

import static org.junit.Assert.assertEquals;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.junit.Test;

public class TestBigDecimal {

    static BigDecimal round(BigDecimal d) {
        return round(d, 0);
    }

    static BigDecimal round(BigDecimal d, int scale) {
        return d.setScale(scale, RoundingMode.HALF_UP);
    }

    @Test
    public void testRound() {
        assertEquals(new BigDecimal("123"), round(new BigDecimal("123.456")));
        assertEquals(new BigDecimal("123"), round(new BigDecimal("123.456789")));
        assertEquals(new BigDecimal("123457"), round(new BigDecimal("123456.789")));
        assertEquals(new BigDecimal("1234"), round(new BigDecimal("1234.3")));
        assertEquals(new BigDecimal("1234"), round(new BigDecimal("1234.4")));
        assertEquals(new BigDecimal("1235"), round(new BigDecimal("1234.5")));
        assertEquals(new BigDecimal("1235"), round(new BigDecimal("1234.6")));
        assertEquals(new BigDecimal("123.5"), round(new BigDecimal("123.456789"), 1));
        assertEquals(new BigDecimal("123.46"), round(new BigDecimal("123.456789"), 2));
        assertEquals(new BigDecimal("123.457"), round(new BigDecimal("123.456789"), 3));
    }

    record Str<T>(T t, String s) {
    }

    @Test
    public void testStr() {
        Str<Integer> si = new Str<>(3, "three");
        Str<String> ss = new Str<>("san", "three");
        System.out.printf("si=%s%n", si);
        System.out.printf("ss=%s%n", ss);
    }

}
