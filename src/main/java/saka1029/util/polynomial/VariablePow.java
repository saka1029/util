package saka1029.util.polynomial;

public class VariablePow implements Comparable<VariablePow> {
    final String name;
    final int pow;

    VariablePow(String name, int pow) {
        this.name = name;
        this.pow = pow;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof VariablePow v
            && v.name.equals(name)
            && v.pow == pow;
    }

    @Override
    public int hashCode() {
        return name.hashCode() << 4 | pow;
    }

    @Override
    public int compareTo(VariablePow r) {
        int c = name.compareTo(r.name);
        if (c != 0)
            return c;
        return Integer.compare(pow, r.pow);
    }

}
