package saka1029.util.eval;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * SYNTAX
 * <pre>
 * statement       = expression
 *                 | define-variable
 *                 | define-function
 * define-variable = ID '=' expression
 * define-function = ID '(' [ ID { ',' ID } ] ')' '=' expression
 * expression      = [ '+' | '-' ] term { [ '+' | '-' ] term }
 * term            = factor { [ '*' | '/' | '%' ] factor }
 * factor          = primary { '^' factor }
 * primary         = ID [ '(' [ expression { ',' expression } ] ')' ]
 *                 | NUMBER
 *                 | '(' expression ')'
 * </pre>
 */
public class Parser {

    enum TokenType { ID, NUMBER, OTHER }

    final Reader reader;
    int ch;
    String token;
    TokenType type;

    Parser(Reader reader) {
        this.reader = reader;
        ch();
        token();
    }

    public static Parser of(Reader reader) {
        return new Parser(reader);
    }

    public static Parser of(String source) {
        return new Parser(new StringReader(source));
    }

    static boolean isDigit(int ch) {
        return ch >= '0' && ch <= '9';
    }

    static boolean isOperatorChar(int ch) {
        return switch (ch) {
            case '!', '$', '%', '&', '-', '=', '^', '~' -> true;
            case '|', '@', '+', '*', '<', '>', '/', '?' -> true;
            case '.', ';', ':'-> true;
            default -> false;
        };
    }

    static boolean isIdFirstChar(int ch) {
        return Character.isAlphabetic(ch) || ch == '_';
    }

    static boolean isIdRestChar(int ch) {
        return isIdFirstChar(ch) || isDigit(ch);
    }

    int ch() {
        try {
            return ch = reader.read();
        } catch (IOException e) {
            throw new EvalException(e);
        }
    }

    // 本来の浮動小数点パターン
    // static final Pattern NUMBER = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?");
    static final Pattern NUMBER = Pattern.compile("[-+]?[0-9]+(\\.[0-9]+)?([eE][-+]?[0-9]+)?");

    StringBuilder sb = new StringBuilder();

    void sbClear() {
        sb.setLength(0);
    }

    void sbAppend(int ch) {
        sb.append((char)ch);
        ch();
    }

    void sbAppendDigit() {
        do {
            sbAppend(ch);
        } while (isDigit(ch));
    }

    String num() {
        sbAppendDigit();
        if (ch == '.') {
            sbAppend(ch); // '.'
            if (!isDigit(ch))
                throw new EvalException("Illegal number: '%s%c'", sb, ch);
            sbAppendDigit();
        }
        if (ch == 'e' || ch == 'E') {
            sbAppend(ch); // 'e' or 'E'
            if (ch == '+' || ch == '-')
                sbAppend(ch); // '+' or '-'
            if (!isDigit(ch))
                throw new EvalException("Illegal number format: '%s%c'", sb, ch);
            sbAppendDigit();
        }
        type = TokenType.NUMBER;
        return sb.toString();
    }

    String op() {
        while (isOperatorChar(ch))
            sbAppend(ch);
        return sb.toString();
    }

    String id() {
        while (isIdRestChar(ch))
            sbAppend(ch);
        type = TokenType.ID;
        return sb.toString();
    }

    String token() {
        type = TokenType.OTHER;
        while (Character.isWhitespace(ch))
            ch();
        return token = switch (ch) {
            case -1 -> null;
            case '(', ')', ',' -> {
                int c = ch;
                ch();
                yield Character.toString(c);
            }
            case '+', '-' -> {
                sbClear();
                sbAppend(ch);
                yield isDigit(ch) ? num() : op();
            }
            default -> {
                sbClear();
                if (isDigit(ch))
                    yield num();
                else if (isIdFirstChar(ch)) 
                    yield id();
                else if (isOperatorChar(ch))
                    yield op();
                else
                    throw new EvalException("Unknown char '%c'", (char) ch);
            }
        };
    }

    static boolean eq(String s, String... comps) {
        if (s != null)
            for (String c : comps)
                if (s.equals(c))
                    return true;
        return false;
    }

    Expression variableOrFuncall() {
        String name = token;
        token();  // skip name
        if (eq(token, "(")) {
            token();  // skip "("
            List<Expression> args = new ArrayList<>();
            if (token != null && !eq(token, ")")) {
                args.add(expression());
                while (eq(token, ",")) {
                    token();  // skip ","
                    args.add(expression());
                }
            }
            if (!eq(token, ")"))
                throw new EvalException("')' expected");
            token();
            return Funcall.of(name, args.toArray(Expression[]::new));
        } else
            return Variable.of(name);
    }

    Expression primary() {
        Expression e;
        if (type == TokenType.NUMBER) {
            e = Number.of(Double.parseDouble(token));
            token();
        } else if (type == TokenType.ID) {
            e = variableOrFuncall();
        } else if (eq(token, "(")) {
            token();  // skip "("
            e = expression();
            if (!eq(token, ")"))
                throw new EvalException("')' expected");
            token();
        } else
            throw new EvalException("Unknown token '%s'", token);
        return e;
    }

    Expression factor() {
        Expression e = primary();
        while (eq(token, "^")) {
            String op = token;
            token();
            e = Funcall.of(op, e, factor());
        }
        return e;
    }

    Expression term() {
        Expression e = factor();
        while (eq(token, "*", "/", "%")) {
            String op = token;
            token();
            e = Funcall.of(op, e, factor());
        }
        return e;
    }

    Expression expression() {
        if (token == null)
            return null;
        Expression e = term();
        while (eq(token, "+", "-")) {
            String op = token;
            token();
            e = Funcall.of(op, e, term());
        }
        return e;
    }

    Expression userdef(Funcall f) {
        List<String> args = new ArrayList<>();
        for (Expression a : f.arguments)
            if (a instanceof Variable v)
                args.add(v.name);
            else
                throw new EvalException("Variable expected but: %s", a);
        UserFunc func = UserFunc.of(expression(), args.toArray(String[]::new));
        return c -> { c.function(f.name, func); return Double.NaN; };
    }

    public Expression statement() {
        if (token == null)
            return null;
        Expression e = expression();
        if (eq(token, "=")) {
            token(); // skip "="
            if (e instanceof Variable v) {
                Expression body = expression();
                return c -> { c.variable(v.name, body); return Double.NaN; };
            } else if (e instanceof Funcall f) {
                return userdef(f);
            } else
                throw new EvalException("Variable of function header expected but: %s", e);
        } else
            return e;
    }

    public List<Expression> readAll() {
        List<Expression> list = new ArrayList<>();
        Expression e;
        while ((e = statement()) != null)
            list.add(e);
        return list;
    }

    public List<String> tokens() {
        List<String> list = new ArrayList<>();
        while (token != null) {
            list.add(token);
            token();
        }
        return list;
    }

}
