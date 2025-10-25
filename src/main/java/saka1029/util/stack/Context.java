package saka1029.util.stack;

import java.util.LinkedList;

public class Context {

    LinkedList<Value> stack = new LinkedList<>();
    LinkedList<Instruction> instructions = new LinkedList<>();

    public int stackSize() {
        return stack.size();
    }

    public void push(Value value) {
        stack.addLast(value);
    }

    public Value pop() {
        return stack.removeLast();
    }

    public void run(Instruction... instructions) {
        for (Instruction inst : instructions)
            this.instructions.addLast(inst);
    }

    public void start() {
        for (Instruction inst : instructions)
            inst.execute(this);
    }   
}
