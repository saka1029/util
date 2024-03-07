package saka1029.util.vec;

/**
 * SYNTAX:
 * <pre>
 * statement  = [ ID '=' ] expression
 * expression = [ '-' ] term { [ '+' | '-' ] term }
 * term       = factor { [ '*' | '/' ] factor }
 * exp        = sequence { '^' factor }
 * sequence   = primary { primary }
 * primary    = '(' expression ')' | ID | NUMBER
 * </pre>
 */
public class Parser {

}
