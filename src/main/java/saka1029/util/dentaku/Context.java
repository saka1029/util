package saka1029.util.dentaku;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Context {
    final Map<String, Expression> variables = new HashMap<>();

    public static Context of() {
        Context context = new Context();
        context.variables.put("pi", c -> Vector.of(Math.PI));
        context.variables.put("e", c -> Vector.of(Math.E));
        return context;
    }

    public Expression variable(String name) {
        return variables.get(name);
    }

    public void variable(String name, Expression e) {
        variables.put(name, e);
    }

    public Set<Entry<String, Expression>> variables() {
        return variables.entrySet();
    }
}
