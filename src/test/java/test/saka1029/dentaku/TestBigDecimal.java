package test.saka1029.dentaku;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;
import java.util.List;

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

    static String digits(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0, max = s.length(); i < max; ++i) {
            char c = s.charAt(i);
            if (Character.isDigit(c))
                sb.append(Character.digit(c, 10));
            else
                return null;
        }
        return sb.toString();
    }

    @Test
    public void testDigits() {
        assertEquals("12345", digits("１２３４５"));
        // assertEquals(new BigDecimal("123.45"), new BigDecimal("１２３．４５"));
    }

    static BigDecimal encode(BigDecimal b, BigDecimal... s) {
        BigDecimal r = BigDecimal.ZERO;
        for (BigDecimal d : s)
            r = r.multiply(b).add(d);
        return r;
    }

    @Test
    public void testEncode() {
        assertEquals(new BigDecimal("1234"), encode(BigDecimal.TEN,
            new BigDecimal(1), new BigDecimal(2), new BigDecimal(3), new BigDecimal(4)));
        assertEquals(new BigDecimal("1234"), encode(new BigDecimal("16"),
            new BigDecimal(4), new BigDecimal(13), new BigDecimal(2)));
    }

    static BigDecimal[] decode(BigDecimal b, BigDecimal v) {
        List<BigDecimal> r = new LinkedList<>();
        while (v.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal[] t = v.divideAndRemainder(b);
            v = t[0];
            r.addFirst(t[1]);
        }
        return r.toArray(BigDecimal[]::new);
    }

    @Test
    public void testDecode() {
        assertArrayEquals(new BigDecimal[] {
            new BigDecimal(1), new BigDecimal(2), new BigDecimal(3), new BigDecimal(4)},
            decode(BigDecimal.TEN, new BigDecimal("1234")));
        assertArrayEquals(new BigDecimal[] {
            new BigDecimal(4), new BigDecimal(13), new BigDecimal(2)},
            decode(new BigDecimal("16"), new BigDecimal("1234")));
    }
}
