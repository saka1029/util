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

    public static final Decs EMPTY = Decs.of();
    public static final BigDecimal TRUE = BigDecimal.ONE;
    public static final BigDecimal FALSE = BigDecimal.ZERO;
    public static final MathContext MATH_CONTEXT = MathContext.DECIMAL128;

    final BigDecimal[] elements;

    int size() {
        return elements.length;
    }

    BigDecimal get(int index) {
        return elements[index];
    }

    Stream<BigDecimal> stream() {
        return Arrays.stream(elements);
    }

    Decs(BigDecimal... elements) {
        this.elements = elements;
    }

    public static Decs of(BigDecimal... elements) {
        return new Decs(elements);
    }

    public static Decs of(Stream<BigDecimal> stream) {
        return Decs.of(stream.toArray(BigDecimal[]::new));
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(elements);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Decs d && Arrays.equals(d.elements, elements);
    }

    @Override
    public String toString() {
        return size() == 0 ? "Empty"
            : stream()
                .map(d -> d.toString())
                .collect(Collectors.joining(", "));
    }

    // Unary method

    public Decs reduce(BigDecimal unit, BinaryOperator<BigDecimal> op) {
        return Decs.of(stream().reduce(unit, op));
    }

    public Decs reduce(BigDecimal unit, UnaryOperator<BigDecimal> one,BinaryOperator<BigDecimal> many) {
        switch (size()) {
            case 0: return Decs.of(unit);
            case 1: return Decs.of(one.apply(elements[0]));
            default: return Decs.of(stream().reduce(many).get());
        }
    }

    public Decs map(UnaryOperator<BigDecimal> mapper) {
        return Decs.of(stream().map(mapper));
    }

    public Decs sum() {
        return reduce(BigDecimal.ZERO, (a, b) -> a.add(b));
    }

    public Decs subtract() {
        return reduce(BigDecimal.ZERO, BigDecimal::negate, (a, b) -> a.subtract(b));
    }

    public Decs mult() {
        return reduce(BigDecimal.ONE, (a, b) -> a.multiply(b));
    }

    public Decs divide() {
        return reduce(BigDecimal.ONE,
            d -> BigDecimalMath.reciprocal(d, MATH_CONTEXT),
            (a, b) -> a.divide(b, MATH_CONTEXT));
    }

    public Decs negate() {
        return map(BigDecimal::negate);
    }

    // Binary method

    public Decs zip(BinaryOperator<BigDecimal> op, Decs right) {
        int lsize = size(), rsize = right.size();
        if (lsize == 0)
            return right;
        else if (rsize == 0)
            return this;
        else if (lsize == 1)
            return right.map(d -> op.apply(elements[0], d));
        else if (rsize == 1)
            return this.map(d -> op.apply(d, right.elements[0]));
        else if (lsize == rsize)
            return Decs.of(IntStream.range(0, lsize)
                .mapToObj(i -> op.apply(elements[i], right.elements[i])));
        else
            throw new DecsException("zip: Invalid size l=(%s) r=(%s)", this, right);
    }
}
