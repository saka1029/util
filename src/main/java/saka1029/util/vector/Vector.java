package saka1029.util.vector;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class Vector implements Expression {
    public static final MathContext MATH_CONTEXT = MathContext.DECIMAL128;
    public static final Vector NaN = new Vector();

    private final BigDecimal[] elements;

    Vector(BigDecimal... elements) {
        this.elements = elements;
    }

    public static Vector of(BigDecimal... elements) {
        if (elements.length <= 0)
            throw new IllegalArgumentException("elements");
        return new Vector(elements);
    }

    public static BigDecimal number(double value) {
        return new BigDecimal(value, MATH_CONTEXT);
    }

    public static BigDecimal number(String value) {
        // MathContextが無い点に注意。あると大きい桁数の場合、有効桁34桁に丸められてしまう。
        return new BigDecimal(value);
    }

    public static BigDecimal divide(BigDecimal left, BigDecimal right) {
        return left.divide(right, MATH_CONTEXT);
    }

    public static BigDecimal pow(BigDecimal left, BigDecimal right) {
        return Vector.number(Math.pow(left.doubleValue(), right.doubleValue()));
    }

    public static Vector of(double... elements) {
        BigDecimal[] a = Arrays.stream(elements)
            .mapToObj(s -> number(s))
            .toArray(BigDecimal[]::new);
        return of(a);
    }

    public static Vector iota(int n) {
        BigDecimal[] a = new BigDecimal[n];
        for (int i = 0; i < n; ++i)
            a[i] = number(i + 1);
        return of(a);
    }

    public static Vector iota0(int n) {
        BigDecimal[] a = new BigDecimal[n];
        for (int i = 0; i < n; ++i)
            a[i] = number(i);
        return of(a);
    }

    public int length() {
        return elements.length;
    }

    public BigDecimal get(int index) {
        return elements[index];
    }

    @Override
    public Vector eval(Context context) {
        return this;
    }

    public Vector append(Vector right) {
        int lSize = elements.length, rSize = right.elements.length;
        BigDecimal[] n = new BigDecimal[lSize + rSize];
        System.arraycopy(elements, 0, n, 0, lSize);
        System.arraycopy(right.elements, 0, n, lSize, rSize);
        return new Vector(n);
    }

    public Vector apply(UnaryOperator<BigDecimal> operator) {
        int size = elements.length;
        BigDecimal[] r = new BigDecimal[size];
        for (int i = 0; i < size; ++i)
            r[i] = operator.apply(elements[i]);
        return new Vector(r);
    }

    public Vector apply(BinaryOperator<BigDecimal> operator, Vector right) {
        int lSize = elements.length, rSize = right.elements.length;
        BigDecimal[] a;
        if (lSize == 1) {
            a = new BigDecimal[rSize];
            for (int i = 0; i < rSize; ++i)
                a[i] = operator.apply(elements[0], right.elements[i]);
        } else if (rSize == 1) {
            a = new BigDecimal[lSize];
            for (int i = 0; i < lSize; ++i)
                a[i] = operator.apply(elements[i], right.elements[0]);
        } else if (rSize == lSize) {
            a = new BigDecimal[lSize];
            for (int i = 0; i < lSize; ++i)
                a[i] = operator.apply(elements[i], right.elements[i]);
        } else
            throw new VectorException("Illegal vector length %d and %d",
                elements.length, right.elements.length);
        return new Vector(a);
    }
    
    public Vector insert(BinaryOperator<BigDecimal> operator) {
        int size = elements.length;
        BigDecimal r = elements[0];
        for (int i = 1; i < size; ++i)
            r = operator.apply(r, elements[i]);
        return new Vector(r);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(elements);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Vector v && Arrays.equals(v.elements, elements);
    }

    @Override
    public String toString() {
        return Arrays.stream(elements)
            .map(e -> e.toString())
            .collect(Collectors.joining(" "));
    }
}
