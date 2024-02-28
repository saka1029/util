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
        return new Context(null);
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
