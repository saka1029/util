package saka1029.util.polynomial;

import java.util.List;
import java.util.stream.Stream;

public class Factor implements Expression {
    final int coeficient;
    final List<Exponent> elements;

    Factor(int coeficient, Exponent... elements) {
        this.coeficient = coeficient;
        this.elements = Stream.of(elements).toList();
    }

}
