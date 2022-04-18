package util.scanner;

public class TokenString implements Tokenizer {

    public final int type;
    public final char quote;
    
    public TokenString(int type, char quote) {
        this.type = type;
        this.quote = quote;
    }
    
    public TokenString(int type) {
        this(type, '\"');
    }

    @Override
    public Token tokenize(CharSeq g) {
        if (g.peek() != quote) return null;
        int index = g.index();
        StringBuilder sb = new StringBuilder();
        sb.append((char)g.peekNext());
        while (!g.isEof() && g.peek() != quote) {
            switch (g.peek()) {
            case '\\':
                sb.append((char)g.peekNext()).append((char)g.peekNext());
                break;
            default:
                sb.append((char)g.peekNext());
                break;
            }
        }
        if (g.peek() == quote)
            sb.append((char)g.peekNext());
        return new Token(index, type, sb.toString());
    }

}
