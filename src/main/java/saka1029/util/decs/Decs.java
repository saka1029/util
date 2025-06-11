package saka1029.util.decs;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Decs {

    public static final Decs EMPTY = Decs.of();
    public static final BigDecimal TRUE = BigDecimal.ONE;
    public static final BigDecimal FALSE = BigDecimal.ZERO;

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

    public Decs map(UnaryOperator<BigDecimal> mapper) {
        return Decs.of(stream().map(mapper));
    }

    // Unary operators

    public Decs sum() {
        return Decs.of(
            stream().reduce(BigDecimal.ZERO, (a, b) -> a.add(b))
        );
    }

    public Decs subtract() {
        return Decs.of(
            switch (size()) {
                case 0, 1 -> stream().reduce(BigDecimal.ZERO, (a, b) -> a.subtract(b));
                default -> stream().reduce((a, b) -> a.subtract(b)).get();
            }
        );
    }

    public Decs negate() {
        return map(BigDecimal::negate);
    }

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
