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
    int cp;
    final Map<Integer, Boolean> vars = new HashMap<>();
    
    Parser(String input) {
        this.input = input.codePoints().toArray();
        this.index = 0;
        get();
    }

    int get() {
        return cp = index >= input.length ? -1 : input[index++];
    }

    void spaces() {
        while (Character.isWhitespace(cp))
            get();
    }

    boolean eat(int expected) {
        spaces();
        if (cp == expected) {
            get();
            return true;
        }
        return false;
    }

    static int variable(int cp) {
        if (cp >= 'a' && cp <= 'z' || cp >= 'A' && cp <= 'Z')
            return cp;
        else if (cp >= 'ａ' && cp <= 'ｚ' || cp >= 'Ａ' && cp <= 'Ｚ')
            return cp;
        else if (cp >= '0' && cp <= '9')
            return cp - '0';
        else if (cp >= '０' && cp <= '９')
            return cp - '０';
        else if (cp >= 'ｧ' && cp <= 'ﾝ')
            return cp;
        else if (cp > 255)  // 全角ひらがな、全角カタカナ、漢字など（大雑把な判定）
            return cp;
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
