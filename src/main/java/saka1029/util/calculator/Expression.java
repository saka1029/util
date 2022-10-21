package saka1029.util.calculator;

import java.util.Map;

/**
 * <pre>
 * SYNTAX
 * statement  : [ id '=' ] expression
 * expression : term { ('+' | '-' ) term }
 * term       : factor { ( '*' | '/' ) factor }
 * factor     : atom [ '^' factor ]
 * atom       : [ '-' ] ( number | id | '(' expression ')' )
 * </pre>
 */
@FunctionalInterface
public interface Expression {
    double eval(Map<String, Expression> variables);
    
    public static class ParseException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        ParseException(String format, Object... args) {
            super(format.formatted(args));
        }
    }

    public static class EvaluationException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        EvaluationException(String format, Object... args) {
            super(format.formatted(args));
        }
    }

    public static Expression parse(String s) {
        return new Object() {
            int length = s.length(), index = 0, ch = get();
            
            int get() {
                return ch = index < length ? s.charAt(index++) : -1;
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
            
            StringBuilder buffer = new StringBuilder();
            
            void bufferClear() {
                buffer.setLength(0);
            }
            
            void bufferAppend(int ch) {
                buffer.append((char)ch);
            }
            
            void bufferAppendGet(int ch) {
                bufferAppend(ch);
                get();
            }
            
            String bufferToString() {
                return buffer.toString();
            }

            void bufferInteger() {
                while (Character.isDigit(ch))
                    bufferAppendGet(ch);
            }

            Expression atom() {
                Expression atom;
                boolean minus = false;
                if (eat('-'))
                    minus = true;
                if (eat('(')) {
                    atom = expression();
                    if (!eat(')'))
                        throw new ParseException("')' expected");
                } if (Character.isDigit(ch)) {
                    bufferClear();
                    bufferInteger();
                    if (ch == '.') {
                        bufferAppendGet(ch);
                        bufferInteger();
                        if (ch == 'e' || ch == 'E') {
                            bufferAppendGet(ch);
                            if (ch == '-' || ch == '+')
                                bufferAppendGet(ch);
                            if (!Character.isDigit(ch))
                                throw new ParseException("digit expected after 'E'");
                            bufferInteger();
                        }
                    }
                    double value = Double.valueOf(bufferToString());
                    atom = variables -> value;
                } else if (Character.isAlphabetic(ch)) {
                    bufferClear();
                    while (Character.isAlphabetic(ch) || Character.isDigit(0) || ch == '_')
                        bufferAppendGet(ch);
                    String name = bufferToString();
                    atom = variables -> {
                        Expression e = variables.get(name);
                        if (e == null)
                            throw new EvaluationException("undefined variable '%s'", name);
                        return e.eval(variables);
                    };
                } else
                    throw new ParseException("unknown char '%c'", ch);
                if (minus) {
                    Expression arg = atom;
                    atom = variables -> arg.eval(variables);
                }
                return atom;
            }

            Expression factor() {
                Expression atom = atom();
                if (eat('^')) {
                    Expression left = atom, right = factor();
                    atom = variables -> Math.pow(left.eval(variables), right.eval(variables));
                }
                return atom;
            }

            Expression term() {
                Expression factor = factor();
                while (true)
                    if (eat('*')) {
                        Expression left = factor, right = factor();
                        factor = variables -> left.eval(variables) * right.eval(variables);
                    } else if (eat('/')) {
                        Expression left = factor, right = factor();
                        factor = variables -> left.eval(variables) / right.eval(variables);
                    } else
                        break;
                return factor;
            }

            Expression expression() {
                Expression term = term();
                while (true)
                    if (eat('+')) {
                        Expression left = term, right = term();
                        term = variables -> left.eval(variables) + right.eval(variables);
                    } else if (eat('-')) {
                        Expression left = term, right = term();
                        term = variables -> left.eval(variables) - right.eval(variables);
                    } else
                        break;
                return term;
            }

            Expression parse() {
                Expression e = expression();
                return e;
            }
        }.parse();
    }

}
