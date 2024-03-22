package saka1029.util.dentaku;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Context {
    final Context parent;
    final Operators operators;
    final Map<String, Str<Expression>> variables = new HashMap<>();

    private Context(Operators operators, Context parent) {
        this.parent = parent;
        this.operators = operators;
    }

    public static Context of(Operators functions) {
        Context context = new Context(functions, null);
        context.initialize();
        return context;
    }

    public Context child() {
        return new Context(operators, this);
    }

    public Operators operators() {
        return operators;
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

    void eval(String line) {
        Parser.parse(operators, line).eval(this);
    }

    private void initialize() {
        variable("PI", Value.PI);
        variable("E", Value.E);
        eval("ave x = + x / length x");
        eval("variance x = + (x - ave x ^ 2) / length x");
        eval("sd x = sqrt variance x");
        eval("t-score x = x - ave x / sd x * 10 + 50");
    }

}
