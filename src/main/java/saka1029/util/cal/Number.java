package saka1029.util.cal;

public class Number implements Expression {
    public final double value;

    Number(double value) {
        this.value = value;
    }

    public static Number of(double value) {
        return new Number(value);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Number n && value == n.value;
    }

    @Override
    public double eval(Context c) {
        return value;
    }

    @Override
    public String string() {
        return Double.toString(value).replaceFirst("\\.0$", "");
    }
}
