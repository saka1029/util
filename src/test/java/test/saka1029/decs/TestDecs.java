package test.saka1029.decs;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.function.BinaryOperator;

import org.junit.Test;

import static saka1029.util.decs.Decs.*;
import saka1029.util.decs.DecsException;

public class TestDecs {

    static BigDecimal d(String s) { return new BigDecimal(s); }
    static BigDecimal d(int s) { return new BigDecimal(s); }

    @Test
    public void testEquals() {
        assertArrayEquals(decs(), decs());
        assertArrayEquals(decs(d(1)), decs(d(1)));
        assertArrayEquals(decs(d(1), d(2)), decs(d(1), d(2)));
        assertNotEquals(decs(d(1), d(2)), decs(d(1)));
        assertNotEquals(decs(d(1), d(2)), "1, 2");
    }

    @Test
    public void testToString() {
        assertEquals("()", string(decs()));
        assertEquals("(1)", string(decs(d(1))));
        assertEquals("(1, 3)", string(decs(d(1), d(3))));
        assertEquals("(1, 3, 5)", string(decs(d(1), d(3), d(5))));
    }

    @Test
    public void testSumUnary() {
        assertArrayEquals(decs(d(0)), sum(decs()));
        assertArrayEquals(decs(d(1)), sum(decs(d(1))));
        assertArrayEquals(decs(d(4)), sum(decs(d(1), d(3))));
        assertArrayEquals(decs(d(9)), sum(decs(d(1), d(3), d(5))));
    }

    @Test
    public void testSubtractUnary() {
        assertArrayEquals(decs(d(0)), subtract(decs()));
        assertArrayEquals(decs(d(-1)), subtract(decs(d(1))));
        assertArrayEquals(decs(d(-2)), subtract(decs(d(1), d(3))));
        assertArrayEquals(decs(d(-7)), subtract(decs(d(1), d(3), d(5))));
    }

    @Test
    public void testNegateUnary() {
        assertArrayEquals(decs(), negate(decs()));
        assertArrayEquals(decs(d(-1)), negate(decs(d(1))));
        assertArrayEquals(decs(d(-1), d(-3)), negate(decs(d(1), d(3))));
        assertArrayEquals(decs(d(-1), d(-3), d(-5)), negate(decs(d(1), d(3), d(5))));
    }

    @Test
    public void testMultUnary() {
        assertArrayEquals(decs(d(1)), mult(decs()));
        assertArrayEquals(decs(d(1)), mult(decs(d(1))));
        assertArrayEquals(decs(d(3)), mult(decs(d(1), d(3))));
        assertArrayEquals(decs(d(15)), mult(decs(d(1), d(3), d(5))));
    }

    @Test
    public void testDivideUnary() {
        assertArrayEquals(decs(d(1)), divide(decs()));
        assertArrayEquals(decs(d("0.5")), divide(decs(d(2))));
        assertArrayEquals(decs(d("0.25")), divide(decs(d(1), d(4))));
        assertArrayEquals(decs(d("0.1")), divide(decs(d(1), d(2), d(5))));
    }

    @Test
    public void testZip() {
        BinaryOperator<BigDecimal> op = (a, b) -> a.add(b);
        assertArrayEquals(decs(), zip(decs(), decs(), op));
        assertArrayEquals(decs(d(1)), zip(decs(), decs(d(1)), op));
        assertArrayEquals(decs(d(1), d(2)), zip(decs(), decs(d(1), d(2)), op));
        assertArrayEquals(decs(d(10)), zip(decs(d(10)), decs(), op));
        assertArrayEquals(decs(d(11)), zip(decs(d(10)), decs(d(1)), op));
        assertArrayEquals(decs(d(11), d(12)), zip(decs(d(10)), decs(d(1), d(2)), op));
        assertArrayEquals(decs(d(10), d(20)), zip(decs(d(10), d(20)), decs(), op));
        assertArrayEquals(decs(d(11), d(21)), zip(decs(d(10), d(20)), decs(d(1)), op));
        assertArrayEquals(decs(d(11), d(22)), zip(decs(d(10), d(20)), decs(d(1), d(2)), op));
        try {
            zip(decs(d(10), d(20), d(30)), decs(d(1), d(2)), op);
            fail();
        } catch (DecsException e) {
            assertEquals("zip: Invalid size l=(10, 20, 30) r=(1, 2)", e.getMessage());
        }
    }

    @Test
    public void testAddBinary() {
        assertArrayEquals(decs(), add(decs(), decs()));
        assertArrayEquals(decs(d(1)), add(decs(), decs(d(1))));
        assertArrayEquals(decs(d(1), d(2)), add(decs(), decs(d(1), d(2))));
        assertArrayEquals(decs(d(10)), add(decs(d(10)), decs()));
        assertArrayEquals(decs(d(11)), add(decs(d(10)), decs(d(1))));
        assertArrayEquals(decs(d(11), d(12)), add(decs(d(10)), decs(d(1), d(2))));
        assertArrayEquals(decs(d(10), d(20)), add(decs(d(10), d(20)), decs()));
        assertArrayEquals(decs(d(11), d(21)), add(decs(d(10), d(20)), decs(d(1))));
        assertArrayEquals(decs(d(11), d(22)), add(decs(d(10), d(20)), decs(d(1), d(2))));
    }

    @Test
    public void testSubtractBinary() {
        assertArrayEquals(decs(), subtract(decs(), decs()));
        assertArrayEquals(decs(d(1)), subtract(decs(), decs(d(1))));
        assertArrayEquals(decs(d(1), d(2)), subtract(decs(), decs(d(1), d(2))));
        assertArrayEquals(decs(d(10)), subtract(decs(d(10)), decs()));
        assertArrayEquals(decs(d(9)), subtract(decs(d(10)), decs(d(1))));
        assertArrayEquals(decs(d(9), d(8)), subtract(decs(d(10)), decs(d(1), d(2))));
        assertArrayEquals(decs(d(10), d(20)), subtract(decs(d(10), d(20)), decs()));
        assertArrayEquals(decs(d(9), d(19)), subtract(decs(d(10), d(20)), decs(d(1))));
        assertArrayEquals(decs(d(9), d(18)), subtract(decs(d(10), d(20)), decs(d(1), d(2))));
    }
}
