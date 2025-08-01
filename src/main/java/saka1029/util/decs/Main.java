package saka1029.util.decs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
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

    static final ParsedLine DUMMY = new ParsedLine() {
        @Override public String word() { return ""; }
        @Override public int wordCursor() { return 0; }
        @Override public int wordIndex() { return 0; }
        @Override public List<String> words() { return List.of(); }
        @Override public String line() { return ""; }
        @Override public int cursor() { return 0; }
    };

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
                    throw new SyntaxError(0, 0, e.getMessage());
                };
            }
            return DUMMY;
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
                if (result == Decs.EXIT)
                    break;
                else if (result != Decs.NO_VALUE)
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

    static String addPrompt(String s) {
        return s.lines()
            .map(x -> PROMPT + x)
            .collect(Collectors.joining(NL));
    }

    public static void file(BufferedReader reader, PrintWriter writer) throws IOException {
        Parser parser = Parser.create();
        parser.context.output = s -> writer.println(s);
        StringBuilder input = new StringBuilder();
        while (true) {
            String line = reader.readLine();
            if (line == null)
                break;
            input.append(line).append(NL);
            Expression expression = null;
            try {
                expression = parser.parse(input.toString());
            } catch (EOFException e) {
                continue;   // read and add next line to input
            } catch (SyntaxException e) {
                expression = c -> {throw e;};
            }
            try {
                writer.println(addPrompt(input.toString()));
                input.setLength(0);
                BigDecimal[] result = expression.eval(parser.context);
                if (result == Decs.EXIT)
                    break;
                else if (result != Decs.NO_VALUE)
                    writer.println(Decs.string(result));
            } catch (SyntaxException | UndefException | ValueException | ArithmeticException e) {
                writer.println(e.getMessage());
            }
            writer.flush();
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
            throw new IllegalArgumentException("usage: java saka1029.util.decs.Main [-f FILE]");
    }
}
