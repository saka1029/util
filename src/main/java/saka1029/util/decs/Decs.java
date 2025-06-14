package saka1029.util.decs;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;
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
    public static final BigDecimal TRUE = BigDecimal.ONE;
    public static final BigDecimal FALSE = BigDecimal.ZERO;
    public static final BigDecimal MINUS_ONE = BigDecimal.valueOf(-1L);
    public static final BigDecimal ZERO = BigDecimal.ZERO;
    public static final BigDecimal ONE = BigDecimal.ONE;
    public static final MathContext MATH_CONTEXT = MathContext.DECIMAL128;

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

    public static BigDecimal[] decs(BigDecimal... elements) {
        return elements.clone();
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

    public static boolean equals(BigDecimal[] decs, BigDecimal[] right) {
        return java.util.Arrays.equals(decs, right);
    }

    public static String string(BigDecimal[] decs) {
        return stream(decs)
            .map(d -> d.toString())
            .collect(Collectors.joining(", ", "(", ")"));
    }

    static boolean bool(BigDecimal d) {
        return !d.equals(ZERO);
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

    public static BigDecimal[] mult(BigDecimal[] decs) {
        return reduce(decs, BigDecimal.ONE, (a, b) -> a.multiply(b));
    }

    public static BigDecimal[] divide(BigDecimal[] decs) {
        return reduce(decs, BigDecimal.ONE,
            d -> BigDecimalMath.reciprocal(d, MATH_CONTEXT),
            (a, b) -> a.divide(b, MATH_CONTEXT));
    }

    public static BigDecimal pow(BigDecimal left, BigDecimal right) {
        return BigDecimalMath.isLongValue(left)
                ? BigDecimalMath.pow(left, right.longValue(), MATH_CONTEXT)
                : BigDecimalMath.pow(left, right, MATH_CONTEXT).stripTrailingZeros();
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
    public static BigDecimal[] not(BigDecimal[] decs) {
        return map(decs, a -> dec(!bool(a)));
    }

    // unary single method

    public static BigDecimal[] single(BigDecimal[] decs,
            Function<BigDecimal, BigDecimal[]> operation) {
        if (decs.length != 1)
            throw new DecsException("Invalid argument %s", string(decs));
        return operation.apply(decs[0]);
    }

    public static BigDecimal[] iota(BigDecimal[] decs) {
        return single(decs, d -> decs(IntStream.rangeClosed(1, d.intValueExact())
            .mapToObj(i -> BigDecimal.valueOf(i))));
    }

    public static BigDecimal[] iota0(BigDecimal[] decs) {
        return single(decs, d -> decs(IntStream.range(0, d.intValueExact())
            .mapToObj(i -> BigDecimal.valueOf(i))));
    }

    // unary special method

    public static BigDecimal[] reverse(BigDecimal[] decs) {
        int length = decs.length;
        BigDecimal[] result = new BigDecimal[length];
        for (int i = 0, j = length - 1; i < length; ++i, --j)
            result[j] = decs[i];
        return result;
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
            throw new DecsException(
                "zip: Invalid size l=%s r=%s", string(left), string(right));
    }

    public static BigDecimal[] add(BigDecimal[] left, BigDecimal[] right) {
        return zip(left, right, BigDecimal::add);
    }

    public static BigDecimal[] subtract(BigDecimal[] left, BigDecimal[] right) {
        return zip(left, right, BigDecimal::subtract);
    }

    public static BigDecimal[] multply(BigDecimal[] left, BigDecimal[] right) {
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
}
