package saka1029.util.eval;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        Func func = c.function(name);
        double[] values = Arrays.stream(arguments)
            .mapToDouble(a -> a.eval(c))
            .toArray();
        return func.call(c, values);
    }

    @Override
    public String string() {
        return Stream.of(arguments)
            .map(Expression::string)
            .collect(Collectors
                .joining(" ", "(" + name + " ", ")"));
    }
}