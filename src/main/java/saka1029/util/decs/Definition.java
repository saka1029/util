package saka1029.util.decs;

import java.util.HashMap;
import java.util.Map;

public class Definition {

    static class Entry {
        Def<Expression> variable;     
        Def<Unary> unary;     
        Def<Binary> binary;     
    }

    final Map<String, Entry> definitions = new HashMap<>();

    Entry get(String name) {
        return definitions.computeIfAbsent(name, key -> new Entry());
    }

    public void variable(String name, boolean builtin, String help, Expression e) {
        Entry entry = get(name);
        entry.variable = new Def<>(e, builtin, help);
    }

    public Def<Expression> variable(String name) {
        if (!definitions.containsKey(name))
            return null;
        return definitions.get(name).variable;
    }

    public void unary(String name, boolean builtin, String help, Unary e) {
        Entry entry = get(name);
        entry.unary = new Def<>(e, builtin, help);
    }

    public Def<Unary> unary(String name) {
        if (!definitions.containsKey(name))
            return null;
        return definitions.get(name).unary;
    }

    public void binary(String name, boolean builtin, String help, Binary e) {
        Entry entry = get(name);
        entry.binary = new Def<>(e, builtin, help);
    }

    public Def<Binary> binary(String name) {
        if (!definitions.containsKey(name))
            return null;
        return definitions.get(name).binary;
    }

}
