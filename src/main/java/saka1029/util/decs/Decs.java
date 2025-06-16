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
    public static final BigDecimal TRUE = BigDecimal.ONE;
    public static final BigDecimal FALSE = BigDecimal.ZERO;
    public static final BigDecimal MINUS_ONE = BigDecimal.valueOf(-1L);
    public static final BigDecimal ZERO = BigDecimal.ZERO;
    public static final BigDecimal ONE = BigDecimal.ONE;
    // public static final MathContext MATH_CONTEXT = MathContext.DECIMAL128;
    public static final MathContext MATH_CONTEXT = new MathContext(100);

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

    public static BigDecimal[] decs(BigDecimal... elements) {
        return elements.clone();
    }

    public static BigDecimal[] decs(List<BigDecimal> list) {
        return decs(list.toArray(BigDecimal[]::new));
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
        System.out.printf("pow(%s, %s)%n", left, right);
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
    public static BigDecimal[] not(BigDecimal[] decs) {
        return map(decs, a -> dec(!bool(a)));
    }

    // unary single method

    public static BigDecimal single(BigDecimal[] decs) {
        if (decs.length != 1)
            throw new DecsException("Single value expected but %s", string(decs));
        return decs[0];
    }

    public static BigDecimal[] single(BigDecimal[] decs,
            Function<BigDecimal, BigDecimal[]> operation) {
            single(decs);
        return operation.apply(decs[0]);
    }

    public static BigDecimal[] iota(BigDecimal[] decs) {
        return decs(IntStream.rangeClosed(1, single(decs).intValue())
            .mapToObj(i -> dec(i)));
    }

    public static BigDecimal[] iota0(BigDecimal[] decs) {
        return decs(IntStream.range(0, single(decs).intValue())
            .mapToObj(i -> dec(i)));
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
    
    // binary special method

    // public static boolean isInteger(BigDecimal dec) {
    //     return dec.signum() == 0
    //         || dec.scale() <= 0
    //         || dec.stripTrailingZeros().scale() <= 0;
    // }

    public static BigInteger[] bigIneger(BigDecimal[] decs) {
        return Stream.of(decs)
            .map(BigDecimal::toBigIntegerExact)
            .toArray(BigInteger[]::new);
    }

    /**
     * 
     * @param left 単一の正の整数
     * @param right 単一または複数の正の整数
     * @r
     * 
     */
    public static BigDecimal[] base(BigDecimal[] left, BigDecimal[] right) {
        BigInteger[] lint = bigIneger(left), rint = bigIneger(right);
        if (lint.length != 1)
            throw new DecsException("Single value expected but %s", string(left));
        Deque<BigInteger> result = new LinkedList<>();
        BigInteger r = lint[0].abs();
        if (rint.length == 1) {
            BigInteger base = rint[0].abs();
            while (r.compareTo(BigInteger.ZERO) > 0) {
                BigInteger[] dr = r.divideAndRemainder(base);
                result.addFirst(dr[1]);
                r = dr[0];
            }
            if (result.size() == 0)
                result.addFirst(BigInteger.ZERO);
        } else {
            for (int i = rint.length - 1; i >= 0 && r.compareTo(BigInteger.ZERO) > 0; --i) {
                BigInteger[] dr = r.divideAndRemainder(rint[i].abs());
                result.addFirst(dr[1]);
                r = dr[0];
            }
            if (result.size() == 0 || r.compareTo(BigInteger.ZERO) != 0)
                result.addFirst(r);
        }
        return decs(result.stream().map(Decs::dec));
    }
}
