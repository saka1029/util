package saka1029.util.vec;

/**
 * SYNTAX:
 * <pre>
 * statement  = [ ID '=' ] expression
 * expression = [ '-' ] term { [ '+' | '-' ] term }
 * term       = factor { [ '*' | '/' ] factor }
 * factor     = sequence { '^' factor }
 * sequence   = primary { primary }
 * primary    = '(' expression ')' | ID | NUMBER
 * </pre>
 */
public class Parser {
    final String input;
    int index = 0;
    int ch;

    private Parser(String input) {
        this.input = input;
        get();
    }

    public static Parser of(String input) {
        return new Parser(input);
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

    static boolean isDigit(int ch) {
        return ch >= '0' && ch <= '9';
    }

    static boolean isIdFirst(int ch) {
        return ch >= 'A' && ch <= 'Z'
            || ch >= 'a' && ch <= 'z'
            || ch == '_'
            || ch >= 256;
    }

    static boolean isIdRest(int ch) {
        return isIdFirst(ch) || ch >= '0' && ch <= '9';
    }

    Expression id() {
        StringBuilder sb = new StringBuilder();
        while (isIdRest(ch)) {
            sb.append((char)ch);
            get();
        }
        String name = sb.toString();
        return Variable.of(name);
    }

    Expression number() {
        StringBuilder sb = new StringBuilder();
        while (isDigit(ch)) {
            sb.append((char)ch);
            get();
        }
        double d = Double.parseDouble(sb.toString());
        return Vec.of(d);
    }

    Expression primary() {
        Expression e;
        if (eat('(')) {
            e = expression();
            if (!eat(')'))
                throw new RuntimeException("')' expected");
        } else if (isIdFirst(ch)) {
            e = id();
        } else if (isDigit(ch)) {
            e = number();
        } else
            throw new RuntimeException("Unknown char: 0x%02x".formatted(ch));
        return e;
    }

    static boolean isPrimary(int ch) {
        return ch == '(' || isIdFirst(ch) || isDigit(ch);
    }

    Expression sequence() {
        Expression primary = primary();
        spaces();
        while (isPrimary(ch)) {
            Expression left = primary, right = primary();
            // TODO: この連結方式は効率が悪い。
            primary = c -> left.eval(c).append(right.eval(c));
            spaces();
        }
        return primary;
    }

    Expression factor() {
        Expression e = sequence();
        while (true)
            if (eat('^')) {
                Expression left = e, right = factor();
                e = c -> Vec.calculate((a, b) -> Math.pow(a, b), left.eval(c), right.eval(c));
            } else
                break;
        return e;
    }

    Expression term() {
        Expression e = factor();
        while (true)
            if (eat('*')) {
                Expression left = e, right = factor();
                e = c -> Vec.calculate((a, b) -> a * b, left.eval(c), right.eval(c));
            } else if (eat('/')) {
                Expression left = e, right = factor();
                e = c -> Vec.calculate((a, b) -> a / b, left.eval(c), right.eval(c));
            } else
                break;
        return e;
    }

    Expression expression() {
        boolean minus = false;
        if (eat('-'))
            minus = true;
        Expression e = term();
        while (true)
            if (eat('+')) {
                Expression left = e, right = term();
                e = c -> Vec.calculate((a, b) -> a + b, left.eval(c), right.eval(c));
            } else if (eat('-')) {
                Expression left = e, right = term();
                e = c -> Vec.calculate((a, b) -> a - b, left.eval(c), right.eval(c));
            } else
                break;
        if (minus) {
            Expression left = e;
            e = c -> Vec.calculate(a -> -a, left.eval(c));
        }
        return e;
    }

    public Expression statement() {
        Expression e = expression();
        if (eat('=')) {
            if (e instanceof Variable v) {
                Expression value = expression();
                return c -> { c.variable(v.name, value); return Vec.NAN; };
            } else
                throw new RuntimeException("Variable expected before '='");
        } else
            return e;
    }

}
