package saka1029.util.csp;

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
 * SEND+MORE=MONEY -> number(S, E, N, D) + number(M, O, R, E) == number(M, O, N, E, Y)
 */
public class Fukumen {

    final int[] input;
    int index;
    int cp;
    final Map<Integer, Boolean> vars = new HashMap<>();
    
    Fukumen(String input) {
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
        }
        int v = variable(cp);
        if (v < 0)
            throw new RuntimeException("DIGIT expected");
        StringBuilder sb = new StringBuilder("number(");
        boolean first = true;
        do {
            if (!first)
                sb.append(",");
            if (cp <= 9)
                sb.append(cp);
            else {
                sb.appendCodePoint(cp);
                Boolean b = vars.get(cp);
                if (b == null)
                    b = false;
                vars.put(cp, first | b);
            }
            get();
            v = variable(cp);
            first = false;
        } while (v >= 0);
        sb.append(")");
        return sb.toString();
    }

    String multExpr() {
        StringBuilder sb = new StringBuilder(primary());
        while (true)
            if (eat('*'))
                sb.append(" * ").append(primary());
            else if (eat('/'))
                sb.append(" / ").append(primary());
            else
                break;
        return sb.toString();
    }

    String addExpr() {
        StringBuilder sb = new StringBuilder(multExpr());
        while (true)
            if (eat('+'))
                sb.append(" + ").append(multExpr());
            else if (eat('-'))
                sb.append(" - ").append(multExpr());
            else
                break;
        return sb.toString();
    }

    String compExpr() {
        StringBuilder sb = new StringBuilder(addExpr());
        while (true)
            if (eat('=')) {
                sb.append(" == ").append(addExpr());
            } else if (eat('!')) {
                if (eat('='))
                    sb.append(" == ").append(addExpr());
                else
                    throw new RuntimeException("Unknown operator '!'");
            } else if (eat('<')) {
                if (eat('='))
                    sb.append(" <= ").append(addExpr());
                else
                    sb.append(" < ").append(addExpr());
            } else if (eat('>')) {
                if (eat('='))
                    sb.append(" >= ").append(addExpr());
                else
                    sb.append(" > ").append(addExpr());
            } else 
                break;
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

    String parse() {
        return expression();
    }
    
    public record Result(Map<Integer, Boolean> vars, String constraint) {}

    public static Result parse(String input) {
        Fukumen parser = new Fukumen(input);
        String constraint = parser.parse();
        return new Result(parser.vars, constraint);
    }

}
