package util.scanner;

import java.util.Arrays;

public class TokenKeywords implements Tokenizer {
    
    public final int type;
    public final String[] words;
    
    public TokenKeywords(int type, String... words) {
        this.type = type;
        this.words = Arrays.copyOf(words, words.length);
    }

    @Override
    public Token tokenize(CharSeq g) {
        for (int i = 0, size = words.length; i < size; ++i) {
            String s = words[i];
            if (!g.startsWith(s)) continue;
            Token t = new Token(g.index(), type + i, s);
            g.advance(s.length());
            return t;
        }
        return null;
    }

}
