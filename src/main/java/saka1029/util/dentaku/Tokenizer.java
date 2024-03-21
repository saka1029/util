package saka1029.util.dentaku;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * <pre>
 * LP       = '('
 * RP       = ')'
 * ID       = ID-FIRST { ID-REST }
 * ID-FIRST = JAVA-ALPHABETIC | '_'
 * ID-REST  = ID-FIRST | JAVA-DIGIT | '-'
 * ID       = JAVA-ALPHABETIC { JAVA-ALPHABETIC | JAVA-DIGIT }
 * SPECIAL  = SP { SP }
 * SP       = '!' | '$' | '%' | '&' | '-'
 *          | '=' | '^' | '~' | '|' | '@'
 *          | '+' | '*' | '<' | '>' | '/'
 *          | '.'
 * NUMBER   = [ '-' ] DIGITS
 *            [ '.' DIGITS]
 *            [ ( 'e' | 'E') [ '+' | '-' ] DIGITS ]
 * DIGITS   = DIGIT { DIGIT }
 * DIGIT    = '0' | '1' | '2' | '3' | '4'
 *          | '5' | '6' | '7' | '8' | '9'
 * </pre>
 */
public class Tokenizer {
    public enum Type {
        END, LP, RP, ID, SPECIAL, NUMBER;
    }

    public record Token(Type type, String string) {
        public BigDecimal number() {
            return new BigDecimal(string);
        }
        @Override
        public final String toString() {
            return type + ":" + string;
        }
    }

    public static final Token END = new Token(Type.END, "");
    public static final Token LP = new Token(Type.LP, "(");
    public static final Token RP = new Token(Type.RP, ")");

    final int[] input;
    int index;
    int ch;

    private Tokenizer(int[] input) {
        this.input = input;
        this.index = 0;
        ch();
    }

    public static Tokenizer of(String input) {
        return new Tokenizer(input.codePoints().toArray());
    }

    public static List<Token> tokens(String input) {
        Tokenizer t = of(input);
        List<Token> tokens = new ArrayList<>();
        Token token;
        while ((token = t.get()) != END)
            tokens.add(token);
        return tokens;
    }

    int ch() {
        return ch = index < input.length ? input[index++] : -1;
    }

    boolean eat(int expected) {
        if (ch == expected) {
            ch();
            return true;
        }
        return false;
    }

    static final Set<Integer> SPECIALS = Set.of(
        (int)'!', (int)'$', (int)'%', (int)'&', (int)'-',
        (int)'=', (int)'^', (int)'~', (int)'|', (int)'@',
        (int)'+', (int)'*', (int)'<', (int)'>', (int)'/',
        (int)'.'
    );

    static boolean isSpecial(int ch) {
        return SPECIALS.contains(ch);
    }

    static boolean isIdFirst(int ch) {
        return Character.isAlphabetic(ch) || ch == '_';
    }

    static boolean isIdRest(int ch) {
        return isIdFirst(ch) || Character.isDigit(ch) || ch == '-';
    }

    static boolean isDigit(int ch) {
        return ch >= '0' && ch <= '9';
    }

    StringBuilder sb = new StringBuilder();

    void clear() {
        sb.setLength(0);
    }

    void append(int ch) {
        sb.appendCodePoint(ch);
    }

    void appendCh() {
        append(ch);
        ch();
    }

    Token numberOrSpecial() {
        append('-');
        return isDigit(ch) ? number() : special();
    }

    Token id() {
        while (isIdRest(ch))
            appendCh();
        return new Token(Type.ID, sb.toString());
    }

    Token special() {
        while (isSpecial(ch))
            appendCh();
        return new Token(Type.SPECIAL, sb.toString());
    }

    void digits() {
        if (!isDigit(ch))
            throw new ValueException("Digit expected but 0x%04x", ch);
        while (isDigit(ch))
            appendCh();;
    }

    Token number() {
        digits();
        if (ch == '.') {
            appendCh();
            digits();
        }
        if (ch == 'e' || ch == 'E') {
            appendCh();
            if (ch == '+' || ch == '-')
                appendCh();
            digits();
        }
        return new Token(Type.NUMBER, sb.toString());
    }

    public Token get() {
        while (Character.isWhitespace(ch))
            ch();
        clear();
        if (ch == -1)
            return END;
        else if (eat('('))
            return LP;
        else if (eat(')'))
            return RP;
        else if (eat('-'))
            return numberOrSpecial();
        else if (isIdFirst(ch))
            return id();
        else if (isSpecial(ch))
            return special();
        else if (isDigit(ch))
            return number();
        else
            throw new ValueException("Unknown char '0x%04X'", ch);
    }
}
