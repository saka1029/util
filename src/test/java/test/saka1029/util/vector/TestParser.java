package test.saka1029.util.vector;

import static org.junit.Assert.assertEquals;
import java.math.BigDecimal;
import org.junit.Test;
import saka1029.util.vector.Context;
import saka1029.util.vector.Parser;
import saka1029.util.vector.Vector;

public class TestParser {

    static Vector eval(Context c, String input) {
        return Parser.of(input).statement().eval(c);
    }

    @Test
    public void testVector() {
        Context c = Context.of();
        assertEquals(Vector.of(1, 2, 3), eval(c, "1 2 3"));
    }

    @Test
    public void testExpression() {
        Context c = Context.of();
        assertEquals(Vector.of(2, 3, 4), eval(c, "1 2 3 + 1"));
        assertEquals(Vector.of(2, 3, 4), eval(c, "1 + 1 2 3"));
        assertEquals(Vector.of(0, -1, -2), eval(c, "1 - 1 2 3"));
        assertEquals(Vector.of(2, 2, 2), eval(c, "3 4 5 - 1 2 3"));
    }

    static BigDecimal num(double a) {
        return Vector.number(a);
    }

    static BigDecimal div(double a, double b) {
        return Vector.divide(num(a), num(b));
    }

    static BigDecimal pow(double a, double b) {
        return Vector.pow(num(a), num(b));
    }

    static BigDecimal sqrt(double a) {
        return num(a).sqrt(Vector.MATH_CONTEXT);
    }

    @Test
    public void testTerm() {
        Context c = Context.of();
        assertEquals(Vector.of(2, 4, 6), eval(c, "1 2 3 * 2"));
        assertEquals(Vector.of(2, 4, 6), eval(c, "2 * 1 2 3"));
        assertEquals(Vector.of(div(1,1), div(1,2), div(1,3)), eval(c, "1 / 1 2 3"));
        assertEquals(Vector.of(div(3,1), div(4,2), div(5,3)), eval(c, "3 4 5 / 1 2 3"));
    }

    @Test
    public void testFactor() {
        Context c = Context.of();
        assertEquals(Vector.of(1, 4, 9), eval(c, "1 2 3 ^ 2"));
        assertEquals(Vector.of(pow(1,0.5), pow(2,0.5), pow(3,0.5)), eval(c, "1 2 3 ^ 0.5"));
        assertEquals(Vector.of(2, 4, 8), eval(c, "2 ^ 1 2 3"));
    }

    @Test
    public void testUnary() {
        Context c = Context.of();
        assertEquals(Vector.of(-1, -2, -3), eval(c, "- 1 2 3"));
        assertEquals(Vector.of(6), eval(c, "sum 1 2 3"));
        assertEquals(Vector.of(-6), eval(c, "sum - 1 2 3"));
        assertEquals(Vector.of(6), eval(c, "+ 1 2 3"));
        assertEquals(Vector.of(8), eval(c, "+ 1 2 3 + 2"));
        assertEquals(Vector.of(8), eval(c, "2 + + 1 2 3"));
        assertEquals(Vector.of(sqrt(1), sqrt(2), sqrt(3)), eval(c, "sqrt 1 2 3"));
        assertEquals(Vector.of(3), eval(c, "length 1 2 3"));
        assertEquals(Vector.of(24), eval(c, "* 1 2 3 4"));
        assertEquals(Vector.of(1, 2, 3, 4), eval(c, "iota 4"));
        assertEquals(Vector.of(0, 1, 2, 3), eval(c, "iota 4 - 1"));
        assertEquals(Vector.of(1, 4, 9, 16), eval(c, "iota 4 ^ 2"));
        assertEquals(Vector.of(2, 4, 8, 16), eval(c, "2 ^ iota 4"));
    }

}