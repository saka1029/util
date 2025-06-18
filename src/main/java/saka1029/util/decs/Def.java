package saka1029.util.decs;

public class Def<T> {
    final T expression;
    final String help;

    Def(T expression, String help) {
        this.expression = expression;
        this.help = help;
    }
}
