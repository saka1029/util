package saka1029.util.vec;

/**
 * SYNTAX:
 * <pre>
 * statement  = [ ID '=' ] expression
 * expression = [ '-' ] term { [ '+' | '-' ] term }
 * term       = factor { [ '*' | '/' ] factor }
 * exp        = sequence { '^' factor }
 * sequence   = primary { primary }
 * primary    = '(' expression ')' | ID | NUMBER
 * </pre>
 */
public class Parser {
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
        if (expected == ch) {
            get();
            return true;
        }
        return false;
    }

    Expression factor() {
    }

    Expression term() {
        Expression e = factor();
        while (true)
            if (eat('*')) {
                Expression left = e;
                e = c -> Vec.calculate((a, b) -> a * b, left.eval(c), factor().eval(c));
            } else if (eat('/')) {
                Expression left = e;
                e = c -> Vec.calculate((a, b) -> a / b, left.eval(c), factor().eval(c));
            } else
                break;
        return e;
    }

    Expression expression() {
        Expression e = term();
        while (true)
            if (eat('+')) {
                Expression left = e;
                e = c -> Vec.calculate((a, b) -> a + b, left.eval(c), term().eval(c));
            } else if (eat('-')) {
                Expression left = e;
                e = c -> Vec.calculate((a, b) -> a - b, left.eval(c), term().eval(c));
            } else
                break;
        return e;
    }

    Expression statement() {
        Expression e = expression();
        if (eat('=')) {
            if (e instanceof Variable v) {
                Expression value = expression();
                return c -> { c.variable(v.name, value); return Vec.NaN; };
            } else
                throw new RuntimeException("Variable expected before '='");
        } else
            return e;
    }

}
