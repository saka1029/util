package saka1029.util.eval;

public interface Func extends Expression {

    double call(Context c, double... arguments);

    default double eval(Context c) {
        throw new EvalException("can't eval");
    }
}
