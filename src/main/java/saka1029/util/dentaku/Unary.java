package saka1029.util.dentaku;

import java.math.BigDecimal;

public interface Unary {
    BigDecimal[] apply(Context context, BigDecimal[] argument);

    default Unary select() {
        throw new ValueException("Cannot select");
    }
}
