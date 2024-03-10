package saka1029.util.vector;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Vector implements Expression {
    public static final Vector NaN = new Vector();

    private final BigDecimal[] elements;

    Vector(BigDecimal... elements) {
        this.elements = elements;
    }

    static Vector of(BigDecimal... elements) {
        if (elements.length <= 0)
            throw new IllegalArgumentException("elements");
        return new Vector(elements);
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

    public Vector apply(Unary operator) {
        int size = elements.length;
        BigDecimal[] r = new BigDecimal[size];
        for (int i = 0; i < size; ++i)
            r[i] = operator.apply(elements[i]);
        return new Vector(r);
    }

    public Vector apply(Binary operator, Vector right) {
        int lSize = elements.length, rSize = elements.length;
        BigDecimal[] n;
        if (lSize == 1) {
            n = new BigDecimal[rSize + 1];
            for (int i = 0; i <= rSize; ++i)
                n[i] = operator.apply(elements[0], right.elements[i]);
        } else if (rSize == 1) {
            n = new BigDecimal[lSize + 1];
            for (int i = 0; i <= lSize; ++i)
                n[i] = operator.apply(elements[i], right.elements[0]);
        } else if (rSize == lSize) {
            int size = lSize + rSize;
            n = new BigDecimal[size];
            for (int i = 0; i < size; ++i)
                n[i] = operator.apply(elements[i], right.elements[i]);
        } else
            throw new VectorException("Illegal vector length %d and %d",
                elements.length, right.elements.length);
        return new Vector(n);
    }
    
    public Vector insert(Binary operator) {
        int size = elements.length;
        BigDecimal r = elements[0];
        for (int i = 1; i < size; ++i)
            r = operator.apply(r, elements[i]);
        return new Vector(r);
    }

    @Override
    public String toString() {
        return Arrays.stream(elements)
            .map(e -> e.toString())
            .collect(Collectors.joining(" "));
    }
}
