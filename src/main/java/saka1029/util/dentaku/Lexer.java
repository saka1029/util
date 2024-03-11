package saka1029.util.dentaku;

import java.math.BigDecimal;

public class Lexer {
    public record Token(int type, String string) {
        public Token(int type) {
            this(type, Character.toString(type));
        }

        public BigDecimal number() {
            return Vector.number(string);
        }

        @Override
        public final String toString() {
            if (Character.toString(type).equals(string))
                return "%c".formatted((char)type);
            else
                return "%c:%s".formatted((char)type, string);
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
        return new Token('n', sb.toString());
    }

    Token id() {
        clear();
        while (isIdRest(ch))
            appendGet();
        return new Token('i', sb.toString());
    }

    public Token read() {
        spaces();
        if (ch == -1)
            return null;
        switch (ch) {
            case '(':
            case ')':
            case '+':
            case '-':
            case '*':
            case '/':
            case '%':
            case '^':
            case '=':
            case '!':
                int t = ch;
                get();
                return new Token(t);
            default:
                if (isDigit(ch))
                    return number();
                else if (isIdFirst(ch))
                    return id();
                else
                    throw new VectorException("Unknown char 0x%02X", ch);
        }
    }
}
