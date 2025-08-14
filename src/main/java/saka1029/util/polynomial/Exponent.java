package saka1029.util.polynomial;

public class Exponent {
    final Primary primary;
    final int pow;

    Exponent(Primary primary, int pow) {
        this.primary = primary;
        this.pow = pow;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Exponent v
            && v.primary.equals(primary)
            && v.pow == pow;
    }

    @Override
    public int hashCode() {
        return primary.hashCode() << 4 | pow;
    }
}
