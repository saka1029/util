package saka1029.util.vector;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import saka1029.util.vector.Lexer.Token;
 
/**
 * SYNTAX:
 * expression = term { ( '+' | '-' ) term }
 * term       = factor { ( '*' | '/' ) factor }
 * factor     = unary { '^' factor }
 * unary      = vector | UOP unary
 * vector     = primary { primary }
 * primary    = '(' expression ')' | ID | NUMBER
 */
public class Parser {
    final Lexer lexer;
    static final Map<String, Function<Expression, Expression>> uops = new HashMap<>();
    static {
        uops.put("-", e -> c -> e.eval(c).apply(a -> a.negate()));
        uops.put("sqrt", e -> c -> e.eval(c).apply(a -> a.sqrt(Vector.MATH_CONTEXT)));
        uops.put("sum", e -> c -> e.eval(c).insert((a, b) -> a.add(b)));
        uops.put("+", e -> c -> e.eval(c).insert((a, b) -> a.add(b)));
        uops.put("*", e -> c -> e.eval(c).insert((a, b) -> a.multiply(b)));
        uops.put("length", e -> c -> Vector.of(e.eval(c).length()));
        uops.put("iota", e -> c -> {
            Vector v = e.eval(c);
            if (v.length() != 1)
                throw new VectorException("Required one argument but %d", v.length());
            return Vector.iota(v.get(0).intValue());
        });
        uops.put("iota0", e -> c -> {
            Vector v = e.eval(c);
            if (v.length() != 1)
                throw new VectorException("Required one argument but %d", v.length());
            return Vector.iota0(v.get(0).intValue());
        });
        uops.put("ave", e -> c -> {
            Vector v = e.eval(c);
            return Vector.of(Vector.divide(v.insert((a, b) -> a.add(b)).get(0), Vector.number(v.length())));
        });
    }

    Token token;

    Parser(String input) {
        lexer = Lexer.of(input);
        get();
    }

    public static Parser of(String input) {
        return new Parser(input);
    }

    Token get() {
        return token = lexer.read();
    }

    boolean eat(int expected) {
        if (token != null && token.type() == expected) {
            get();
            return true;
        }
        return false;
    }

    Expression primary() {
        Expression e;
        if (eat('(')) {
            e = expression();
            if (!eat(')'))
                throw new VectorException("')' expected");
        } else if (token.type() == 'i') {
            e = Variable.of(token.string());
            get();
        } else if (token.type() == 'n') {
            e = Vector.of(token.number());
            get();
        } else
            throw new VectorException("Unknown token: '%s'", token.string());
        return e;
    }

    static boolean isPrime(Token token) {
        if (token == null)
            return false;
        return switch (token.type()) {
            case '(', 'n', 'i' -> true;
            default -> false;
        };
    }

    Expression vector() {
        Expression e = primary();
        while (isPrime(token)) {
            Expression l = e, r = primary();
            e = c -> l.eval(c).append(r.eval(c));
        }
        return e;
    }

    Expression unary() {
        if (token == null)
            throw new VectorException("Unexpected end");
        String name = token.string();
        if (uops.containsKey(name)) {
            get();
            Expression e = unary();
            return uops.get(name).apply(e);
        } else
            return vector();
    }

    Expression factor() {
        Expression e = unary();
        while (eat('^')) {
            Expression l = e, r = factor();
            e = c -> l.eval(c).apply(
                (a, b) -> Vector.pow(a, b), r.eval(c));
        }
        return e;
    }

    Expression term() {
        Expression e = factor();
        while (true)
            if (eat('*')) {
                Expression l = e, r = factor();
                e = c -> l.eval(c).apply((a, b) -> a.multiply(b), r.eval(c));
            } else if (eat('/')) {
                Expression l = e, r = factor();
                e = c -> l.eval(c).apply((a, b) -> Vector.divide(a, b), r.eval(c));
            } else
                break;
        return e;

    }

    public Expression expression() {
        Expression e = term();
        while (true)
            if (eat('+')) {
                Expression l = e, r = term();
                e = c -> l.eval(c).apply((a, b) -> a.add(b), r.eval(c));
            } else if (eat('-')) {
                Expression l = e, r = term();
                e = c -> l.eval(c).apply((a, b) -> a.subtract(b), r.eval(c));
            } else
                break;
        return e;
    }

    public Expression statement() {
        Expression e = expression();
        if (eat('=')) {
            if (e instanceof Variable v) {
                Expression value = expression();
                return c -> { c.variable(v.name, value); return Vector.NaN; };
            } else
                throw new VectorException("Variable expected before '='");
        } else
            return e;
    }

}
