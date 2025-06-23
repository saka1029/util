package saka1029.util.decs;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ExpressionWithVariables implements Expression {
    public final Expression expression;
    public final List<String> variables;

    public ExpressionWithVariables(Expression expression, List<String> variables) {
        this.expression = expression;
        this.variables = new ArrayList<>(variables);
    }

    @Override
    public BigDecimal[] apply(Context context) {
        return expression.apply(context);
    }
}
