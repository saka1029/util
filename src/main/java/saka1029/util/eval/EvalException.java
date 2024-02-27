package saka1029.util.eval;

public class EvalException extends RuntimeException {
    public EvalException(String message, Object... args) {
        super(message.formatted(args));
    }
    
}
