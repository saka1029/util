package saka1029.util.decs;

import java.math.BigDecimal;

public interface Binary {
    BigDecimal[] apply(Context context, BigDecimal[] left, BigDecimal[] right);
}
