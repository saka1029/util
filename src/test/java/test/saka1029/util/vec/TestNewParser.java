package test.saka1029.util.vec;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class TestNewParser {

    static class Parser {
        final String input;
        int index = 0;
        int ch;

        Parser(String input) {
            this.input = input;
            get();
        }

        int get() {
            return ch = index < input.length() ? input.charAt(index++) : -1;
        }

        void spaces() {
            while (Character.isWhitespace(ch))
                get();
        }

        boolean eat(int expected) {
            spaces();
            if (ch == expected) {
                get();
                return true;
            }
            return false;
        }

        String gen(String op, String left, String right) {
            return "(%s %s %s)".formatted(op, left, right);
        }

        String gen(String op, String left) {
            return "(%s %s)".formatted(op, left);
        }

        boolean isPrimary(int ch) {
            return Character.isAlphabetic(ch)
                || Character.isDigit(ch)
                || ch == '(';
        }

        String primary() {
            String e;
            if (eat('(')) {
                e = expression();
                if (!eat(')'))
                    throw new RuntimeException("')' expected");
            } if (Character.isAlphabetic(ch)) {
                e = "%c".formatted((char)ch);
                get();
            } else if (Character.isDigit(ch)) {
                e = "%c".formatted((char)ch);
                get();
            } else
                throw new RuntimeException("Unknowun char 0x%02x".formatted(ch));
            return e;
        }

        String vector() {
            String e = primary();
            spaces();
            while (isPrimary(ch)) {
                e = e + " " + primary();
                spaces();
            }
            return gen("vec", e);
        }

        String unary() {
            if (eat('-'))
                return gen("-", unary());
            else
                return vector();
        }

        String factor() {
            String e = unary();
            while (true)
                if (eat('^'))
                    e = gen("^", e, factor());
                else
                    break;
            return e;
        }

        String term() {
            String e = factor();
            while (true)
                if (eat('*'))
                    e = gen("*", e, factor());
                else if (eat('/'))
                    e = gen("/", e, factor());
                else
                    break;
            return e;
        }

        String expression() {
            String e = term();
            while (true)
                if (eat('+'))
                    e = gen("+", e, term());
                else if (eat('-'))
                    e = gen("-", e, term());
                else
                    break;
            return e;
        }
    }

    String parse(String input) {
        return new Parser(input).expression();
    }

    @Test
    public void testVector() {
        assertEquals("(vec 1 2 3)", parse("1 2 3"));
    }

    @Test
    public void testPlus() {
        assertEquals("(+ (vec 3) (vec a))", parse("3 + a"));
        assertEquals("(+ (vec 3 4) (vec a))", parse("3 4 + a"));
        assertEquals("(+ (vec 3) (vec a b))", parse("3 + a b"));
    }

    @Test
    public void testUnary() {
        assertEquals("(+ (vec 3) (- (vec a)))", parse("3 + -a"));
        assertEquals("(- (vec 3) (- (vec a)))", parse("3 - -a"));
        assertEquals("(- (- (vec 3)) (vec a))", parse("-3 - a"));
        assertEquals("(- (- (vec 3 4 5)) (vec a))", parse("- 3 4 5 - a"));
        assertEquals("(+ (+ (vec 3) (- (vec a 3))) (- (vec 2)))", parse("3 + -a 3 + -2"));
        assertEquals("(- (- (vec a)))", parse("--a"));
        assertEquals("(- (vec 3) (- (- (vec a))))", parse("3 - --a"));
    }

}
