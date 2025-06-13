package saka1029.util.decs;

import java.util.ArrayList;
import java.util.List;

public class Scanner {

    public enum TokenType {
        LP, RP, COMMA, AT,
        PLUS, MINUS, MULT, DIV, MOD,
        EQ, NE, GT, GE, LT, LE, NOT,
        ASSIGN, NUM, ID, DOTID,
    }

    public static class Token {
        public final TokenType type;
        public final String string;
        public Token(TokenType type, String string) {
            this.type = type;
            this.string = string;
        }
        @Override
        public boolean equals(Object obj) {
            return obj instanceof Token t
                && type.equals(t.type) && string.equals(t.string);
        }
        @Override
        public String toString() {
            return "Token(%s, %s)".formatted(type, string);
        }
    }

    int input[], index, ch;

    int get() {
        return ch = index < input.length ? input[index++] : -1;
    }

    void spaces() {
        while (Character.isWhitespace(ch))
            get();
    }

    boolean eat(int expected) {
        get();
        if (ch == expected) {
            get();
            return true;
        }
        return false;
    }

    TokenType get(TokenType type) {
        get();
        return type;
    }

    static boolean isDigit(int ch) {
        return Character.isDigit(ch);
    }

    static boolean isAlpha(int ch) {
        return Character.isAlphabetic(ch);
    }

    String str(int ch) {
        return ch == -1 ? "EOF" : "'%c'".formatted(ch);
    }

    TokenType dotid() {
        get(); // skip '.'
        id();
        return TokenType.DOTID;
    }

    void digits() {
        if (!isDigit(ch))
            error("Digit expected but %s", str(ch));
         while (isDigit(ch))
            get();
    }

    TokenType number() {
        digits();
        if (ch == '.') {
            get();
            digits();
        }
        if (ch == 'e' || ch == 'E') {
            get();
            if (ch == '+' || ch == '-')
                get();
            digits();
        }
        return TokenType.NUM;
    }

    TokenType id() {
        while (isAlpha(ch) || isDigit(ch))
            get();
        return TokenType.ID;
    }

    static TokenType error(String format, Object... args) {
        throw new RuntimeException(format.formatted(args));
    }

    Token token() {
        int start = index - 1;
        TokenType type = switch (ch) {
            case '(' -> get(TokenType.LP);
            case ')' -> get(TokenType.RP);
            case ',' -> get(TokenType.COMMA);
            case '@' -> get(TokenType.AT);
            case '+' -> get(TokenType.PLUS);
            case '-' -> get(TokenType.MINUS);
            case '*' -> get(TokenType.MULT);
            case '/' -> get(TokenType.DIV);
            case '%' -> get(TokenType.MOD);
            case '=' -> eat('=') ? TokenType.EQ : TokenType.ASSIGN;
            case '!' -> eat('=') ? TokenType.NE : TokenType.NOT;
            case '>' -> eat('=') ? TokenType.GE : TokenType.GT;
            case '<' -> eat('=') ? TokenType.LE : TokenType.LT;
            case '.' -> dotid();
            default -> isDigit(ch) ? number()
                : isAlpha(ch) ? id()
                : error("Unknown char %s", str(ch));
        };
        int end = ch == -1 ? index : index - 1;
        return new Token(type, new String(input, start, end - start));
    }

    public List<Token> scan(String input) {
        this.input = input.codePoints().toArray();
        this.index = 0;
        get();
        List<Token> list = new ArrayList<>();
        while (true) {
            spaces();
            if (ch == -1)
                break;
            list.add(token());
        }
        return list;
    }

}
