package saka1029.util.decs;

import java.math.BigDecimal;
import java.util.List;
import org.jline.reader.ParsedLine;
import org.jline.reader.SyntaxError;
import saka1029.util.decs.Scanner.Token;
import saka1029.util.decs.Scanner.TokenType;

/**
 * <pre>
 * syntax:
 * statement    = expression
 *              | id '=' expression
 *              | name id '=' expression
 *              | id name id '=' expression
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
 * power        = unary [ '^' uop ]
 * unary        = primary | [ '@' ] uop unary
 * primary      = '(' [ expression ] ')' | id | num
 * 
 * cop          = '==' | '!=' | '>' | '>=' | '<' | '<='
 * name         = id | special
 * uop          = id | special // defined in context
 * bop          = id | special // defined in context
 * </pre>
 */
public class Parser implements org.jline.reader.Parser {
    static final Token END = new Token(TokenType.END, "EOF");

    public final Context context;
    List<Token> tokens;
    Token token, prev;
    int index = 0;
    Scanner scanner = new Scanner();

    public Parser(Context context) {
        this.context = context;
    }

    Token get() {
        return token = index < tokens.size() ? tokens.get(index++) : END;
    }

    boolean eat(TokenType expected) {
        if (token.type == expected) {
            prev = token;
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
        } else if (eat(TokenType.NUM)) {
            BigDecimal[] decs = Decs.decs(prev.string);
            return c -> decs;
        } else if (eat(TokenType.ID) && context.isVariable(prev.string)) {
            String name = prev.string;
            return c -> c.variable(name).expression.apply(c);
        } else
            throw error("Unexpected token '%s'", token.string);
    }

    Expression unary() {
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
            if (eat(TokenType.ID) && context.isBinary(prev.string)) {
                String name = prev.string;
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

    Expression statement() {
        return expression();
    }

    public Expression parse(String input) {
        tokens = scanner.scan(input);
        index = 0;
        get();
        return statement();
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
