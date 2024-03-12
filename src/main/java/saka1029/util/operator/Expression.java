package saka1029.util.operator;

/**
 * SYNTAX
 * <pre>
 * statement  = [ VAR | UOP VAR | VAR BOP VAR ] ] '=' ] expression
 * expression = term { BOP term }
 * term       = sequence | UOP term
 * sequence   = primary { primary }
 * primary    = '(' expression ')' | VAR | NUMBER
 * DIGIT      = '0'..'9'
 * DIGITS     = DIGIT { DIGIT }
 * NUMBER     = DIGITS [ '.' DIGITS ] [ ( 'e' | 'E' ) [ '+' | '-' ] DIGITS ]
 * UOP        = SPECIALS | ID
 * BOP        = SPECIALS | ID
 * SPECIAL    = '+' | '-' | '*' | '/' | '%' | '$' | '&' | '<' | '>' | '@' | '!'
 * SPECIALS   = SPECIAL { SPECIAL }
 * VAR        = ID
 * ID         = ID-FIRST { ID-REST }
 * ID-FIRST   = <Character.isAlphabetic>
 * ID-REST    = <Character.isDigit>
 * </pre>
 * 
 * <pre>
 * ID1 '=' binary           : define variable (ID1:VAR)
 * ID1 ID2 '=' binary       : define unary operator (ID1:UOP, ID2:VAR)
 * ID1 ID2 ID₃ '=' binary   : define bynary operator (ID1:VAR1, ID2:BOP, ID3:VAR2)
 * </pre>
 */
public interface Expression {
    Value eval(Context context);
}
