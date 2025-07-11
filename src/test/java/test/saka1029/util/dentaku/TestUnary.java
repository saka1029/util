package test.saka1029.util.dentaku;

import org.junit.Test;
import saka1029.util.dentaku.Context;
import saka1029.util.dentaku.UnaryMap;
import saka1029.util.dentaku.UnaryInsert;
import static org.junit.Assert.assertArrayEquals;
import static saka1029.util.dentaku.Value.*;
import java.math.BigDecimal;

public class TestUnary {

    @Test
    public void testMap() {
        Context c = Context.of();
        assertArrayEquals(array("-1 -2 -3"), UnaryMap.of(BigDecimal::negate).apply(c, array("1 2 3")));
        assertArrayEquals(array("-1"), UnaryMap.of(BigDecimal::negate).apply(c, array("1")));
        assertArrayEquals(EMPTY, UnaryMap.of(BigDecimal::negate).apply(c, EMPTY));
    }

    @Test
    public void testReduce() {
        Context c = Context.of();
        assertArrayEquals(array("6"), UnaryInsert.of(BigDecimal::add, BigDecimal.ZERO).apply(c, array("1 2 3")));
        assertArrayEquals(array("1"), UnaryInsert.of(BigDecimal::add, BigDecimal.ZERO).apply(c, array("1")));
        assertArrayEquals(array("0"), UnaryInsert.of(BigDecimal::add, BigDecimal.ZERO).apply(c, EMPTY));
    }

    @Test
    public void testReduceSubtract() {
        Context c = Context.of();
        assertArrayEquals(array("-4"), UnaryInsert.of(BigDecimal::subtract, BigDecimal.ZERO, BigDecimal::negate).apply(c, array("1 2 3")));
        assertArrayEquals(array("-1"), UnaryInsert.of(BigDecimal::subtract, BigDecimal.ZERO, BigDecimal::negate).apply(c, array("1 2")));
        assertArrayEquals(array("-1"), UnaryInsert.of(BigDecimal::subtract, BigDecimal.ZERO, BigDecimal::negate).apply(c, array("1")));
        assertArrayEquals(array("0"), UnaryInsert.of(BigDecimal::subtract, BigDecimal.ZERO, BigDecimal::negate).apply(c, EMPTY));
    }

    // @Test
    // public void testReduceDivide() {
    //     Context c = Context.of();
    //     assertArrayEquals(array("4"), UnaryInsert.of(Value::divide, Value::reciprocal).apply(c, array("24 2 3")));
    //     assertArrayEquals(array("4"), UnaryInsert.of(Value::divide, Value::reciprocal).apply(c, array("12 3")));
    //     assertArrayEquals(array("0.5"), UnaryInsert.of(Value::divide, Value::reciprocal).apply(c, array("2")));
    //     assertArrayEquals(EMPTY, UnaryInsert.of(Value::divide, Value::reciprocal).apply(c, EMPTY));
    // }

}
