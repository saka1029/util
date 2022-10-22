package saka1029.util.calculator;

public class ParseException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    ParseException(String format, Object... args) {
        super(format.formatted(args));
    }
}
