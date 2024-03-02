package saka1029.util.cal;

public class UserFunc implements Func {
    final Expression body;
    final String[] names;

    UserFunc(Expression body, String... names) {
        this.body = body;
        this.names = names;
    }

    public static UserFunc of(Expression body, String... names) {
        return new UserFunc(body, names);
    }

    @Override
    public double call(Context c, double... arguments) {
        int length = names.length;
        if (arguments.length != length)
            throw new EvalException("Expected %d arguments but %d", names.length, arguments.length);
        Context child = c.child();
        for (int i = 0; i < length; ++i)
            child.variable(names[i], Number.of(arguments[i]));
        return body.eval(child);
    }
}
