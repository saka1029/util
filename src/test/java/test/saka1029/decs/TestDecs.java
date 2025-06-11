package test.saka1029.decs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.function.BinaryOperator;

import org.junit.Test;

import saka1029.util.decs.Decs;
import saka1029.util.decs.DecsException;

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
        assertEquals("Empty", decs().toString());
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

    @Test
    public void testMult() {
        assertEquals(decs(d(1)), decs().mult());
        assertEquals(decs(d(1)), decs(d(1)).mult());
        assertEquals(decs(d(3)), decs(d(1), d(3)).mult());
        assertEquals(decs(d(15)), decs(d(1), d(3), d(5)).mult());
    }

    @Test
    public void testDivide() {
        assertEquals(decs(d(1)), decs().divide());
        assertEquals(decs(d("0.5")), decs(d(2)).divide());
        assertEquals(decs(d("0.25")), decs(d(1), d(4)).divide());
        assertEquals(decs(d("0.1")), decs(d(1), d(2), d(5)).divide());
    }

    @Test
    public void testZip() {
        BinaryOperator<BigDecimal> op = (a, b) -> a.add(b);
        assertEquals(decs(), decs().zip(op, decs()));
        assertEquals(decs(d(1)), decs().zip(op, decs(d(1))));
        assertEquals(decs(d(1), d(2)), decs().zip(op, decs(d(1), d(2))));
        assertEquals(decs(d(10)), decs(d(10)).zip(op, decs()));
        assertEquals(decs(d(11)), decs(d(10)).zip(op, decs(d(1))));
        assertEquals(decs(d(11), d(12)), decs(d(10)).zip(op, decs(d(1), d(2))));
        assertEquals(decs(d(10), d(20)), decs(d(10), d(20)).zip(op, decs()));
        assertEquals(decs(d(11), d(21)), decs(d(10), d(20)).zip(op, decs(d(1))));
        assertEquals(decs(d(11), d(22)), decs(d(10), d(20)).zip(op, decs(d(1), d(2))));
        try {
            decs(d(10), d(20), d(30)).zip(op, decs(d(1), d(2)));
            fail();
        } catch (DecsException e) {
            assertEquals("zip: Invalid size l=(10, 20, 30) r=(1, 2)", e.getMessage());
        }
    }

}
