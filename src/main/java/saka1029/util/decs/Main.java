package saka1029.util.decs;

import java.io.IOException;
import org.jline.reader.EOFError;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.ParsedLine;
import org.jline.reader.Parser;
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

    static EOFError error(String message, Object... args) {
        return new EOFError(0, 0, message.formatted(args));
    }

    static AttributedString color(String s, int style) {
        return new AttributedStringBuilder()
            .style(AttributedStyle.DEFAULT.foreground(style))
            .append(s)
            .style(AttributedStyle.DEFAULT.foregroundDefault())
            .toAttributedString();
    }

    static class ExpressionParser implements Parser {
 
        int input[], ch, index;

        int get() {
            return ch = index < input.length ? input[index++] : -1;
        }

        void spaces() {
            while (Character.isWhitespace(ch))
                get();
        }

        boolean eat(int expected) {
            spaces();
            if (ch == expected) {
                get();
                return true;
            }
            return false;
        }

        long factor() {
            if (eat(-1)) {
                throw error("Unexpected end");
            } else if (eat('(')) {
                long value = expression();
                if (!eat(')'))
                    throw error("')' expected");
                return value;
            } else if (Character.isDigit(ch)) {
                long value = 0;
                do {
                    value = value * 10 + Character.digit(ch, 10);
                    get();
                } while (Character.isDigit(ch));
                return value;
            } else
                throw new SyntaxError(0, 0, "Unknown char '%c'".formatted(ch));
        }

        long term() {
            long e = factor();
            while (true)
                if (eat('*'))
                    e *= factor();
                else if (eat('/'))
                    e /= factor();
                else if (eat('%'))
                    e %= factor();
                else
                    break;
            return e;
        }

        long expression() {
            long e = eat('-') ? -term() : term();
            while (true)
                if (eat('+'))
                    e += term();
                else if (eat('-'))
                    e -= term();
                else
                    break;
            return e;
        }

        AttributedString result;

        /**
         * SYNTAX:
         * <ul>
         * <li>expression = [ '-' ] term { ( '+' | '-' ) term }</li>
         * <li>term       = factor { ( '*' | '/' | '%' ) factor }</li>
         * <li>factor     = '(' expression ')' | number</li>
         * <li>number     = { DIGIT }</li>
         * <li>DIGIT      = '0' ... '9'</li>
         * </ul>
         */
        @Override
        public ParsedLine parse(String line, int cursor, ParseContext context) throws SyntaxError {
            this.input = line.codePoints().toArray();
            this.index = 0;
            get();
            try {
                this.result = new AttributedString("" + expression());
            } catch (EOFError e) {
                throw e;
            } catch (SyntaxError s) {
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
        lineReader.setVariable(LineReader.SECONDARY_PROMPT_PATTERN, "      >   ");;

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
