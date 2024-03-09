package saka1029.util.vec;

public class VecException extends RuntimeException {
    public VecException(String message, Object... args) {
        super(message.formatted(args));
    }
}
