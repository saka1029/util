package saka1029.util.calculator;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.Test;

class TestExpression {

    static final double E = 5e-6;

    @Test
    void testParse() {
        Map<String, Expression> m = Map.of();
        assertEquals(12.3, Expression.parse("12.3").eval(m), E);
        assertEquals(1 + 2 + 3, Expression.parse("1 + 2 + 3").eval(m), E);
        assertEquals(1 + 2 * 3, Expression.parse("1 + 2 * 3").eval(m), E);
        assertEquals((1 + 2) * 3, Expression.parse("(1 + 2) * 3").eval(m), E);
        assertEquals(2.0 / 3, Expression.parse("2.0 / 3").eval(m), E);
        assertEquals(2.0 / 3, Expression.parse("2 / 3").eval(m), E);
        assertEquals(1 + 2.0 / 3, Expression.parse("1 + 2 / 3").eval(m), E);
        assertEquals(1 + Math.pow(2, 3) / 3, Expression.parse("1 + 2 ^ 3 / 3").eval(m), E);
        assertEquals(Math.pow(2, Math.pow(3, 2)), Expression.parse("2 ^ 3 ^ 2").eval(m), E);
        assertEquals(Math.pow(Math.pow(2, 3), 2), Expression.parse("(2 ^ 3) ^ 2").eval(m), E);
    }

    @Test
    void testToString() {
        assertEquals("12.3", Expression.parse("12.3").toString());
        assertEquals("1 + 2 + 3", Expression.parse("1 + 2 + 3").toString());
        assertEquals("(1 + 2) * 3", Expression.parse("(1 + 2) * 3").toString());
        assertEquals("1 + 2 ^ 3 / 3", Expression.parse("1 + 2 ^ 3 / 3").toString());
    }

}
