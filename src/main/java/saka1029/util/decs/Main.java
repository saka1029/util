package saka1029.util.decs;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;
import org.jline.reader.Buffer;
import org.jline.reader.Candidate;
import org.jline.reader.EOFError;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.ParsedLine;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.completer.ArgumentCompleter;
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

    // static class ParsedLineImpl implements ParsedLine {

    //     final List<String> words;
    //     final String line;

    //     ParsedLineImpl(String line, List<String> words) {
    //         this.line = line;
    //         this.words = words;
    //     }

    //     @Override
    //     public String word() {
    //         return words.get(0);
    //     }

    //     @Override
    //     public int wordCursor() {
    //         return 0;
    //     }

    //     @Override
    //     public int wordIndex() {
    //         return 0;
    //     }

    //     @Override
    //     public List<String> words() {
    //         return words;
    //     }

    //     @Override
    //     public String line() {
    //         throw new UnsupportedOperationException("Unimplemented method 'line'");
    //     }

    //     @Override
    //     public int cursor() {
    //         return 0;
    //     }

    // }

    static class ExpressionParser implements org.jline.reader.Parser {
 
        Parser parser = new Parser();
        AttributedString result;
        StringBuilder out = new StringBuilder();
        ExpressionParser() {
            parser.context.output = s -> out.append(s).append(NL);
        }

        @Override
        public ParsedLine parse(String line, int cursor, ParseContext context) {
            try {
                out.setLength(0);
                BigDecimal[] decs = parser.eval(line);
                if (decs != Decs.NO_VALUE)
                    out.append(Decs.string(decs)).append(NL);
                result = out.length() > 0 ? new AttributedString(out) : null;
            } catch (EOFException e) {
                throw new EOFError(0, 0, e.getMessage());
            } catch (SyntaxException | UndefException | ValueException | ArithmeticException e) {
                this.result = color(e.getMessage(), AttributedStyle.RED);
            }
            // String token = parser.tokens.get(parser.tokens.size() - 1).string;
            // String token = parser.tokens.get(0).string;
            // return new ArgumentCompleter.ArgumentLine(token, 0);
            // return new ParsedLineImpl(line, parser.tokens.stream().map(t -> t.string).toList());
            return null;
        }
    }

    static final AttributedString INTERRUPTED = color("Interrupted!", AttributedStyle.RED);

    public static void main(String[] args) throws IOException {
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
}
