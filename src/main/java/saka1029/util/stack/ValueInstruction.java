package saka1029.util.stack;

public interface ValueInstruction extends Instruction, Value {

    default void execute(Context context) {
        context.push(this);
    }

}
