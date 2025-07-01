package saka1029.util.decs;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import ch.obermuhlner.math.big.BigDecimalMath;

public class Decs {

    private Decs() {
    }

    public static final BigDecimal[] EMPTY = new BigDecimal[] {};
    public static final BigDecimal[] NO_VALUE = new BigDecimal[] {};
    public static final BigDecimal TRUE = BigDecimal.ONE;
    public static final BigDecimal FALSE = BigDecimal.ZERO;
    public static final BigDecimal MINUS_ONE = BigDecimal.valueOf(-1L);
    public static final BigDecimal ZERO = BigDecimal.ZERO;
    public static final BigDecimal ONE = BigDecimal.ONE;
    // public static final MathContext MATH_CONTEXT = MathContext.DECIMAL128;
    public static final MathContext MATH_CONTEXT = new MathContext(100);

    static ValueException error(String message, Object... args) {
        return new ValueException(message, args);
    }

    public static Stream<BigDecimal> stream(BigDecimal[] elements) {
        return Arrays.stream(elements);
    }

    public static BigDecimal dec(int i) {
        return BigDecimal.valueOf(i);
    }

    public static BigDecimal dec(long i) {
        return BigDecimal.valueOf(i);
    }

    public static BigDecimal dec(double i) {
        return BigDecimal.valueOf(i);
    }

    public static BigDecimal dec(String s) {
        return new BigDecimal(s);
    }

    public static BigDecimal dec(boolean b) {
        return b ? TRUE : FALSE;
    }

    public static BigDecimal dec(BigInteger i) {
        return new BigDecimal(i);
    }

    public static BigDecimal[] decs() {
        return EMPTY;
    }

    public static BigDecimal[] decs(BigDecimal... elements) {
        return elements.clone();
    }

    public static BigDecimal[] decs(int... values) {
        return decs(IntStream.of(values).mapToObj(i -> dec(i)));
    }

    public static BigDecimal[] decs(List<BigDecimal> list) {
        return list.toArray(BigDecimal[]::new);
    }

    public static BigDecimal[] decs(Deque<BigDecimal> list) {
        return list.toArray(BigDecimal[]::new);
    }

    public static BigDecimal[] decs(Stream<BigDecimal> stream) {
        return stream.toArray(BigDecimal[]::new);
    }

    public static BigDecimal[] decs(String s) {
        s = s.trim();
        return s.equals("") ? EMPTY
            : decs(Stream.of(s.split("\\s+"))
                .map(x -> dec(x)));
    }

    public static int hashCode(BigDecimal[] decs) {
        return java.util.Arrays.hashCode(decs);
    }

    public static boolean equals(BigDecimal left, BigDecimal right) {
        return left.compareTo(right) == 0;
    }

    public static boolean equals(BigDecimal[] left, BigDecimal[] right) {
        return left.length == right.length
            && IntStream.range(0, left.length)
                .allMatch(i -> equals(left[i], right[i]));
    }

    public static String string(BigDecimal dec) {
        return dec.toString().replaceFirst("\\.0+$", "");
    }

    public static String string(BigDecimal[] decs) {
        return decs.length == 1 ?  string(decs[0])
            : stream(decs)
                .map(d -> string(d))
                .collect(Collectors.joining(", ", "(", ")"));
    }

    static boolean bool(BigDecimal d) {
        return !d.equals(ZERO);
    }

    public static BigDecimal[] pi() {
        return decs(PI);
    }

    public static BigDecimal E = BigDecimalMath.e(MATH_CONTEXT);

    public static BigDecimal[] e() {
        return decs(E);
    }

    // unary reduce method

    public static BigDecimal[] reduce(BigDecimal[] decs,
            BigDecimal unit, BinaryOperator<BigDecimal> op) {
        return decs(stream(decs).reduce(unit, op));
    }

    public static BigDecimal[] reduce(BigDecimal[] decs,
            BigDecimal unit, UnaryOperator<BigDecimal> one,BinaryOperator<BigDecimal> many) {
        switch (decs.length) {
            case 0: return decs(unit);
            case 1: return decs(one.apply(decs[0]));
            default: return decs(stream(decs).reduce(many).get());
        }
    }

    public static BigDecimal[] add(BigDecimal[] decs) {
        return reduce(decs, BigDecimal.ZERO, (a, b) -> a.add(b));
    }

    public static BigDecimal[] subtract(BigDecimal[] decs) {
        return reduce(decs, BigDecimal.ZERO, BigDecimal::negate, (a, b) -> a.subtract(b));
    }

    public static BigDecimal[] multiply(BigDecimal[] decs) {
        return reduce(decs, BigDecimal.ONE, (a, b) -> a.multiply(b));
    }

    public static BigDecimal[] divide(BigDecimal[] decs) {
        return reduce(decs, BigDecimal.ONE,
            d -> BigDecimalMath.reciprocal(d, MATH_CONTEXT),
            (a, b) -> a.divide(b, MATH_CONTEXT));
    }

    public static BigDecimal pow(BigDecimal left, BigDecimal right) {
        // System.out.printf("pow(%s, %s)%n", left, right);
        return BigDecimalMath.isLongValue(right)
                ? BigDecimalMath.pow(left, right.longValue(), MATH_CONTEXT)
                : BigDecimalMath.pow(left, right, MATH_CONTEXT);
    }

    /**
     * べき乗を計算する。
     * 右結合である点に注意する。
     * pow(2, 3, 2) = 2 ^ (3 ^ 2) = 512
     * 
     * @param decs
     * @return
     */
    public static BigDecimal[] pow(BigDecimal[] decs) {
        return reduce(reverse(decs), ONE, (a, b) -> pow(b, a));
    }

    public static BigDecimal[] and(BigDecimal[] decs) {
        return reduce(decs, ONE, (a, b) -> dec(bool(a) && bool(b)));
    }

    public static BigDecimal[] or(BigDecimal[] decs) {
        return reduce(decs, ZERO, (a, b) -> dec(bool(a) || bool(b)));
    }

    // unary map method

    public static BigDecimal[] map(BigDecimal[] decs, UnaryOperator<BigDecimal> mapper) {
        return decs(stream(decs).map(mapper));
    }

    public static BigDecimal[] negate(BigDecimal[] decs) {
        return map(decs, BigDecimal::negate);
    }

    public static BigDecimal[] abs(BigDecimal[] decs) {
        return map(decs, BigDecimal::abs);
    }

    public static BigDecimal[] reciprocal(BigDecimal[] decs) {
        return map(decs, d -> BigDecimalMath.reciprocal(d, MATH_CONTEXT));
    }

    public static BigDecimal[] not(BigDecimal[] decs) {
        return map(decs, a -> dec(!bool(a)));
    }

    public static BigDecimal factorial(BigDecimal dec) {
        BigInteger n = dec.toBigIntegerExact();
        if (n.signum() < 0)
            n = BigInteger.ZERO;
        BigInteger r = BigInteger.ONE;
        for ( ; n.signum() > 0; n = n.subtract(BigInteger.ONE))
            r = r.multiply(n);
        return dec(r);
    }

    public static BigDecimal[] factorial(BigDecimal[] decs) {
        return map(decs, a -> factorial(a));
    }

    public static BigDecimal PI = BigDecimalMath.pi(MATH_CONTEXT);
    public static BigDecimal DEG180 = dec(180);
    public static BigDecimal PI_DIV_DEG180 = PI.divide(DEG180, MATH_CONTEXT);

    public static BigDecimal[] radian(BigDecimal[] decs) {
        return map(decs, a -> a.multiply(PI_DIV_DEG180, MATH_CONTEXT));
    }

    public static BigDecimal[] degree(BigDecimal[] decs) {
        return map(decs, a -> a.divide(PI_DIV_DEG180, MATH_CONTEXT));
    }

    public static BigDecimal[] sin(BigDecimal[] decs) {
        return map(decs, a -> BigDecimalMath.sin(a, MATH_CONTEXT));
    }

    public static BigDecimal[] cos(BigDecimal[] decs) {
        return map(decs, a -> BigDecimalMath.cos(a, MATH_CONTEXT));
    }

    public static BigDecimal[] tan(BigDecimal[] decs) {
        return map(decs, a -> BigDecimalMath.tan(a, MATH_CONTEXT));
    }

    public static BigDecimal[] ln(BigDecimal[] decs) {
        return map(decs, a -> BigDecimalMath.log(a, MATH_CONTEXT));
    }

    public static BigDecimal[] log10(BigDecimal[] decs) {
        return map(decs, a -> BigDecimalMath.log10(a, MATH_CONTEXT));
    }

    public static BigDecimal[] log2(BigDecimal[] decs) {
        return map(decs, a -> BigDecimalMath.log2(a, MATH_CONTEXT));
    }

    // unary single method

    public static BigDecimal single(BigDecimal[] decs) {
        if (decs.length != 1)
            throw error("Single value expected but %s", string(decs));
        return decs[0];
    }

    public static BigDecimal[] iota(BigDecimal[] decs) {
        return decs(IntStream.rangeClosed(1, single(decs).intValue())
            .mapToObj(i -> dec(i)));
    }

    public static BigDecimal[] iota0(BigDecimal[] decs) {
        return decs(IntStream.range(0, single(decs).intValue())
            .mapToObj(i -> dec(i)));
    }

    static void sieve(boolean[] primes, int n) {
        int size = primes.length;
        for (int i = n + n; i < size; i += n)
            primes[i] = true;
    }

    public static BigDecimal[] primes(BigDecimal[] decs) {
        int size = single(decs).intValue();
        boolean[] primes = new boolean[size];
        primes[0] = primes[1] = true;
        int max = (int)Math.sqrt(size);
        sieve(primes, 2);
        for (int i = 3; i <= max; i += 2)
            sieve(primes, i);
        return decs(IntStream.range(0, size)
            .filter(i -> !primes[i])
            .mapToObj(i -> dec(i)));
    }

    // unary special method

    public static BigDecimal[] length(BigDecimal[] decs) {
        return decs(decs.length);
    }

    public static BigDecimal[] reverse(BigDecimal[] decs) {
        int length = decs.length;
        BigDecimal[] result = new BigDecimal[length];
        for (int i = 0, j = length - 1; i < length; ++i, --j)
            result[j] = decs[i];
        return result;
    }

    public static BigDecimal[] sort(BigDecimal[] decs) {
        return decs(Stream.of(decs).sorted());
    }

    // binary zip method

    public static BigDecimal[] zip(BigDecimal[] left, BigDecimal[] right,
            BinaryOperator<BigDecimal> op) {
        int lsize = left.length, rsize = right.length;
        if (lsize == 0)
            return right;
        else if (rsize == 0)
            return left;
        else if (lsize == 1)
            return map(right, d -> op.apply(left[0], d));
        else if (rsize == 1)
            return map(left, d -> op.apply(d, right[0]));
        else if (lsize == rsize)
            return decs(IntStream.range(0, lsize)
                .mapToObj(i -> op.apply(left[i], right[i])));
        else
            throw error("zip: Invalid size l=%s r=%s", string(left), string(right));
    }

    public static BigDecimal[] add(BigDecimal[] left, BigDecimal[] right) {
        return zip(left, right, BigDecimal::add);
    }

    public static BigDecimal[] subtract(BigDecimal[] left, BigDecimal[] right) {
        return zip(left, right, BigDecimal::subtract);
    }

    public static BigDecimal[] multiply(BigDecimal[] left, BigDecimal[] right) {
        return zip(left, right, BigDecimal::multiply);
    }

    public static BigDecimal[] divide(BigDecimal[] left, BigDecimal[] right) {
        return zip(left, right, (a, b) -> a.divide(b, MATH_CONTEXT));
    }

    public static BigDecimal[] mod(BigDecimal[] left, BigDecimal[] right) {
        return zip(left, right, (a, b) -> a.remainder(b, MATH_CONTEXT));
    }

    public static BigDecimal[] pow(BigDecimal[] left, BigDecimal[] right) {
        return zip(left, right, (a, b) ->
            BigDecimalMath.isLongValue(b)
                ? BigDecimalMath.pow(a, b.longValue(), MATH_CONTEXT)
                : BigDecimalMath.pow(a, b, MATH_CONTEXT).stripTrailingZeros());
    }

    public static BigDecimal sign(int sign) {
        return sign < 0 ? MINUS_ONE
            : sign == 0 ? ZERO
            : ONE;
    }

    public static BigDecimal[] compare(BigDecimal[] left, BigDecimal[] right) {
        return zip(left, right, (a, b) -> sign(a.compareTo(b)));
    }

    public static BigDecimal[] compare(BigDecimal[] left, BigDecimal[] right,
            Function<Integer, BigDecimal> conv) {
        return zip(left, right, (a, b) -> conv.apply(a.compareTo(b)));
    }

    public static BigDecimal[] eq(BigDecimal[] left, BigDecimal[] right) {
        return compare(left, right, c -> dec(c == 0));
    }

    public static BigDecimal[] ne(BigDecimal[] left, BigDecimal[] right) {
        return compare(left, right, c -> dec(c != 0));
    }

    public static BigDecimal[] lt(BigDecimal[] left, BigDecimal[] right) {
        return compare(left, right, c -> dec(c < 0));
    }

    public static BigDecimal[] le(BigDecimal[] left, BigDecimal[] right) {
        return compare(left, right, c -> dec(c <= 0));
    }

    public static BigDecimal[] gt(BigDecimal[] left, BigDecimal[] right) {
        return compare(left, right, c -> dec(c > 0));
    }

    public static BigDecimal[] ge(BigDecimal[] left, BigDecimal[] right) {
        return compare(left, right, c -> dec(c >= 0));
    }

    public static boolean trues(BigDecimal[] decs) {
        return Stream.of(decs).allMatch(d -> !d.equals(ZERO));
    }

    public static boolean falses(BigDecimal[] decs) {
        return Stream.of(decs).allMatch(d -> d.equals(ZERO));
    }

    public static BigDecimal[] and(BigDecimal[] left, BigDecimal[] right) {
        return zip(left, right, (a, b) -> dec(bool(a) && bool(b)));
    }

    public static BigDecimal[] or(BigDecimal[] left, BigDecimal[] right) {
        return zip(left, right, (a, b) -> dec(bool(a) || bool(b)));
    }

    public static BigDecimal[] log(BigDecimal[] left, BigDecimal[] right) {
        return zip(left, right, (a, b) -> BigDecimalMath.log(a, MATH_CONTEXT)
            .divide(BigDecimalMath.log(b, MATH_CONTEXT), MATH_CONTEXT));
    }
    
    // binary special method

    public static BigDecimal[] base(BigDecimal[] left, BigDecimal[] right) {
        if (Stream.of(right).anyMatch(d -> d.signum() <= 0))
            throw error("base: must all positive but %s", string(right));
        BigDecimal m = single(left);
        if (m.signum() < 0)
            throw error("base: must not negative but %s", string(left));
        Deque<BigDecimal> r = new LinkedList<>();
        BigDecimal[] dr;
        if (right.length == 1) {
            for (BigDecimal base = right[0]; m.signum() > 0; m = dr[0], r.addFirst(dr[1])) 
                dr = m.divideAndRemainder(base);
        } else {
            for (int i = right.length - 1; i >= 0 && m.signum() > 0; --i, m = dr[0], r.addFirst(dr[1])) 
                dr = m.divideAndRemainder(right[i]);
            if (m.signum() != 0)
                r.addFirst(m);
        }
        if (r.size() == 0)
            r.addFirst(ZERO);
        return decs(r);
    }

    public static BigDecimal[] decimal(BigDecimal[] left, BigDecimal[] right) {
        // BigDecimal base = single(right).abs();
        // return decs(Stream.of(left)
        //     .reduce(ZERO, (a, b) -> a.multiply(base).add(b)));
        int lsize = left.length, rsize = right.length;
        BigDecimal result = BigDecimal.ZERO;
        if (rsize == 1) {
            BigDecimal base = right[0].abs();
            for (int i = 0; i < lsize; ++i)
                result = result.multiply(base).add(left[i].abs());
        } else if (lsize == rsize) {
            for (int i = 0; i < rsize; ++i)
                result = result.multiply(right[i].abs()).add(left[i].abs());
        } else if (lsize == rsize + 1) {
            result = left[0];
            for (int i = 0; i < rsize; ++i)
                result = result.multiply(right[i].abs()).add(left[i + 1].abs());
        } else
            throw error("Illegal length left=%s rigth=%s", string(left), string(right));
        return decs(result);
    }

    public static BigDecimal[] concat(BigDecimal[] left, BigDecimal[] right) {
        BigDecimal[] result = new BigDecimal[left.length + right.length];
        System.arraycopy(left, 0, result, 0, left.length);
        System.arraycopy(right, 0, result, left.length, right.length);
        return result;
    }
}
