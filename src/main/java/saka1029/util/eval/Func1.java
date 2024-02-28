package saka1029.util.eval;

public interface Func1 extends Func {

    double callfix(Context c, double arg0);

    default double call(Context c, double... arguments) {
        if (arguments.length != 1)
            throw new EvalException("Must be 1 arguments");
        return callfix(c, arguments[0]);
    }
    
}
