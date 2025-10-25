package test.saka1029.util.stack;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import saka1029.util.stack.*;

public class TestContext {

    @Test
    public void testStart() {
        Context context = new Context();
        context.addCode(
            InstructionSet.load(1),
            InstructionSet.load(2),
            InstructionSet.ADD);
        context.start();
        assertEquals(1, context.stackSize());
        Value result = context.pop();
        assertEquals(Int.of(3), result);
    }

    @Test
    public void testCast () {
        Context context = new Context();
        context.addCode(
            InstructionSet.load(true),
            InstructionSet.load(false),
            InstructionSet.ADD);
        try {
            context.start();
            fail();
        } catch (RuntimeException e) {
            assertEquals("Cannot cast Bool to Int", e.getMessage());
        }
    }
}
  