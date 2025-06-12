package saka1029.util.decs;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;
import java.util.function.BinaryOperator;
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
    public static final MathContext MATH_CONTEXT = MathContext.DECIMAL128;

    public static Stream<BigDecimal> stream(BigDecimal[] elements) {
        return Arrays.stream(elements);
    }

    public static BigDecimal[] decs(BigDecimal... elements) {
        return elements.clone();
    }

    public static BigDecimal[] decs(Stream<BigDecimal> stream) {
        return stream.toArray(BigDecimal[]::new);
    }

    public static int hashCode(BigDecimal[] decs) {
        return Arrays.hashCode(decs);
    }

    public static boolean equals(BigDecimal[] decs, BigDecimal[] right) {
        return Arrays.equals(decs, right);
    }

    public static String string(BigDecimal[] decs) {
        return stream(decs)
            .map(d -> d.toString())
            .collect(Collectors.joining(", ", "(", ")"));
    }

    // Unary method

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

    public static BigDecimal[] map(BigDecimal[] decs, UnaryOperator<BigDecimal> mapper) {
        return decs(stream(decs).map(mapper));
    }

    public static BigDecimal[] sum(BigDecimal[] decs) {
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

    public static BigDecimal[] negate(BigDecimal[] decs) {
        return map(decs, BigDecimal::negate);
    }

    // Binary method

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
}
