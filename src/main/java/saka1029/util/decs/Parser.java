package saka1029.util.decs;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.jline.reader.ParsedLine;
import org.jline.reader.SyntaxError;
import saka1029.util.decs.Context.Undo;
import saka1029.util.decs.Scanner.Token;
import saka1029.util.decs.Scanner.TokenType;

/**
 * <pre>
 * syntax:
 * statement    = expression
 *              | id '=' expression
 *              | id id '=' expression
 *              | id id id '=' expression
 *              | 'exit'
 *              | 'help' name
 *              | 'solve' expression
 * expression   = binary { ',' binary }
 * binary       = or { bop or }
 * or           = and { 'or' and }
 * and          = comp { 'and' comp }
 * comp         = add { cop add }
 * add          = mult { ( '+' | '-' ) mult }
 * mult         = power { ( '*' | '/' | '%' ) power }
 * power        = unary [ '^' power ]
 * unary        = uop unary | primary
 * primary      = '(' [ expression ] ')' | id | num
 * 
 * name         = ',' | 'or' | 'and' | cop
 *              | '+' | '-' | '*' | '/' | '%' | '^'
 * cop          = '==' | '!=' | '>' | '>=' | '<' | '<='
 * uop          = id // defined in context
 * bop          = id // defined in context
 * </pre>
 */
public class Parser implements org.jline.reader.Parser {
    static final Token END = new Token(TokenType.END, "EOF");

    public final Context context;
    List<Token> tokens;
    Token token;
    int index = 0;
    Scanner scanner = new Scanner();
    List<String> variables = new ArrayList<>();

    public Parser(Context context) {
        this.context = context;
    }

    public Parser() {
        this(new Context());
    }

    Token get() {
        return token = index < tokens.size() ? tokens.get(index++) : END;
    }

    boolean is(TokenType expected) {
        return token.type == expected;
    }

    boolean eat(TokenType expected) {
        if (token.type == expected) {
            get();
            return true;
        }
        return false;
    }

    DecsException error(String format, Object... args) {
        return new DecsException(format, args);
    }

    Expression primary() {
        if (eat(TokenType.LP)) {
            Expression e = expression();
            if (!eat(TokenType.RP))
                throw error("')' expected");
            return e;
        } else if (is(TokenType.NUM)) {
            BigDecimal[] decs = Decs.decs(token.string);
            get();  // skip NUM
            return c -> decs;
        } else if (is(TokenType.ID)) {
            String name = token.string;
            get();  // skip ID
            variables.add(name);
            return c -> c.variable(name).expression.apply(c);
        } else
            throw error("Unexpected token '%s'", token.string);
    }

    Expression unary() {
        if (is(TokenType.ID) && context.isUnary(token.string)) {
            String name = token.string;
            get();  // skip ID
            Expression arg = unary();
            return c -> c.unary(name).expression.apply(c, arg.apply(c));
        } else
            return primary();
    }

    Expression power() {
        Expression e = unary();
        if (eat(TokenType.POW)) {
            Expression left = e, right = power();
            return c -> Decs.pow(left.apply(c), right.apply(c));
        }
        return e;
    }

    Expression mult() {
        Expression e = power();
        while (true) {
            Expression left = e;
            if (eat(TokenType.MULT)) {
                Expression right = power();
                e = c -> Decs.multiply(left.apply(c), right.apply(c));
            } else if (eat(TokenType.DIV)) {
                Expression right = power();
                e = c -> Decs.divide(left.apply(c), right.apply(c));
            } else if (eat(TokenType.MOD)) {
                Expression right = power();
                e = c -> Decs.mod(left.apply(c), right.apply(c));
            } else 
                break;
        }
        return e;
    }

    Expression add() {
        Expression e = mult();
        while (true) {
            Expression left = e;
            if (eat(TokenType.PLUS)) {
                Expression right = mult();
                e = c -> Decs.add(left.apply(c), right.apply(c));
            } else if (eat(TokenType.MINUS)) {
                Expression right = mult();
                e = c -> Decs.subtract(left.apply(c), right.apply(c));
            }
            else 
                break;
        }
        return e;
    }

    Expression comp() {
        Expression e = add();
        if (eat(TokenType.EQ)) {
            Expression right = add();
            return c -> Decs.eq(e.apply(c), right.apply(c));
        } else if (eat(TokenType.NE)) {
            Expression right = add();
            return c -> Decs.ne(e.apply(c), right.apply(c));
        } else if (eat(TokenType.GT)) {
            Expression right = add();
            return c -> Decs.gt(e.apply(c), right.apply(c));
        } else if (eat(TokenType.GE)) {
            Expression right = add();
            return c -> Decs.ge(e.apply(c), right.apply(c));
        } else if (eat(TokenType.LT)) {
            Expression right = add();
            return c -> Decs.lt(e.apply(c), right.apply(c));
        } else if (eat(TokenType.LE)) {
            Expression right = add();
            return c -> Decs.le(e.apply(c), right.apply(c));
        }
        return e;
    }

    Expression and() {
        Expression e = comp();
        while (true) {
            Expression left = e;
            if (eat(TokenType.OR)) {
                Expression right = comp();
                e = c -> Decs.and(left.apply(c), right.apply(c));
            } else
                break;
        }
        return e;
    }

    Expression or() {
        Expression e = and();
        while (true) {
            Expression left = e;
            if (eat(TokenType.OR)) {
                Expression right = add();
                e = c -> Decs.or(left.apply(c), right.apply(c));
            } else
                break;
        }
        return e;
    }

    Expression binary() {
        Expression e = or();
        while (true) {
            Expression left = e;
            if (is(TokenType.ID) && context.isBinary(token.string)) {
                String name = token.string;
                get();  // skip ID
                Expression right = or();
                e = c -> c.binary(name).expression.apply(c, left.apply(c), right.apply(c));
            } else
                break;
        }
        return e;
    }

    Expression expression() {
        Expression e = binary();
        while (true) {
            Expression left = e;
            if (eat(TokenType.COMMA)) {
                Expression right = binary();
                e = c -> Decs.concat(left.apply(c), right.apply(c));
            } else
                break;
        }
        return e;
    }


    /**
     * variable = 3 + 2
     * input.length = 5
     * index = 1;
     * token = ID:"variable"
     */
    Expression defineVariable() {
        String name = token.string;
        get();   // skip ID (name)
        get();   // skip '='
        Expression e = expression();
        return c -> {
            c.variable(name, e, "variable " + name);
            return Decs.NO_VALUE;
        };
    }

    Expression defineUnary() {
        String name = token.string;
        get();   // skip ID (name)
        String arg = token.string;
        get();   // skip ID (argument)
        get();   // skip '='
        Expression e = expression();
        return c -> {
            Unary unary = (cc, a) -> {
                try (Undo u = cc.variableTemp(arg, ccc -> a, "temp unary argument " + arg)) {
                    return e.apply(cc);
                }
            };
            c.unary(name, unary, "unary " + name);
            return Decs.NO_VALUE;
        };
    }

    Expression defineBinary() {
        String left = token.string;
        get();   // skip ID (left)
        String name = token.string;
        get();   // skip ID (name)
        String right = token.string;
        get();   // skip ID (right)
        get();   // skip '='
        Expression e = expression();
        return c -> {
            Binary binary = (cc, l, r) -> {
                try (Undo ul = cc.variableTemp(left, ccc -> l, "temp binary left argument " + left);
                    Undo ur = cc.variableTemp(right, ccc -> r, "temp binary right argument " + right)) {
                    return e.apply(cc);
                }
            };
            c.binary(name, binary, "binary " + name);
            return Decs.NO_VALUE;
        };
    }

    Expression statement() {
        if (tokens.size() >= index + 2
            && token.type == TokenType.ID
            && tokens.get(index).type == TokenType.ASSIGN)
            return defineVariable();
        else if (tokens.size() >= index + 3
            && token.type == TokenType.ID
            && tokens.get(index).type == TokenType.ID 
            && tokens.get(index + 1).type == TokenType.ASSIGN)
            return defineUnary();
        else if (tokens.size() >= index + 4
            && token.type == TokenType.ID
            && tokens.get(index).type == TokenType.ID 
            && tokens.get(index + 1).type == TokenType.ID 
            && tokens.get(index + 2).type == TokenType.ASSIGN)
            return defineBinary();
        else
            return expression();
    }

    public Expression parse(String input) {
        tokens = scanner.scan(input);
        index = 0;
        get();
        variables.clear();
        return new ExpressionWithVariables(statement(), variables);
    }

    public BigDecimal[] eval(String input) {
        return parse(input).apply(context);
    }

    @Override
    public ParsedLine parse(String line, int cursor, ParseContext context) throws SyntaxError {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'parse'");
    }
}
