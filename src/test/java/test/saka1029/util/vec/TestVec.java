package test.saka1029.util.vec;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import saka1029.util.vec.Vec;

public class TestVec {

    static Vec vec(double... elements) {
        return Vec.of(elements);
    }

    @Test
    public void testAppend() {
        assertEquals(vec(1, 2, 3, 4), vec(1, 2).append(vec(3, 4)));
    }

    @Test
    public void testUnaryOperator() {
        assertEquals(vec(2, 3, 4), Vec.calculate(x -> x + 1, vec(1, 2, 3)));
    }

    @Test
    public void testBinaryOperator() {
        assertEquals(vec(3, 5, 7), Vec.calculate((x, y) -> x + y, vec(1, 2, 3), vec(2, 3, 4)));
        assertEquals(vec(2, 3, 4), Vec.calculate((x, y) -> x + y, vec(1, 2, 3), vec(1)));
        assertEquals(vec(2, 3, 4), Vec.calculate((x, y) -> x + y, vec(1), vec(1, 2, 3)));
    }

    @Test
    public void testInsert() {
        assertEquals(vec(1), Vec.insert((x, y) -> x + y, vec(1)));
        assertEquals(vec(3), Vec.insert((x, y) -> x + y, vec(1, 2)));
        assertEquals(vec(6), Vec.insert((x, y) -> x + y, vec(1, 2, 3)));
    }

}
