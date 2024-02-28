package saka1029.util.eval;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class Parser {
    final Reader reader;
    int ch;
    String token;

    Parser(Reader reader) {
        this.reader = reader;
        ch();
        token();
    }

    public static Parser of(Reader reader) {
        return new Parser(reader);
    }

    public static Parser of(String source) {
        return new Parser(new StringReader(source));
    }

    static boolean isOperatorChar(int ch) {
        return switch (ch) {
            case '!', '$', '%', '&', '-', '=', '^', '~' -> true;
            case '|', '@', '+', '*', '<', '>', '/', '?' -> true;
            default -> false;
        };
    }

    static boolean isIdFirstChar(int ch) {
        return Character.isAlphabetic(ch) || ch == '_';
    }

    static boolean isIdRestChar(int ch) {
        return isIdFirstChar(ch) || ch >= '0' && ch <= '9';
    }

    int ch() {
        try {
            return ch = reader.read();
        } catch (IOException e) {
            throw new EvalException(e);
        }

    }

    String token() {
        while (Character.isWhitespace(ch))
            ch();
        switch (ch) {
            case -1:
                return token = null;
            case '(':
                ch();
                return token = "(";
            case ')':
                ch();
                return token = ")";
            case ',':
                ch();
                return token = ",";
            default:
                StringBuilder sb = new StringBuilder();
                if (isIdFirstChar(ch)) {
                    do {
                        sb.append((char) ch);
                        ch();
                    } while (isIdRestChar(ch));
                    return token = sb.toString();
                } else if (isOperatorChar(ch)) {
                    do {
                        sb.append((char) ch);
                        ch();
                    } while (isOperatorChar(ch));
                    return token = sb.toString();
                } else
                    throw new EvalException("Unknown char '%c'", (char) ch);
        }
    }

    public List<Expression> read() {
        List<Expression> list = new ArrayList<>();
        return list;
    }

}
