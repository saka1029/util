package saka1029.util.eval;

import static org.junit.Assert.assertEquals;
import java.util.List;
import org.junit.Test;

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
        assertEquals(List.of("123","1.2","+2","+2.2","-3","-3.3","3e5","-3.4e6","-3.4e-66","e"),
            tokens("123 1.2 +2 +2.2 -3 -3.3 3e5 -3.4e6 -3.4e-66e"));
    }

    @Test
    public void testOperators() {
        assertEquals(List.of("123","<","e",";","(","ij","<=","0",")"),
            tokens(" 123 < e; (ij <= 0) "));
        assertEquals(List.of("+","a","-","b"), tokens("  +a -b"));
        assertEquals(List.of("++","a","-+","b"), tokens("  ++a -+b"));
    }

    static Expression read1(String source) {
        return Parser.of(source).read().get(0);
    }

    static Context context() {
        Context c = Context.of();
        c.function2("+", (x, a, b) -> a + b);
        c.function2("-", (x, a, b) -> a - b);
        c.function2("*", (x, a, b) -> a * b);
        c.function2("/", (x, a, b) -> a / b);
        c.function2("%", (x, a, b) -> a % b);
        c.function2("^", (x, a, b) -> Math.pow(a, b));
        return c;
    }

    @Test
    public void testRead() {
        Context c = context();
        List<Expression> list = Parser.of(" 1 2 3 ").read();
        assertEquals(3, list.size());
        assertEquals(1.0, list.get(0).eval(c), DELTA);
        assertEquals(2.0, list.get(1).eval(c), DELTA);
        assertEquals(3.0, list.get(2).eval(c), DELTA);
    }

    @Test
    public void testParen() {
        Context c = context();
        assertEquals(27.0, read1("3 * (4 + 5)").eval(c), DELTA);
        assertEquals("(* 3 (+ 4 5))", read1("3 * (4 + 5)").string());
    }

    @Test
    public void testExpression() {
        Context c = context();
        assertEquals(3.0, read1("3").eval(c), DELTA);
        assertEquals(4.2, read1("3 + 1.2").eval(c), DELTA);
        assertEquals(3.12, read1("3 + 1.2 * 0.1").eval(c), DELTA);
        assertEquals(3 + Math.pow(0.2, Math.pow(2, 3)), read1("3 + 0.2 ^ 2 ^ 3").eval(c), DELTA);
        assertEquals("(+ 3 (^ 0.2 (^ 2 3)))", read1("3 + 0.2 ^ 2 ^ 3").string());
        c.variable("x", Number.of(3.3));
        assertEquals(6.6, read1("x + x").eval(c), DELTA);
    }

    @Test
    public void testFuncall() {
        Context c = context();
        c.function0("three", (x) -> 3);
        c.function1("sqrt", (x, a) -> Math.sqrt(a));
        c.function2("hypot", (x, a, b) -> Math.hypot(a, b));
        assertEquals(3.0, read1("three()").eval(c), DELTA);
        assertEquals(3.0, read1("sqrt(9)").eval(c), DELTA);
        assertEquals(5.0, read1("hypot(1 + 2, 2 * 2)").eval(c), DELTA);
    }
    
}
