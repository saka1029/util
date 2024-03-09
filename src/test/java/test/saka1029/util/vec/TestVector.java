package test.saka1029.util.vec;

public class TestVector {
    interface Vec {
        Vec append(Vec right);
    }

    static class Number implements Vec {
        public final double value;

        Number(double value) {
            this.value = value;
        }

        @Override
        public Vec append(Vec right) {
            return switch (right) {
                case Number n -> Array.of(value, n.value);
                case Array a -> a.appendLeft(this);
                default -> throw new RuntimeException();
            };
        }
    }
    
    static class Array implements Vec {
        final double[] elements;

        Array(double... elements) {
            this.elements = elements;
        }

        public static Array of(double... elements) {
            return new Array(elements);
        }

        public int size() {
            return elements.length;
        }

        Array append(Array right) {
            double[] n = new double[size() + right.size()];
            System.arraycopy(elements, 0, n, 0, size());
            System.arraycopy(right.elements, 0, n, size(), right.size());
            return new Array(n);
        }

        Array appendRight(Number right) {
            double[] n = new double[size() + 1];
            System.arraycopy(elements, 0, n, 0, size());
            n[size()] = right.value;
            return new Array(n);
        }

        Array appendLeft(Number left) {
            double[] n = new double[1 + size()];
            n[0] = left.value;
            System.arraycopy(elements, 0, n, 1, size());
            return new Array(n);
        }

        @Override
        public Vec append(Vec right) {
            return switch (right) {
                case Number n -> appendRight(n);
                case Array a -> append(a);
                default -> throw new RuntimeException();
            };
        }
    }

}
