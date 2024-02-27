package saka1029.util.eval;

public class Variable implements Expression {
    public final String name;

    Variable(String name) {
        this.name = name;
    }

    public static Variable of(String name) {
        return new Variable(name);
    }

    @Override
    public double eval(Context c) {
        return c.get(name).eval(c);
    }
    
    @Override
    public String toString() {
        return name;
    }
}
