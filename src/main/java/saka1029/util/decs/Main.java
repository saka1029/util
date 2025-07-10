package saka1029.util.decs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.jline.reader.EOFError;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.ParsedLine;
import org.jline.reader.SyntaxError;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

/**
 * @see https://takemikami.com/2019/0302-javacuijline.html
 */
public class Main {

    static AttributedString color(String s, int style) {
        return new AttributedStringBuilder()
            .style(AttributedStyle.DEFAULT.foreground(style))
            .append(s)
            .style(AttributedStyle.DEFAULT.foregroundDefault())
            .toAttributedString();
    }

    static final String NL = System.lineSeparator();
    static final String PROMPT = "    ";
    static final String SECONDARY_PROMPT = "    ";

    static class ExpressionParser implements org.jline.reader.Parser {
 
        Parser parser = Parser.create();
        Expression expression;

        @Override
        public ParsedLine parse(String line, int cursor, ParseContext context) {
            try {
                expression = parser.parse(line);
            } catch (EOFException e) {
                throw new EOFError(0, 0, e.getMessage());
            } catch (SyntaxException e) {
                expression = c -> {
                    c.output.accept(e.getMessage());
                    return Decs.NO_VALUE;
                };
            }
            return null;
        }
    }

    static final AttributedString INTERRUPTED = color("Interrupted!", AttributedStyle.RED);

    public static void jline() throws IOException {
        // JLine terminal の準備
        Terminal terminal = TerminalBuilder.builder()
            .system(true)
            .build();

        // Parser の準備
        ExpressionParser parser = new ExpressionParser();
        LineReader lineReader = LineReaderBuilder.builder()
            .terminal(terminal)
            .parser(parser)
            .build();
        lineReader.setVariable(LineReader.SECONDARY_PROMPT_PATTERN, SECONDARY_PROMPT);
        parser.parser.context.output = s -> lineReader.printAbove(color(s, AttributedStyle.GREEN));

        // REPL
        lineReader.printAbove("Ctrl-D to exit.  Ctrl-C to interrupt");
        while (true) {
            try {
                lineReader.readLine(PROMPT);
                BigDecimal[] result = parser.expression.eval(parser.parser.context);
                if (result != Decs.NO_VALUE)
                    lineReader.printAbove(Decs.string(result));
            } catch (EndOfFileException e) {        // catch Ctrl-D
                break;
            } catch (SyntaxError | UndefException | ValueException | ArithmeticException e) {
                lineReader.printAbove(color(e.getLocalizedMessage(), AttributedStyle.RED));
            } catch (UserInterruptException e) {    // catch Ctrl-C
                lineReader.printAbove(INTERRUPTED);
            }
        }
    }

    public static void file(BufferedReader reader, PrintWriter writer) throws IOException {
        Parser parser = Parser.create();
        StringBuilder out = new StringBuilder();
        parser.context.output = s -> out.append(s).append(NL);
        StringBuilder line = new StringBuilder();
        while (true) {
            String input = reader.readLine();
            if (input == null)
                break;
            line.append(input).append(NL);
            try {
                String lineString = line.toString();
                Expression e = parser.parse(lineString);
                lineString.lines()
                    .forEach(s -> out.append(PROMPT).append(s).append(NL));
                out.append(Decs.string(e.eval(parser.context)));
                writer.println(out);
                writer.flush();
                line.setLength(0);
                out.setLength(0);
            } catch (EOFException e) {
                continue;
            } catch (SyntaxException | UndefException | ValueException | ArithmeticException e) {
                writer.println(e.getMessage());
            }
        }

    }

    public static void file(String file) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(file))) {
            PrintWriter writer = new PrintWriter(System.out); 
            file(reader, writer);
        }
    }

    public static void main(String[] args) throws IOException {
        int len = args.length;
        if (len == 0)
            jline();
        else if (len == 2 && args[0].equals("-f"))
            file(args[1]);
        else
            throw new IllegalArgumentException("usage: decs [-f FILE]");
    }
}
