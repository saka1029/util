package saka1029.util.stack;

public class Instruction {

    public static int i(Executable executable) { return ((Int) executable).value; }
    public static Executable i(int value) { return Int.of(value); }
    public static boolean b(Executable executable) { return ((Bool) executable).value; }
    public static Executable b(boolean value) { return Bool.of(value); }

    public static Executable ADD = c -> c.push(Int.of(i(c.pop()) + i(c.pop())));
    public static Executable MINUS = c -> c.push(Int.of(-i(c.pop()) + i(c.pop())));
    public static Executable MULT = c -> c.push(Int.of(i(c.pop()) * i(c.pop())));
    public static Executable of(int value) { return i(value); }
    public static Executable of(boolean value) { return b(value); }

}
