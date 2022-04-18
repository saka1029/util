package util.scanner;

public class Token {

    public final int index;
    public final int type;
    public final String value;
    
    public Token(int index, int type, String value) {
        this.index = index;
        this.type = type;
        this.value = value;
    }
    
    @Override
    public String toString() {
        return String.format(
            "Token(index=%s,type=%s,value=%s)",
            index, type, value);
    }
}
