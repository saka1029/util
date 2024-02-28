package saka1029.util.eval;

public interface Func0 extends Func {

    double callfix(Context c);

    default double call(Context c, double... arguments) {
        if (arguments.length != 0)
            throw new EvalException("Must be 0 arguments");
        return callfix(c);
    }
    
}
