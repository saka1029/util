package util.language.stack.values;

import java.util.function.Consumer;
import java.util.function.Function;

public interface Collection extends Value, Iterable<Value> {

    interface Builder {
        Builder add(Value element);
        Value build();
    }

    Builder builder();

    default void repeat(Consumer<Value> repeater) {
        for (Value e : this)
            repeater.accept(e);
    }

    default Value map(Function<Value, Value> mapper) {
        Builder builder = builder();
        for (Value e : this)
            builder.add(mapper.apply(e));
        return builder.build();
    }

    default Value filter(Function<Value, Value> filter) {
        Builder builder = builder();
        for (Value e : this)
            if (((Bool)filter.apply(e)).value)
                builder.add(e);
        return builder.build();
    }

    default Value subList(Value start, Value length) {
        Builder builder = builder();
        Value max = start.add(length);
        Value i = Int.ZERO;
        for (Value e : this) {
            if (((Bool)i.ge(start).and(i.lt(max))).value)
                builder.add(e);
            i = i.add(Int.ONE);
        }
        return builder.build();
    }

}
