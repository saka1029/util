package saka1029.util.polynomial;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Factor implements Expression {
    final int coeficient;
    final List<Exponent> elements;

    Factor(int coeficient, Exponent... elements) {
        this.coeficient = coeficient;
        this.elements = Stream.of(elements).toList();
    }

    public static Factor of(int coeficient, Exponent... elements) {
        return new Factor(coeficient, elements);
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
        return coeficient + elements.stream()
            .map(e -> e.toString())
            .collect(Collectors.joining());
    }

}
