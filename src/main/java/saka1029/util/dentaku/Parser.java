package saka1029.util.dentaku;

import java.util.List;
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
    final Operators ops;
    final List<Token> tokens;
    int index;
    Token token;

    Parser(Operators ops, String input) {
        this.ops = ops;
        this.tokens = Lexer.of(input).tokens();
        this.index = 0;
        get();
    }

    public static Parser of(Operators ops, String input) {
        return new Parser(ops, input);
    }

    public static Expression parse(Operators ops, String input) {
        Parser parser = new Parser(ops, input);
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

    Token peek(int offset) {
        int i = index + offset;
        return i < tokens.size() ? tokens.get(i) : Token.END;
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
        UnaryOperator<Expression> e;
        if ((e = ops.unary(token.string())) != null) {
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

    public Expression defineVariable() {
        if (token.type() != Type.ID)
            throw new VectorException("ID expected before '='");
        String name = token.string();
        get(); // skip ID
        get(); // skip '='
        Expression body = expression();
        return c -> { c.variable(name, body); return Vector.NaN; };
    }

    public Expression defineUnary() {
        if (token.type() != Type.ID || peek(0).type() != Type.ID)
            throw new VectorException("Two IDs expected before '='");
        String name = token.string();
        get(); // skip ID 
        String arg = token.string();
        get(); // skip ID
        get(); // skip '='
        Expression body = expression();
        UnaryCall call = new UnaryCall(arg, body);
        return c -> { c.unary(name, call); return Vector.NaN; };
    }

    public Expression statement() {
        if (token == Token.END)
            return null;
        else if (peek(0) == Token.EQ)
            return defineVariable();
        else if (peek(1) == Token.EQ)
            return defineUnary();
        else
            return expression();
    }

}
