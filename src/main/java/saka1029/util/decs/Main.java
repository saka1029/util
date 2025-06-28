package saka1029.util.decs;

import java.io.IOException;
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

    static class ExpressionParser implements org.jline.reader.Parser {
 
        Parser parser = new Parser();
        AttributedString result;

        @Override
        public ParsedLine parse(String line, int cursor, ParseContext context) throws SyntaxError {
            try {
                this.result = new AttributedString(Decs.string(parser.eval(line)));
            } catch (EOFException e) {
                throw new EOFError(0, 0, e.getMessage());
            } catch (SyntaxException s) {
                this.result = color(s.getMessage(), AttributedStyle.RED);
            }
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
            .build();
        lineReader.setVariable(LineReader.SECONDARY_PROMPT_PATTERN, "    >   ");;

        // REPL
        while (true) {
            try {
                lineReader.readLine("    ");
                lineReader.printAbove(parser.result);
            } catch (EndOfFileException e) {        // catch Ctrl-D
                break;
            } catch (UserInterruptException e) {    // catch Ctrl-C
                lineReader.printAbove(INTERRUPTED);
            }
        }
    }
}
