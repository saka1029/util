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

    public static interface Undo extends AutoCloseable {
        void close();
    }

    static <T> void put(Map<String, T> map, String key, T value) {
        if (value == null)
            map.remove(key);
        else
            map.put(key, value);
    }

    public void variable(String name, Expression expression, String help) {
        put(variables, name, new Help<>(expression, help));
        put(unarys, name, null);
        put(binarys, name, null);
    }

    public Undo variableTemp(String name, Expression expression, String help) {
        Help<Expression> oldVariable = variables.get(name);
        Help<Unary> oldUnary = unarys.get(name);
        Help<Binary> oldBinary = binarys.get(name);
        variable(name, expression, help);
        return () -> {
            put(variables, name, oldVariable);
            put(unarys, name, oldUnary);
            put(binarys, name, oldBinary);
        };
    }

    public void unary(String name, Unary unary, String help) {
        put(unarys, name, new Help<>(unary, help));
        put(variables, name, null);
    }

    public void binary(String name, Binary binary, String help) {
        put(binarys, name, new Help<>(binary, help));
        put(variables, name, null);
    }

}
