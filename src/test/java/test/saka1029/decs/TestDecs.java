package test.saka1029.decs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.math.BigDecimal;

import org.junit.Test;

import saka1029.util.decs.Decs;

public class TestDecs {

    static BigDecimal d(String s) { return new BigDecimal(s); }
    static BigDecimal d(int s) { return new BigDecimal(s); }

    static Decs decs(BigDecimal... elements) {
        return Decs.of(elements);
    }

    @Test
    public void testEquals() {
        assertEquals(decs(), decs());
        assertEquals(decs(d(1)), decs(d(1)));
        assertEquals(decs(d(1), d(2)), decs(d(1), d(2)));
        assertNotEquals(decs(d(1), d(2)), decs(d(1)));
        assertNotEquals(decs(d(1), d(2)), "1, 2");
    }

    @Test
    public void testToString() {
        assertEquals("", decs().toString());
        assertEquals("1", decs(d(1)).toString());
        assertEquals("1, 3", decs(d(1), d(3)).toString());
        assertEquals("1, 3, 5", decs(d(1), d(3), d(5)).toString());
    }

    @Test
    public void testSum() {
        assertEquals(decs(d(0)), decs().sum());
        assertEquals(decs(d(1)), decs(d(1)).sum());
        assertEquals(decs(d(4)), decs(d(1), d(3)).sum());
        assertEquals(decs(d(9)), decs(d(1), d(3), d(5)).sum());
    }

    @Test
    public void testSubtract() {
        assertEquals(decs(d(0)), decs().subtract());
        assertEquals(decs(d(-1)), decs(d(1)).subtract());
        assertEquals(decs(d(-2)), decs(d(1), d(3)).subtract());
        assertEquals(decs(d(-7)), decs(d(1), d(3), d(5)).subtract());
    }

    @Test
    public void testNegate() {
        assertEquals(decs(), decs().negate());
        assertEquals(decs(d(-1)), decs(d(1)).negate());
        assertEquals(decs(d(-1), d(-3)), decs(d(1), d(3)).negate());
        assertEquals(decs(d(-1), d(-3), d(-5)), decs(d(1), d(3), d(5)).negate());
    }

}
