package saka1029.util.declisp;

public class Help {

    public final VT type;
    public final Symbol name;
    public final Expr args;
    public final String text;

    public Help(VT type, Symbol name, Expr args, String text) {
        this.type = type;
        this.name = name;
        this.args = args;
        this.text = text;
    }

    @Override
    public String toString() {
        if (type == VT.var)
            return "%s %s".formatted(type, name);
        else
            return "%s %s : %s".formatted(type, new Cons(name, args).toString(), text);
    }

}
