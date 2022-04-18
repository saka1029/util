package util.scanner;

import java.util.ArrayList;
import java.util.List;

public class Scanner {
    
    public static final int MIN_TYPE = 65536;
    public static final Token EOF = new Token(Integer.MAX_VALUE, -1, "");
    
    private final List<Tokenizer> skips = new ArrayList<>();
    private final List<Tokenizer> tokens = new ArrayList<>();
    private CharSeq getter;
    private Token token;

    public Scanner addSkip(Tokenizer tokenizer) {
        skips.add(tokenizer);
        return this;
    }

    public Scanner addToken(Tokenizer tokenizer) {
        tokens.add(tokenizer);
        return this;
    }
    
    public Scanner source(String source) {
        getter = new CharSeq(source);
        return this;
    }
    
    private void skip() {
        while (true) {
            boolean match = false;
            for (Tokenizer e : skips) {
                Token t = e.tokenize(getter);
                if (t != null) {
                    match = true;
                }
            }
            if (!match)
                break;
        }
    }

    public Scanner next() {
        if (token == EOF)
            return this;
        if (getter.isEof()) {
            token = EOF;
            return this;
        }
        skip();
        for (Tokenizer e : tokens) {
            token = e.tokenize(getter);
            if (token != null)
                return this;
        }
        throw new RuntimeException("no token match");
    }
    
    public int type() { return token.type; }
    public String value() { return token.value; }
    public String valueAndNext() { String r = value(); next(); return r; }

}
