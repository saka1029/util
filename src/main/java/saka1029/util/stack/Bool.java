package saka1029.util.stack;

public class Bool implements ValueInstruction {

    public final boolean value;
    public static final Bool TRUE = new Bool(true);
    public static final Bool FALSE = new Bool(false);

    private Bool(boolean value) {
        this.value = value;
    }

    public static Bool of(boolean value) {
        return value ? TRUE : FALSE;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

    @Override
    public int hashCode() {
        return Boolean.hashCode(value);
    }

    @Override
    public String toString() {
        return Boolean.toString(value);
    }

}
