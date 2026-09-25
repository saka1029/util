package saka1029.util.declisp;

import java.util.HashMap;
import java.util.Map;

public class Env {
    final Map<Symbol, Expr> map = new HashMap<>();
    final Env prev;
    public Env() { this.prev = null; }
    public Env(Env prev) { this.prev = prev; }

    public Symbol define(Symbol key, Expr value) {
        map.put(key, value);
        return key;
    }

    public Expr get(Symbol key) {
        for (Env e = this; e != null; e = e.prev) {
            Expr value = e.map.get(key);
            if (value != null)
                return value;
        }
        throw new DecLispException("Env.get(): symbol '%s' not found", key);
    }

    public Expr set(Symbol key, Expr value) {
        for (Env e = this; e != null; e = e.prev) {
            if (e.map.containsKey(key)) {
                e.map.put(key, value);
                return value;
            }
        }
        throw new DecLispException("Env.set(): symbol '%s' not found", key);
    }
    @Override
    public String toString() {
        return map.toString() + (prev == null ? "" : " -> " + prev.toString());
    }
}