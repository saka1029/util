package saka1029.util.eval;

import java.util.Arrays;

public class Funcall implements Expression {
    final String name;
    final Expression[] arguments;
    
    Funcall(String name, Expression... arguments) {
        this.name = name;
        this.arguments = arguments;
    }

    public static Funcall of(String name, Expression... arguments) {
        return new Funcall(name, arguments);
    }

    @Override
    public double eval(Context c) {
        Expression e = c.get(name);
        if (!(e instanceof Func func))
            throw new EvalException("'%s' is not a function".formatted(name));
        double[] values = Arrays.stream(arguments)
            .mapToDouble(a -> a.eval(c))
            .toArray();
        return func.call(c, values);
    }

}
