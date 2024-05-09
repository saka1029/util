package saka1029.util.dentaku;

import java.math.BigDecimal;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

public class UnaryInsert implements Unary {
    public final BinaryOperator<BigDecimal> baseOperator;
    public final UnaryOperator<BigDecimal> singleOperator;

    UnaryInsert(BinaryOperator<BigDecimal> baseOperator, UnaryOperator<BigDecimal> singleOperator) {
        this.baseOperator = baseOperator;
        this.singleOperator = singleOperator;
    }

    public static UnaryInsert of(BinaryOperator<BigDecimal> baseOperator, UnaryOperator<BigDecimal> singleOperator) {
        return new UnaryInsert(baseOperator, singleOperator);
    }

    public static UnaryInsert of(BinaryOperator<BigDecimal> baseOperator) {
        return new UnaryInsert(baseOperator, UnaryOperator.identity());
    }

    @Override
    public BigDecimal[] apply(Context context, BigDecimal[] argument) {
        int size = argument.length;
        if (size < 1)
            return argument;
        if (size == 1)
            return new BigDecimal[] {singleOperator.apply(argument[0])};
        BigDecimal result = argument[0];
        for (int i = 1; i < size; ++i)
            result = baseOperator.apply(result, argument[i]);
        return new BigDecimal[] {result};
    }
}
