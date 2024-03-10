package saka1029.util.vector;

import java.math.BigDecimal;
import java.util.Map;
import java.util.function.Function;
import saka1029.util.vector.Lexer.Token;
 
/**
 * SYNTAX:
 * expression = term { ( '+' | '-' ) term }
 * term       = factor { ( '*' | '/' ) factor }
 * factor     = unary { '^' factor }
 * unary      = vector | ( '-' | ID ) unary
 * vector     = primary { primary }
 * primary    = '(' expression ')' | ID | NUMBER
 */
public class Parser {
    final Lexer lexer;
    final Map<String, Function<Expression, Expression>> uops = Map.of(
        "-", e -> c -> e.eval(c).apply(a -> a.negate()),
        "sum", e -> c -> e.eval(c).insert((a, b) -> a.add(b))
    );

    Token token;

    Parser(String input) {
        lexer = Lexer.of(input);
        get();
    }

    Token get() {
        return token = lexer.read();
    }

    boolean eat(int expected) {
        if (token.type() == expected) {
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
        int type = token.type();
        String name = token.string();
        if ((type == '-' || type == 'i') && uops.containsKey(name)) {
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
                (a, b) -> new BigDecimal(Math.pow(a.doubleValue(), b.doubleValue())), r.eval(c));
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
                e = c -> l.eval(c).apply((a, b) -> a.divide(b), r.eval(c));
            } else
                break;
        return e;

    }

    Expression expression() {
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

}
