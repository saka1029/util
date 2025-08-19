package saka1029.util.polynomial;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Factor implements Expression {
    final int coeficient;
    final Set<Exponent> exponents;

    Factor(int coeficient, Exponent... exponents) {
        this.coeficient = coeficient;
        Map<Primary, Exponent> map = new HashMap<>();
        for (Exponent e : exponents)
            map.compute(e.primary, (k, v) -> v == null ? e : Exponent.of(e.primary, v.pow + e.pow));
        this.exponents = map.values().stream().collect(Collectors.toSet());
    }

    public static Factor of(int coeficient, Exponent... exponents) {
        return new Factor(coeficient, exponents);
    }

    public static Factor of(Exponent... exponents) {
        return of(1, exponents);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Factor f
            && f.coeficient == coeficient
            && f.exponents.equals(exponents);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coeficient, exponents);
    }

    @Override
    public String toString() {
        String s = exponents.stream()
            .map(e -> e.toString())
            .collect(Collectors.joining("*"));
        if (coeficient != 1)
            s = coeficient + "*" + s;
        return s;
    }

}
