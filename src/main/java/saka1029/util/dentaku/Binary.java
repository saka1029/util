package saka1029.util.dentaku;

import java.math.BigDecimal;

public interface Binary {

    BigDecimal[] apply(Context context, BigDecimal[] left, BigDecimal[] right);

    default Binary select() {
        throw new ValueException("Cannot select");
    }
}
