package test.saka1029.dentaku;

import static org.junit.Assert.assertEquals;
import java.math.BigDecimal;
import java.util.Arrays;
import org.junit.Test;
import saka1029.util.dentaku.Value;

public class TestValue {

    static BigDecimal dec(double element) {
        return new BigDecimal(element);
    }

    static Value value(double... elements) {
        return Value.of(Arrays.stream(elements)
            .mapToObj(BigDecimal::new)
            .toArray(BigDecimal[]::new));
    }

    @Test
    public void testBigDecimal() {
        assertEquals(3, new BigDecimal("123.456").scale());
        assertEquals(5, new BigDecimal("1.23456").scale());
        assertEquals(0, new BigDecimal("123456").scale());
        assertEquals(3, new BigDecimal("-123.456").scale());
        assertEquals(5, new BigDecimal("-1.23456").scale());
        assertEquals(1, new BigDecimal("0.5").scale());
        assertEquals(1, new BigDecimal("0.5").multiply(new BigDecimal(2)).scale());
    }

    @Test
    public void testOf() {
        Value v123 = Value.of(dec(1), dec(2), dec(3));
        assertEquals(value(1, 2, 3), v123);
    }

    @Test
    public void testMap() {
        Value v = value();
        Value v1234 = value(1, 2, 3, 4);
        assertEquals(value(), v.map(BigDecimal::negate));
        assertEquals(value(-1, -2, -3, -4), v1234.map(BigDecimal::negate));
        // assertEquals(value(-1, 0, 1), value(-4, 0, 8).map(Value.SIGN));
        // assertEquals(value(-1, 0, 1), value(-Math.PI/2, 0, Math.PI/2).map(Value.SIN));
        // assertEquals(value(-1, 1, -1), value(-Math.PI, 0, Math.PI).map(Value.COS));
    }

    @Test
    public void testReduce() {
        // Value v = value();
        // Value v1234 = value(1, 2, 3, 4);
        // assertEquals(value(0), v.reduce(BigDecimal::add, BigDecimal.ZERO));
        // assertEquals(value(10), v1234.reduce(BigDecimal::add, BigDecimal.ZERO));
        // assertEquals(value(1), v.reduce(BigDecimal::multiply, BigDecimal.ONE));
        // assertEquals(value(24), v1234.reduce(BigDecimal::multiply, BigDecimal.ONE));
    }

    @Test
    public void testCumulate() {
        // Value v = value();
        // Value v1234 = value(1, 2, 3, 4);
        // assertEquals(value(), v.cumulate(BigDecimal::add, BigDecimal.ZERO));
        // assertEquals(value(1, 3, 6, 10), v1234.cumulate(BigDecimal::add, BigDecimal.ZERO));
        // assertEquals(value(), v.cumulate(BigDecimal::multiply, BigDecimal.ONE));
        // assertEquals(value(1, 2, 6, 24), v1234.cumulate(BigDecimal::multiply, BigDecimal.ONE));
    }

    @Test
    public void testReduceBOP() {
        // Value v = value();
        // Value v1234 = value(1, 2, 3, 4);
        // assertEquals(value(0), v.reduce(Value.ADD));
        // assertEquals(value(10), v1234.reduce(Value.ADD));
        // assertEquals(value(1), v.reduce(Value.MULT));
        // assertEquals(value(24), v1234.reduce(Value.MULT));
        // assertEquals(value(1), v1234.reduce(Value.MIN));
        // assertEquals(value(4), v1234.reduce(Value.MAX));
        // assertEquals(Value.of(Value.MAX_VALUE), v.reduce(Value.MIN));
        // assertEquals(Value.of(Value.MIN_VALUE), v.reduce(Value.MAX));
    }

    @Test
    public void testCumulateBOP() {
        // Value v = value();
        // Value v1234 = value(1, 2, 3, 4);
        // assertEquals(value(), v.cumulate(Value.ADD));
        // assertEquals(value(1, 3, 6, 10), v1234.cumulate(Value.ADD));
        // assertEquals(value(), v.cumulate(Value.MULT));
        // assertEquals(value(1, 2, 6, 24), v1234.cumulate(Value.MULT));
    }

    @Test
    public void testBinary() {
        Value v2 = value(2);
        Value v1234 = value(1, 2, 3, 4);
        Value v5678 = value(5, 6, 7, 8);
        assertEquals(value(3, 4, 5, 6), v1234.binary(BigDecimal::add, v2));
        assertEquals(value(3, 4, 5, 6), v2.binary(BigDecimal::add, v1234));
        assertEquals(value(6, 8, 10, 12), v1234.binary(BigDecimal::add, v5678));
        assertEquals(value(2, 4, 6, 8), v1234.binary(BigDecimal::multiply, v2));
        assertEquals(value(2, 4, 6, 8), v2.binary(BigDecimal::multiply, v1234));
        assertEquals(value(5, 12, 21, 32), v1234.binary(BigDecimal::multiply, v5678));
    }

    @Test
    public void testMapLogical() {
        // Value v1101 = value(1, 1, 0, 1);
        // assertEquals(value(0, 0, 1, 0), v1101.map(Value.NOT));
    }

    @Test
    public void testBinaryCompare() {
        // Value a = value(-1, 0, 1);
        // assertEquals(value(1, 0, 0), a.binary(Value.EQ, value(-1)));
        // assertEquals(value(0, 1, 0), a.binary(Value.EQ, value(0)));
        // assertEquals(value(0, 0, 1), a.binary(Value.EQ, value(1)));
        // assertEquals(value(0, 1, 1), a.binary(Value.NE, value(-1)));
        // assertEquals(value(1, 0, 1), a.binary(Value.NE, value(0)));
        // assertEquals(value(1, 1, 0), a.binary(Value.NE, value(1)));
        // assertEquals(value(0, 0, 0), a.binary(Value.LT, value(-1)));
        // assertEquals(value(1, 0, 0), a.binary(Value.LT, value(0)));
        // assertEquals(value(1, 1, 0), a.binary(Value.LT, value(1)));
        // assertEquals(value(1, 0, 0), a.binary(Value.LE, value(-1)));
        // assertEquals(value(1, 1, 0), a.binary(Value.LE, value(0)));
        // assertEquals(value(1, 1, 1), a.binary(Value.LE, value(1)));
        // assertEquals(value(0, 1, 1), a.binary(Value.GT, value(-1)));
        // assertEquals(value(0, 0, 1), a.binary(Value.GT, value(0)));
        // assertEquals(value(0, 0, 0), a.binary(Value.GT, value(1)));
        // assertEquals(value(1, 1, 1), a.binary(Value.GE, value(-1)));
        // assertEquals(value(0, 1, 1), a.binary(Value.GE, value(0)));
        // assertEquals(value(0, 0, 1), a.binary(Value.GE, value(1)));
    }

    @Test
    public void testBinaryLogical() {
        // Value a = value(1, 1, 0, 0), b = value(1, 0, 1, 0);
        // assertEquals(value(1, 0, 0, 0), a.binary(Value.AND, b));
        // assertEquals(value(1, 1, 1, 0), a.binary(Value.OR, b));
        // assertEquals(value(0, 1, 1, 0), a.binary(Value.XOR, b));
    }

    @Test
    public void testBinaryFilter() {
        assertEquals(value(1, 3), value(1, 0, 1, 0).filter(value(1, 2, 3, 4)));
        assertEquals(value(1, 3, 4), value(1, 0, 1, 1).filter(value(1, 2, 3, 4)));
        assertEquals(value(1, 2, 3, 4), value(1).filter(value(1, 2, 3, 4)));
        assertEquals(value(), value(0).filter(value(1, 2, 3, 4)));
    }

}
