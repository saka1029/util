package saka1029.util.dentaku;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.BitSet;

public class Scalar {
    private Scalar() {
    }

    public static final BigDecimal ZERO = BigDecimal.ZERO;
    public static final BigDecimal ONE = BigDecimal.ONE;
    public static final BigDecimal TWO = BigDecimal.TWO;
    public static final int PRIMES_SIZE = 10000;
    public static final BigDecimal PRIMES_SIZE_BIG_DECIMAL = BigDecimal.valueOf(PRIMES_SIZE);
    public static final BitSet PRIMES;
    static void sieve(int n) {
        for (int i = n + n; i < PRIMES_SIZE; i += n)
            PRIMES.clear(i);
    }
    static {
        PRIMES = new BitSet(PRIMES_SIZE);
        PRIMES.set(2, PRIMES_SIZE);
        sieve(2);
        for (int i = 3, max = (int)Math.sqrt(PRIMES_SIZE); i <= max; i += 2)
            if (PRIMES.get(i))
                sieve(i);
    }

    public static boolean b(BigDecimal d) {
        return !d.equals(ZERO);
    }

    public static BigDecimal b(boolean b) {
        return b ? ONE : ZERO;
    }

    public static double d(BigDecimal d) {
        return d.doubleValue();
    }

    public static BigDecimal d(double f) {
        return BigDecimal.valueOf(f);
    }


    public static int i(BigDecimal d) {
        return d.intValue();
    }

    public static BigDecimal i(int i) {
        return BigDecimal.valueOf(i);
    }

    public static boolean isInt(BigDecimal d) {
        return d.setScale(0, RoundingMode.DOWN).compareTo(d) == 0;
    }

    public static BigDecimal checkInt(BigDecimal d) {
        BigDecimal i = d.setScale(0, RoundingMode.DOWN);
        if (i.compareTo(d) != 0)
            throw new ValueException("Integer expected but %s", d);
        return i;
    }

    public static BigDecimal s(String s) {
        return new BigDecimal(s);
    }

    public static String s(BigDecimal d) {
        return d.toString();
    }

    public static BigDecimal neg(BigDecimal a) {
        return a.negate();
    }

    public static BigDecimal add(BigDecimal a, BigDecimal b) {
        return a.add(b);
    }

    public static BigDecimal sub(BigDecimal a, BigDecimal b) {
        return a.subtract(b);
    }

    public static BigDecimal mult(BigDecimal a, BigDecimal b) {
        return a.multiply(b);
    }

    public static BigDecimal div(BigDecimal a, BigDecimal b) {
        return a.divide(b, MathContext.DECIMAL128);
    }

    public static boolean isPrime(BigDecimal d) {
        checkInt(d);
        if (d.compareTo(TWO) < 0)
            return false;
        if (d.compareTo())

    }
}
