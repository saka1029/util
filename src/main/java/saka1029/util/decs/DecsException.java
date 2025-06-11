package saka1029.util.decs;

public class DecsException extends RuntimeException {
    public DecsException(String message, Object... args) {
        super(message.formatted(args));
    }
}