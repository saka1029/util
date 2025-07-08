package saka1029.util.decs;

import java.io.IOException;
import java.math.BigDecimal;
import org.jline.reader.EOFError;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.ParsedLine;
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

    static class ExpressionParser implements org.jline.reader.Parser {
 
        Parser parser = Parser.create();
        AttributedString result;
        AttributedString out;
        ExpressionParser() {
            parser.context.output = s -> out = AttributedString.join(
                AttributedString.EMPTY, out,
                color(s, AttributedStyle.GREEN), AttributedString.NEWLINE);
        }

        @Override
        public ParsedLine parse(String line, int cursor, ParseContext context) {
            try {
                out = AttributedString.EMPTY;
                BigDecimal[] decs = parser.eval(line);
                if (decs != Decs.NO_VALUE)
                    out = AttributedString.join(
                        AttributedString.EMPTY, out,
                        new AttributedString(Decs.string(decs)), AttributedString.NEWLINE);
                result = out == AttributedString.EMPTY ? null : out;
            } catch (EOFException e) {
                throw new EOFError(0, 0, e.getMessage());
            } catch (SyntaxException | UndefException | ValueException | ArithmeticException e) {
                result = color(e.getMessage(), AttributedStyle.RED);
            }
            return null;
        }
    }

    static final AttributedString INTERRUPTED = color("Interrupted!", AttributedStyle.RED);

    public static void jline(String[] args) throws IOException {
        // JLine terminal の準備
        Terminal terminal = TerminalBuilder.builder()
            .system(true)
            .build();

        // Parser の準備
        ExpressionParser parser = new ExpressionParser();
        LineReader lineReader = LineReaderBuilder.builder()
            .terminal(terminal)
            .parser(parser)
            // .completer((reader, cursor, candidates) -> {
            //     Buffer buffer = reader.getBuffer();
            //     // String buffer = commandLine.line().substring(0, commandLine.cursor());
            //     // System.out.println("buffer=" + buffer.toString());
            //     Stream.of("factorial", "iota", "iota0", "iseven", "isodd")
            //         .filter(s -> s.startsWith(buffer.toString()))
            //         .forEach(s -> candidates.add(new Candidate(s, s, null, null, null, null, true)));
            // })
            .build();
        lineReader.setVariable(LineReader.SECONDARY_PROMPT_PATTERN, "    >   ");;

        // REPL
        lineReader.printAbove("Ctrl-D to exit.  Ctrl-C to interrupt");
        while (true) {
            try {
                lineReader.readLine("    ");
                if (parser.result != null)
                    lineReader.printAbove(parser.result);
            } catch (EndOfFileException e) {        // catch Ctrl-D
                break;
            } catch (UserInterruptException e) {    // catch Ctrl-C
                lineReader.printAbove(INTERRUPTED);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        jline(args);
    }
}
