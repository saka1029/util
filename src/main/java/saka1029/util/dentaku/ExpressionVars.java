package saka1029.util.dentaku;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public class ExpressionVars implements Expression {
    public final Expression e;
    public final List<String> variables;

    ExpressionVars(Expression e, Set<String> variables) {
        this.e = e;
        this.variables = variables.stream().sorted().toList();
    }

    public static ExpressionVars of(Expression e, Set<String> variables) {
        return new ExpressionVars(e, variables);
    }

    @Override
    public BigDecimal[] eval(Context context) {
        return e.eval(context);
    }
}
