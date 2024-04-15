package saka1029.util.dentaku;

public record UnaryCallMap(String variable, Expression body) implements Unary {

    // @Override
    // public Value apply(Context context, Value argument) {
    //     Context child = context.child();
    //     child.variable(variable, argument, variable);
    //     return body.eval(child);
    // }

    /**
     * 以下を動作させるための実装。
     * ただしaveは正常に動作しなくなる。
     * <pre>
     * F n = * (1 to n)
     * F (1 t0 10)
     * </pre>
     */
    @Override
    public Value apply(Context context, Value argument) {
        Context child = context.child();
        return argument.map(a -> {
            child.variable(variable, Value.of(a), variable);
            return body.eval(child).oneElement();
        });
    }
}
