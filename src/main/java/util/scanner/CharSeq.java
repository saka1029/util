package util.scanner;

public class CharSeq {
    
    public static final int EOF = -1;
    public final String string;
    private int index;
    
    public CharSeq(String string) {
        this.string = string;
        this.index = 0;
    }
    
    public int index() {
        return index;
    }

    public boolean isEof() {
        return index >= string.length();
    }
    
    public int peek(int ahead) {
        if (index + ahead >= string.length())
            return EOF;
        return string.charAt(index + ahead);
    }

    public int peek() {
        if (isEof()) return EOF;
        return string.charAt(index);
    }
    
    public int peekNext() {
        int r = peek();
        next();
        return r;
    }
    
    public boolean startsWith(String word) {
        if (isEof()) return false;
        return string.startsWith(word, index);
    }
    
    public void advance(int size) {
        index += size;
    }

    public int next() {
        if (isEof()) return EOF;
        return string.charAt(index++);
    }
    
    @Override
    public String toString() {
        return String.format("CharGetter(ch=%s)",
            peek() == EOF ? "EOF" : Character.toString((char)peek()));
    }
}
