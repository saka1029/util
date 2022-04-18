package util.scanner;

public class TokenAnyChar implements Tokenizer {

    @Override
    public Token tokenize(CharSeq g) {
        return new Token(g.index(), g.peek(),
            Character.toString((char)g.peekNext()));
    }

}
