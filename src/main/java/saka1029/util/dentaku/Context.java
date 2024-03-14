package saka1029.util.dentaku;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.UnaryOperator;
import java.util.Set;

public class Context {
    final Context parent;
    final Map<String, Expression> variables = new HashMap<>();
    final Operators ops;

    Context(Operators ops) {
        this.ops = ops;
        this.parent = null;
    }

    Context(Context parent) {
        this.ops = parent.ops;
        this.parent = parent;
    }

    public static Context of(Operators ops) {
        Context context = new Context(ops);
        context.variables.put("pi", c -> Vector.of(Math.PI));
        context.variables.put("e", c -> Vector.of(Math.E));
        return context;
    }

    public Context child() {
        return new Context(this);
    }

    public Expression variable(String name) {
        Expression e = variables.get(name);
        return e != null ? e : parent != null ? parent.variable(name) : null;
    }

    public void variable(String name, Expression e) {
        variables.put(name, e);
    }

    public Set<Entry<String, Expression>> variables() {
        return variables.entrySet();
    }

    public Operators operators() {
        return ops;
    }

    public UnaryOperator<Expression> unary(String name) {
        return ops.unary(name);
    }

    public void unary(String name, UnaryOperator<Expression> body) {
        ops.unary(name, body);
    }
}
