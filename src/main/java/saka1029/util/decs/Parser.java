package saka1029.util.decs;

import java.math.BigDecimal;
import java.util.List;
import org.jline.reader.ParsedLine;
import org.jline.reader.SyntaxError;
import saka1029.util.decs.Scanner.Token;
import saka1029.util.decs.Scanner.TokenType;

public class Parser implements org.jline.reader.Parser {
    List<Token> tokens;
    Token token, prev;
    int index = 0;
    Scanner scanner = new Scanner();

    Token get() {
        return token = index < tokens.size() ? tokens.get(index++) : null;
    }

    boolean eat(TokenType expected) {
        if (token != null && token.type == expected) {
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
        if (eat(TokenType.NUM)) {
            System.out.println("prev.string=" + prev.string);
            BigDecimal[] decs = Decs.decs(prev.string);
            return c -> decs;
        } else if (eat(TokenType.LP)) {
            Expression e = expression();
            if (!eat(TokenType.RP))
                throw error("')' expected");
            return e;
        } else
            throw error("Unexpected token '%s'", token.string);
    }

    Expression expression() {
        Expression e = primary();
        while (true) {
            if (eat(TokenType.PLUS)) {
                Expression left = e, right = primary();
                e = c -> Decs.add(left.apply(c), right.apply(c));
            } else if (eat(TokenType.MINUS)) {
                Expression left = e, right = primary();
                e = c -> Decs.subtract(left.apply(c), right.apply(c));
            }
            else 
                break;
        }
        return e;
    }

    public Expression parse(String input) {
        tokens = scanner.scan(input);
        index = 0;
        get();
        return expression();
    }

    @Override
    public ParsedLine parse(String line, int cursor, ParseContext context) throws SyntaxError {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'parse'");
    }
}
