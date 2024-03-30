package saka1029.util.dentaku;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;

import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.DefaultParser;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

public class Main {

    interface Term extends Closeable {
        String readLine() throws IOException;
        PrintWriter writer();
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
        out.println("D      : 十進数(小数可)");
        out.println("B      : 真偽値(0:偽, 1:真)");
        out.println("I      : 整数");
        out.println("V      : 十進数の並び");
        out.println("Vb     : 真偽値の並び");
        out.println("Vi     : 整数の並び");
    }

    static void helpSyntax(PrintWriter out) {
        out.println("statement       = define-variable");
        out.println("                | define-unary");
        out.println("                | define-binary");
        out.println("                | expression");
        out.println("define-variable = ID '=' expression");
        out.println("define-unary    = IDSPECIAL ID '=' expression");
        out.println("define-binary   = ID IDSPECIAL ID '=' expression");
        out.println("expression      = unary { BOP unary }");
        out.println("unary           = sequence");
        out.println("                | UOP unary");
        out.println("                | MOP UOP unary'");
        out.println("sequence        = primary { primary }");
        out.println("primary         = '(' expression ')'");
        out.println("                | VAR");
        out.println("                | NUMBER { NUMBER }");
    }

    static void help(Context context, PrintWriter out, String... items) {
        if (items.length <= 1) {
            out.println("Ctrl-D          : exit program");
            out.println(".exit           : exit program");
            out.println(".quit           : exit program");
            out.println(".end            : exit program");
            out.println(".help           : show this message");
            out.println(".help variable  : show all variables");
            out.println(".help unary     : show all unary operators");
            out.println(".help binary    : show all binary operators");
            out.println(".help high      : show all high-order operators");
            out.println(".help NAME      : show help for NAME");
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
                    context.variables().stream()
                        .sorted()
                        .forEach(s -> out.println(s));
                        break;
                case "unary":
                    context.operators().unarys().stream()
                        .sorted()
                        .forEach(s -> out.println(s));
                        break;
                case "binary":
                    context.operators().binarys().stream()
                        .sorted()
                        .forEach(s -> out.println(s));
                        break;
                case "high":
                    context.operators().highs().stream()
                        .sorted()
                        .forEach(s -> out.println(s));
                        break;
                default:
                    println(out, context.variableString(name));
                    println(out, context.operators().unaryString(name));
                    println(out, context.operators().binaryString(name));
                    println(out, context.operators().highString(name));
                    break;
            }
        }
    }

    static void run(Term term) throws IOException {
        PrintWriter out = term.writer();
        Operators functions = Operators.of();
        Context context = Context.of(functions);
        out.println("Type '.help' for help.");
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
            }
            try {
                Expression e = Parser.parse(functions, line);
                Value value = e.eval(context);
                if (value != Value.NaN)
                    out.println(value);
            } catch (ValueException | ArithmeticException | NumberFormatException ex) {
                out.println(ex.getMessage());
            }
        }
    }

    public static void main(String[] args) throws IOException {
        String prompt = "  ", prompt2 = "    ";
        Term term = new JlineConsole(prompt, prompt2);
        run(term);
    }

}
