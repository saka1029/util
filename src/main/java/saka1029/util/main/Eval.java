package saka1029.util.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import saka1029.util.eval.Context;
import saka1029.util.eval.EvalException;
import saka1029.util.eval.Expression;
import saka1029.util.eval.Parser;

public class Eval {

    static void run(Reader reader, Writer writer) throws IOException {
        Context context = Context.of();
        context.function1("neg", (x, a) -> -a);
        context.function2("+", (x, a, b) -> a + b);
        context.function2("-", (x, a, b) -> a - b);
        context.function2("*", (x, a, b) -> a * b);
        context.function2("/", (x, a, b) -> a / b);
        context.function2("%", (x, a, b) -> a % b);
        context.function2("^", (x, a, b) -> Math.pow(a, b));
        context.function2("hypot", (x, a, b) -> Math.hypot(a, b));
        context.function1("sqrt", (x, a) -> Math.sqrt(a));
        PrintWriter out = new PrintWriter(writer, true);
        BufferedReader in = new BufferedReader(reader);
        while (true) {
            out.print("> ");
            out.flush();
            String line = in.readLine();
            if (line == null)
                break;
            if (line.isBlank())
                continue;
            try {
                Expression expression = Parser.of(line).read();
                double d = expression.eval(context);
                if (!Double.isNaN(d))
                    out.println(Double.toString(d).replaceFirst("\\.0$", ""));
            } catch (EvalException e) {
                System.err.println(e.getMessage());
            }
        }
        out.println();
    }

    public static void main(String[] args) throws IOException {
        Reader reader = new InputStreamReader(System.in);
        Writer writer = new OutputStreamWriter(System.out);
        run(reader, writer);
    }
    
}
