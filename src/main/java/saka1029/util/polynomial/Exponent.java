package saka1029.util.polynomial;

import java.util.Objects;

public class Exponent {
    public static final Exponent CONSTANT = Exponent.of(Variable.of("_"), 0);
    final Primary primary;
    final int pow;

    Exponent(Primary primary, int pow) {
        this.primary = primary;
        this.pow = pow;
    }

    public static Exponent of(Primary primary, int pow) {
        return new Exponent(primary, pow);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Exponent v
            && v.primary.equals(primary)
            && v.pow == pow;
    }

    @Override
    public int hashCode() {
        return Objects.hash(primary, pow);
    }

    @Override
    public String toString() {
        String p = primary.toString();
        if (primary instanceof Expression)
            p = "(" + p + ")";
        return p + "^" + pow;
    }
}
