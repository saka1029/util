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
    public String toString() {
        return name;
    }
}
