package saka1029.util.dentaku;

import java.util.function.UnaryOperator;

public class UnaryCall implements UnaryOperator<Expression> {
    final String variable;
    final Expression body;

    UnaryCall(String variable, Expression body) {
        this.variable = variable;
        this.body = body;
    }

    @Override
    public Expression apply(Expression t) {
        return c -> {
            Context child = c.child();
            child.variable(variable, t);
            return body.eval(child);
        };
    }
}
