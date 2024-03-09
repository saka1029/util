package saka1029.util.vec;

import java.util.Arrays;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.Collectors;

public class Vec implements Expression {
    public static Vec NAN = new Vec();

    final double[] elements;

    private Vec(double... elements) {
        this.elements = elements;
    }

    public static Vec of(double... elements) {
        if (elements.length == 0)
            throw new VecException("No elements");
        return new Vec(elements.clone());
    }

    @Override
    public Vec eval(Context context) {
        return this;
    }

    public Vec append(Vec right) {
        int lsize = elements.length, rsize = right.elements.length;
        double[] a = new double[lsize + rsize];
        System.arraycopy(elements, 0, a, 0, lsize);
        System.arraycopy(right.elements, 0, a, lsize, rsize);
        return new Vec(a);
    }

    public static Vec calculate(DoubleUnaryOperator op, Vec vec) {
        int size = vec.elements.length;
        double[] a = new double[size];
        for (int i = 0; i < size; ++i)
            a[i] = op.applyAsDouble(vec.elements[i]);
        return new Vec(a);
    } 

    public static Vec calculate(DoubleBinaryOperator op, Vec left, Vec right) {
        int lsize = left.elements.length, rsize = right.elements.length;
        double[] a;
        if (lsize == 1) {
            a = new double[rsize];
            double e = left.elements[0];
            for (int i = 0; i < rsize; ++i)
                a[i] = op.applyAsDouble(e, right.elements[i]);
        } else if (rsize == 1) {
            a = new double[lsize];
            double e = right.elements[0];
            for (int i = 0; i < lsize; ++i)
                a[i] = op.applyAsDouble(left.elements[i], e);
        } else if (lsize == rsize) {
            a = new double[lsize];
            for (int i = 0; i < lsize; ++i)
                a[i] = op.applyAsDouble(left.elements[i], right.elements[i]);
        } else
            throw new VecException("Vec size mismatch %d and %d", lsize, rsize);
        return new Vec(a);
    } 

    public static Vec insert(DoubleBinaryOperator op, Vec vec) {
        int size = vec.elements.length;
        double r = vec.elements[0];
        for (int i = 1; i < size; ++i)
            r = op.applyAsDouble(r, vec.elements[i]);
        return new Vec(r);
    }

    public static Vec iota(double n) {
        double[] a = new double[(int)n];
        for (int i = 0; i < n; ++i)
            a[i] = i;
        return new Vec(a);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(elements);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Vec other = (Vec) obj;
        if (!Arrays.equals(elements, other.elements))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return Arrays.stream(elements)
            .mapToObj(d -> Double.toString(d).replaceFirst("\\.0$", ""))
            .collect(Collectors.joining(" "));
    }
}
