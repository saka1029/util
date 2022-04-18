package util.scanner;

public class TokenNumber implements Tokenizer {

    public final int type;
    
    public TokenNumber(int type) {
        this.type = type;
    }

    public boolean isDigit(int ch) {
        return Character.isDigit(ch);
    }

    @Override
    public Token tokenize(CharSeq g) {
        if (!isDigit(g.peek()) && (g.peek() != '-' || !isDigit(g.peek(1))))
            return null;
        int index = g.index();
        StringBuilder sb = new StringBuilder();
        sb.append((char)g.peekNext());  // '-' or DIGIT
        while (isDigit(g.peek()))
            sb.append((char)g.peekNext());
        if (g.peek() == '.') {
            sb.append((char)g.peekNext());
            while (isDigit(g.peek()))
                sb.append((char)g.peekNext());
        }
        if (Character.toLowerCase(g.peek()) == 'e'
            && (g.peek(1) == '+' || g.peek(1) == '-' || isDigit(g.peek(1)))) {
            sb.append((char)g.peekNext());  // 'e' or 'E'
            sb.append((char)g.peekNext());  // '+' or '-' or DIGIT
            while (isDigit(g.peek()))
                sb.append((char)g.peekNext());
        }
        return new Token(index, type, sb.toString());
    }

}
