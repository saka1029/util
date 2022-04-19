package util.language.stack.main;

import java.io.StringReader;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import util.language.stack.core.Context;
import util.language.stack.core.Executable;
import util.language.stack.values.Bool;
import util.language.stack.values.Value;

public class Stack {

    static Executable unary(UnaryOperator<Value> operator) {
        return c -> c.push(operator.apply(c.pop()));
    }

    static Executable binary(BinaryOperator<Value> operator) {
        return c -> {
            Value right = c.pop();
            c.push(operator.apply(c.pop(), right));
        };
    }

    static Function<Value, Value> lambda(Context context, Value lambda) {
        return v -> {
            context.push(v);
            lambda.run(context);
            return context.pop();
        };
    }

    static Consumer<Value> consumer(Context context, Value consumer) {
        return v -> {
            context.push(v);
            consumer.run(context);
        };
    }

    static void initialize(Context context) {
        context.add("dup", c -> c.push(c.top()));
        context.add("drop", c -> c.pop());

        context.add("swap", c -> {
            Value a = c.pop(), b = c.pop();
            c.push(a);
            c.push(b);
        });

        context.add("over", c -> c.push(c.stack[c.sp - 2]));

        context.add("execute", c -> c.pop().run(c));

        context.add("if", c -> {
            Value orElse = c.pop(), then = c.pop(), predicate = c.pop();
            if (((Bool)predicate).value)
                then.run(c);
            else
                orElse.run(c);
        });

        context.add("define", c -> {
            Value codes = c.pop();
            Value name = c.pop();
            c.globals.put(name.toString(), codes);
        });

        context.add("&&", c -> {
            Value right = c.pop(), left = c.pop();
            if (((Bool)left).value)
                right.run(c);
            else
                c.push(Bool.FALSE);
        });
        context.add("!!", c -> {
            Value right = c.pop(), left = c.pop();
            if (((Bool)left).value)
                c.push(Bool.TRUE);
            else
                right.run(c);
        });

        context.add("==", binary(Value::eq));
        context.add("!=", binary(Value::ne));

        context.add("!", unary(Value::not));
        context.add("&", binary(Value::and));
        context.add("|", binary(Value::or));
        context.add("^", binary(Value::xor));

        context.add("negate", unary(Value::negate));
        context.add("+", binary(Value::add));
        context.add("-", binary(Value::sub));
        context.add("*", binary(Value::mul));
        context.add("/", binary(Value::div));
        context.add("%", binary(Value::mod));

        context.add("<", binary(Value::lt));
        context.add("<=", binary(Value::le));
        context.add(">", binary(Value::gt));
        context.add(">=", binary(Value::ge));

        context.add("size", c -> c.push(c.pop().size()));

        context.add("for", c -> {
            Value f = c.pop();
            c.pop().repeat(consumer(c, f));
        });

        context.add("map", c -> {
            Value f = c.pop();
            c.push(c.pop().map(lambda(c, f)));
        });

        context.add("filter", c -> {
            Value f = c.pop();
            c.push(c.pop().filter(lambda(c, f)));
        });

        context.add("range", binary(Value::range));

    }

    public static Context context(int stackSize) {
        Context context = new Context(stackSize);
        initialize(context);
        return context;
    }

    public static void repl(Context context, java.io.Reader reader) {
        for (;;) {
            Value element = Reader.read(context, reader);
            if (element == Reader.END_OF_STREAM)
                break;
            element.execute(context);
        }
    }

    public static void repl(Context context, String source) {
        repl(context, new StringReader(source));
    }

}
