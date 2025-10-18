package saka1029.util.stack;

public class Instruction {

    public static Executable ADD = c -> c.push(Int.of(((Int) c.pop()).value + ((Int) c.pop()).value));

}
