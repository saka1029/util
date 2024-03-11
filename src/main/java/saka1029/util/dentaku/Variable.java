package saka1029.util.dentaku;

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
    public Vector eval(Context context) {
        Expression e = context.variable(name);
        if (e == null)
            throw new VectorException("variable '%s' not found", name);
        return e.eval(context);
    }

    @Override
    public String toString() {
        return name;
    }

}
