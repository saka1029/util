package saka1029.util.stack;

import java.util.LinkedList;
import java.util.List;

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

    public void addCode(List<Instruction> instructions) {
        for (Instruction inst : instructions)
            this.codes.addLast(inst);
    }

    public void setCode(int index, Instruction instruction) {
        this.codes.set(index, instruction);
    }

    public void addIf(List<Instruction> cond,
            List<Instruction> then, List<Instruction> orElse) {
        addCode(cond);
        int afterCond = codes.size();
        addCode(InstructionSet.NOP);       
        addCode(then);
        int afterThen = codes.size();
        addCode(InstructionSet.NOP);       
        addCode(orElse);
        int next = codes.size();
        setCode(afterCond, InstructionSet.branchFalse(afterThen + 1 - (afterCond + 1)));
        setCode(afterThen, InstructionSet.branch(next - (afterThen + 1)));
    }

    public boolean step() {
        if (pc >= codes.size())
            throw new RuntimeException("Memory violation memory size=%d, pc=%d"
                .formatted(codes.size(), pc));
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
