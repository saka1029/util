package saka1029.util.vec;

public class Scanner {

    public class Token {
        public final int type;
        public final String string;
        public final double number;
        Token(int type, String string, double number) {
            this.type = type;
            this.string = string;
            this.number = number;
        }
        public Token(int type) {
            this(type, null, Double.NaN);
        }
        public Token(String value) {
            this('i', value, Double.NaN);
        }
        public Token(double value) {
            this('n', null, value);
        }
    }

    final String input;
    int index = 0;
    int ch = get();

    Scanner(String input) {
        this.input = input;
    }

    public static Scanner of(String input) {
        return new Scanner(input);
    }

    int get() {
        return ch = index < input.length() ? input.charAt(index++) : -1;
    }

    void spaces() {
        while (Character.isWhitespace(ch))
            get();
    }

    static boolean isDigit(int ch) {
        return ch >= '0' && ch <= '9';
    }

    static boolean isIdFirst(int ch) {
        return ch >= 'A' && ch <= 'Z'
            || ch >= 'a' && ch <= 'z'
            || ch == '_'
            || ch >= 256;
    }

    static boolean isIdRest(int ch) {
        return isIdFirst(ch) || ch >= '0' && ch <= '9';
    }

    StringBuilder sb = new StringBuilder();
    
    void clear() {
        sb.setLength(0);
    }

    void append(int ch) {
        sb.append((char)ch);
    }
    void appendGet() {
        append(ch);
        get();
    }

    void digits() {
        if (!isDigit(ch))
            throw new RuntimeException("Digit expected but 0x%02X".formatted(ch));
        do {
            appendGet();
        } while (isDigit(ch));
    }

    Token number(int prefix) {
        clear();
        if (prefix > 0)
            append(prefix);
        digits();
        if (ch == '.') {
            appendGet();
            digits();
        }
        if (ch == 'e' || ch == 'E') {
            appendGet();
            if (ch == '+' || ch == '-')
                appendGet();
            digits();
        }
        return new Token(Double.parseDouble(sb.toString()));
    }

    Token id() {
        clear();
        do {
            appendGet();
        } while (isIdRest(ch));
        return new Token(sb.toString());
    }

    public Token read() {
        spaces();
        switch (ch) {
            case -1: 
            case '(': 
            case ')': 
            case '=': 
            case '+': 
            case '*':
            case '/':
            case '%':
            case '^':
                int op = ch;
                get();
                return new Token(op);
            case '-':
                get();
                return isDigit(ch) ? number('-') : new Token('-');
        }
        if (isDigit(ch))
            return number(-1);
        else if (isIdFirst(ch))
            return id();
        else
            throw new RuntimeException("Unknown char 0x%02X".formatted(ch));
    }
}
