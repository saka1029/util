package saka1029.util.eval;

import java.util.HashMap;
import java.util.Map;

public class Variable implements Expression {
    static final Map<String, Variable> all = new HashMap<>();
    public final String name;

    Variable(String name) {
        this.name = name;
    }

    public static Variable of(String name) {
        return all.computeIfAbsent(name, k -> new Variable(name));
    }

    @Override
    public double eval(Context c) {
        return c.variable(name).eval(c);
    }
    
    @Override
    public String toString() {
        return name;
    }
}
