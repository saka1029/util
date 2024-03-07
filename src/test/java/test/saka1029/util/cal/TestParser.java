package test.saka1029.util.cal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import java.util.List;
import org.junit.Test;
import saka1029.util.cal.Context;
import saka1029.util.cal.Expression;
import saka1029.util.cal.Number;
import saka1029.util.cal.Parser;

public class TestParser {

    static final double DELTA = 5e-6;

    static List<String> tokens(String source) {
        return Parser.of(source).tokens();
    }

    @Test
    public void testTokens() {
        assertEquals(List.of("abc", "123"), tokens("    abc     123    "));
        assertEquals(List.of("(", "123", ")"), tokens("(123)"));
        assertEquals(List.of("123", ",", "3", "abc"), tokens("123, 3abc"));
    }

    @Test
    public void testId() {
        assertEquals(List.of("123","a","123","+","a","1","-","bb"), tokens(" 123a 123+a 1-bb"));
    }

    @Test
    public void testNumbers() {
        assertEquals(List.of("123","1.2","+","2","+","2.2","-","3","-","3.3","3e5","-","3.4e6","-","3.4e-66","e"),
            tokens("123 1.2 +2 +2.2 -3 -3.3 3e5 -3.4e6 -3.4e-66e"));
    }

    @Test
    public void testOperators() {
        assertEquals(List.of("123","<","e",";","(","ij","<=","0",")"),
            tokens(" 123 < e; (ij <= 0) "));
        assertEquals(List.of("+","a","-","b"), tokens("  +a -b"));
        assertEquals(List.of("++","a","-+","b"), tokens("  ++a -+b"));
    }

    static Expression read(String source) {
        return Parser.of(source).read();
    }

    @Test
    public void testRead() {
        Context c = Context.of();
        Parser parser = Parser.of(" 1 2 3 ");
        assertEquals(1.0, parser.read().eval(c), DELTA);
        assertEquals(2.0, parser.read().eval(c), DELTA);
        assertEquals(3.0, parser.read().eval(c), DELTA);
        assertNull(parser.read());
    }

    @Test
    public void testParen() {
        Context c = Context.of();
        assertEquals(27.0, read("3 * (4 + 5)").eval(c), DELTA);
        assertEquals("(* 3 (+ 4 5))", read("3 * (4 + 5)").string());
    }

    @Test
    public void testExpression() {
        Context c = Context.of();
        assertEquals(3.0, read("3").eval(c), DELTA);
        assertEquals(4.2, read("3 + 1.2").eval(c), DELTA);
        assertEquals(3.12, read("3 + 1.2 * 0.1").eval(c), DELTA);
        assertEquals(3 + Math.pow(0.2, Math.pow(2, 3)), read("3 + 0.2 ^ 2 ^ 3").eval(c), DELTA);
        assertEquals("(+ 3 (^ 0.2 (^ 2 3)))", read("3 + 0.2 ^ 2 ^ 3").string());
        c.variable("x", Number.of(3.3));
        assertEquals(6.6, read("x + x").eval(c), DELTA);
        assertEquals(-6.0, read("- 3 - 3").eval(c), DELTA);
    }

    @Test
    public void testFuncall() {
        Context c = Context.of();
        c.function0("three", (x) -> 3);
        c.function1("sqrt", (x, a) -> Math.sqrt(a));
        c.function2("hypot", (x, a, b) -> Math.hypot(a, b));
        assertEquals(3.0, read("three()").eval(c), DELTA);
        assertEquals(3.0, read("sqrt(9)").eval(c), DELTA);
        assertEquals(5.0, read("hypot(1 + 2, 2 * 2)").eval(c), DELTA);
    }
    
    @Test
    public void testDefineVariable() {
        Context c = Context.of();
        assertEquals(Double.NaN, read("x = 3 + 2").eval(c), DELTA);
        assertEquals(5.0, read("x").eval(c), DELTA);
        assertEquals(Double.NaN, read("y = x + 2").eval(c), DELTA);
        assertEquals(7.0, read("y").eval(c), DELTA);
        assertEquals(Double.NaN, read("x = 3^2").eval(c), DELTA);
        assertEquals(11.0, read("y").eval(c), DELTA);
        assertEquals(Double.NaN, read("𩸽 = 3 + 2").eval(c), DELTA);
        assertEquals(5.0, read("𩸽").eval(c), DELTA);
    }
    
    @Test
    public void testDefineFunction() {
        Context c = Context.of();
        assertEquals(Double.NaN, read("二倍(x) = x + x").eval(c), DELTA);
        assertEquals(6.0, read("二倍(3)").eval(c), DELTA);
        c.function1("sqrt", (x, a) -> Math.sqrt(a));
        assertEquals(Double.NaN, read("斜辺(x, y) = sqrt(x^2 + y^2)").eval(c), DELTA);
        assertEquals(10.0, read("斜辺(二倍(3), 二倍(4))").eval(c), DELTA);
    }
}
