package saka1029.util.language.stack.values;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class Chars implements Collection {

    public final int[] value;

    private Chars(int[] value) {
        this.value = value;
    }

    public static Chars of(String s) {
        return new Chars(s.codePoints().toArray());
    }

    public static Chars of(int[] value) {
        return new Chars(value.clone());
    }

    public static Chars of(int left, Chars right) {
        int length = right.value.length;
        int[] array = new int[length + 1];
        System.arraycopy(right.value, 0, array, 1, length);
        array[0] = left;
        return new Chars(array);
    }

    public static Chars of(Chars left, int right) {
        int length = left.value.length;
        int[] array = new int[length + 1];
        System.arraycopy(left.value, 0, array, 0, length);
        array[length] = right;
        return new Chars(array);
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj.getClass() == Chars.class && Arrays.equals(((Chars)obj).value, value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }

    @Override
    public int compareTo(Value o) {
        if (o.getClass() == Chars.class)
            return Arrays.compare(((Chars)o).value, value);
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return new String(value, 0, value.length);
    }

    @Override
    public Value add(Value right) {
        if (right.getClass() == Int.class)
            return of(this, ((Int)right).value);
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<Value> iterator() {
        return new Iterator<Value>() {

            int index = 0;

            @Override
            public boolean hasNext() {
                return index < value.length;
            }

            @Override
            public Value next() {
                return Int.of(value[index++]);
            }

        };
    }

    @Override
    public Builder builder() {
        return new Builder() {

            java.util.List<Integer> list = new ArrayList<>();

            @Override
            public Builder add(Value element) {
                list.add(((Int)element).value);
                return this;
            }

            @Override
            public Value build() {
                return of(list.stream().mapToInt(i -> i).toArray());
            }

        };
    }

}
