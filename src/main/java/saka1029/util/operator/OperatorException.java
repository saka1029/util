package saka1029.util.operator;

public class OperatorException extends RuntimeException {
    public OperatorException(String message, Object... args) {
        super(message.formatted(args));
    }
}
