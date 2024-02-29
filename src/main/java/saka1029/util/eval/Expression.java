package saka1029.util.eval;

public interface Expression {

    double eval(Context c);

    default String string() {
        throw new RuntimeException("No string() method");
    }

}