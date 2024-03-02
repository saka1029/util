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

    static void help(PrintWriter out) {
        out.println("Type '/exit' or '/quit' to exit:");
        out.println();
        out.println("SYNTAX:");
        out.println(" statement       = expression");
        out.println("                 | define-variable");
        out.println("                 | define-function.");
        out.println(" define-variable = ID '=' expression.");
        out.println(" define-function = ID '(' [ ID { ',' ID } ] ')' '=' expression.");
        out.println(" expression      = [ '+' | '-' ] term { [ '+' | '-' ] term }.");
        out.println(" term            = factor { [ '*' | '/' | '%' ] factor }.");
        out.println(" factor          = primary { '^' factor }.");
        out.println(" primary         = ID [ '(' [ expression { ',' expression } ] ')' ]");
        out.println("                 | NUMBER");
        out.println("                 | '(' expression ')'.");
    }

    static void run(Reader reader, Writer writer) throws IOException {
        Context context = Context.of();
        PrintWriter out = new PrintWriter(writer, true);
        BufferedReader in = new BufferedReader(reader);
        L: while (true) {
            out.print("> ");
            out.flush();
            String line = in.readLine();
            if (line == null)
                break L;
            if (line.isBlank())
                continue L;
            switch (line) {
                case "/help":
                    help(out);
                    continue L;
                case "/exit":
                case "/quit":
                    break L;
            }
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
