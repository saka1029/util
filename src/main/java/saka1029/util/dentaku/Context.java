package saka1029.util.dentaku;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.UnaryOperator;
import java.util.Set;

public class Context {
    final Context parent;
    final Map<String, Expression> variables = new HashMap<>();
    final Operators operators;

    Context(Operators ops) {
        this.operators = ops;
        this.parent = null;
    }

    Context(Context parent) {
        this.operators = parent.operators;
        this.parent = parent;
    }

    public static Context of(Operators ops) {
        Context context = new Context(ops);
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
        operators.unary(name, null);
    }

    public Set<Entry<String, Expression>> variables() {
        return variables.entrySet();
    }

    public Operators operators() {
        return operators;
    }

    public UnaryOperator<Expression> unary(String name) {
        return operators.unary(name);
    }

    public void unary(String name, UnaryOperator<Expression> body) {
        operators.unary(name, body);
        variables.put(name, null);
    }
}
