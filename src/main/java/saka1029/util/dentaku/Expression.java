package saka1029.util.dentaku;

import java.math.BigDecimal;

public interface Expression {
    BigDecimal[] eval(Context context);
}
