package test.saka1029.util.stack;

import static org.junit.Assert.assertEquals;
import java.util.List;
import org.junit.Test;
import saka1029.util.stack.*;

public class TestContext {

    @Test
    public void testContextStart () {
        Context context = new Context();
        List<Executable> instructions = List.of(
            Instruction.of(1),
            Instruction.of(2),
            Instruction.ADD
        );
        context.run(instructions);
        context.start();
        assertEquals(1, context.stackSize());
        Executable result = context.pop();
        assertEquals(Int.of(3), result);
    }
}
  