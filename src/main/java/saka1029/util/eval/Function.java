package saka1029.util.eval;

public class Function implements Expression {
    public final String[] argumentNames;
    final Expression body;

    Function(Expression body, String... argumentNames) {
        this.body = body;
        this.argumentNames = argumentNames;
    }

    @Override
    public double eval(Context c) {
        return body.eval(c);
    }
    
}
