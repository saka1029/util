package saka1029.util.vector;

import java.util.HashMap;
import java.util.Map;

public class Context {
    final Map<String, Expression> variables = new HashMap<>();

    public Expression variable(String name) {
        return variables.get(name);
    }

    public void variable(String name, Expression e) {
        variables.put(name, e);
    }
}
