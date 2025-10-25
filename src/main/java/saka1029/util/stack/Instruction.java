package saka1029.util.stack;

public interface Instruction {

    void execute(Context context);

    public static Instruction of(int value) {
        return Int.of(value);
    }

    public static Instruction of(boolean value) {
        return Bool.of(value);
    }

    public static int i(Value value) { return ((Int) value).value; }
    public static boolean b(Value value) { return ((Bool) value).value; }
    public static Int value(int value) { return Int.of(value); }
    public static Bool value(boolean value) { return Bool.of(value); }

    public static final Instruction ADD = c -> c.push(value(i(c.pop()) + i(c.pop())));

}
