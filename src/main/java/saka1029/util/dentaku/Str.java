package saka1029.util.dentaku;

public class Str<T> {
    public final T t;
    public final String string;

    Str(T t, String string) {
        this.t = t;
        this.string = string;
    }

    public static <T> Str<T> of(T t, String string) {
        return new Str<>(t, string);
    }

    @Override
    public String toString() {
        return string;
    }
}
