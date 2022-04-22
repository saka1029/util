package saka1029.util.language.stack.core;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import saka1029.util.language.stack.values.Value;

public class Context {

    public int sp = 0;
    public final Value[] stack;
    public Map<String, Value> globals = new HashMap<>();

    public Context(int stackSize) {
        this.stack = new Value[stackSize];
    }

    public Value top() {
        return stack[sp - 1];
    }

    public void push(Value value) {
        stack[sp++] = value;
    }

    public Value pop() {
        return stack[--sp];
    }

    public static Value code(String name, Executable e) {
        return new Value() {

            @Override
            public void execute(Context c) {
                e.execute(c);
            }

            @Override
            public String toString() {
                return name;
            }
        };
    }

    public void add(String name, Executable e) {
        globals.put(name, code(name, e));
    }

    @Override
    public String toString() {
        return Arrays.stream(Arrays.copyOf(stack, sp))
            .map(i -> i.toString())
            .collect(Collectors.joining(" ", "[", "]"));
    }

}
