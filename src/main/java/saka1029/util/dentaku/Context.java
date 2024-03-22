package saka1029.util.dentaku;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Context {
    final Context parent;
    final Operators functions;
    final Map<String, Str<Expression>> variables = new HashMap<>();

    private Context(Operators functions, Context parent) {
        this.parent = parent;
        this.functions = functions;
    }

    public static Context of(Operators functions) {
        Context context = new Context(functions, null);
        context.variable("PI", Value.PI);
        context.variable("E", Value.E);
        return context;
    }

    public Context child() {
        return new Context(functions, this);
    }

    public Operators operators() {
        return functions;
    }
    public Expression variable(String name) {
        Str<Expression> e = variables.get(name);
        return e != null ? e.op : parent != null ? parent.variable(name) : null;
    }

    public String variableString(String name) {
        Str<Expression> e = variables.get(name);
        return e != null ? e.string : parent != null ? parent.variableString(name) : null;
    }

    public List<String> variables() {
        return variables.values().stream()
            .map(s -> s.string)
            .toList();
    }

    public void variable(String name, Expression e, String string) {
        variables.put(name, Str.of(e, string));
    }

    public void variable(String name, Value value) {
        variables.put(name, Str.of(x -> value, "%s = %s".formatted(name, value)));
    }

}
