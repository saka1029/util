package saka1029.util.main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Comparator;
import java.util.Map.Entry;
import saka1029.util.cal.Context;
import saka1029.util.cal.EvalException;
import saka1029.util.cal.Expression;
import saka1029.util.cal.Parser;

public class Cal {

    static String string(double v) {
        return Double.toString(v).replaceFirst("\\.0$", "");
    }

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

    enum Command {
        EXIT, COMMAND, NOT_COMMAND
    }

    static void vars(Context context, PrintWriter out) {
        context.variables().stream()
            .sorted(Comparator.comparing(Entry::getKey))
            .forEach(e -> {
                out.printf("%s -> ", e.getKey());
                try {
                    out.print(string(e.getValue().eval(context)));
                } catch (EvalException ex) {
                    out.print("unknown");
                }
                out.println();
            });
    }

    static Command command(Context context, PrintWriter out, String line) {
        switch (line) {
            case "/help":
                help(out);
                return Command.COMMAND;
            case "/syntax":
                syntax(out);
                return Command.COMMAND;
            case "/exit":
            case "/quit":
                return Command.EXIT;
            case "/vars":
                vars(context, out);
                return Command.COMMAND;
            case "/funcs":
                context.functions().stream().sorted().forEach(v -> out.println(v));
                return Command.COMMAND;
            default:
                return Command.NOT_COMMAND;
        }

    }

    static void run(String prompt, Reader reader, Writer writer, boolean echo) throws IOException {
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
            if (echo)
                out.println(line);
            if (line.isBlank())
                continue L;
            switch (command(context, out, line)) {
                case EXIT:
                    break L;
                case COMMAND:
                    continue L;
                case NOT_COMMAND:
                    break;
            }
            try {
                Parser parser = Parser.of(line);
                Expression expression = parser.read();
                Expression next = parser.read();
                double d = expression.eval(context);
                if (!Double.isNaN(d))
                    out.println(string(d));
                if (next != null)
                    System.err.println("(Extra tokens were discarded)");
            } catch (EvalException e) {
                System.err.println(e.getMessage());
            }
        }
        out.println();
    }

    static void usage() {
        System.err.println("USAGE:");
        System.err.println("java saka1029.util.main.Cal [-f FILE] [-e]");
        System.err.println("-f FILE    : Input file");
        System.err.println("-e         : Echo back");
        System.exit(1);
    }

    public static void main(String[] args) throws IOException {
        String prompt = "> ";
        boolean echo = false;
        String file = null;
        for (int i = 0; i < args.length; ++i)
            switch (args[i]) {
                case "-f":
                    if (++i < args.length) {
                        file = args[i++];
                        echo = true;
                    } else
                        usage();
                    break;
                case "-e":
                    echo = true;
                    break;
                default:
                    usage();
            }
        Reader reader = file != null ? new FileReader(file) : new InputStreamReader(System.in);
        run(prompt, reader, new OutputStreamWriter(System.out), echo);
    }
    
}
