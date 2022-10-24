package saka1029.util.calculator;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

class TestExpression {

    static final double E = 5e-6;

    @Test
    void testConstantExpression() {
        Map<String, Expression> m = Map.of();
        assertEquals(12.3, Expression.of("12.3").eval(m), E);
        assertEquals(-12.3, Expression.of("-12.3").eval(m), E);
        assertEquals(1 + 2 + 3, Expression.of("1 + 2 + 3").eval(m), E);
        assertEquals(1 + 2 * 3, Expression.of("1 + 2 * 3").eval(m), E);
        assertEquals((1 + 2) * 3, Expression.of("(1 + 2) * 3").eval(m), E);
        assertEquals(2.0 / 3, Expression.of("2.0 / 3").eval(m), E);
        assertEquals(2.0 / 3, Expression.of("2 / 3").eval(m), E);
        assertEquals(1 + 2.0 / 3, Expression.of("1 + 2 / 3").eval(m), E);
        assertEquals(1 + Math.pow(2, 3) / 3, Expression.of("1 + 2 ^ 3 / 3").eval(m), E);
        assertEquals(Math.pow(2, Math.pow(3, 2)), Expression.of("2 ^ 3 ^ 2").eval(m), E);
        assertEquals(Math.pow(Math.pow(2, 3), 2), Expression.of("(2 ^ 3) ^ 2").eval(m), E);
    }
    
    @Test
    void testVariables() {
        Map<String, Expression> m = new HashMap<>(Map.of(
            "x", Expression.of("12.3"),
            "y", Expression.of("x + 1")));
        assertEquals(Math.pow(12.3, 2) - 1, Expression.of("x^2 - 1").eval(m), E);
        assertEquals(Math.pow(12.3 + 1, 2) - 1, Expression.of("y^2 - 1").eval(m), E);
        m.put("x", Expression.of("2"));
        assertEquals(Math.pow(2 + 1, 2) - 1, Expression.of("y^2 - 1").eval(m), E);
    }

    @Test
    void testToString() {
        assertEquals("12.3", Expression.of("12.3").toString());
        assertEquals("1 + 2 + 3", Expression.of("1 + 2 + 3").toString());
        assertEquals("(1 + 2) * 3", Expression.of("(1 + 2) * 3").toString());
        assertEquals("1 + 2 ^ 3 / 3", Expression.of("   1 + 2 ^ 3 / 3   ").toString());
        assertEquals("x^2 - 1", Expression.of("x^2 - 1").toString());
    }
}
