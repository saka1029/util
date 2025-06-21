package saka1029.util.decs;

import java.util.HashMap;
import java.util.Map;

public class Context {

    final Map<String, Help<Expression>> variables = new HashMap<>();
    final Map<String, Help<Unary>> unarys = new HashMap<>();
    final Map<String, Help<Binary>> binarys = new HashMap<>();

    public boolean isVariable(String name) {
        return variables.containsKey(name);
    }

    public Help<Expression> variable(String name) {
        Help<Expression> r = variables.get(name);
        if (r == null)
            throw new DecsException("variable '%s' undef", name);
        return r;
    }

    public boolean isUnary(String name) {
        return unarys.containsKey(name);
    }

    public Help<Unary> unary(String name) {
        Help<Unary> r = unarys.get(name);
        if (r == null)
            throw new DecsException("unary '%s' undef", name);
        return r;
    }

    public boolean isBinary(String name) {
        return binarys.containsKey(name);
    }

    public Help<Binary> binary(String name) {
        Help<Binary> r = binarys.get(name);
        if (r == null)
            throw new DecsException("binary '%s' undef", name);
        return r;
    }

    public void variable(String name, Expression expression, String help) {
        variables.put(name, new Help<>(expression, help));
        unarys.remove(name);
        binarys.remove(name);
    }

    public void unary(String name, Unary unary, String help) {
        unarys.put(name, new Help<>(unary, help));
        variables.remove(name);
    }

    public void binary(String name, Binary binary, String help) {
        binarys.put(name, new Help<>(binary, help));
        variables.remove(name);
    }

}
