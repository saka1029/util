package saka1029.util.eval;

public interface Func2 extends Func {

    double callfix(Context c, double arg0, double arg1);

    default double call(Context c, double... arguments) {
        if (arguments.length != 2)
            throw new EvalException("Must be 2 arguments");
        return callfix(c, arguments[0], arguments[1]);
    }
    
}
