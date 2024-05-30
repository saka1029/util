package saka1029.util.dentaku;

import java.util.ArrayList;
import java.util.List;

public class Tokenizer {

    public enum Type {
        END, ID, NUMBER, LP, RP,
        SPECIAL,
        ASSIGN,
        CONCAT,
        OR, AND, COMP, ADD, MULT, POWER,
        SELECT,
    }

    public record Token(Type type, String string) {
    }

    public static Token END = new Token(Type.END, "");

    final int[] input;
    final StringBuilder sb = new StringBuilder();
    int index = 0, current = 0;
    int ch;

    private Tokenizer(int[] input) {
        this.input = input;
        this.ch = get();
    }

    public static List<Token> tokens(String input) {
        Tokenizer t = new Tokenizer(input.codePoints().toArray());
        List<Token> tokens = new ArrayList<>();
        Token token;
        while ((token = t.token()) != END)
            tokens.add(token);
        return tokens;
    }

    int get() {
        current = index;
        return ch = index < input.length ? input[index++] : -1;
    }

    static boolean isIdFirst(int ch) {
        return Character.isAlphabetic(ch) || ch == '_' || ch == '?';
    }

    static boolean isIdRest(int ch) {
        return isIdFirst(ch) || Character.isDigit(ch);
    }

    static boolean isDigit(int ch) {
        return Character.isDigit(ch);
    }

    Type advance(Type t) {
        get();
        return t;
    }

    void append(int ch) {
        sb.appendCodePoint(ch);
    }

    void appendGet() {
        append(ch);
        get();
    }

    void digits() {
        if (!isDigit(ch))
            error("Digit expected but '%c'", ch);
        while (isDigit(ch))
            appendGet();
    }

    Type number() {
        digits();
        if (ch == '.') {
            appendGet();
            digits();
        }
        if (ch == 'e' || ch == 'E') {
            appendGet();
            if (ch == '+' || ch == '-')
                appendGet();
            digits();
        }
        return Type.NUMBER;
    }

    Type id() {
        while (isIdRest(ch))
            appendGet();
        return switch (sb.toString()) {
            case "and" -> Type.AND;
            case "or", "xor" -> Type.OR;
            default -> Type.ID;
        };
    }

    Type error(String format, Object... args) {
        throw new ValueException(format, args);
    }

    Token token() {
        while (Character.isWhitespace(ch))
            get();
        sb.setLength(0);
        if (ch == -1)
            return END;
        int start = current;
        Type type = switch (ch) {
            case '(' -> advance(Type.LP);
            case ')' -> advance(Type.RP);
            case ':' -> advance(Type.ASSIGN);
            case '@' -> advance(Type.SELECT);
            case ',' -> advance(Type.CONCAT);
            case '+', '-' -> advance(Type.ADD);
            case '*', '/', '%' -> advance(Type.MULT);
            case '^' -> advance(Type.POWER);
            case '~', '=' -> advance(Type.COMP);
            case '<', '>' -> get() == '=' ? advance(Type.COMP) : Type.COMP;
            case '!' -> get() == '=' ?  advance(Type.COMP)
                : ch == '~' ? advance(Type.COMP)
                : error("Unknown token '!'");
            default -> isDigit(ch) ?  number()
                : isIdFirst(ch) ? id()
                : error("Unknown character '%c'(0x%04X)", ch, ch);
        };
        return new Token(type, new String(input, start, current - start));
    }

}