package saka1029.util.dentaku;

public class Str<T> {
    public final T op;
    public final String string;

    private Str(T op, String string) {
        this.op = op;
        this.string = string;
    }

    public static <T> Str<T> of(T op, String string) {
        return new Str<>(op, string);
    }

    @Override
    public final String toString() {
        return string;
    }
}
