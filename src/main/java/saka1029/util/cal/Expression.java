package saka1029.util.cal;

public interface Expression {

    double eval(Context c);

    default String string() {
        throw new RuntimeException("No string() method");
    }

}