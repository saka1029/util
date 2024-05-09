package saka1029.util.dentaku;

import java.math.BigDecimal;
import java.util.Arrays;
import static saka1029.util.dentaku.Value.*;

public class UnaryDefined implements Unary {

    public final String variable;
    public final Expression expression;

    UnaryDefined(String variable, Expression expression) {
        this.variable = variable;
        this.expression = expression;
    }

    public static UnaryDefined of(String variable, Expression expression) {
        return new UnaryDefined(variable, expression);
    }

    @Override
    public BigDecimal[] apply(Context context, BigDecimal[] argument) {
        Context child = context.child();
        child.variable(variable, x -> argument, variable);
        return expression.eval(child);
    }

    @Override
    public Unary select() {
        return (context, argument) -> {
            Context child = context.child();
            return Arrays.stream(argument)
                .filter(d -> {
                    BigDecimal[] array = new BigDecimal[] {d};
                    child.variable(variable, x -> array, variable);
                    return b(expression.eval(child)[0]); })
                .toArray(Value::array);
        };
    }
}
