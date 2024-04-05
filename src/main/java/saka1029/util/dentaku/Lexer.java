package saka1029.util.dentaku;

import java.util.ArrayList;
import java.util.List;

/**
 * SYNTAX
 * <pre>
 * LP       = '('
 * RP       = ')'
 * ASSIGN   = '='
 * SPECIAL  = '+' | '-' | '*' | '/' | '%' | '^'
 *          | '==' | '!=' | '<' | '<=' | '>' | '>='
 *          | '~' | '!~'
 * ID       = id-first { id-rest }
 * id-first = java-alphabetic | '_'
 * id-rest  = id-first | java-digit | '.'
 * NUMBER   = [ '-' ] digits
 *            [ '.' digits ]
 *            [ ( 'e' | 'E' ) [ '+' | '-' ] digits]
 * </pre>
 */
public class Lexer {

    public enum Type {
        END, LP, RP, ASSIGN, ID, NUMBER, SPECIAL;
    }

    public record Token(Type type, String string) {
    }

    public static final Token END = new Token(Type.END, "");

    final int[] input;
    int ch, current, index, start;

    Lexer(int[] input) {
        this.input = input;
        this.index = 0;
        get();
    }

    public static List<Token> tokens(String input) {
        Lexer lexer = new Lexer(input.codePoints().toArray());
        List<Token> list = new ArrayList<>();
        Token t;
        while ((t = lexer.token()).type != Type.END)
            list.add(t);
        return list;
    }

    int get() {
        current = index;
        return ch = index < input.length ? input[index++] : -1;
    }

    static boolean isDigit(int ch) {
        return ch >= '0' && ch <= '9';
    }

    static boolean isIdFirst(int ch) {
        return Character.isAlphabetic(ch) || ch == '_';
    }

    static boolean isIdRest(int ch) {
        return isIdFirst(ch) || Character.isDigit(ch) || ch == '.';
    }

    Type getReturn(Type type) {
        get();
        return type;
    }

    void digit() {
        if (!isDigit(ch))
            throw new ValueException("Digit expected but 0x%04X", ch);
        while (isDigit(ch))
            get();
    }

    Type number() {
        digit();
        if (ch == '.') {
            get();
            digit();
        }
        if (ch == 'e' || ch == 'E') {
            get();
            if (ch == '+' | ch == '-')
                get();
            digit();
        }
        return Type.NUMBER;
    }

    Type id() {
        while (isIdRest(ch))
            get();
        return Type.ID;
    }

    Type error(String format, Object... args) {
        throw new ValueException(format, args);
    }

    Token token() {
        while (Character.isWhitespace(ch))
            get();
        if (ch == -1)
            return END;
        int start = current;
        Type type = switch (ch) {
            case '(' -> getReturn(Type.LP);
            case ')' -> getReturn(Type.RP);
            case '+', '*', '/', '%', '^', '~' -> getReturn(Type.SPECIAL);
            case '=' -> get() == '=' ? getReturn(Type.SPECIAL) : Type.ASSIGN;
            case '<', '>' -> get() == '=' ? getReturn(Type.SPECIAL) : Type.SPECIAL;
            case '-' -> isDigit(get()) ? number() : Type.SPECIAL;
            case '!' -> get() == '=' || ch == '~' ? getReturn(Type.SPECIAL) : error("UnknownTOken '!");
            default -> isDigit(ch) ? number() : isIdFirst(ch) ? id() : error("Unknown char 0x%04X", ch);
        };
        return new Token(type, new String(input, start, current - start));
    }
    
}
