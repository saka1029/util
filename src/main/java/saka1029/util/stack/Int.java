package saka1029.util.stack;

public class Int extends Value {

    public final int value;
    public static final Int ZERO = new Int(0);
    public static final Int ONE = new Int(1);

    Int(int value) {
        this.value = value;
    }

    public static Int of(int value) {
        return new Int(value);
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof Int other
            && value == other.value;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }
}
