package util.language.stack.values;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Collectors;

import util.language.stack.core.Context;

public class List implements Collection {

    protected final Value[] value;

    private List(Value... values) {
        this.value = values;
    }

    public static List of(Value... values) {
        return new List(values.clone());
    }

    public static List of(List list, Value last) {
        int length = list.value.length;
        Value[] array = new Value[length + 1];
        System.arraycopy(list.value, 0, array, 0, length);
        array[length] = last;
        return new List(array);
    }

    public static List of(Value first, List list) {
        int length = list.value.length;
        Value[] array = new Value[length + 1];
        array[0] = first;
        System.arraycopy(list.value, 0, array, 1, length);
        return new List(array);
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj.getClass() == List.class && Arrays.equals(value, ((List)obj).value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }

    @Override
    public int compareTo(Value o) {
        if (o instanceof List)
            return Arrays.compare(value, ((List)o).value);
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return Arrays.stream(value)
            .map(e -> e.toString())
            .collect(Collectors.joining(" ", "[", "]"));
    }

    @Override
    public void run(Context c) {
        for (Value e : value)
            e.execute(c);
    }

    @Override
    public Value add(Value right) {
        return of(this, right);
    }

    @Override
    public Value size() {
        return Int.of(value.length);
    }

    @Override
    public Iterator<Value> iterator() {
        return Arrays.asList(value).iterator();
    }

    public static class Builder implements Collection.Builder {

        final java.util.List<Value> list = new ArrayList<>();

        @Override
        public util.language.stack.values.Collection.Builder add(Value element) {
            list.add(element);
            return this;
        }

        @Override
        public Value build() {
            return List.of(list.toArray(Value[]::new));
        }

    }

    @Override
    public util.language.stack.values.Collection.Builder builder() {
        return new Builder();
    }
}
