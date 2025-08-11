package saka1029.util.formula;

import java.util.ArrayList;
import java.util.List;

public class Sequence implements Expression {
    final List<Expression> elements = new ArrayList<>();

    Sequence(List<Expression> elements) {
        this.elements.addAll(elements);
    }
}
