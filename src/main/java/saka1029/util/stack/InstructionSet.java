package saka1029.util.stack;

public class InstructionSet {

    public static Instruction load(int value) { return Int.of(value); }
    public static Instruction load(boolean value) { return Bool.of(value); }
    public static Instruction branch(int pc) {
        return new Instruction() {
            @Override public void execute(Context c) { c.pc += pc; }
            @Override public String toString() { return "b %d".formatted(pc); }
        };
    }
    public static Instruction branchFalse(int pc) {
        return new Instruction() { @Override public void execute(Context c) { if (!b(c.pop())) c.pc += pc; }
            @Override public String toString() { return "bf %d".formatted(pc); }
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

    public static final Instruction NOP = new Instruction() {
        @Override public void execute(Context c) {  }
        @Override public String toString() { return "nop"; }
    };
    public static final Instruction HALT = new Instruction() {
        @Override public void execute(Context c) {  }
        @Override public String toString() { return "halt"; }
    };
    public static final Instruction ADD = new Instruction() {
        @Override public void execute(Context c) {
            c.push(value(i(c.pop()) + i(c.pop())));
        }
        @Override public String toString() { return "add"; }
    };
    public static final Instruction SUB = new Instruction() {
        @Override public void execute(Context c) {
            c.push(value(-i(c.pop()) + i(c.pop())));
        }
        @Override public String toString() { return "sub"; }
    };
    public static final Instruction MULT = new Instruction() {
        @Override public void execute(Context c) {
            c.push(value(i(c.pop()) * i(c.pop())));
        }
        @Override public String toString() { return "mult"; }
    };
    public static final Instruction DIV = c -> new Instruction() {
        @Override public void execute(Context c) {
            Value r = c.pop();
            c.push(value(i(c.pop()) / i(r)));
        }
        @Override public String toString() { return "div"; }
    };  
    public static final Instruction MOD = new Instruction() {
        @Override public void execute(Context c) {
            Value r = c.pop();
            c.push(value(i(c.pop()) % i(r)));
        }
        @Override public String toString() { return "mod"; }
    };
    public static final Instruction EQ = new Instruction() {
        @Override public void execute(Context c) {
            c.push(value(c.pop().equals(c.pop())));
        }
        @Override public String toString() { return "eq"; }
    };
    public static final Instruction NE = new Instruction() {
        @Override public void execute(Context c) {
            c.push(value(!c.pop().equals(c.pop())));
        }
        @Override public String toString() { return "ne"; }
    };

}
