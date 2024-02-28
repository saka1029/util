package saka1029.util.eval;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

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

    static boolean isDigit(int ch) {
        return ch >= '0' && ch <= '9';
    }

    static boolean isOperatorChar(int ch) {
        return switch (ch) {
            case '!', '$', '%', '&', '-', '=', '^', '~' -> true;
            case '|', '@', '+', '*', '<', '>', '/', '?', '.' -> true;
            default -> false;
        };
    }

    static boolean isIdFirstChar(int ch) {
        return Character.isAlphabetic(ch) || ch == '_';
    }

    static boolean isIdRestChar(int ch) {
        return isIdFirstChar(ch) || isDigit(ch);
    }

    int ch() {
        try {
            return ch = reader.read();
        } catch (IOException e) {
            throw new EvalException(e);
        }
    }

    // 本来の浮動小数点パターン
    // static final Pattern NUMBER = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?");
    static final Pattern NUMBER = Pattern.compile("[-+]?[0-9]+(\\.[0-9]+)?([eE][-+]?[0-9]+)?");

    static boolean isNumberChar(int ch) {
        return switch (ch) {
            case '+', '-', '.', 'e', 'E' -> true;
            default -> isDigit(ch);
        };
    }

    StringBuilder sb = new StringBuilder();

    void sbClear() {
        sb.setLength(0);
    }

    void sbAppend(int ch) {
        sb.append((char)ch);
        ch();
    }

    void sbAppendDigit() {
        do {
            sbAppend(ch);
        } while (isDigit(ch));
    }

    String num() {
        sbAppendDigit();
        if (ch == '.') {
            sbAppend(ch); // '.'
            if (!isDigit(ch))
                throw new EvalException("Illegal number: '%s%c'", sb, ch);
            sbAppendDigit();
        }
        if (ch == 'e' || ch == 'E') {
            sbAppend(ch); // 'e' or 'E'
            if (ch == '+' || ch == '-')
                sbAppend(ch); // '+' or '-'
            if (!isDigit(ch))
                throw new EvalException("Illegal number format: '%s%c'", sb, ch);
            sbAppendDigit();
        }
        return sb.toString();
    }

    String op() {
        do {
            sbAppend(ch);
        } while (isOperatorChar(ch));
        return sb.toString();
    }

    String id() {
        do {
            sbAppend(ch);
        } while (isIdRestChar(ch));
        return token = sb.toString();
    }

    String token() {
        while (Character.isWhitespace(ch))
            ch();
        return token = switch (ch) {
            case -1 -> null;
            case '(', ')', ',' -> {
                int c = ch;
                ch();
                yield Character.toString(c);
            }
            case '+', '-' -> {
                sbClear();
                sbAppend(ch);
                yield isDigit(ch) ? num() : op();
            }
            default -> {
                sbClear();
                if (isDigit(ch))
                    yield num();
                else if (isIdFirstChar(ch)) 
                    yield id();
                else if (isOperatorChar(ch))
                    yield op();
                else
                    throw new EvalException("Unknown char '%c'", (char) ch);
            }
        };
    }

    public List<String> tokens() {
        List<String> list = new ArrayList<>();
        while (token != null) {
            list.add(token);
            token();
        }
        return list;
    }

    public List<Expression> read() {
        List<Expression> list = new ArrayList<>();
        return list;
    }

}
