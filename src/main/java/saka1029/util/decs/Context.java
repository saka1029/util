package saka1029.util.decs;

import java.util.HashMap;
import java.util.Map;

public class Context {

    final Map<String, Help<Expression>> variables = new HashMap<>();
    final Map<String, Help<Unary>> unarys = new HashMap<>();
    final Map<String, Help<Binary>> binarys = new HashMap<>();

    boolean isVariable(String name) {
        return variables.containsKey(name);
    }

    Help<Expression> variable(String name) {
        Help<Expression> r = variables.get(name);
        if (r == null)
            throw new DecsException("variable '%s' undef", name);
        return r;
    }

    boolean isUnary(String name) {
        return unarys.containsKey(name);
    }

    Help<Unary> unary(String name) {
        Help<Unary> r = unarys.get(name);
        if (r == null)
            throw new DecsException("unary '%s' undef", name);
        return r;
    }

    boolean isBinary(String name) {
        return binarys.containsKey(name);
    }

    Help<Binary> binary(String name) {
        Help<Binary> r = binarys.get(name);
        if (r == null)
            throw new DecsException("binary '%s' undef", name);
        return r;
    }

    void variable(String name, Expression expression, String help) {
        variables.put(name, new Help<>(expression, help));
        unarys.remove(name);
        binarys.remove(name);
    }

    void unary(String name, Unary unary, String help) {
        unarys.put(name, new Help<>(unary, help));
        variables.remove(name);
    }

    void binary(String name, Binary binary, String help) {
        binarys.put(name, new Help<>(binary, help));
        variables.remove(name);
    }

}
