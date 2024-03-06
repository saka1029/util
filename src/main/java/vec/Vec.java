package vec;

import java.util.Arrays;

public class Vec {
    final double[] elements;

    Vec(double[] elements) {
        this.elements = elements;
    }

    public static Vec of(double... elements) {
        return new Vec(elements);
    }

    public Vec append(Vec right) {
        double[] a = new double[size() + right.size()];
        System.arraycopy(elements, 0, a, 0, elements.length);
        System.arraycopy(right.elements, 0, a, elements.length, right.elements.length);
        return new Vec(a);
    }

    public int size() {
        return elements.length;
    }

    public double get(int index) {
        return elements[index];
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
}
