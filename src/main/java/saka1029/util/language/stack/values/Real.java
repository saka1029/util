package saka1029.util.language.stack.values;

public class Real implements Num {

    public final double value;

    private Real(double value) {
        this.value = value;
    }

    public static Real of(double value) {
        return new Real(value);
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj.getClass() == Real.class && value == ((Real)obj).value;
    }

    @Override
    public int hashCode() {
        return Double.hashCode(value);
    }

    @Override
    public int compareTo(Value o) {
        if (o.getClass() == Real.class)
            return Double.compare(value, ((Real)o).value);
        if (o.getClass() == Int.class)
            return Double.compare(value, ((Int)o).value);
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return Double.toString(value);
    }

    @Override
    public Value negate() {
        return of(-value);
    }

    @Override
    public Value add(Value right) {
        if (right instanceof Real)
            return of(value + ((Real)right).value);
        else if (right instanceof Int)
            return of(value + ((Int)right).value);
        throw new UnsupportedOperationException();
    }

    @Override
    public Value sub(Value right) {
        if (right instanceof Real)
            return of(value - ((Real)right).value);
        else if (right instanceof Int)
            return of(value - ((Int)right).value);
        throw new UnsupportedOperationException();
    }

    @Override
    public Value mul(Value right) {
        if (right instanceof Real)
            return of(value * ((Real)right).value);
        else if (right instanceof Int)
            return of(value * ((Int)right).value);
        throw new UnsupportedOperationException();
    }

    @Override
    public Value div(Value right) {
        if (right instanceof Real)
            return of(value / ((Real)right).value);
        else if (right instanceof Int)
            return of(value / ((Int)right).value);
        throw new UnsupportedOperationException();
    }

}
