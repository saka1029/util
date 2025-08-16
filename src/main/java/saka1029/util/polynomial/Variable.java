package saka1029.util.polynomial;

public class Variable implements Primary {
    final String name;

    Variable(String name) {
        this.name = name;
    }

    public static Variable of(String name) {
        return new Variable(name);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Variable v && v.name.equals(name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }
}
