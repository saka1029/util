package saka1029.util.decs;

public class Def<T> {
    final T expression;
    final boolean builtin;
    final String help;

    Def(T expression, boolean builtin, String help) {
        this.expression = expression;
        this.builtin = builtin;
        this.help = help;
    }
}
