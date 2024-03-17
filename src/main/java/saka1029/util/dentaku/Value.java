package saka1029.util.dentaku;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Value {
    private final BigDecimal[] elements;

    Value(BigDecimal ... elements) {
        this.elements = elements;
    }

    public static Value of(BigDecimal... elements) {
        return new Value(elements.clone());
    }

    public Value map(UnaryOperator<BigDecimal> operator) {
        return new Value(Arrays.stream(elements)
            .map(e -> operator.apply(e))
            .toArray(BigDecimal[]::new));
    }

    public Value binary(BinaryOperator<BigDecimal> operator, Value right) {
        if (elements.length == 1)
            return new Value(Arrays.stream(right.elements)
                .map(e -> operator.apply(elements[0], e))
                .toArray(BigDecimal[]::new));
        else if (right.elements.length == 1)
            return new Value(Arrays.stream(elements)
                .map(e -> operator.apply(e, right.elements[0]))
                .toArray(BigDecimal[]::new));
        else if (right.elements.length == elements.length)
            return new Value(IntStream.range(0, elements.length)
                .mapToObj(i -> operator.apply(elements[i], right.elements[i]))
                .toArray(BigDecimal[]::new));
        else
            throw new RuntimeException();
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(elements);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Value r && Arrays.equals(elements, r.elements);
    }

    @Override
    public String toString() {
        return Arrays.stream(elements)
            .map(e -> e.toString())
            .collect(Collectors.joining(" "));
    }
}
