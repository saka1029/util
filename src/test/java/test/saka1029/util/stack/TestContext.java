package test.saka1029.util.stack;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;
import org.junit.Test;
import saka1029.util.stack.*;
import static saka1029.util.stack.InstructionSet.*;

public class TestContext {

    static Value eval(Instruction... instructions) {
        Context context = new Context();
        context.addCode(instructions);
        context.start();
        if (context.stackSize() != 1)
            throw new RuntimeException("Stack size is not 1: " + context.stackSize());
        return context.pop();
    }  

    @Test
    public void testStart() {
        Value result = eval(load(1), load(2), ADD, HALT);
        assertEquals(Int.of(3), result);
    }

    @Test
    public void testCast() {
        try {
            eval(load(true), load(false), ADD, HALT);
            fail();
        } catch (RuntimeException e) {
            assertEquals("Cannot cast Bool to Int", e.getMessage());
        }
    }

    @Test
    public void testEq() {
        Value result = eval(load(3), load(3), EQ, HALT);
        assertEquals(Bool.TRUE, result);
    }

    @Test
    public void testNe() {
        Value result = eval(load(false), load(3), NE, HALT);
        assertEquals(Bool.TRUE, result);
        Value result2= eval(load(false), load(false), NE, HALT);
        assertEquals(Bool.FALSE, result2);
    }

    @Test
    public void testAddIfTrue() {
        Context context = new Context();
        context.addIf(List.of(load(true)), List.of(load(1)), List.of(load(2)));
        context.addCode(HALT);
        context.start();
        if (context.stackSize() != 1)
            throw new RuntimeException("Stack size is not 1: " + context.stackSize());
        assertEquals(Int.of(1), context.pop());
    }

    @Test
    public void testAddIfFalse() {
        Context context = new Context();
        context.addIf(List.of(load(false)), List.of(load(1)), List.of(load(2)));
        context.addCode(HALT);
        System.out.println(context.codes);
        context.start();
        if (context.stackSize() != 1)
            throw new RuntimeException("Stack size is not 1: " + context.stackSize());
        assertEquals(Int.of(2), context.pop());
    }
}
