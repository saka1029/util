package test.saka1029.decs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import org.junit.Test;
import saka1029.util.decs.Context;
import saka1029.util.decs.Decs;
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
        assertDecsEquals(Decs.decs("3"), parser.eval(" 1 + 2 "));
        assertDecsEquals(Decs.decs("10"), parser.eval(" 2 * (2 + 3) "));
        assertDecsEquals(Decs.decs("512"), parser.eval(" 2 ^ 3 ^ 2 "));

    }

    @Test
    public void testConcat() {
        Parser parser = new Parser();
        assertDecsEquals(Decs.decs("1 2 3"), parser.eval(" 1, 2, 3"));
    }

    @Test
    public void testBinary() {
        Parser parser = new Parser();
        BigDecimal[] two = Decs.decs("2");
        parser.context.binary("xx", (c, a, b) -> Decs.add(a, Decs.multiply(b, two)), "a xx b = a + 2 * b");
        assertDecsEquals(Decs.decs("7 10 13"), parser.eval(" (1, 2, 3) xx (2, 3, 4) xx 1"));
    }

    @Test
    public void testDefineVariable() {
        Parser parser = new Parser();
        assertTrue(Decs.NO_VALUE == parser.eval(" v = 1 + 2"));
        Expression e = parser.parse("v + 3");
        assertDecsEquals(Decs.decs("6"), e.apply(parser.context));
        assertTrue(e instanceof ExpressionWithVariables);
        assertEquals(List.of("v"), ((ExpressionWithVariables)e).variables);
    }
}
