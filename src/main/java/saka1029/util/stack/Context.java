package saka1029.util.stack;

import java.util.Iterator;
import java.util.LinkedList;

public class Context {

    LinkedList<Executable> stack = new LinkedList<>();
    LinkedList<Iterator<Executable>> instructions = new LinkedList<>();

    public int stackSize() {
        return stack.size();
    }

    public void push(Executable executable) {
        stack.addLast(executable);
    }

    public Executable pop() {
        return stack.removeLast();
    }

    public void run(Iterable<Executable> instructions) {
        this.instructions.addLast(instructions.iterator());
    }

    public void start() {
        while (!instructions.isEmpty()) {
            Iterator<Executable> it = instructions.getLast();
            if (it.hasNext())
                it.next().execute(this);
            else
                instructions.removeLast();
        }
    }   
}
