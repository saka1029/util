package saka1029.util.language.stack.values;

public class Int implements Num {

    public static final Int ZERO = of(0);
    public static final Int ONE = of(1);

    public final int value;

    private Int(int value) {
        this.value = value;
    }

    public static Int of(int value) {
        return new Int(value);
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj.getClass() == Int.class && ((Int)obj).value == value;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }

    @Override
    public int compareTo(Value o) {
        if (o.getClass() == Int.class)
            return Integer.compare(value, ((Int)o).value);
        else if (o.getClass() == Real.class)
            return -o.compareTo(this);
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }

    @Override
    public Value not() {
        return of(~value);
    }

    @Override
    public Value and(Value right) {
        if (right.getClass() != Int.class)
            throw new RuntimeException();
        return of(value & ((Int)right).value);
    }

    @Override
    public Value or(Value right) {
        if (right.getClass() != Int.class)
            throw new RuntimeException();
        return of(value | ((Int)right).value);
    }

    @Override
    public Value xor(Value right) {
        if (right.getClass() != Int.class)
            throw new RuntimeException();
        return of(value ^ ((Int)right).value);
    }

    @Override
    public Value negate() {
        return of(-value);
    }

    @Override
    public Value add(Value right) {
        if (right.getClass() == Int.class)
            return of(value + ((Int)right).value);
        else if (right.getClass() == Real.class)
            return Real.of(value + ((Real)right).value);
        else if (right.getClass() == List.class)
            return List.of(this, (List)right);
        else if (right.getClass() == Chars.class)
            return Chars.of(value, (Chars)right);
        throw new UnsupportedOperationException();
    }

    @Override
    public Value sub(Value right) {
        if (right.getClass() == Int.class)
            return of(value - ((Int)right).value);
        else if (right.getClass() == Real.class)
            return Real.of(value - ((Real)right).value);
        throw new UnsupportedOperationException();
    }

    @Override
    public Value mul(Value right) {
        if (right.getClass() == Int.class)
            return of(value * ((Int)right).value);
        else if (right.getClass() == Real.class)
            return Real.of(value * ((Real)right).value);
        throw new UnsupportedOperationException();
    }

    @Override
    public Value div(Value right) {
        if (right.getClass() == Int.class)
            return of(value / ((Int)right).value);
        else if (right.getClass() == Real.class)
            return Real.of(value / ((Real)right).value);
        throw new UnsupportedOperationException();
    }

    @Override
    public Value mod(Value right) {
        if (right.getClass() == Int.class)
            return of(value % ((Int)right).value);
        throw new UnsupportedOperationException();
    }

    @Override
    public Value range(Value end) {
        if (end.getClass() == Int.class)
            return Range.of(value, ((Int)end).value);
        throw new UnsupportedOperationException();
    }

}
