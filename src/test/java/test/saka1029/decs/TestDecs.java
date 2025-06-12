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

    @Test
    public void testDecs() {
        assertArrayEquals(decs(), decs("   "));
        assertArrayEquals(decs(dec(1), dec(2.34)), decs("1 2.34"));
    }

    @Test
    public void testEquals() {
        assertArrayEquals(decs(""), decs(""));
        assertArrayEquals(decs("1"), decs("1"));
        assertArrayEquals(decs("1 2"), decs("1 2"));
        assertNotEquals(decs("1 2"), decs("1"));
        assertNotEquals(decs("1 2"), "1 2");
    }

    @Test
    public void testToString() {
        assertEquals("()", string(decs("")));
        assertEquals("(1)", string(decs("1")));
        assertEquals("(1, 3)", string(decs("1 3")));
        assertEquals("(1, 3, 5)", string(decs("1 3 5")));
    }

    @Test
    public void testSumUnary() {
        assertArrayEquals(decs("0"), sum(decs("")));
        assertArrayEquals(decs("1"), sum(decs("1")));
        assertArrayEquals(decs("4"), sum(decs("1 3")));
        assertArrayEquals(decs("9"), sum(decs("1 3 5")));
    }

    @Test
    public void testSubtractUnary() {
        assertArrayEquals(decs("0"), subtract(decs("")));
        assertArrayEquals(decs("-1"), subtract(decs("1")));
        assertArrayEquals(decs("-2"), subtract(decs("1 3")));
        assertArrayEquals(decs("-7"), subtract(decs("1 3 5")));
    }

    @Test
    public void testNegateUnary() {
        assertArrayEquals(decs(""), negate(decs("")));
        assertArrayEquals(decs("-1"), negate(decs("1")));
        assertArrayEquals(decs("-1 -3"), negate(decs("1 3")));
        assertArrayEquals(decs("-1 -3 -5"), negate(decs("1 3 5")));
    }

    @Test
    public void testMultUnary() {
        assertArrayEquals(decs(dec(1)), mult(decs()));
        assertArrayEquals(decs(dec(1)), mult(decs(dec(1))));
        assertArrayEquals(decs(dec(3)), mult(decs(dec(1), dec(3))));
        assertArrayEquals(decs(dec(15)), mult(decs(dec(1), dec(3), dec(5))));
    }

    @Test
    public void testDivideUnary() {
        assertArrayEquals(decs(dec(1)), divide(decs()));
        assertArrayEquals(decs(dec("0.5")), divide(decs(dec(2))));
        assertArrayEquals(decs(dec("0.25")), divide(decs(dec(1), dec(4))));
        assertArrayEquals(decs(dec("0.1")), divide(decs(dec(1), dec(2), dec(5))));
    }

    @Test
    public void testZip() {
        BinaryOperator<BigDecimal> op = (a, b) -> a.add(b);
        assertArrayEquals(decs(), zip(decs(), decs(), op));
        assertArrayEquals(decs(dec(1)), zip(decs(), decs(dec(1)), op));
        assertArrayEquals(decs(dec(1), dec(2)), zip(decs(), decs(dec(1), dec(2)), op));
        assertArrayEquals(decs(dec(10)), zip(decs(dec(10)), decs(), op));
        assertArrayEquals(decs(dec(11)), zip(decs(dec(10)), decs(dec(1)), op));
        assertArrayEquals(decs(dec(11), dec(12)), zip(decs(dec(10)), decs(dec(1), dec(2)), op));
        assertArrayEquals(decs(dec(10), dec(20)), zip(decs(dec(10), dec(20)), decs(), op));
        assertArrayEquals(decs(dec(11), dec(21)), zip(decs(dec(10), dec(20)), decs(dec(1)), op));
        assertArrayEquals(decs(dec(11), dec(22)), zip(decs(dec(10), dec(20)), decs(dec(1), dec(2)), op));
        try {
            zip(decs(dec(10), dec(20), dec(30)), decs(dec(1), dec(2)), op);
            fail();
        } catch (DecsException e) {
            assertEquals("zip: Invalid size l=(10, 20, 30) r=(1, 2)", e.getMessage());
        }
    }

    @Test
    public void testAddBinary() {
        assertArrayEquals(decs(), add(decs(), decs()));
        assertArrayEquals(decs(dec(1)), add(decs(), decs(dec(1))));
        assertArrayEquals(decs(dec(1), dec(2)), add(decs(), decs(dec(1), dec(2))));
        assertArrayEquals(decs(dec(10)), add(decs(dec(10)), decs()));
        assertArrayEquals(decs(dec(11)), add(decs(dec(10)), decs(dec(1))));
        assertArrayEquals(decs(dec(11), dec(12)), add(decs(dec(10)), decs(dec(1), dec(2))));
        assertArrayEquals(decs(dec(10), dec(20)), add(decs(dec(10), dec(20)), decs()));
        assertArrayEquals(decs(dec(11), dec(21)), add(decs(dec(10), dec(20)), decs(dec(1))));
        assertArrayEquals(decs(dec(11), dec(22)), add(decs(dec(10), dec(20)), decs(dec(1), dec(2))));
    }

    @Test
    public void testSubtractBinary() {
        assertArrayEquals(decs(), subtract(decs(), decs()));
        assertArrayEquals(decs(dec(1)), subtract(decs(), decs(dec(1))));
        assertArrayEquals(decs(dec(1), dec(2)), subtract(decs(), decs(dec(1), dec(2))));
        assertArrayEquals(decs(dec(10)), subtract(decs(dec(10)), decs()));
        assertArrayEquals(decs(dec(9)), subtract(decs(dec(10)), decs(dec(1))));
        assertArrayEquals(decs(dec(9), dec(8)), subtract(decs(dec(10)), decs(dec(1), dec(2))));
        assertArrayEquals(decs(dec(10), dec(20)), subtract(decs(dec(10), dec(20)), decs()));
        assertArrayEquals(decs(dec(9), dec(19)), subtract(decs(dec(10), dec(20)), decs(dec(1))));
        assertArrayEquals(decs(dec(9), dec(18)), subtract(decs(dec(10), dec(20)), decs(dec(1), dec(2))));
    }
}
