package saka1029.util.fukumen;

import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 * expression  = or-expr
 * or-expr     = and-expr { '|' and-expr }
 * and-expr    = comp-expr { '&' comp-expr }
 * comp-expr   = add-expr [ COMP add-expr ]
 * COMP        = '=' | '!=' | '<' | <=' | '>' | '>='
 * add-expr    = mult-expr { ('+' | '-') mult-expr }
 * mult-expr   = primary { ('*' | '/') primary }
 * primary     = '(' expression ')' | VARIABLE
 * </pre>
 * SEND+MORE=MONEY -> sequence(S, E, N, D) + sequence(M, O, R, E) == sequence(M, O, N, E, Y)
 */
public class Parser {

    final int[] input;
    int index;
    int ch;
    final Map<Integer, Boolean> vars = new HashMap<>();
    
    Parser(String input) {
        this.input = input.codePoints().toArray();
        this.index = 0;
        get();
    }

    int get() {
        return ch = index >= input.length ? -1 : input[index++];
    }

    void spaces() {
        while (Character.isWhitespace(ch))
            get();
    }

    boolean eat(int expected) {
        spaces();
        if (ch == expected) {
            get();
            return true;
        }
        return false;
    }

    static int variable(int ch) {
        if (ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z')
            return ch;
        else if (ch >= 'ａ' && ch <= 'ｚ' || ch >= 'Ａ' && ch <= 'Ｚ')
            return ch;
        else if (ch >= '0' && ch <= '9')
            return ch - '0';
        else if (ch >= '０' && ch <= '９')
            return ch - '０';
        else if (ch >= 'ｧ' && ch <= 'ﾝ')
            return ch;
        else if (ch > 255)  // 全角ひらがな、全角カタカナ、漢字など（大雑把な判定）
            return ch;
        else
            return -1;
    }

    String primary() {
        if (eat('(')) {
            String e = expression();
            if (!eat(')'))
                throw new RuntimeException("')' expected");
            return "(%s)".formatted(e);
        } else
            return null;
    }

    String compExpr() {
        StringBuilder sb = new StringBuilder(primary());
        return sb.toString();
    }

    String andExpr() {
        StringBuilder sb = new StringBuilder(compExpr());
        while (eat('&'))
            sb.append(" && ").append(compExpr());
        return sb.toString();
    }

    String orExpr() {
        StringBuilder sb = new StringBuilder(andExpr());
        while (eat('|'))
            sb.append(" || ").append(andExpr());
        return sb.toString();
    }

    String expression() {
        return orExpr();
    }

    void parse() {
        String exp = expression();
    }

    public static void parse(String input) {
        Parser parser = new Parser(input);
        parser.parse();
    }

}
