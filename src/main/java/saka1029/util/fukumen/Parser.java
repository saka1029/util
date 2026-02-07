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

    String compExpr() {
        return null;
    }

    String andExpr() {
        return compExpr();
    }

    String orExpr() {
        return andExpr();
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
