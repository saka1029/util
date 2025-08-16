package saka1029.util.polynomial;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Term implements Expression {
    final List<Factor> factors;

    Term(Factor... factors) {
        this.factors = Stream.of(factors).toList();
    }

    public static Term of(Factor... factors) {
        return new Term(factors);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Term t
            && t.factors.equals(factors);
    }

    @Override
    public int hashCode() {
        return factors.hashCode();
    }

    @Override
    public String toString() {
        return factors.stream()
            .map(f -> (f.coeficient >= 0 ? "+" : "") + f)
            .collect(Collectors.joining());
    }
}
