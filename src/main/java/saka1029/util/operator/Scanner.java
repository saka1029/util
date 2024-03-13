package saka1029.util.operator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Scanner {

    public enum Type {
        END, LP, RP, ID, SPECIAL, NUM
    }

    public record Token(Type type, String string) {

        public Token(Type type) {
            this(type, null);
        }

        public static final Token END = new Token(Type.END);
        public static final Token LP = new Token(Type.LP);
        public static final Token RP = new Token(Type.RP);

        public BigDecimal number() {
            return Value.number(string);
        }

        @Override
        public final String toString() {
            return type + (string == null ? "" : ":" + string);
        }
    }

    final int[] input;
    int index, ch;
    Token token;

    Scanner(String input) {
        this.input = input.codePoints().toArray();
        this.index = 0;
        get();
    }

    public static Scanner of(String input) {
        return new Scanner(input);
    }

    int get() {
        return ch = index < input.length ? input[index++] : -1;
    }

    void spaces() {
        while (Character.isWhitespace(ch))
            get();
    }

    static final Set<Integer> SPECIALS = Set.of(
        (int)'+', (int)'-', (int)'*', (int)'/', (int)'%',
        (int)'$', (int)'&', (int)'<', (int)'>', (int)'=',
        (int)'@', (int)'!'
    );

    static boolean isSpecial(int ch) {
        return SPECIALS.contains(ch);
    }

    static boolean isIdFirst(int ch) {
        return Character.isAlphabetic(ch) || ch == '_';
    }

    static boolean isIdRest(int ch) {
        return isIdFirst(ch) || Character.isDigit(ch);
    }

    static boolean isDigit(int ch) {
        return ch >= '0' && ch <= '9';
    }

    StringBuilder sb = new StringBuilder();

    void clear() {
        sb.setLength(0);
    }

    void appendGet() {
        sb.appendCodePoint(ch);
        get();
    }

    Token special() {
        clear();
        do {
            appendGet();
        } while (isSpecial(ch));
        return new Token(Type.SPECIAL, sb.toString());
    }

    Token id() {
        clear();
        do {
            appendGet();
        } while (isIdRest(ch));
        return new Token(Type.ID, sb.toString());
    }

    void digits() {
        if (!isDigit(ch))
            throw new OperatorException("Digit expected");
        do {
            appendGet();
        } while (isDigit(ch));
    }

    Token number() {
        clear();
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
        return new Token(Type.NUM, sb.toString());
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
            default:
                if (isSpecial(ch))
                    return special();
                else if (isIdFirst(ch))
                    return id();
                else if (isDigit(ch))
                    return number();
                else
                    throw new OperatorException("Unknown char 0x%04x", ch);
        }
    }

    public List<Token> tokens() {
        List<Token> tokens = new ArrayList<>();
        Token t;
        while ((t = read()) != Token.END)
            tokens.add(t);
        return tokens;
    }
}
