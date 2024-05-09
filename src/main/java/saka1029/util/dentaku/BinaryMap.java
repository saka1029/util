package saka1029.util.dentaku;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.function.BinaryOperator;
import java.util.stream.IntStream;
import static saka1029.util.dentaku.Value.*;

public class BinaryMap implements Binary {

    public final BinaryOperator<BigDecimal> baseOperator;

    BinaryMap(BinaryOperator<BigDecimal> baseOperator) {
        this.baseOperator = baseOperator;
    }

    public static BinaryMap of(BinaryOperator<BigDecimal> baseOperator) {
        return new BinaryMap(baseOperator);
    }

    @Override
    public BigDecimal[] apply(Context context, BigDecimal[] left, BigDecimal[] right) { 
        int lsize = left.length, rsize = right.length;
        if (lsize == 0)
            return right;
        else if (rsize == 0)
            return left;
        else if (lsize == 1)
            return Arrays.stream(right)
                .map(r -> baseOperator.apply(left[0], r))
                .toArray(Value::array);
        else if (rsize == 1)
            return Arrays.stream(left)
                .map(l -> baseOperator.apply(l, right[0]))
                .toArray(Value::array);
        else if (lsize == rsize)
            return IntStream.range(0, lsize)
                .mapToObj(i -> baseOperator.apply(left[i], right[i]))
                .toArray(Value::array);
        else
            throw new ValueException("map: Illegal length left=%d right=%d", lsize, rsize);
    }

    // @Override
    // public Unary insert() {
    //     return (context, argument) -> {
    //         int size = argument.length;
    //         if (size <= 1)
    //             return argument;
    //         BigDecimal d = argument[0];
    //         for (int i = 1; i < size; ++i)
    //             d = baseOperator.apply(d, argument[i]);
    //         return new BigDecimal[] {d};
    //     };
    // }

    @Override
    public Binary select() {
        return (context, left, right) -> {
            int lsize = left.length, rsize = right.length;
            if (lsize == 0)
                return right;
            else if (rsize == 0)
                return left;
            else if (lsize == 1)
                return Arrays.stream(right)
                    .filter(r -> b(baseOperator.apply(left[0], r)))
                    .toArray(Value::array);
            else if (rsize == 1)
                return Arrays.stream(left)
                    .filter(l -> b(baseOperator.apply(l, right[0])))
                    .toArray(Value::array);
            else if (lsize == rsize)
                return IntStream.range(0, lsize)
                    .filter(i -> b(baseOperator.apply(left[i], right[i])))
                    .mapToObj(i -> left[i])
                    .toArray(Value::array);
            else
                throw new ValueException("map: Illegal length left=%d right=%d", lsize, rsize);
        };
    }
}
