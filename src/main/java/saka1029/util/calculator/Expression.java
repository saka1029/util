package saka1029.util.calculator;

import java.util.Map;

/**
 * <pre>
 * SYNTAX
 * statement  : [ id '=' ] expression
 * expression : term { ('+' | '-' ) term }
 * term       : factor { ( '*' | '/' ) factor }
 * factor     : atom [ '^' atom ]
 * atom       : [ '-' ] ( number | id | '(' expression ')' )
 * </pre>
 */
@FunctionalInterface
public interface Expression {
    double eval(Map<String, Expression> variables);

}
