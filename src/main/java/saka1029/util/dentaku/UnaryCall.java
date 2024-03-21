package saka1029.util.dentaku;

public record UnaryCall(String variable, Expression body) implements Unary {

    @Override
    public Value apply(Context context, Value argument) {
        Context child = context.child();
        child.variable(variable, argument, variable);
        return body.eval(child);
    }
}
