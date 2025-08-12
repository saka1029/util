package saka1029.util.polynomial;

import java.util.List;
import java.util.stream.Stream;

public class Factor implements Expression {
    final int k;
    final List<VariablePow> variables;

    Factor(int k, VariablePow... variables) {
        this.k = k;
        this.variables = Stream.of(variables).toList();
    }

}
