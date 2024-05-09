package saka1029.util.dentaku;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import saka1029.util.dentaku.Tokenizer.Token;
import saka1029.util.dentaku.Tokenizer.Type;
import static saka1029.util.dentaku.Value.*;

public class Parser {
    final Context context;
    final String input;
    final List<Token> tokens;
    final Set<String> variables = new HashSet<>();
    int index = 0;
    Token token;

    Parser(Context context, List<Token> tokens, String input) {
        this.input = input;
        this.context = context;
        this.tokens = tokens;
        get();
    }

    public static Expression parse(Context context, String input) {
        input = input.trim();
        Parser parser = new Parser(context, Tokenizer.tokens(input), input);
        Expression expression = parser.statement();
        if (expression == null)
            throw new ValueException("No token");
        if (parser.token != Tokenizer.END)
            throw new ValueException("Extra token '%s'", parser.token.string());
        return expression;
    }

    Token get() {
        return token = index < tokens.size() ? tokens.get(index++) : Tokenizer.END;
    }

    Token peek(int i) {
        int p = i + index;
        return p < tokens.size() ? tokens.get(p) : Tokenizer.END;
    }
    
    boolean is(Token token, Type... expected) {
        return Arrays.stream(expected)
            .anyMatch(t -> t == token.type());
    }

    Expression primary() {
        if (is(token, Type.LP)) {
            get(); // skip '('
            Expression e = expression();
            if (!is(token, Type.RP))
                throw new ValueException("')' expected");
            get(); // skip ')'
            return e;
        } if (is(token, Type.ID)) {
            String name = token.string();
            variables.add(name);
            get(); // skip variable
            return c -> c.variable(name).t.eval(c);
        } if (is(token, Type.NUMBER)) {
            BigDecimal[] value = new BigDecimal[] {dec(token.string())};
            get(); // skip number
            return c -> value;
        } else
            throw new ValueException("Unknown token '%s'", token.string());
    }

    Expression factor() {
        if (is(token, Type.SELECT)) {
            String selectName = token.string();
            get(); // skip select
            if (!context.isUnary(token.string()))
                throw new ValueException("Unary expected after '%s'", selectName);
            String name = token.string();
            get(); // skip unary
            Expression arg = factor();
            return c -> c.unary(name).t.select().apply(c, arg.eval(c));
        } else if (context.isUnary(token.string())) {
            String name = token.string();
            get(); // skip unary
            Expression arg = factor();
            return c -> c.unary(name).t.apply(c, arg.eval(c));
        } else
            return primary();
    }

    Expression term() {
        Expression e = factor();
        while (true)
            if (is(token, Type.SELECT)) {
                String selectName = token.string();
                get(); // skip select
                if (!context.isBinary(token.string()))
                    throw new ValueException("Binary expected after '%s'", selectName);
                String name = token.string();
                get(); // skip binary
                Expression l = e, r = factor();
                e = c -> c.binary(name).t.select().apply(c, l.eval(c), r.eval(c));
            } else if (context.isBinary(token.string())) {
                String name = token.string();
                get(); // skip binary
                Expression l = e, r = factor();
                e = c -> c.binary(name).t.apply(c, l.eval(c), r.eval(c));
            } else
                break;
        return ExpressionVars.of(e, variables);
    }

    Expression expression() {
        Expression e = term();
        while (true)
            if (is(token, Type.CONCAT)) {
                get(); // skip ','
                Expression l = e, r = term();
                e = c -> Context.concat(c, l.eval(c), r.eval(c));
            } else
                break;
        return ExpressionVars.of(e, variables);
    }

    void checkId(Token token) {
        if (!is(token, Type.ID))
            throw new ValueException("ID expected but '%s'", token.string());
        if (context.isOperator(token.string()))
            throw new ValueException("'%s' is already used as operator name", token.string());
    }

    Expression defineVariable() {
        String name = token.string();
        checkId(token);
        get(); // skip ID
        get(); // skip ASSIGN
        Expression e = expression();
        return c -> { c.variable(name, e, input); return NaN; };
    }

    Expression defineUnary() {
        String name = token.string();
        checkId(token);
        get(); // skip ID
        String variable = token.string();
        checkId(token);
        get(); // skip ID
        get(); // skip ASSIGN
        Expression e = expression();
        return c -> {
            c.unary(name, UnaryDefined.of(variable, e), input);
            return NaN;
        };
    }

    Expression defineBinary() {
        String leftName = token.string();
        checkId(token);
        get(); // skip ID
        String name = token.string();
        checkId(token);
        get(); // skip ID
        String rightName = token.string();
        checkId(token);
        get(); // skip ID
        get(); // skip ASSIGN
        Expression e = expression();
        return c -> {
            c.binary(name, (context, left, right) -> {
                Context child = context.child();
                child.variable(leftName, x -> left, leftName);
                child.variable(rightName, x -> right, rightName);
                return e.eval(child);
            }, input);
            return NaN;
        };
    }

    Expression statement() {
        variables.clear();
        if (is(token, Type.END))
            return null;
        if (is(peek(0), Type.ASSIGN))
            return defineVariable();
        else if (is(peek(1), Type.ASSIGN))
            return defineUnary();
        else if (is(peek(2), Type.ASSIGN))
            return defineBinary();
        else
            return expression();
    }
}
