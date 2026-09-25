package saka1029.util.declisp;

public class Help {

    public final VT type;
    public final String name, args, text;

    public Help(VT type, String name, String args, String text) {
        this.type = type;
        this.name = name;
        this.args = args;
        this.text = text;
    }

    @Override
    public String toString() {
        return "%s (%s %s) : %s".formatted(type, name, args, text);
    }

}
