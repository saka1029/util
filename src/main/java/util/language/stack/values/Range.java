package util.language.stack.values;

import java.util.Iterator;
import java.util.Objects;

public class Range implements Collection {

    public final int start, end;

    private Range(int start, int end) {
        if (start > end)
            throw new IllegalArgumentException();
        this.start = start;
        this.end = end;
    }

    public static Range of(int start, int end) {
        return new Range(start, end);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (obj.getClass() != Range.class)
            return false;
        Range o = (Range)obj;
        return o.start == start && o.end == end;
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }

    @Override
    public String toString() {
        return "[" + start + " ... " + end + "]";
    }

    @Override
    public Value size() {
        return Int.of(end - start);
    }

    @Override
    public Value add(Value right) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int compareTo(Value o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<Value> iterator() {
        return new Iterator<Value>() {

            int i = start;

            @Override
            public boolean hasNext() {
                return i < end;
            }

            @Override
            public Value next() {
                return Int.of(i++);
            }

        };
    }

    @Override
    public Builder builder() {
        return new List.Builder();
    }

}
