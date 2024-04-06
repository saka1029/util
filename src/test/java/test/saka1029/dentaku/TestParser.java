package test.saka1029.dentaku;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import saka1029.util.dentaku.Context;
import saka1029.util.dentaku.Expression;
import saka1029.util.dentaku.Operators;
import saka1029.util.dentaku.Parser;
import saka1029.util.dentaku.Value;

public class TestParser {

    static Expression parse(Operators ops, String input) {
        return Parser.parse(ops, input);
    }

    static Value eval(Context context, String input) {
        return parse(context.operators(), input).eval(context);
    }

    @Test
    public void testToString() {
        Operators ops = Operators.of();
        Context c = Context.of(ops);
        Expression e0 = parse(ops, "  1 + 2 + 3 ");
        assertEquals("1 + 2 + 3", e0.toString());
        assertEquals(eval(c, "6"), e0.eval(c));
        Expression e1 = parse(ops, "  a = 1 + 2 + 3 ");
        assertEquals(Value.NaN, e1.eval(c));
        assertEquals("a = 1 + 2 + 3", c.variableString("a"));
        Expression e2 = parse(ops, "  a x = 1 + x ");
        assertEquals(Value.NaN, e2.eval(c));
        assertEquals("a x = 1 + x", ops.unaryString("a"));
        Expression e3 = parse(ops, "  x a y = x + y ");
        assertEquals(Value.NaN, e3.eval(c));
        assertEquals("x a y = x + y", ops.binaryString("a"));
    }

    @Test
    public void testNumber() {
        Operators ops = Operators.of();
        Context c = Context.of(ops);
        assertEquals(eval(c, "1"), eval(c, "  1 "));
        assertEquals(eval(c, "1 2"), eval(c, "  1   2 "));
    }

    @Test
    public void testBinary() {
        Operators ops = Operators.of();
        Context c = Context.of(ops);
        assertEquals(eval(c, "3"), eval(c, "  1  + 2"));
        assertEquals(eval(c, "4 6"), eval(c, "1 2 + 3 4"));
        assertEquals(eval(c, "3 5"), eval(c, "1 2 + 3 4 - 1 1"));
    }

    @Test
    public void testUnary() {
        Operators ops = Operators.of();
        Context c = Context.of(ops);
        assertEquals(eval(c, "4"), eval(c, "length 1 2 3 4"));
        assertEquals(eval(c, "-1 -2 -3 -4"), eval(c, "- 1 2 3 4"));
        assertEquals(eval(c, "10"), eval(c, "+ 1 2 3 4"));
        assertEquals(eval(c, "24"), eval(c, "* 1 2 3 4"));
        assertEquals(eval(c, "1 -1 0"), eval(c, "sign 5 -2 0"));
    }

    @Test
    public void testDefineVariable() {
        Operators ops = Operators.of();
        Context c = Context.of(ops);
        assertEquals(Value.NaN, eval(c, "a = 1 2 3"));
        assertEquals(eval(c, "1 2 3"), eval(c, "a"));
        assertEquals(Value.NaN, eval(c, "a = (1 2 3)"));
        assertEquals(eval(c, "1 2 3"), eval(c, "a"));
        assertEquals(Value.NaN, eval(c, "a = - 1 2 3"));
        assertEquals(eval(c, "-1 -2 -3"), eval(c, "a"));
        assertEquals(Value.NaN, eval(c, "b = a + 1"));
        assertEquals(eval(c, "0 -1 -2"), eval(c, "b"));
        assertEquals(Value.NaN, eval(c, "a = 3"));
        assertEquals(eval(c, "4"), eval(c, "b"));
    }

    // @Test
    // public void testHighOperator() {
    //     Operators ops = Operators.of();
    //     Context c = Context.of(ops);
    //     assertEquals(eval(c, "6"), eval(c, "@ + 1 2 3"));
    //     assertEquals(eval(c, "1 3 6"), eval(c, "@@ + 1 2 3"));
    //     assertEquals(eval(c, "24"), eval(c, "@ * 1 2 3 4"));
    //     assertEquals(eval(c, "1 2 6 24"), eval(c, "@@ * 1 2 3 4"));
    // }

    @Test
    public void testTo() {
        Operators ops = Operators.of();
        Context c = Context.of(ops);
        assertEquals(eval(c, "3 4 5"), eval(c, "3 to 5"));
        assertEquals(eval(c, "5 4 3"), eval(c, "5 to 3"));
    }

    @Test
    public void testDefineUnary() {
        Operators ops = Operators.of();
        Context c = Context.of(ops);
        assertEquals(Value.NaN, eval(c, "a = 1 to 4"));
        assertEquals(eval(c, "3 4"), eval(c, "a > 2 filter a"));
        assertEquals(Value.NaN, eval(c, "select.gt2 x = x > 2 filter x"));
        assertEquals(eval(c, "3 4"), eval(c, "select.gt2 a"));
        assertEquals(eval(c, "3 4"), eval(c, "select.gt2 (1 to 4)"));
        assertEquals(Value.NaN, eval(c, "average x = + x / length x"));
        assertEquals(eval(c, "2.5"), eval(c, "average a"));
        assertEquals(eval(c, "2 4"), eval(c, "not (a % 2) filter a"));
        assertEquals(Value.NaN, eval(c, "even x = not (x % 2) filter x"));
        assertEquals(eval(c, "2 4 6 8 10"), eval(c, "even (1 to 10)"));
    }

    @Test
    public void testFibonacci() {
        Operators ops = Operators.of();
        Context c = Context.of(ops);
        assertEquals(Value.NaN, eval(c, "fib x = x (x at -2 + (x at -1))"));
        assertEquals(Value.NaN, eval(c, "f = fib fib fib fib 0 1"));
        assertEquals(eval(c, "0 1 1 2 3 5"), eval(c, "f"));
    }

    /**
     * フィボナッチ数列の一般項 
     */
    @Test
    public void testFibonacciGeneral() {
        Operators ops = Operators.of();
        Context c = Context.of(ops);
        assertEquals(Value.NaN, eval(c,
            "fib n = 1 + sqrt 5 / 2 ^ n - (1 - sqrt 5 / 2 ^ n) / sqrt 5"));
        assertEquals(eval(c, "0 1 1 2 3 5 8"), eval(c, "int fib (0 to 6)"));
    }

    /**
     * 円周率を求めるライプニッツの公式
     */
    @Test
    public void testLeibniz() {
        Operators ops = Operators.of();
        Context c = Context.of(ops);
        assertEquals(Value.NaN, eval(c, "pi.term n = -1 ^ n / (2 * n + 1)"));
        assertEquals(Value.NaN, eval(c, "pi.sum range = 4 * + pi.term range"));
        assertEquals(eval(c, "3.2323"), eval(c, "pi.sum (0 to 10) round 4"));
        assertEquals(eval(c, "3.1515"), eval(c, "pi.sum (0 to 100) round 4"));
        assertEquals(eval(c, "3.1426"), eval(c, "pi.sum (0 to 1000) round 4"));
        assertEquals(eval(c, "3.1417"), eval(c, "pi.sum (0 to 10000) round 4"));
    }

    @Test
    public void testDefineBinary() {
        Operators ops = Operators.of();
        Context c = Context.of(ops);
        assertEquals(Value.NaN, eval(c, "a = 1 to 4"));
        assertEquals(eval(c, "3 4"), eval(c, "a > 2 filter a"));
        assertEquals(Value.NaN, eval(c, "p select.gt x = x > p filter x"));
        assertEquals(eval(c, "3 4"), eval(c, "2 select.gt a"));
    }

    @Test
    public void testLateBinding() {
        Operators ops = Operators.of();
        Context c = Context.of(ops);
        assertEquals(Value.NaN, eval(c, "f x = x + 1"));
        assertEquals(Value.NaN, eval(c, "g x = f x + 1"));
        assertEquals(eval(c, "2"), eval(c, "g 0"));
        assertEquals(Value.NaN, eval(c, "f x = x + 2"));
        assertEquals(eval(c, "3"), eval(c, "g 0"));
    }

    @Test
    public void testSin() {
        Operators ops = Operators.of();
        Context c = Context.of(ops);
        assertEquals(eval(c, "1.999999999999999861967979879025"), eval(c, "1 / sin radian 45 ^ 2"));
    }

    @Test
    public void testSolve() {
        Operators ops = Operators.of();
        Context c = Context.of(ops);
        eval(c, "a = 1 2 3");
        eval(c, "b = 4 5 6");
        StringBuilder sb = new StringBuilder();
        int count = Value.solve(Parser.parse(ops, "a + b == 7"), c, s -> sb.append(s).append(System.lineSeparator()));
        assertEquals(count, 3);
        assertEquals("a=1 b=6%na=2 b=5%na=3 b=4%n".formatted(), sb.toString());
    }

}
