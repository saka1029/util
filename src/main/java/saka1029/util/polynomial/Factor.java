package saka1029.util.polynomial;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Factor implements Expression {
    final int coeficient;
    final List<Exponent> elements;

    Factor(int coeficient, Exponent... elements) {
        this.coeficient = coeficient;
        Map<Primary, Exponent> map = new HashMap<>();
        for (Exponent e : elements)
            map.compute(e.primary, (k, v) -> v == null ? e : Exponent.of(e.primary, v.pow + e.pow));
        this.elements = map.values().stream().toList();
    }

    public static Factor of(int coeficient, Exponent... elements) {
        return new Factor(coeficient, elements);
    }

    public static Factor of(Exponent... elements) {
        return of(1, elements);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Factor f
            && f.coeficient == coeficient
            && f.elements.equals(elements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coeficient, elements);
    }

    @Override
    public String toString() {
        String s = elements.stream()
            .map(e -> e.toString())
            .collect(Collectors.joining("*"));
        if (coeficient != 1)
            s = coeficient + "*" + s;
        return s;
    }

}
