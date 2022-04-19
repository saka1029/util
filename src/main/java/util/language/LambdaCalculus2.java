package util.language;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class LambdaCalculus2 {

    private LambdaCalculus2() {
    }

    public static class LambdaCalculusException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public LambdaCalculusException(String message) {
            super(message);
        }
    }

    public static class Bind<K, V> {

        public final Bind<K, V> previous;
        public final K key;
        public final V value;

        public Bind(Bind<K, V> previous, K key, V value) {
            this.previous = previous;
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return bindToString(this);
        }
    }

    public static <K, V> Bind<K, V> bind(Bind<K, V> previous, K key, V value) {
        return new Bind<>(previous, key, value);
    }

    public static <K, V> V get(Bind<K, V> bind, K key) {
        for (; bind != null; bind = bind.previous)
            if (bind.key.equals(key))
                return bind.value;
        return null;
    }

    public static <K, V> String bindToString(Bind<K, V> bind) {
        StringBuilder sb = new StringBuilder("{");
        String sep = "";
        for (; bind != null; bind = bind.previous, sep = ", ")
            sb.append(sep).append(bind.key).append("=").append(bind.value);
        return sb.append("}").toString();
    }

    public static class IntHolder {
        public int value = 0;
    }

    public static abstract class Expression {

        abstract void toNormalizedString(Bind<BoundVariable, String> bind, IntHolder number,
            StringBuilder sb);

        public String toNormalizedString() {
            StringBuilder sb = new StringBuilder();
            toNormalizedString(null, new IntHolder(), sb);
            return sb.toString();
        }
    }

    public static abstract class Variable extends Expression {

        public final String name;

        protected Variable(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static class BoundVariable extends Variable {

        public BoundVariable(String name) {
            super(name);
        }

        @Override
        void toNormalizedString(Bind<BoundVariable, String> bind, IntHolder number,
            StringBuilder sb) {
            sb.append(get(bind, this));
        }
    }

    public static class FreeVariable extends Variable {

        static final Map<String, FreeVariable> all = new HashMap<>();

        private FreeVariable(String name) {
            super(name);
        }

        public static FreeVariable of(String name) {
            return all.computeIfAbsent(name, k -> new FreeVariable(k));
        }

        @Override
        void toNormalizedString(Bind<BoundVariable, String> bind, IntHolder number,
            StringBuilder sb) {
            sb.append(name);
        }
    }

    public static class Lambda extends Expression {

        public final BoundVariable variable;
        public final Expression body;

        public Lambda(BoundVariable variable, Expression body) {
            this.variable = variable;
            this.body = body;
        }

        @Override
        public String toString() {
            return "λ" + variable + "." + body;
        }

        @Override
        void toNormalizedString(Bind<BoundVariable, String> bind, IntHolder number,
            StringBuilder sb) {
            String name = "%" + number.value++;
            sb.append("λ").append(name).append(".");
            body.toNormalizedString(bind(bind, variable, name), number, sb);
        }
    }

    public static class Application extends Expression {

        public final Expression function, argument;

        public Application(Expression function, Expression argument) {
            Objects.requireNonNull(function, "function");
            Objects.requireNonNull(argument, "argument");
            this.function = function;
            this.argument = argument;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            if (function instanceof Lambda)
                sb.append("(");
            sb.append(function);
            if (function instanceof Lambda)
                sb.append(")");
            sb.append(" ");
            if (!(argument instanceof Variable))
                sb.append("(");
            sb.append(argument);
            if (!(argument instanceof Variable))
                sb.append(")");
            return sb.toString();
        }

        @Override
        void toNormalizedString(Bind<BoundVariable, String> bind, IntHolder number,
            StringBuilder sb) {
            if (function instanceof Lambda)
                sb.append("(");
            function.toNormalizedString(bind, number, sb);
            if (function instanceof Lambda)
                sb.append(")");
            sb.append(" ");
            if (!(argument instanceof Variable))
                sb.append("(");
            argument.toNormalizedString(bind, number, sb);
            if (!(argument instanceof Variable))
                sb.append(")");
        }
    }

    public static abstract class Command extends Expression {

        abstract Expression reduce(Bind<BoundVariable, Expression> bind,
            Map<String, Expression> context,
            Expression argument);

        @Override
        void toNormalizedString(Bind<BoundVariable, String> bind, IntHolder number,
            StringBuilder sb) {
            sb.append(toString());
        }
    }

    public static final Command DEFINE = new Command() {

        @Override
        public String toString() {
            return "define";
        }

        @Override
        Expression reduce(Bind<BoundVariable, Expression> bind, Map<String, Expression> context,
            Expression argument) {
            if (!(argument instanceof FreeVariable))
                throw new LambdaCalculusException("usage: define FREE_VARIABLE EXPRESSION");
            return new Command() {

                final String name = ((FreeVariable) argument).name;

                @Override
                public String toString() {
                    return "define-" + name;
                }

                @Override
                Expression reduce(Bind<BoundVariable, Expression> bind,
                    Map<String, Expression> context,
                    Expression argument) {
                    context.put(name, argument);
                    return argument;
                }
            };
        }
    };

    /**
     * 完全ベータ簡約します。
     * 
     * <ul>
     * <li>内側から外側に向かって簡約します。</li>
     * <li>ラムダ式の内部も簡約します。</li>
     * </ul>
     * @param expression 簡約化する式を指定します。
     * @param bind 束縛変数と値のペアを指定します。
     * @param context 自由変数と値のペアを指定します。
     * @return 簡約化された式を返します。
     */
    public static Expression reduceFull(Expression expression, Bind<BoundVariable, Expression> bind,
        Map<String, Expression> context) {
        if (expression instanceof BoundVariable variable) {
            Expression e = get(bind, variable);
            if (e != null)
                return e;
            return variable;
        } else if (expression instanceof FreeVariable variable) {
            Expression subst = context.get(variable.name);
            return subst != null ? reduceFull(subst, null, context) : variable;
        } else if (expression instanceof Lambda lambda) {
            BoundVariable newVariable = new BoundVariable(lambda.variable.name);
            Expression newBody = reduceFull(lambda.body, bind(bind, lambda.variable, newVariable),
                context);
            return new Lambda(newVariable, newBody);
        } else if (expression instanceof Application application) {
            Expression head = reduceFull(application.function, bind, context);
            Expression tail = reduceFull(application.argument, bind, context);
            if (head instanceof Lambda lambda)
                return reduceFull(lambda.body, bind(bind, lambda.variable, tail), context);
            else if (head instanceof Command command)
                return command.reduce(bind, context, tail);
            return new Application(head, tail);
        } else if (expression instanceof Command command) {
            return command;
        } else
            throw new RuntimeException("unknown expression: " + expression);
    }

    public static Expression reduceFull(Expression expression, Map<String, Expression> context) {
        return reduceFull(expression, null, context);
    }

    public static Expression reduceFull(Expression expression) {
        return reduceFull(expression, Collections.emptyMap());
    }

    /**
     * 名前呼び戦略でベータ簡約します。
     * 
     * <ul>
     * <li>外側から内側に向かって簡約します。</li>
     * <li>ラムダ式の内部は簡約しません。</li>
     * </ul>
     * @param expression 簡約化する式を指定します。
     * @param bind 束縛変数と値のペアを指定します。
     * @param context 自由変数と値のペアを指定します。
     * @return 簡約化された式を返します。
     */
    public static Expression reduceByName(Expression expression, Bind<BoundVariable, Expression> bind,
        Map<String, Expression> context) {
        if (expression instanceof BoundVariable variable) {
            Expression e = get(bind, variable);
            if (e != null)
                return e;
            return variable;
        } else if (expression instanceof FreeVariable variable) {
            Expression subst = context.get(variable.name);
            return subst != null ? reduceByName(subst, null, context) : variable;
        } else if (expression instanceof Lambda lambda) {
            BoundVariable newVariable = new BoundVariable(lambda.variable.name);
            Expression newBody = reduceByName(lambda.body, bind(bind, lambda.variable, newVariable),
                context);
            return new Lambda(newVariable, newBody);
        } else if (expression instanceof Application application) {
            Expression head = reduceByName(application.function, bind, context);
            Expression tail = reduceByName(application.argument, bind, context);
            if (head instanceof Lambda lambda)
                return reduceByName(lambda.body, bind(bind, lambda.variable, tail), context);
            else if (head instanceof Command command)
                return command.reduce(bind, context, tail);
            return new Application(head, tail);
        } else if (expression instanceof Command command) {
            return command;
        } else
            throw new RuntimeException("unknown expression: " + expression);
    }

    public static Expression reduceByName(Expression expression, Map<String, Expression> context) {
        return reduceFull(expression, null, context);
    }

    public static Expression reduceByName(Expression expression) {
        return reduceFull(expression, Collections.emptyMap());
    }

    public static Expression parse(String source) {
        return new Object() {

            int[] codePoints = source.codePoints().toArray();
            int length = codePoints.length;
            int index = 0;
            int ch = ' ';

            boolean isVariableChar(int ch) {
                switch (ch) {
                case -1:
                case 'λ':
                case '\\':
                case '(':
                case ')':
                case '.':
                    return false;
                default:
                    return !Character.isWhitespace(ch);
                }
            }

            int next() {
                return ch = index < length ? codePoints[index++] : -1;
            }

            void skipSpaces() {
                while (Character.isWhitespace(ch))
                    next();
            }

            Lambda parseLambda(Bind<String, BoundVariable> bind) {
                skipSpaces();
                if (!isVariableChar(ch))
                    throw new LambdaCalculusException("variable expected");
                String name = parseVariableName();
                skipSpaces();
                BoundVariable variable = new BoundVariable(name);
                Bind<String, BoundVariable> newBind = bind(bind, name, variable);
                Expression body;
                if (ch == '.') {
                    next(); // skip '.'
                    body = parse(newBind);
                } else
                    body = parseLambda(newBind);
                return new Lambda(variable, body);
            }

            Expression parseParen(Bind<String, BoundVariable> bind) {
                skipSpaces();
                Expression e = parse(bind);
                skipSpaces();
                if (ch != ')')
                    throw new LambdaCalculusException("')' expected");
                next(); // skip ')'
                return e;
            }

            String parseVariableName() {
                StringBuilder sb = new StringBuilder();
                for (; isVariableChar(ch); next())
                    sb.appendCodePoint(ch);
                return sb.toString();
            }

            Variable parseVariable(Bind<String, BoundVariable> bind) {
                String name = parseVariableName();
                BoundVariable variable = get(bind, name);
                return variable != null ? variable : FreeVariable.of(name);
            }

            Expression parseTerm(Bind<String, BoundVariable> bind) {
                skipSpaces();
                switch (ch) {
                case -1:
                    throw new LambdaCalculusException("unexpected end of string");
                case 'λ':
                case '\\':
                    next(); // skip 'λ' or '\\'
                    return parseLambda(bind);
                case '(':
                    next(); // skip '('
                    return parseParen(bind);
                default:
                    if (!isVariableChar(ch))
                        throw new LambdaCalculusException(
                            new StringBuilder("unexpected char '").appendCodePoint(ch).append("'")
                                .toString());
                    return parseVariable(bind);
                }
            }

            Expression parse(Bind<String, BoundVariable> bind) {
                Expression term = parseTerm(bind);
                while (true) {
                    skipSpaces();
                    if (ch != 'λ' && ch != '\\' && ch != '(' && !isVariableChar(ch))
                        break;
                    term = new Application(term, parseTerm(bind));
                }
                return term;
            }

            Expression parse() {
                Expression expression = parse(null);
                if (ch != -1)
                    throw new LambdaCalculusException(
                        "extra string '" + new String(codePoints, index - 1, length - index + 1)
                            + "'");
                return expression;
            }
        }.parse();
    }

    public static class ConsumerWriter extends Writer {

        final Consumer<String> consumer;
        final StringBuilder sb = new StringBuilder();

        public ConsumerWriter(Consumer<String> consumer) {
            this.consumer = consumer;
        }

        void writeLine() {
            if (sb.length() <= 0)
                return;
            consumer.accept(sb.toString());
            sb.setLength(0);
        }

        @Override
        public void write(char[] cbuf, int off, int len) throws IOException {
            String line = new String(cbuf, off, len);
            sb.append(line.replaceAll("[\r\n]", ""));
            if (line.endsWith("\n") || line.endsWith("\r"))
                writeLine();
        }

        @Override
        public void flush() throws IOException {
        }

        @Override
        public void close() throws IOException {
        }
    }

    public static void repl(BiFunction<Expression, Map<String, Expression>, Expression> reducer, Reader reader, Writer writer,
        Map<String, Expression> context, boolean echo, boolean prompt)
        throws IOException {
        String promptStr = "> ";
        BufferedReader input = new BufferedReader(reader);
        PrintWriter output = new PrintWriter(writer, true);
        if (prompt) {
            output.write(promptStr);
            output.flush();
        }
        String line;
        while ((line = input.readLine()) != null) {
            if (echo)
                output.println(line);
            line = line.trim();
            if (line.isEmpty())
                continue;
            if (line.equals("exit"))
                break;
            try {
                Expression result = reducer.apply(parse(line), context);
                output.println(result);
            } catch (LambdaCalculusException e) {
                output.println(e.getMessage());
            }
            if (prompt) {
                output.write(promptStr);
                output.flush();
            }
        }
    }

    /**
     * 入力 echo prompt 標準入力 false true ファイル false false ファイル true true
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        Charset charset = Charset.defaultCharset();
        Reader reader = null;
        Writer writer = null;
        boolean echo = false;
        boolean prompt = false;
        int length = args.length;
        int i = 0;
        L: for (; i < length; ++i) {
            switch (args[i]) {
            case "-echo":
                echo = true;
                break;
            case "-encoding":
                charset = Charset.forName(args[++i]);
                break;
            case "-o":
                writer = new OutputStreamWriter(new FileOutputStream(args[++i]), charset);
                break;
            default:
                break L;
            }
        }
        if (i < length)
            reader = new InputStreamReader(new FileInputStream(args[i++]), charset);
        if (reader == null) {
            reader = new InputStreamReader(System.in);
            prompt = true;
        } else
            prompt = echo;
        if (writer == null)
            writer = new OutputStreamWriter(System.out);
        Map<String, Expression> context = new HashMap<>();
        context.put("define", DEFINE);
        repl(LambdaCalculus2::reduceFull, reader, writer, context, echo, prompt);
    }
}
