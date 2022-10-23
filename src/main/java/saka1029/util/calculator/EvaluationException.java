package saka1029.util.calculator;

public class EvaluationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    EvaluationException(String format, Object... args) {
        super(format.formatted(args));
    }
}
