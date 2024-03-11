package saka1029.util.dentaku;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Map.Entry;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

public class Dentaku {

    static void help(PrintWriter out) {
        out.println("/exit    Exit program");
        out.println("/help    Show available commands");
        out.println("/syntax  Show expression syntax");
        out.println("/vars    Show variables");
        out.println("/quit    Quit program");
    }

    static void syntax(PrintWriter out) {
        out.println(" statement  = [ ID '=' ] expression");
        out.println(" expression = term { ( '+' | '-' ) term }");
        out.println(" term       = factor { ( '*' | '/' ) factor }");
        out.println(" factor     = unary { '^' factor }");
        out.println(" unary      = vector | UOP unary");
        out.println(" vector     = primary { primary }");
        out.println(" primary    = '(' expression ')' | ID | NUMBER");
    }

    static void vars(PrintWriter out, Context context) {
        context.variables().stream()
            .sorted(Entry.comparingByKey())
            .forEach(e -> {
                String value;
                try {
                    value = e.getValue().eval(context).toString();
                } catch (VectorException ex) {
                    value = ex.getMessage();
                }
                out.printf("%s -> %s%n", e.getKey(), value);
            });
    }

    static void eval(String line, PrintWriter out, Context context) {
        try {
            Expression e = Parser.parse(line);
            Vector v = e.eval(context);
            if (v != Vector.NaN)
                out.println(v);
        } catch (VectorException ex) {
            out.println(ex.getMessage());
        }
    }

    static void run(Reader input, Writer output, String prompt) throws IOException {
        BufferedReader in = new BufferedReader(input);
        PrintWriter out = new PrintWriter(output, true);
        Context context = Context.of();
        LOOP: while (true) {
            out.print(prompt);
            out.flush();
            String line = in.readLine();
            if (line == null)
                break;
            line = line.trim();
            switch (line) {
                case "/exit":
                case "/quit":
                    break LOOP;
                case "/syntax":
                    syntax(out);
                    break;
                case "/help":
                    help(out);
                    break;
                case "/vars":
                    vars(out, context);
                    break;
                default:
                    eval(line, out, context);
                    break;
            }
        }
        out.println();
    }

    static void usage() {
        System.err.printf("java %s%n", Dentaku.class.getName());
        System.exit(1);
    }

    public static void run(String prompt) throws IOException {
        try (Terminal terminal = TerminalBuilder.terminal()) {
            LineReader lineReader = LineReaderBuilder.builder()
                .terminal(terminal)
                .build();
            Context context = Context.of();
            LOOP: while (true) {
                String line = lineReader.readLine(prompt);
                if (line == null)
                    break;
                line = line.trim();
                switch (line) {
                    case "/exit":
                    case "/quit":
                        break LOOP;
                    case "/syntax":
                        syntax(terminal.writer());
                        break;
                    case "/help":
                        help(terminal.writer());
                        break;
                    case "/vars":
                        vars(terminal.writer(), context);
                        break;
                    default:
                        eval(line, terminal.writer(), context);
                        break;
                }
            }
            terminal.writer().println();
        }
    }

    public static void main(String[] args) throws IOException {
        // usage();
        Reader input = new InputStreamReader(System.in);
        Writer output = new OutputStreamWriter(System.out);
        String prompt = "    ";
        // run(input, output, prompt);
        run(prompt);
    }

}
