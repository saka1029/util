package saka1029.util.dentaku;

public class VectorException extends RuntimeException {
    public VectorException(String format, Object... arguments) {
        super(format.formatted(arguments));
    }

}
