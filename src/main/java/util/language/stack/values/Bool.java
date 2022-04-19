package util.language.stack.values;

public class Bool implements Value {

    public static final Bool TRUE = new Bool(true);
    public static final Bool FALSE = new Bool(false);

    public final boolean value;

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
    public int compareTo(Value o) {
        if (o.getClass() != Bool.class)
            throw new RuntimeException();
        return Boolean.compare(value, ((Bool)o).value);
    }

    @Override
    public String toString() {
        return Boolean.toString(value);
    }

    @Override
    public Value not() {
        return of(!value);
    }

    @Override
    public Value and(Value right) {
        if (right.getClass() == Bool.class)
            return of(value && ((Bool)right).value);
        throw new UnsupportedOperationException();
    }

    @Override
    public Value or(Value right) {
        if (right.getClass() == Bool.class)
            return of(value || ((Bool)right).value);
        throw new UnsupportedOperationException();
    }

    @Override
    public Value xor(Value right) {
        if (right.getClass() == Bool.class)
            return of(value ^ ((Bool)right).value);
        throw new UnsupportedOperationException();
    }

    @Override
    public Value add(Value right) {
        if (right.getClass() == List.class)
            return List.of(this, (List)right);
        throw new UnsupportedOperationException();
    }

}
