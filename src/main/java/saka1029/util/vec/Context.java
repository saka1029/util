package saka1029.util.vec;

import java.util.HashMap;
import java.util.Map;

public class Context {
    Map<String, Expression> variables = new HashMap<>();

    public Expression variable(String name) {
        return variables.get(name);
    }

    public void variable(String name, Expression e) {
        variables.put(name, e);
    }

}
