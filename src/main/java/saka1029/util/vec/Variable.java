package saka1029.util.vec;

import java.util.HashMap;
import java.util.Map;

public class Variable implements Expression {
    static final Map<String, Variable> variables = new HashMap<>();
    public final String name;

    Variable(String name) {
        this.name = name;
    }

    public static Variable of(String name) {
        return variables.computeIfAbsent(name, k -> new Variable(k));
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public Vec eval(Context context) {
        Expression e = context.variable(name);
        if (e == null)
            throw new RuntimeException("variable '%s' not found".formatted(name));
        return e.eval(context);
    }

}
