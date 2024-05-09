package saka1029.util.dentaku;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.function.UnaryOperator;
import static saka1029.util.dentaku.Value.*;

public class UnaryMap implements Unary {
    public final UnaryOperator<BigDecimal> baseOperator;

    UnaryMap(UnaryOperator<BigDecimal> baseOperator) {
        this.baseOperator = baseOperator;
    }

    public static UnaryMap of(UnaryOperator<BigDecimal> baseOperator) {
        return new UnaryMap(baseOperator);
    }

    @Override
    public BigDecimal[] apply(Context context, BigDecimal[] argument) {
        return Arrays.stream(argument)
            .map(baseOperator)
            .toArray(Value::array);
    }

    @Override
    public Unary select() {
        return (context, argument) ->
            Arrays.stream(argument)
                .filter(d -> b(baseOperator.apply(d)))
                .toArray(Value::array);
    }
}
