package saka1029.util.declisp;

import static saka1029.util.declisp.Common.*;
import static saka1029.util.declisp.DecLisp.defaultEnv;

import java.io.IOException;
import java.util.List;

import org.jline.reader.CompletingParsedLine;
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
import org.jline.utils.InfoCmp.Capability;

public class Main {

    static class SimpleDecListParser implements Parser {

        static class MyParsedLine implements CompletingParsedLine {
            final String word;
            public MyParsedLine(String word) { this.word = word; }
            @Override public String word() { return word; }
            @Override public int wordCursor() { return 0; }
            @Override public int wordIndex() { return 0; }
            @Override public List<String> words() { return List.of(); }
            @Override public String line() { return word; }
            @Override public int cursor() { return 0; }
            @Override public CharSequence escape(CharSequence candidate, boolean complete) { return null; }
            @Override public int rawWordCursor() { return 0; }
            @Override public int rawWordLength() { return 0; }
        }

        @Override
        public ParsedLine parse(String line, int cursor, ParseContext context) throws SyntaxError {
            try {
                new Reader(line).read();
                return new MyParsedLine(line);
            } catch (DecLispEOFException x) {
                throw new EOFError(0, 0, x.getMessage());
            }
        }
    }

    static final String ERROR_COLOR  = "\u001b[00;91m";
    static final String ERROR_COLOR_END  = "\u001b[00m";
    static final String PROMPT = "\u001b[00;92mdecl> ";

    public static void main(String[] args) {
    try {
        Terminal terminal = TerminalBuilder.builder().system(true).build();
        LineReader reader = LineReaderBuilder.builder()
            .parser(new SimpleDecListParser())
            .terminal(terminal)
            .variable(LineReader.SECONDARY_PROMPT_PATTERN, "%M> ")
            .build();

        Env env = defaultEnv();

        while (true) {
            try {
                String line = reader.readLine(PROMPT);
                if ("exit".equalsIgnoreCase(line))
                    break;
                try {
                    Expr evaled = eval(env, line);
                    terminal.puts(Capability.orig_pair);
                    terminal.writer().println(evaled);
                    terminal.flush();
                } catch (DecLispException x) {
                    // terminal.puts(Capability.set_a_foreground, 1);   // IOErrorになる。
                    terminal.writer().print(ERROR_COLOR);
                    terminal.writer().println(x.getMessage());
                    terminal.writer().print(ERROR_COLOR_END);
                    terminal.flush();
                }
            } catch (UserInterruptException uie) {
                System.out.println("Ctrl-C entered");
            }
        }
        // terminal.writer().println("Goodbye!");
        terminal.close();

    } catch (EndOfFileException e) {
        System.out.println("EOF entered");
    } catch (IOException e) {
        System.err.println("Error creating terminal: " + e.getMessage());
    }
}
}
