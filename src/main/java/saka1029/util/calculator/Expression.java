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
 * number     : integer [ '.' integer ] [ ( 'e' | 'E' ) ( '+' | '-' ) integer ]
 * integer    : digit { digit }
 * id         : alphabet { alphabet | digit | '_' }
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
            
            boolean idFirst(int ch) {
                return Character.isAlphabetic(ch);
            }
            
            boolean idRest(int ch) {
                return idFirst(ch) || Character.isDigit(ch) || ch == '_';
            }

            Expression variable(String name) {
                return variables -> {
                    Expression e = variables.get(name);
                    if (e == null)
                        throw new EvaluationException("undefined variable '%s'", name);
                    return e.eval(variables);
                };
            }

            Expression atom() {
                Expression atom;
                boolean minus = false;
                if (eat('-'))
                    minus = true;
                if (eat('(')) {
                    atom = expression(null, 0);
                    if (!eat(')'))
                        throw new ParseException("')' expected");
                } else if (Character.isDigit(ch)) {
                    bufferClear();
                    bufferInteger();
                    if (ch == '.') {
                        bufferAppendGet(ch);
                        bufferInteger();
                    }
                    if (ch == 'e' || ch == 'E') {
                        bufferAppendGet(ch);
                        if (ch == '-' || ch == '+')
                            bufferAppendGet(ch);
                        if (!Character.isDigit(ch))
                            throw new ParseException("digit expected after 'E'");
                        bufferInteger();
                    }
                    double value = Double.valueOf(bufferToString());
                    atom = variables -> value;
                } else if (idFirst(ch)) {
                    bufferClear();
                    bufferAppendGet(ch);
                    while (idRest(ch))
                        bufferAppendGet(ch);
                    String name = bufferToString();
                    atom = variable(name);
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

            Expression expression(Expression t, int start) {
                Expression term = t;
                if (t == null) {
                    start = index - 1;
                    term = term();
                }
                while (true)
                    if (eat('+')) {
                        Expression left = term, right = term();
                        term = variables -> left.eval(variables) + right.eval(variables);
                    } else if (eat('-')) {
                        Expression left = term, right = term();
                        term = variables -> left.eval(variables) - right.eval(variables);
                    } else
                        break;
                int end = index;
                Expression termFinal = term;
                String stringFinal = s.substring(start, end).trim();
                return new Expression() {
                    @Override
                    public double eval(Map<String, Expression> variables) {
                        return termFinal.eval(variables);
                    }
                    @Override
                    public String toString() {
                        return stringFinal;
                    }
                };
            }

            Expression statement() {
                spaces();
                int start = index - 1;
                if (idFirst(ch)) {
                    bufferClear();
                    bufferAppendGet(ch);
                    while (idRest(ch))
                        bufferAppendGet(ch);
                    String name = bufferToString();
                    if (eat('=')) {
                        Expression expression = expression(null, 0);
                        return variables -> {
                            variables.put(name, expression);
                            return 0;
                        };
                    } else
                        return expression(variable(name), start);
                }
                return expression(null, 0);
            }

            Expression parse() {
                Expression e = statement();
                if (ch != -1)
                    throw new ParseException("extra string '%'", s.substring(index - 1));
                return e;
            }
        }.parse();
    }

}
