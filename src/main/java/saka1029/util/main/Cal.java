package saka1029.util.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import saka1029.util.cal.Context;
import saka1029.util.cal.EvalException;
import saka1029.util.cal.Expression;
import saka1029.util.cal.Parser;

public class Cal {

    static void help(PrintWriter out) {
        out.println("COMMAND:");
        out.println(" /exit   : Exit");
        out.println(" /quit   : Exit");
        out.println(" /vars   : List variable names");
        out.println(" /funcs  : List function names");
        out.println(" /syntax : Show syntax");
    }

    static void syntax(PrintWriter out) {
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

    static void run(String prompt, Reader reader, Writer writer) throws IOException {
        Context context = Context.of();
        PrintWriter out = new PrintWriter(writer, true);
        BufferedReader in = new BufferedReader(reader);
        out.println("Type '/help' to help.");
        L: while (true) {
            out.print(prompt);
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
                case "/syntax":
                    syntax(out);
                    continue L;
                case "/exit":
                case "/quit":
                    break L;
                case "/vars":
                    context.variables().stream().sorted().forEach(v -> out.println(v));
                    continue L;
                case "/funcs":
                    context.functions().stream().sorted().forEach(v -> out.println(v));
                    continue L;
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
        run("> ", reader, writer);
    }
    
}
