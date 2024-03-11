package saka1029.util.dentaku;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Context {
    final Map<String, Expression> variables = new HashMap<>();

    public static Context of() {
        return new Context();
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
