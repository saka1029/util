package saka1029.util.stack;

public class InstructionSet {

    public static Instruction load(int value) { return Int.of(value); }
    public static Instruction load(boolean value) { return Bool.of(value); }
    public static Instruction branch(int pc) { return c -> c.pc = pc; }
    public static Instruction branchFalse(int pc) {
        return c -> {
            if (!b(c.pop()))
                c.pc = pc;
        };
    }
    public static <T extends Value> T cast(Value value, Class<T> clazz) {
        try {
            return clazz.cast(value);
        } catch (ClassCastException e) {
            throw new RuntimeException("Cannot cast %s to %s".formatted(
                value.getClass().getSimpleName(), clazz.getSimpleName()));  
        }
    }

    public static int i(Value value) { return cast(value, Int.class).value; }
    public static boolean b(Value value) { return cast(value, Bool.class).value; }
    public static Int value(int value) { return Int.of(value); }
    public static Bool value(boolean value) { return Bool.of(value); }

    public static final Instruction NOP = c -> {};
    public static final Instruction HALT = c -> {};
    public static final Instruction ADD = c -> c.push(value(i(c.pop()) + i(c.pop())));
    public static final Instruction SUB = c -> c.push(value(-i(c.pop()) + i(c.pop())));
    public static final Instruction MULT = c -> c.push(value(i(c.pop()) * i(c.pop())));
    public static final Instruction DIV = c -> { Value r = c.pop(); c.push(value(i(c.pop()) / i(r))); };
    public static final Instruction MOD = c -> { Value r = c.pop(); c.push(value(i(c.pop()) % i(r))); };
    public static final Instruction EQ = c -> c.push(value(c.pop().equals(c.pop())));
    public static final Instruction NE = c -> c.push(value(!c.pop().equals(c.pop())));

}
