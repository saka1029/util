package saka1029.util.polynomial;

/**
 * EBNF:
 * expression = term
 * term       = factor { ( '+' | '-') factor }
 * factor     = power { ( '*' | '/') power }
 * power      = primary [ '^' power ]
 * primary    = '(' expr ')' | id | num
 */
public interface Expression extends Primary {
}
