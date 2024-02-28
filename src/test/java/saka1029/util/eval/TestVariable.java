package saka1029.util.eval;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class TestVariable {
    static final double DELTA = 5e-6;

    @Test
    public void testVariable() {
        Context c = Context.of();
        Variable v = Variable.of("abc");
        c.variable("abc", Number.of(123));
        assertEquals(123.0, v.eval(c), DELTA);
    }
    
}
