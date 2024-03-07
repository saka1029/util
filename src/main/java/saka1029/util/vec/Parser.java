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

    Expression expression() {
        return null;
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
