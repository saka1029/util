package saka1029.util.eval;

import java.util.HashMap;
import java.util.Map;

public class Context {
    private final Context parent;
    private final Map<String, Expression> variables;
    private final Map<String, Func> functions;

    Context(Context parent) {
        this.parent = parent;
        this.variables = new HashMap<>();
        this.functions = new HashMap<>();
    }

    public static Context of() {
        Context c = new Context(null);
        initialize(c);
        return c;
    }

    public static void initialize(Context c) {
        c.variable("pi", x -> Math.PI);
        c.variable("e", x -> Math.E);
        c.function1("neg", (x, a) -> -a);
        c.function2("+", (x, a, b) -> a + b);
        c.function2("-", (x, a, b) -> a - b);
        c.function2("*", (x, a, b) -> a * b);
        c.function2("/", (x, a, b) -> a / b);
        c.function2("%", (x, a, b) -> a % b);
        c.function2("^", (x, a, b) -> Math.pow(a, b));
        c.function2("hypot", (x, a, b) -> Math.hypot(a, b));
        c.function1("sqrt", (x, a) -> Math.sqrt(a));
        c.function1("abs", (x, a) -> Math.abs(a));
        c.function1("exp", (x, a) -> Math.exp(a));
        c.function1("sin", (x, a) -> Math.sin(a));
        c.function1("asin", (x, a) -> Math.asin(a));
        c.function1("sinh", (x, a) -> Math.sinh(a));
        c.function1("cos", (x, a) -> Math.cos(a));
        c.function1("acos", (x, a) -> Math.acos(a));
        c.function1("cosh", (x, a) -> Math.cosh(a));
        c.function1("tan", (x, a) -> Math.tan(a));
        c.function1("atan", (x, a) -> Math.atan(a));
        c.function1("tanh", (x, a) -> Math.tanh(a));
        c.function1("log", (x, a) -> Math.log10(a));
        c.function1("ln", (x, a) -> Math.log(a));
        c.function1("rad", (x, a) -> Math.toRadians(a));
        c.function1("deg", (x, a) -> Math.toDegrees(a));
    }

    public Context child() {
        return new Context(this);
    }

    public Expression variable(String name) {
        Expression e = variables.get(name);
        if (e != null)
            return e;
        else if (parent != null)
            return parent.variable(name);
        else
            throw new EvalException("'%s' undefined", name);
    }

    public void variable(String name, Expression e) {
        variables.put(name, e);
    }

    public Func function(String name) {
        Func e = functions.get(name);
        if (e != null)
            return e;
        else if (parent != null)
            return parent.function(name);
        else
            throw new EvalException("'%s' undefined function", name);
    }

    public void function(String name, Func e) {
        functions.put(name, e);
    }

    public void function0(String name, Func0 e) {
        functions.put(name, e);
    }

    public void function1(String name, Func1 e) {
        functions.put(name, e);
    }

    public void function2(String name, Func2 e) {
        functions.put(name, e);
    }

    public void function3(String name, Func3 e) {
        functions.put(name, e);
    }
    
}
