package test.saka1029.decs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import org.junit.Test;
import saka1029.util.decs.Context;
import static saka1029.util.decs.Decs.*;
import saka1029.util.decs.Expression;
import saka1029.util.decs.ExpressionWithVariables;
import saka1029.util.decs.Parser;

public class TestParser {

    static void assertDecsEquals(BigDecimal[] expected, BigDecimal[] actual) {
        assertEquals(List.of(expected), List.of(actual));
    }

    @Test
    public void testParser() {
        Context context = new Context();
        Parser parser = new Parser(context);
        assertDecsEquals(decs("3"), parser.eval(" 1 + 2 "));
        assertDecsEquals(decs("10"), parser.eval(" 2 * (2 + 3) "));
        assertDecsEquals(decs("512"), parser.eval(" 2 ^ 3 ^ 2 "));

    }

    @Test
    public void testConcat() {
        Parser parser = new Parser();
        assertDecsEquals(decs("1 2 3"), parser.eval(" 1, 2, 3"));
    }

    @Test
    public void testBinary() {
        Parser parser = new Parser();
        BigDecimal[] two = decs("2");
        parser.context.binary("xx", (c, a, b) -> add(a, multiply(b, two)), "a xx b = a + 2 * b");
        assertDecsEquals(decs("7 10 13"), parser.eval(" (1, 2, 3) xx (2, 3, 4) xx 1"));
    }

    @Test
    public void testDefineVariable() {
        Parser parser = new Parser();
        assertTrue(NO_VALUE == parser.eval(" v = 1 + 2"));
        Expression e = parser.parse("v + 3");
        assertDecsEquals(decs("6"), e.eval(parser.context));
        assertTrue(e instanceof ExpressionWithVariables);
        assertEquals(List.of("v"), ((ExpressionWithVariables)e).variables);
    }

    @Test
    public void testDefineUnary() {
        Parser parser = new Parser();
        parser.eval("x = 100");
        assertTrue(NO_VALUE == parser.eval("f x = x + 2"));
        assertDecsEquals(decs("5"), parser.eval("f 3"));
        assertDecsEquals(decs("100"), parser.eval("x"));
    }

    @Test
    public void testDefineBinary() {
        Parser parser = new Parser();
        parser.eval("x = 100");
        parser.eval("y = 200");
        assertTrue(NO_VALUE == parser.eval("x hypot y = x*x + y*y"));
        assertDecsEquals(decs("25"), parser.eval("3 hypot 4"));
        assertDecsEquals(decs("100"), parser.eval("x"));
        assertDecsEquals(decs("200"), parser.eval("y"));
    }
}
