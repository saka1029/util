package saka1029.util.cal;

public interface Func3 extends Func {

    double callfix(Context c, double arg0, double arg1, double arg2);

    default double call(Context c, double... arguments) {
        if (arguments.length != 3)
            throw new EvalException("Must be 3 arguments");
        return callfix(c, arguments[0], arguments[1], arguments[2]);
    }
    
}
