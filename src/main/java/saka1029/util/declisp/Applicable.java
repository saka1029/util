package saka1029.util.declisp;

public interface Applicable extends Expr {

    Expr apply(Expr args, Env env);

}
