package saka1029.util.decs;

import java.math.BigDecimal;

public interface Expression {
    BigDecimal[] eval(Context context);
}
