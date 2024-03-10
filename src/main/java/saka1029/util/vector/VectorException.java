package saka1029.util.vector;

public class VectorException extends RuntimeException {
    public VectorException(String format, Object... arguments) {
        super(format.formatted(arguments));
    }

}
