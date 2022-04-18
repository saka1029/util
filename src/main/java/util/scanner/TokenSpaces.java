package util.scanner;

public class TokenSpaces implements Tokenizer {
    
    public final int type;
    
    public TokenSpaces(int type) {
        this.type = type;
    }
    
    public static boolean isWhitespace(int ch) {
        return Character.isWhitespace(ch);
    }

    @Override
    public Token tokenize(CharSeq g) {
        if (!isWhitespace(g.peek()))
            return null;
        int i = g.index();
        StringBuilder sb = new StringBuilder();
        while (!g.isEof() && isWhitespace(g.peek())) {
            sb.append((char)g.peek());
            g.next();
        }
        return new Token(i, type, sb.toString());
    }

}