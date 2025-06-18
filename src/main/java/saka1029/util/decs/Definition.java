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

    public void variable(String name, String help, Expression e) {
        Entry entry = definitions.computeIfAbsent(name, key -> new Entry());
        entry.variable = new Def<>(e, help);
        entry.unary = null;
        entry.binary = null;
    }

    public Def<Expression> variable(String name) {
        Entry entry = definitions.get(name);
        if (entry == null || entry.variable == null)
            throw new DecsException("variable '%s' not defined", name);
        return entry.variable;
    }

    public void unary(String name, String help, Unary e) {
        Entry entry = definitions.computeIfAbsent(name, key -> new Entry());
        entry.unary = new Def<>(e, help);
        entry.variable = null;
    }

    public Def<Unary> unary(String name) {
        Entry entry = definitions.get(name);
        if (entry == null || entry.unary == null)
            throw new DecsException("unary '%s' not defined", name);
        return entry.unary;
    }

    public void binary(String name, String help, Binary e) {
        Entry entry = definitions.computeIfAbsent(name, key -> new Entry());
        entry.binary = new Def<>(e, help);
        entry.variable = null;
    }

    public Def<Binary> binary(String name) {
        Entry entry = definitions.get(name);
        if (entry == null || entry.binary == null)
            throw new DecsException("unary '%s' not defined", name);
        return entry.binary;
    }

}
