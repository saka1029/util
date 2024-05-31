package saka1029.util.dentaku;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DateTimeException;
import java.util.Comparator;

import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.DefaultParser;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

public class Main {

    interface Term extends Closeable {
        boolean interactive();
        String readLine() throws IOException;
        PrintWriter writer();
    }

    static class FileConsole implements Term {
        final String prompt;
        final BufferedReader reader;
        final PrintWriter writer;
        
        FileConsole(String prompt, String inFile) throws IOException {
            this.prompt = prompt;
            this.reader = Files.newBufferedReader(Path.of(inFile));
            this.writer = new PrintWriter(System.out);
        }

        @Override
        public boolean interactive() {
            return false;
        }

        @Override
        public void close() throws IOException {
            reader.close();
            writer.flush();
        }

        @Override
        public String readLine() throws IOException {
            String line = reader.readLine();
            if (line != null)
                writer.println(prompt + line);
            return line;
        }

        @Override
        public PrintWriter writer() {
            return writer;
        }
    }

    static class MarkDownConsole implements Term {
        final BufferedReader in;
        final PrintWriter out;
        final String prompt;

        MarkDownConsole(String prompt, String inFile) throws IOException {
            this.prompt = prompt;
            this.in = Files.newBufferedReader(Path.of(inFile));
            this.out = new PrintWriter(inFile.replaceFirst("\\.[^.]*$", ".md"), StandardCharsets.UTF_8);
        }

        @Override
        public void close() throws IOException {
            in.close();
            out.close();
        }

        @Override
        public boolean interactive() {
            return false;
        }

        boolean eval = false;

        @Override
        public String readLine() throws IOException {
            String line;
            while (true) {
                line = in.readLine();
                if (line == null)
                    break;
                if (line.trim().equals("```")) {
                    out.println(line.trim());
                    eval = !eval;
                } else if (eval) {
                    out.println(prompt + line.trim());
                    break;
                } else if (line.trim().equals("````"))
                    out.println(line.trim().substring(1));
                else
                    out.println(line);
            }
            return line;
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
        final String prompt;

        JlineConsole(String prompt, String prompt2) throws IOException {
            this.prompt = prompt;
            terminal = TerminalBuilder.builder().build();
            org.jline.reader.Parser parser = new DefaultParser().eofOnEscapedNewLine(true);
            lineReader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .parser(parser)
                    .variable(LineReader.SECONDARY_PROMPT_PATTERN, prompt2)
                    .build();
            out = terminal.writer();
        }

        @Override
        public boolean interactive() {
            return true;
        }

        @Override
        public void close() throws IOException {
            terminal.close();
        }

        @Override
        public String readLine() throws IOException {
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

    static void println(PrintWriter out, String s) {
        if (s != null)
            out.println(s);
    }

    static void helpType(PrintWriter out) {
        out.println("D   : 数値");
        out.println("B   : 真偽値(0:偽, 1:真)");
        out.println("I   : 整数");
        out.println("(D) : 数値の並び");
        out.println("(B) : 真偽値の並び");
        out.println("(I) : 整数の並び");
    }

    static void helpSyntax(PrintWriter out) {
        out.println("statement       = define-variable");
        out.println("                | define-unary");
        out.println("                | define-binary");
        out.println("                | expression");
        out.println("define-variable = ID '=' expression");
        out.println("define-unary    = ID ID '=' expression");
        out.println("define-binary   = ID ID ID '=' expression");
        out.println("expression      = factor { [ '@' ] BOP factor }");
        out.println("factor          = primary");
        out.println("                | [ '@' ] UOP factor");
        out.println("primary         = '(' expression ')'");
        out.println("                | VAR");
        out.println("                | NUMBER");
    }

    static void help(Context context, PrintWriter out, String... items) {
        if (items.length <= 1) {
            out.println(" control-D        : Exit program or (.exit .quit .end)");
            out.println(".help             : Show this message");
            out.println(".help variable    : Show all variables");
            out.println(".help unary       : Show all unary operators");
            out.println(".help binary      : Show all binary operators");
            out.println(".help NAME        : Show help for NAME");
            out.println(".solve expression : Show values satisfy the expression");
        } else if (items.length == 2) {
            String name = items[1];
            switch (name) {
                case "type":
                    helpType(out);
                    break;
                case "syntax":
                    helpSyntax(out);
                    break;
                case "variable":
                    context.variables()
                        .sorted(Comparator.comparing(p -> p.t.toLowerCase()))
                        .forEach(p -> out.println(p.string));
                        break;
                case "unary":
                    context.unarys()
                        .sorted(Comparator.comparing(p -> p.t.toLowerCase()))
                        .forEach(p -> out.println(p.string));
                        break;
                case "binary":
                    context.binarys()
                        .sorted(Comparator.comparing(p -> p.t.toLowerCase()))
                        .forEach(p -> out.println(p.string));
                        break;
                default:
                    if (context.isVariable(name))
                        println(out, context.variable(name).string);
                    if (context.isUnary(name))
                        println(out, context.unary(name).string);
                    if (context.isBinary(name))
                        println(out, context.binary(name).string);
                    break;
            }
        }
    }

    static void solve(Context c, String s, PrintWriter out) {
        try {
            Expression e = Parser.parse(c, s);
            int count = c.solve(e, out::println);
            out.printf("number of solutions=%d%n", count);
        } catch (ValueException | ArithmeticException | NumberFormatException | DateTimeException ex) {
            out.println(ex.getMessage());
        }
    }

    static void run(Term term) throws IOException {
        PrintWriter out = term.writer();
        Context context = Context.of();
        if (term.interactive())
            out.println("Type '.help' for help, control-D to exit.");
        L: while (true) {
            String line = term.readLine();
            if (line == null)
                break;
            line = line.trim();
            if (line.isEmpty())
                continue;
            String[] items = line.split("\\s+");
            switch (items[0]) {
                case ".quit":
                case ".exit":
                case ".end":
                    break L;
                case ".help":
                    help(context, out, items);
                    continue L;
                case ".solve":
                    solve(context, line.replaceFirst("\\S+\\s*", ""), out);
                    continue L;
            }
            try {
                Expression e = Parser.parse(context, line);
                BigDecimal[] value = e.eval(context);
                if (value != Value.NaN)
                    out.println(Value.str(value));
            } catch (ValueException | ArithmeticException
                | NumberFormatException | DateTimeException ex) {
                out.println(ex.getMessage());
            }
        }
    }

    static void error(String message) {
        throw new IllegalArgumentException(message);
    }
    
    static void usage() {
        throw new IllegalArgumentException("usage: java saka1029.dentaku.Main [-m] [FILE]");
    }

    public static void main(String[] args) throws IOException {
        String prompt = "    ", prompt2 = "        ";
        boolean markDown = false;
        String file = null;
        for (int i = 0; i < args.length; ++i) {
            switch (args[i]) {
                case "-m":
                    markDown = true;
                    break;
                default:
                    file = args[i];
                    break;
            }
        }
        if (file != null && !Files.exists(Paths.get(file)))
            error("No such file : " + file);
        Term term = null;
        try {
            if (markDown && file != null)
                term = new MarkDownConsole(prompt, file);
            else if (!markDown && file != null)
                term = new FileConsole(prompt, file);
            else if (!markDown && file == null)
                term = new JlineConsole(prompt, prompt2);
            else
                usage();
            run(term);
        } finally {
            term.close();
        }
    }

}
