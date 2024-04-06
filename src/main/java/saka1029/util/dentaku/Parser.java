package saka1029.util.dentaku;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import saka1029.util.dentaku.Lexer.Token;
import saka1029.util.dentaku.Lexer.Type;

/**
 * SYNTAX
 * <pre>
 * statement       = define-variable
 *                 | define-unary
 *                 | define-binary
 *                 | expression
 * define-variable = ID '=' expression
 * define-unary    = ID ID '=' expression
 * define-binary   = ID ID ID '=' expression
 * expression      = unary { [ '@' ] BOP unary }
 * unary           = sequence
 *                 | [ '@' ] UOP unary
 * sequence        = primary { primary }
 * primary         = '(' expression ')'
 *                 | VAR
 *                 | NUMBER { NUMBER }
 * </pre>
 */
public class Parser {
    final Operators operators;
    final String input;
    final List<Token> tokens;
    int index;
    Token token;
    final SortedSet<Variable> variableReferences = new TreeSet<>(Comparator.comparing(v -> v.name));

    private Parser(Operators operators, String input) {
        this.operators = operators;
        this.input = input.trim();
        this.tokens = Lexer.tokens(this.input);
        this.index = 0;
        get();
    }

    public static Parser of(Operators functions, String input) {
        return new Parser(functions, input);
    }

    public static Expression parse(Operators functions, String input) {
        Parser parser = of(functions, input);
        Expression result = parser.statement();
        if (parser.token.type() != Type.END)
            throw new ValueException("Extra tokens '%s'", parser.token.string());
        return result;
    }

    Token get() {
        return token = index < tokens.size() ? tokens.get(index++) : Lexer.END;
    }

    Token peek(int offset) {
        int p = index + offset;
        return p < tokens.size() ? tokens.get(p) : Lexer.END;
    }

    boolean is(Token token, Type... types) {
        for (Type t : types)
            if (token.type() == t)
                return true;
        return false;
    }

    boolean isUnary(Token token) {
        return is(token, Type.ID, Type.SPECIAL)
            && operators.unary(token.string()) != null;
    }

    boolean isBinary(Token token) {
        return is(token, Type.ID, Type.SPECIAL)
            && operators.binary(token.string()) != null;
    }

    String id(Token token) {
        if (!is(token, Type.ID))
            return null;
        return token.string();
    }

    Expression defineVariable() {
        String name = id(token);
        if (name == null)
            throw new ValueException("ID expected but '%s'", token.string());
        get(); // skip ID
        get(); // skip '='
        Expression e = expression();
        return c -> { c.variable(name, e, input); return Value.NaN; };
    }

    Expression defineUnary() {
        String operator = id(token);
        if (operator == null)
            throw new ValueException("ID expected but '%s'", token.string());
        get(); // skip operator
        String variable = id(token);
        if (variable == null)
            throw new ValueException("ID expected but '%s'", token.string());
        get(); // skip variable
        get(); // skip '='
        Expression body = expression();
        Unary unary = new UnaryCall(variable, body);
        return c -> { c.operators().unary(operator, unary, input); return Value.NaN; };
    }

    Expression defineBinary() {
        String left = id(token);
        if (left == null)
            throw new ValueException("ID expected but '%s'", token.string());
        get(); // skip left
        String operator = id(token);
        if (operator == null)
            throw new ValueException("ID expected but '%s'", token.string());
        get(); // skip operator
        String right = id(token);
        if (right == null)
            throw new ValueException("ID expected but '%s'", token.string());
        get(); // skip right
        get(); // skip '='
        Expression body = expression();
        Binary binary = new BinaryCall(left, right, body);
        return c -> { c.operators().binary(operator, binary, input); return Value.NaN; };
    }

    Expression primary() {
        Expression e;
        if (is(token, Type.LP)) {
            get(); // skip '('
            e = expression();
            if (!is(token, Type.RP))
                throw new ValueException("')' expected");
            get(); // skip ')'
        } else if (is(token, Type.ID)) {
            Variable v = Variable.of(token.string());
            variableReferences.add(v);
            e = v;
            get(); // ski ID
        } else if (is(token, Type.NUMBER)) {
            List<BigDecimal> list = new ArrayList<>();
            do {
                list.add(new BigDecimal(token.string()));
                get(); // skip NUMBER
            } while (is(token, Type.NUMBER));
            e = Value.of(list);
        } else
            throw new ValueException("Unknown token '%s'", token.string());
        return e;
    }

    boolean isPrimary(Token token) {
        return is(token, Type.LP, Type.NUMBER)
            || is(token, Type.ID)
                && !isUnary(token)
                && !isBinary(token);
                // && !isHigh(token);
    }

    Expression sequence() {
        Expression e = primary();
        while (isPrimary(token)) {
            Expression left = e, right = primary();
            e = c -> left.eval(c).append(right.eval(c));
        }
        return e;
    }

    Expression unary() {
        if (is(token, Type.FILTER)) {
            get(); // skip '@'
            if (!isUnary(token))
                throw new ValueException("Unary expected but '%s'", token.string());
            String unaryName = token.string();
            get(); // skip unary operator
            Expression e = unary();
            return c -> e.eval(c).filter(c, c.operators().unary(unaryName));

        } else if (isUnary(token)) {
            String unaryName = token.string();
            get();  // skip unary operator
            Expression e = unary();
            return c -> c.operators().unary(unaryName).apply(c, e.eval(c));
        } else
            return sequence();
    }

    Expression expression() {
        Expression e = unary();
        while (true) {
            if (is(token, Type.FILTER)) {
                get(); // skip '@'
                if (!isBinary(token))
                    throw new ValueException("Binary expected but '%s'", token.string());
                String binaryName = token.string();
                get();  // skip binary operator
                Expression left = e, right = unary();
                e = c -> left.eval(c).filter(
                    c, c.operators().binary(binaryName), right.eval(c));
            } else if (isBinary(token)) {
                String binaryName = token.string();
                get();  // skip binary operator
                Expression left = e, right = unary();
                e = c -> c.operators().binary(binaryName)
                    .apply(c, left.eval(c), right.eval(c));
            } else
                break;
        }
        return e;
    }

    public Expression statement() {
        if (is(token, Type.END))
            return null;
        else if (is(peek(0), Type.ASSIGN))
            return defineVariable();
        else if (is(peek(1), Type.ASSIGN))
            return defineUnary();
        else if (is(peek(2), Type.ASSIGN))
            return defineBinary();
        else {
            Expression e = expression();
            return new ExpressionVars() {
                @Override
                public Value eval(Context context) {
                    return e.eval(context);
                }
                @Override
                public Variable[] variableReferences() {
                    return variableReferences.toArray(Variable[]::new);
                }
                @Override
                public String toString() {
                    return input;
                }
            };
        }
    }
}
