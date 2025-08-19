package saka1029.util.polynomial;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Term implements Expression {
    final Set<Factor> factors;

    Term(Factor... factors) {
        Map<Set<Exponent>, Integer> map = new HashMap<>();
        for (Factor f : factors)
            map.compute(f.exponents, (k, v) -> v == null ? f.coeficient : v + f.coeficient);
        this.factors = Stream.of(factors).collect(Collectors.toSet());
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
        String s = factors.stream()
            .map(f -> (f.coeficient >= 0 ? "+" : "") + f)
            .collect(Collectors.joining());
        if (s.startsWith("+"))
            s = s.substring(1);
        return s;
    }
}
