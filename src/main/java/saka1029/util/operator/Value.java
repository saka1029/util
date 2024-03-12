package saka1029.util.operator;

import java.math.BigDecimal;
import java.math.MathContext;

public class Value {

    public static MathContext MATH_CONTEXT = MathContext.DECIMAL128;

    public static BigDecimal number(String s) {
        return new BigDecimal(s);
    }
}
