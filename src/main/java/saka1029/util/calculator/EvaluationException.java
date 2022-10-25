package saka1029.util.calculator;

public class EvaluationException extends Exception {
    private static final long serialVersionUID = 1L;

    public EvaluationException(String format, Object... args) {
        super(format.formatted(args));
    }
}
