package test.saka1029.util.stack;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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
        Value result = eval(load(1), load(2), ADD);
        assertEquals(Int.of(3), result);
    }

    @Test
    public void testCast() {
        try {
            eval(load(true), load(false), ADD);
            fail();
        } catch (RuntimeException e) {
            assertEquals("Cannot cast Bool to Int", e.getMessage());
        }
    }

    @Test
    public void testEq() {
        Value result = eval(load(3), load(3), EQ);
        assertEquals(Bool.TRUE, result);
    }

    @Test
    public void testNe() {
        Value result = eval(load(false), load(3), NE);
        assertEquals(Bool.TRUE, result);
        Value result2= eval(load(false), load(false), NE);
        assertEquals(Bool.FALSE, result2);
    }
}
  