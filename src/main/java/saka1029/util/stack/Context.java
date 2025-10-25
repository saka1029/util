package saka1029.util.stack;

import java.util.LinkedList;

public class Context {

    LinkedList<Value> stack = new LinkedList<>();
    public int pc = 0;
    LinkedList<Instruction> codes = new LinkedList<>();

    public int stackSize() {
        return stack.size();
    }

    public void push(Value value) {
        stack.addLast(value);
    }

    public Value pop() {
        return stack.removeLast();
    }

    public void addCode(Instruction... instructions) {
        for (Instruction inst : instructions)
            this.codes.addLast(inst);
    }

    public void setCode(int index, Instruction instruction) {
        this.codes.set(index, instruction);
    }

    public boolean step() {
        if (pc >= codes.size())
            return false;
        Instruction fetch = codes.get(pc++);
        if (fetch == InstructionSet.HALT)
            return false;
        fetch.execute(this);
        return true;
    }

    public void start() {
        pc = 0;
        while (step())
            ;
    }   
}
