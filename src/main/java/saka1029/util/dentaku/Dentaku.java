package saka1029.util.dentaku;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map.Entry;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.DefaultParser;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import java.io.InputStream;

public class Dentaku {

    interface Term extends Closeable {
        String readLine(String prompt) throws IOException;
        PrintWriter writer();
    }

    static class Console implements Term {
        final BufferedReader in;
        final PrintWriter out;

        Console() {
            in = new BufferedReader(new InputStreamReader(System.in));
            out = new PrintWriter(new OutputStreamWriter(System.out));
        }

        @Override
        public void close() throws IOException {
            // do nothing
        }

        @Override
        public String readLine(String prompt) throws IOException {
            out.print(prompt);
            out.flush();;
            return in.readLine();
        }

        @Override
        public PrintWriter writer() {
            return out;
        }
    }

    static class JlineConsole implements Term {
        final Terminal terminal;
        final LineReader lineReader;
        final PrintWriter out;

        JlineConsole() throws IOException {
            terminal = TerminalBuilder.builder().build();
            org.jline.reader.Parser parser = new DefaultParser().eofOnEscapedNewLine(true);
            lineReader = LineReaderBuilder.builder()
                .terminal(terminal)
                .parser(parser)
                .build();
            out = terminal.writer();
        }

        @Override
        public void close() throws IOException {
            terminal.close();
        }

        @Override
        public String readLine(String prompt) throws IOException {
            try {
                return lineReader.readLine(prompt);
            } catch (EndOfFileException e) {
                return null;
            }
        }

        @Override
        public PrintWriter writer() {
            return out;
        }
    }

    static void help(PrintWriter out) {
        out.println(".exit    Exit program");
        out.println(".help    Show available commands");
        out.println(".syntax  Show expression syntax");
        out.println(".vars    Show variables");
        out.println(".unary   Show unary operators");
        out.println(".quit    Quit program");
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
                out.printf("%s = %s%n", e.getKey(), value);
            });
    }

    static void unary(PrintWriter out, Context context) {
        context.operators().names().stream()
            .sorted()
            .forEach(e -> out.printf("%s%n", e));
    }

    static void eval(String line, PrintWriter out, Context context) {
        try {
            Expression e = Parser.parse(context.operators(), line);
            Vector v = e.eval(context);
            if (v != Vector.NaN)
                out.println(v);
        } catch (VectorException | ArithmeticException ex) {
            out.println(ex.getMessage());
        }
    }

    static void usage() {
        System.err.printf("java %s%n", Dentaku.class.getName());
        System.exit(1);
    }

    static final String CONFIG_FILE = "config.txt";
    static void initContext(Context context) {
        try (InputStream is = Dentaku.class.getResourceAsStream(CONFIG_FILE);
            Reader r = new InputStreamReader(is, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(r)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().startsWith("#"))
                    continue;
                Expression e = Parser.parse(context.operators(), line);
                e.eval(context);
            }
        } catch (IOException | VectorException e) {
            System.err.println(e);
            System.exit(1);
        }
    }

    public static void run(Term term, String prompt) throws IOException {
        PrintWriter out = term.writer();
        Operators ops = Operators.of();
        Context context = Context.of(ops);
        initContext(context);
        out.println("Type '.help' to get help");
        LOOP: while (true) {
            String line = term.readLine(prompt);
            if (line == null)
                break LOOP;
            line = line.trim();
            if (line.startsWith("#"))
                continue LOOP;
            switch (line) {
                case ".exit":
                case ".quit":
                    break LOOP;
                case ".syntax":
                    syntax(out);
                    break;
                case ".help":
                    help(out);
                    break;
                case ".vars":
                    vars(out, context);
                    break;
                case ".unary":
                    unary(out, context);
                    break;
                default:
                    eval(line, out, context);
                    break;
            }
        }
        out.println();
    }

    public static void main(String[] args) throws IOException {
        // usage();
        String prompt = "$ ";
        try (Term term = new JlineConsole()) {
            run(term, prompt);
        }
    }
}
