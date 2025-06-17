package saka1029.util.decs;

import java.math.BigDecimal;

public interface Unary {
    BigDecimal[] apply(Context context, BigDecimal[] argument);
}
