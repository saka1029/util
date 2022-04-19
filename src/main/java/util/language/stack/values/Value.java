package util.language.stack.values;

import java.util.function.Consumer;
import java.util.function.Function;

import util.language.stack.core.Context;
import util.language.stack.core.Executable;

public interface Value extends Executable, Comparable<Value> {

    default void execute(Context c) { c.push(this); }

    default int compareTo(Value o) { throw new UnsupportedOperationException(); }

    // for Value
    default void run(Context c) { execute(c); }
    default Value eq(Value right) { return Bool.of(equals(right)); }
    default Value ne(Value right) { return Bool.of(!equals(right)); }
    default Value add(Value right) { return Bool.of(!equals(right)); }

    // for Bool, Int
    default Value not() { throw new UnsupportedOperationException(); }
    default Value and(Value right) { throw new UnsupportedOperationException(); }
    default Value or(Value right) { throw new UnsupportedOperationException(); }
    default Value xor(Value right) { throw new UnsupportedOperationException(); }

    // for Int, Real
    default Value lt(Value right) { return Bool.of(compareTo(right) < 0); }
    default Value le(Value right) { return Bool.of(compareTo(right) <= 0); }
    default Value gt(Value right) { return Bool.of(compareTo(right) > 0); }
    default Value ge(Value right) { return Bool.of(compareTo(right) >= 0); }

    default Value negate() { throw new UnsupportedOperationException(); }
    default Value sub(Value right) { throw new UnsupportedOperationException(); }
    default Value mul(Value right) { throw new UnsupportedOperationException(); }
    default Value div(Value right) { throw new UnsupportedOperationException(); }
    default Value mod(Value right) { throw new UnsupportedOperationException(); }

    // for Collection
    default Value size() { throw new UnsupportedOperationException(); }
    default void repeat(Consumer<Value> repeater) { throw new UnsupportedOperationException(); }
    default Value map(Function<Value, Value> mapper) { throw new UnsupportedOperationException(); }
    default Value filter(Function<Value, Value> filter) { throw new UnsupportedOperationException(); }
    default Value subList(Value start, Value length) { throw new UnsupportedOperationException(); }
    default Value range(Value end) { throw new UnsupportedOperationException(); }

}
