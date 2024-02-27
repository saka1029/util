package saka1029.util.eval;

import java.util.HashMap;
import java.util.Map;

public class Context {
    private final Context parent;
    private final Map<String, Expression> binding;

    Context(Context parent) {
        this.parent = parent;
        this.binding = new HashMap<>();
    }

    public static Context of() {
        return new Context(null);
    }

    public Context child() {
        return new Context(this);
    }

    public Expression get(String name) {
        Expression e = binding.get(name);
        if (e != null)
            return e;
        else if (parent != null)
            return parent.get(name);
        else
            throw new EvalException("'%s' undefined", name);
    }

    public void put(String name, Expression e) {
        binding.put(name, e);
    }

    public void putFunc(String name, Func e) {
        put(name, e);
    }
    
}
