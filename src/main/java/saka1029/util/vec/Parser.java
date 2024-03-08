package saka1029.util.vec;

import saka1029.util.vec.Scanner.Token;

/**
 * SYNTAX:
 * <pre>
 * statement  = [ ID '=' ] expression
 * expression = term { ( '+' | '-' ) term }
 * term       = factor { ( '*' | '/' ) factor }
 * factor     = sequence { '^' factor }
 * sequence   = primary { primary }
 * primary    = '(' expression ')' | ID | NUMBER
 * NUMBER     = DIGITS [ '.' DIGITS ] [ ( 'e' | 'E' ) [ '+' | '-' ] DIGITS ]
 * DIGITS     = '0'..'9' { '0'..'9' }
 * </pre>
 */
public class Parser {
    final Scanner input;
    Token token;

    private Parser(String input) {
        this.input = Scanner.of(input);
        get();
    }

    public static Parser of(String input) {
        return new Parser(input);
    }

    Token get() {
        return token = input.read();
    }

    boolean eat(int expected) {
        if (token.type() == expected) {
            get();
            return true;
        }
        return false;
    }

    boolean or(int... expected) {
        for (int e : expected)
            if (token.type() == e)
                return true;
        return false;
    }

    Expression primary() {
        Expression e;
        if (eat('(')) {
            e = expression();
            if (!eat(')'))
                throw new VecException("')' expected");
        } else if (token.type() == 'i') {
            e = Variable.of(token.string());
            get();
        } else if (token.type() == 'n') {
            e = Vec.of(token.number());
            get();
        } else
            throw new VecException("Unknown token: '%s'", token.string());
        return e;
    }

    static boolean isPrimary(Token token) {
        return switch (token.type()) {
            case '(', 'i', 'n' -> true;
            default -> false;
        };
    }

    Expression sequence() {
        Expression primary = primary();
        while (isPrimary(token)) {
            Expression left = primary, right = primary();
            // TODO: この連結方式は効率が悪い。
            primary = c -> left.eval(c).append(right.eval(c));
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
        int prefix = -1;
        if (or('-', '+', '*')) {
            prefix = token.type();
            get();
        }
        Expression e = term();
        Expression g = e;
        e = switch (prefix) {
            case '-' -> c -> Vec.calculate(a -> -a, g.eval(c)); // TODO: なぜinsertではないのか？
            case '+' -> c -> Vec.insert((a, b) -> a + b, g.eval(c));
            case '*' -> c -> Vec.insert((a, b) -> a * b, g.eval(c));
            default -> e;
        };
        while (true)
            if (eat('+')) {
                Expression left = e, right = term();
                e = c -> Vec.calculate((a, b) -> a + b, left.eval(c), right.eval(c));
            } else if (eat('-')) {
                Expression left = e, right = term();
                e = c -> Vec.calculate((a, b) -> a - b, left.eval(c), right.eval(c));
            } else
                break;
        return e;
    }

    public Expression statement() {
        Expression e = expression();
        if (eat('=')) {
            if (e instanceof Variable v) {
                Expression value = expression();
                return c -> { c.variable(v.name, value); return Vec.NAN; };
            } else
                throw new VecException("Variable expected before '='");
        } else
            return e;
    }

}
