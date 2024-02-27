package saka1029.util.eval;

public class Funcall implements Expression {
    final String func;
    final Expression[] arguments;
    
    Funcall(String func, Expression... arguments) {
        this.func = func;
        this.arguments = arguments;
    }

    @Override
    public double eval(Context c) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'eval'");
    }

}
