package saka1029.util.decs;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Decs {

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
        return stream()
            .map(d -> d.toString())
            .collect(Collectors.joining(", "));
    }

    public Decs map(UnaryOperator<BigDecimal> mapper) {
        return new Decs(
            stream()
                .map(mapper)
                .toArray(BigDecimal[]::new)
        );
    }

    // Unary operators

    public Decs sum() {
        return new Decs(
            stream()
                .reduce(BigDecimal.ZERO, (a, b) -> a.add(b))
        );
    }

    public Decs subtract() {
        return new Decs(
            // stream().reduce(BigDecimal.ZERO, (a, b) -> a.subtract(b))
            switch (size()) {
                case 0, 1 -> stream().reduce(BigDecimal.ZERO, (a, b) -> a.subtract(b));
                default -> stream().reduce((a, b) -> a.subtract(b)).get();
            }
        );
    }

    public Decs negate() {
        return map(BigDecimal::negate);
    }

}
