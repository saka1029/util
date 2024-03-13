package saka1029.util.dentaku;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import saka1029.util.dentaku.Lexer.Token;
import saka1029.util.dentaku.Lexer.Type;
 
/**
 * SYNTAX:
 * <pre>
 * statement  = [ ID '=' ] expression
 * expression = term { ( '+' | '-' ) term }
 * term       = factor { ( '*' | '/' ) factor }
 * factor     = unary { '^' factor }
 * unary      = vector | UOP unary
 * vector     = primary { primary }
 * primary    = '(' expression ')' | ID | NUMBER
 * </pre>
 */
public class Parser {
    static final Map<String, Function<Expression, Expression>> uops = new HashMap<>();

    static Vector evalOne(Expression e, Context c) {
        Vector v = e.eval(c);
        if (v.length() != 1)
            throw new VectorException("Required one argument but %d", v.length());
        return v;
    }

    public static UnaryOperator<BigDecimal> unaryDouble(DoubleUnaryOperator operator) {
        return b -> Vector.number(operator.applyAsDouble(b.doubleValue()));
    }

    static {
        uops.put("-", e -> c -> e.eval(c).apply(a -> a.negate()));
        uops.put("sum", e -> c -> e.eval(c).insert((a, b) -> a.add(b)));
        uops.put("+", e -> c -> e.eval(c).insert((a, b) -> a.add(b)));
        uops.put("*", e -> c -> e.eval(c).insert((a, b) -> a.multiply(b)));
        uops.put("length", e -> c -> Vector.of(e.eval(c).length()));
        uops.put("reverse", e -> c -> e.eval(c).reverse());
        uops.put("sort", e -> c -> e.eval(c).sort());
        uops.put("iota", e -> c -> Vector.iota(evalOne(e, c).get(0).intValue(), 1));
        uops.put("iota0", e -> c -> Vector.iota(evalOne(e, c).get(0).intValue(), 0));
        uops.put("ave", e -> c -> {
            Vector v = e.eval(c);
            return Vector.of(Vector.divide(v.insert((a, b) -> a.add(b)).get(0), Vector.number(v.length())));
        });
        uops.put("sqrt", e -> c -> e.eval(c).apply(a -> a.sqrt(Vector.MATH_CONTEXT)));
        uops.put("abs", e -> c -> e.eval(c).apply(BigDecimal::abs));
        uops.put("sin", e -> c -> e.eval(c).apply(unaryDouble(Math::sin)));
        uops.put("asin", e -> c -> e.eval(c).apply(unaryDouble(Math::asin)));
        uops.put("cos", e -> c -> e.eval(c).apply(unaryDouble(Math::cos)));
        uops.put("acos", e -> c -> e.eval(c).apply(unaryDouble(Math::acos)));
        uops.put("tan", e -> c -> e.eval(c).apply(unaryDouble(Math::tan)));
        uops.put("atan", e -> c -> e.eval(c).apply(unaryDouble(Math::atan)));
        uops.put("log", e -> c -> e.eval(c).apply(unaryDouble(Math::log)));
        uops.put("log10", e -> c -> e.eval(c).apply(unaryDouble(Math::log10)));
    }

    final List<Token> tokens;
    int index;
    Token token;

    Parser(String input) {
        this.tokens = Lexer.of(input).tokens();
        this.index = 0;
        get();
    }

    public static Parser of(String input) {
        return new Parser(input);
    }

    public static Expression parse(String input) {
        Parser parser = new Parser(input);
        Expression e = parser.statement();
        if (e == null)
            throw new VectorException("No expression");
        if (parser.token != Token.END)
            throw new VectorException("Extra string '%s'", parser.token.string());
        return e;
    }

    Token get() {
        return token = index < tokens.size() ? tokens.get(index++) : Token.END;
    }

    boolean eat(Token expected) {
        if (token == expected) {
            get();
            return true;
        }
        return false;
    }

    Expression primary() {
        Expression e;
        if (eat(Token.LP)) {
            e = expression();
            if (!eat(Token.RP))
                throw new VectorException("')' expected");
        } else if (token.type() == Type.ID) {
            e = Variable.of(token.string());
            get();
        } else if (token.type() == Type.NUMBER) {
            e = Vector.of(token.number());
            get();
        } else
            throw new VectorException("Unknown token: '%s'", token.string());
        return e;
    }

    static boolean isPrime(Token token) {
        if (token == Token.LP)
            return true;
        else if (token.type() == Type.ID || token.type() == Type.NUMBER)
            return true;
        else
            return false;
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
        if (token == Token.END)
            throw new VectorException("Unexpected end");
        Function<Expression, Expression> e;
        if ((e = uops.get(token.string())) != null) {
            get();
            Expression u = unary();
            return e.apply(u);
        } else
            return vector();
    }

    Expression factor() {
        Expression e = unary();
        while (eat(Token.CARET)) {
            Expression l = e, r = factor();
            e = c -> l.eval(c).apply(
                (a, b) -> Vector.pow(a, b), r.eval(c));
        }
        return e;
    }

    Expression term() {
        Expression e = factor();
        while (true)
            if (eat(Token.STAR)) {
                Expression l = e, r = factor();
                e = c -> l.eval(c).apply((a, b) -> a.multiply(b), r.eval(c));
            } else if (eat(Token.SLASH)) {
                Expression l = e, r = factor();
                e = c -> l.eval(c).apply((a, b) -> Vector.divide(a, b), r.eval(c));
            } else if (eat(Token.PERCENT)) {
                Expression l = e, r = factor();
                e = c -> l.eval(c).apply((a, b) -> Vector.remainder(a, b), r.eval(c));
            } else
                break;
        return e;

    }

    public Expression expression() {
        if (token == Token.END)
            return null;
        Expression e = term();
        while (true)
            if (eat(Token.PLUS)) {
                Expression l = e, r = term();
                e = c -> l.eval(c).apply((a, b) -> a.add(b), r.eval(c));
            } else if (eat(Token.MINUS)) {
                Expression l = e, r = term();
                e = c -> l.eval(c).apply((a, b) -> a.subtract(b), r.eval(c));
            } else
                break;
        return e;
    }

    public Expression statement() {
        if (token == Token.END)
            return null;
        Expression e = expression();
        if (eat(Token.EQ)) {
            if (e instanceof Variable v) {
                Expression value = expression();
                return c -> { c.variable(v.name, value); return Vector.NaN; };
            } else
                throw new VectorException("Variable expected before '='");
        } else
            return e;
    }

}
