package saka1029.util.dentaku;

import java.util.ArrayList;
import java.util.List;

public class Lexer {

    public enum Type {
        END, ID, NUMBER, OTHER;
    }

    public record Token(Type type, String string) {
    }

    public static final Token END = new Token(Type.END, "");

    final int[] input;
    int ch, index, start;

    Lexer(int[] input) {
        this.input = input;
        this.index = 0;
        get();
    }

    public static List<Token> tokens(String input) {
        Lexer lexer = new Lexer(input.codePoints().toArray());
        List<Token> list = new ArrayList<>();
        Token t;
        while ((t = lexer.token()).type != Type.END)
            list.add(t);
        return list;
    }

    int get() {
        return ch = index < input.length ? input[index++] : -1;
    }

    Type getReturn(Type type) {
        get();
        return type;
    }

    Token token() {
        while (Character.isWhitespace(ch))
            get();
        int start = index - 1;
        Type type = switch (ch) {
            case -1 -> Type.END;
            case '+', '-', '*', '/', '%', '^' -> getReturn(Type.OTHER);
            default -> throw new ValueException("Unknown char 0x%04x", ch);
        };
        int end = index - (index < input.length ? 1 : 0);
        return new Token(type, new String(input, start, end - start));
    }
    
}
