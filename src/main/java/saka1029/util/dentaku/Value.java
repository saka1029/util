package saka1029.util.dentaku;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Value {

    private Value() {}

    public static BigDecimal[] EMPTY = new BigDecimal[] {};
    public static BigDecimal[] NaN = new BigDecimal[] {};
    public static BigDecimal TRUE = BigDecimal.ONE;
    public static BigDecimal FALSE = BigDecimal.ZERO;

    public static BigDecimal[] array(int size) {
        return size == 0 ? EMPTY : new BigDecimal[size];
    }

    public static BigDecimal[] array(String s) {
        s = s.trim();
        if (s.equals(""))
            return EMPTY;
        return Arrays.stream(s.split("\\s+"))
            .map(Value::dec)
            .toArray(Value::array);
    }

    public static boolean equals(BigDecimal left, BigDecimal right) {
        return left.equals(right);
    }

    public static BigDecimal dec(String s) {
        return new BigDecimal(s);
    }

    public static BigDecimal dec(boolean b) {
        return b ? BigDecimal.ONE : BigDecimal.ZERO;
    }

    public static BigDecimal dec(double d) {
        return new BigDecimal(d);
    }

    public static BigDecimal dec(int i) {
        return BigDecimal.valueOf(i);
    }

    public static BigDecimal dec(BigInteger i) {
        return new BigDecimal(i);
    }

    public static boolean b(BigDecimal d) {
        return !d.equals(BigDecimal.ZERO);
    }

    public static int i(BigDecimal d) {
        return d.intValueExact();
    }

    public static String str(BigDecimal[] d) {
        if (d.length == 0)
            return "EMPTY";
        return Arrays.stream(d)
            .map(BigDecimal::toString)
            .collect(Collectors.joining(", "));
    }
}