package saka1029.util.dentaku;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Operators {

    private Map<String, Str<Unary>> uops = new HashMap<>();
    private Map<String, Str<Binary>> bops = new HashMap<>();
    // private Map<String, Str<High>> hops = new HashMap<>();

    private Operators() {
    }

    public static Operators of() {
        return new Operators();
    }

    public Unary unary(String name) {
        Str<Unary> e = uops.get(name);
        return e != null ? e.op : null;
    }

    public String unaryString(String name) {
        Str<Unary> e = uops.get(name);
        return e != null ? e.string : null;
    }

    public void unary(String name, Unary value, String string) {
        uops.put(name, Str.of(value, string));
    }

    public List<String> unarys() {
        return uops.values().stream()
            .map(e -> e.string)
            .toList();
    }

    public Binary binary(String name) {
        Str<Binary> e = bops.get(name);
        return e != null ? e.op : null;
    }

    public List<String> binarys() {
        return bops.values().stream()
            .map(e -> e.string)
            .toList();
    }

    public String binaryString(String name) {
        Str<Binary> e = bops.get(name);
        return e != null ? e.string : null;
    }

    public void binary(String name, Binary value, String string) {
        bops.put(name, Str.of(value, string));
    }
}
