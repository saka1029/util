package saka1029.util.polynomial;

import java.util.List;
import java.util.stream.Stream;

public class Term implements Expression {
    final List<Factor> factors;

    Term(Factor... factors) {
        this.factors = Stream.of(factors).toList();
    }
}
