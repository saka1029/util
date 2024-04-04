package saka1029.util.dentaku;

import java.util.ArrayList;
import java.util.List;

import saka1029.util.dentaku.Tokenizer.Type;

public class Lexer {

    public enum Type {
        END, ID, NUMBER, OTHER;
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
        return isIdFirst(ch) || Character.isDigit(ch) || ch == '-' || ch == '.';
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
        int start = current;
        if (ch == -1)
            return END;
        Type type = switch (ch) {
            case '(', ')', '+', '*', '/', '%', '^', '~' -> getReturn(Type.OTHER);
            case '=', '<', '>' -> get() == '=' ? getReturn(Type.OTHER) : Type.OTHER;
            case '-' -> isDigit(get()) ? number() : Type.OTHER;
            case '!' -> get() == '=' || ch == '~' ? getReturn(Type.OTHER) : error("UnknownTOken '!");
            default -> isDigit(ch) ? number() : isIdFirst(ch) ? id() : error("Unknown char 0x%04X", ch);
        };
        return new Token(type, new String(input, start, current - start));
    }
    
}
