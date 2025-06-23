package saka1029.util.decs;

public class Help<T> {
    public final T expression;
    public final String string;

    public Help(T expression, String string) {
        this.expression = expression;
        this.string = string;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Help h
            && h.expression.equals(expression)
            && h.string.equals(string);
    }
}
