package saka1029.util.vec;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import vec.Vec;

public class TestVec {

    @Test
    public void testAppend() {
        assertEquals(Vec.of(1, 2, 3, 4), Vec.of(1, 2).append(Vec.of(3, 4)));
    }

}
