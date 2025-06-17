package saka1029.util.decs;

public class Definition<T> {
    final T expression;
    final String help;

    Definition(T expression, String help) {
        this.expression = expression;
        this.help = help;
    }
}
