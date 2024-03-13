package saka1029.util.dentaku;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Lexer {

    public enum Type {
        NUMBER, ID, OTHER
    }

    public record Token(Type type, String string) {

        public static final Token END = new Token(Type.OTHER, "*END*");
        public static final Token LP = new Token(Type.OTHER, "(");
        public static final Token RP = new Token(Type.OTHER, ")");
        public static final Token PLUS = new Token(Type.OTHER, "+");
        public static final Token MINUS = new Token(Type.OTHER, "-");
        public static final Token STAR = new Token(Type.OTHER, "*");
        public static final Token SLASH = new Token(Type.OTHER, "/");
        public static final Token PERCENT = new Token(Type.OTHER, "%");
        public static final Token CARET = new Token(Type.OTHER, "^");
        public static final Token EQ = new Token(Type.OTHER, "=");

        public BigDecimal number() {
            return Vector.number(string);
        }

        @Override
        public final String toString() {
            if (type == Type.OTHER)
                return string;
            else
                return "%s:%s".formatted(type, string);
        }
    }

    final int[] input;
    int index;
    int ch;

    Lexer(String input) {
        this.input = input.codePoints().toArray();
        this.index = 0;
        get();
    }

    public static Lexer of(String input) {
        return new Lexer(input);
    }

    int get() {
        return ch = index < input.length ? input[index++] : -1;
    }

    void spaces() {
        while (Character.isWhitespace(ch))
            get();
    }

    static boolean isDigit(int ch) {
        return ch >= '0' && ch <= '9';
    }

    static boolean isIdFirst(int ch) {
        return Character.isAlphabetic(ch) || ch == '_';
    }

    static boolean isIdRest(int ch) {
        return isIdFirst(ch) || Character.isDigit(ch);
    }

    StringBuilder sb = new StringBuilder();

    void clear() {
        sb.setLength(0);
    }

    void append(int ch) {
        sb.appendCodePoint(ch);
    }

    void appendGet() {
        append(ch);
        get();
    }

    void digit() {
        if (!isDigit(ch))
            throw new VectorException("Digit expected");
        while (isDigit(ch))
            appendGet();
    }

    Token number() {
        clear();
        digit();
        if (ch == '.') {
            appendGet();
            digit();
        }
        if (ch == 'e' || ch == 'E') {
            appendGet();
            if (ch == '+' || ch == '-')
                appendGet();
            digit();
        }
        return new Token(Type.NUMBER, sb.toString());
    }

    Token id() {
        clear();
        while (isIdRest(ch))
            appendGet();
        return new Token(Type.ID, sb.toString());
    }

    public Token read() {
        spaces();
        switch (ch) {
            case -1:
                return Token.END;
            case '(':
                get();
                return Token.LP;
            case ')':
                get();
                return Token.RP;
            case '+':
                get();
                return Token.PLUS;
            case '-':
                get();
                return Token.MINUS;
            case '*':
                get();
                return Token.STAR;
            case '/':
                get();
                return Token.SLASH;
            case '%':
                get();
                return Token.PERCENT;
            case '^':
                get();
                return Token.CARET;
            case '=':
                get();
                return Token.EQ;
            default:
                if (isDigit(ch))
                    return number();
                else if (isIdFirst(ch))
                    return id();
                else
                    throw new VectorException("Unknown char 0x%02X", ch);
        }
    }

    public List<Token> tokens() {
        List<Token> tokens = new ArrayList<>();
        Token token;
        while ((token = read()) != Token.END)
            tokens.add(token);
        return tokens;
    }
}
