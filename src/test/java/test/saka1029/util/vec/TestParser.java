package test.saka1029.util.vec;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import saka1029.util.vec.Context;
import saka1029.util.vec.Parser;
import saka1029.util.vec.Vec;

public class TestParser {

    static Vec vec(double... elements) {
        return Vec.of(elements);
    }

    static Vec eval(Context c, String input) {
        return Parser.of(input).statement().eval(c);
    }

    @Test
    public void testVec() {
        Context c = Context.of();
        assertEquals(vec(1, 2), eval(c, "1 2"));
    }

    @Test
    public void testPlus() {
        Context c = Context.of();
        assertEquals(vec(3), eval(c, " 1 + 2"));
        assertEquals(vec(2, 3, 4), eval(c, " 1 + 1 2 3"));
        assertEquals(vec(2, 3, 4), eval(c, " 1 2 3 + 1"));
    }

}
