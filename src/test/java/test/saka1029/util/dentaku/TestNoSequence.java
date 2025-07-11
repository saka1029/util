package test.saka1029.util.dentaku;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class TestNoSequence {

    @Test
    public void testNumber() {
        assertEquals("3", Parser.parse("  3"));
        assertEquals("U-3", Parser.parse("  -3"));
    }

    @Test
    public void testMinus() {
        // (3) - (3)
        assertEquals("3B-3", Parser.parse("  3 - 3"));
        assertEquals("3,(U-3)", Parser.parse("  3 (- 3)"));
        // (3) - (-3)
        assertEquals("3B-U-3", Parser.parse("  3 - -3"));
        assertEquals("3B-U-U-3", Parser.parse("  3 ---3"));
        assertEquals("3B-3,2", Parser.parse("  3 - 3 2"));
        // (3 2) - (3) 2
        assertEquals("3,2B-3,2", Parser.parse("  3 2 - 3 2"));
        // (3 2) - (4 5)
        assertEquals("3,2B-(4,5)", Parser.parse("  3 2 - (4 5)"));
        assertEquals("3B-2B-1", Parser.parse("  3 - 2 - 1"));
    }
    
}
class Parser {
    final String input;
    int index = 0, ch, prev;

    Parser(String input) {
        this.input = input;
        get();
    }

    static String parse(String input) {
        Parser parser = new Parser(input);
        return parser.expression();
    }

    int get() {
        prev = ch;
        return ch = index < input.length() ? input.charAt(index++) : -1;
    }

    void spaces() {
        while (Character.isWhitespace(ch))
            get();
    }

    boolean eat(int... expects) {
        spaces();
        for (int e : expects)
            if (ch == e) {
                get();
                return true;
            }
        return false;
    }

    String primary() {
        if (eat('(')) {
            String s = expression();
            if (!eat(')'))
                throw new RuntimeException("')' expected");
            return "(" + s + ")";
        } else if (Character.isDigit(ch)) {
            get(); // skip digit
            return Character.toString(prev);
        } else
            throw new RuntimeException("Unexpected '%c'".formatted(ch));
    }

    String unary() {
        if (eat('-'))
            return "U" + (char) prev + unary();
        else
            return primary();
    }

    boolean isUnary(int ch) {
        return ch == '-' || ch == '(' || Character.isDigit(ch);
    }

    String expression() {
        spaces();
        if (ch == -1)
            return "";
        String s = unary();
        while (true)
            if (ch == -1)
                break;
            else if (eat('+', '-', '*', '-'))
                s = s + "B" + (char) prev + unary();
            else if (isUnary(ch))
                s = s + "," + unary();
            else
                break;
        return s;
    }
}
