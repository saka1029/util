package saka1029.util.decs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Scanner {

    public enum TokenType {
        LP, RP, COMMA, AT,
        PLUS, MINUS, MULT, DIV, MOD, POW,
        EQ, NE, GT, GE, LT, LE, ASSIGN,
        AND, XOR, OR, NOT, BITNOT,
        CAND, COR,
        HELP, SOLVE, EXIT,
        NUM, ID,
        END,
    }

    static final Map<String, TokenType> RESERVED = Map.ofEntries(
        Map.entry("help", TokenType.HELP), Map.entry("solve", TokenType.SOLVE),
        Map.entry("exit", TokenType.EXIT)
    );


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

    TokenType get(TokenType type) {
        get();
        return type;
    }

    static TokenType error(String format, Object... args) {
        throw new SyntaxException(format, args);
    }

    static boolean isDigit(int ch) {
        return Character.isDigit(ch);
    }

    static boolean isBasedDigit(int ch) {
        return ch >= 'a' && ch <= 'z'
            || ch >= 'A' && ch <= 'Z'
            || ch >= '0' && ch <= '9';
    }

    static boolean isAlpha(int ch) {
        return Character.isAlphabetic(ch);
    }

    static boolean isSpecial(int ch) {
        return switch (ch) {
            case '+', '-', '*', '/', '%', '^',
                '!', '=', '~', '<', '>', '&', '|',
                '@', '$', '?', ':' -> true;
            default -> false;
        };
    }

    String str(int ch) {
        return ch == -1 ? "EOF" : "'%c'".formatted(ch);
    }

    void digits() {
        if (!isDigit(ch))
            error("Digit expected but %s", str(ch));
         while (isDigit(ch))
            get();
    }

    void basedDigits() {
        if (!isBasedDigit(ch))
            error("Based digit expected but %s", str(ch));
        while (isBasedDigit(ch))
            get();
    }

    TokenType number() {
        digits();
        if (ch == 'B' || ch == 'b') {
            get();  // skip 'B'
            basedDigits();
        } else {
            if (ch == '.') {
                get();  // skip '.'
                digits();
            }
            if (ch == 'e' || ch == 'E') {
                get();
                if (ch == '+' || ch == '-')
                    get();
                digits();
            }
        }
        return TokenType.NUM;
    }

    TokenType checkReserved(int start) {
        TokenType t = RESERVED.get(string(start));
        return t != null ? t : TokenType.ID;
    }

    TokenType alpha() {
        int start = index - 1;
        while (isAlpha(ch) || isDigit(ch))
            get();
        return checkReserved(start);
    }

    TokenType special() {
        int start = index - 1;
        while (isSpecial(ch))
            get();
        return checkReserved(start);
    }

    String string(int start) {
        int end = ch == -1 ? index : index - 1;
        return new String(input, start, end -start);
    }

    Token token() {
        int start = index - 1;
        TokenType type = switch (ch) {
            case '(' -> get(TokenType.LP);
            case ')' -> get(TokenType.RP);
            case ',' -> get(TokenType.COMMA);
            case '+' -> get(TokenType.PLUS);
            case '-' -> get(TokenType.MINUS);
            case '*' -> get(TokenType.MULT);
            case '/' -> get(TokenType.DIV);
            case '%' -> get(TokenType.MOD);
            case '~' -> get(TokenType.BITNOT);
            case '^' -> get() == '^' ? get(TokenType.XOR) : TokenType.POW;
            case '=' -> get() == '=' ? get(TokenType.EQ) : TokenType.ASSIGN;
            case '!' -> get() == '=' ? get(TokenType.NE) : TokenType.NOT;
            case '<' -> get() == '=' ? get(TokenType.LE) : TokenType.LT;
            case '>' -> get() == '=' ? get(TokenType.GE) : TokenType.GT;
            case '@' -> get(TokenType.AT);
            case '&' -> get() == '&' ? get(TokenType.CAND) : TokenType.AND;
            case '|' -> get() == '|' ? get(TokenType.COR): TokenType.OR;
            default -> isDigit(ch) ? number()
                : isAlpha(ch) ? alpha()
                : isSpecial(ch) ? special()
                : error("Unknown char %s", str(ch));
        };
        return new Token(type, string(start));
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
