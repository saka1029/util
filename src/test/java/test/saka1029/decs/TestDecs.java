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
        assertArrayEquals(decs("1"), mult(decs("")));
        assertArrayEquals(decs("1"), mult(decs("1")));
        assertArrayEquals(decs("3"), mult(decs("1 3")));
        assertArrayEquals(decs("15"), mult(decs("1 3 5")));
    }

    @Test
    public void testDivideUnary() {
        assertArrayEquals(decs("1"), divide(decs("")));
        assertArrayEquals(decs("0.5"), divide(decs("2")));
        assertArrayEquals(decs("0.25"), divide(decs("1 4")));
        assertArrayEquals(decs("0.1"), divide(decs("1 2 5")));
    }

    @Test
    public void testZip() {
        BinaryOperator<BigDecimal> op = (a, b) -> a.add(b);
        assertArrayEquals(decs(), zip(decs(""), decs(""), op));
        assertArrayEquals(decs("1"), zip(decs(""), decs("1"), op));
        assertArrayEquals(decs("1 2"), zip(decs(""), decs("1 2"), op));
        assertArrayEquals(decs("10"), zip(decs("10"), decs(""), op));
        assertArrayEquals(decs("11"), zip(decs("10"), decs("1"), op));
        assertArrayEquals(decs("11 12"), zip(decs("10"), decs("1 2"), op));
        assertArrayEquals(decs("10 20"), zip(decs("10 20"), decs(""), op));
        assertArrayEquals(decs("11 21"), zip(decs("10 20"), decs("1"), op));
        assertArrayEquals(decs("11 22"), zip(decs("10 20"), decs("1 2"), op));
        try {
            zip(decs("10 20 30"), decs("1 2"), op);
            fail();
        } catch (DecsException e) {
            assertEquals("zip: Invalid size l=(10, 20, 30) r=(1, 2)", e.getMessage());
        }
    }

    @Test
    public void testAddBinary() {
        assertArrayEquals(decs(""), add(decs(""), decs("")));
        assertArrayEquals(decs("1"), add(decs(""), decs("1")));
        assertArrayEquals(decs("1 2"), add(decs(""), decs("1 2")));
        assertArrayEquals(decs("10"), add(decs("10"), decs("")));
        assertArrayEquals(decs("11"), add(decs("10"), decs("1")));
        assertArrayEquals(decs("11 12"), add(decs("10"), decs("1 2")));
        assertArrayEquals(decs("10 20"), add(decs("10 20"), decs("")));
        assertArrayEquals(decs("11 21"), add(decs("10 20"), decs("1")));
        assertArrayEquals(decs("11 22"), add(decs("10 20"), decs("1 2")));
    }

    @Test
    public void testSubtractBinary() {
        assertArrayEquals(decs(), subtract(decs(), decs()));
        assertArrayEquals(decs("1"), subtract(decs(), decs("1")));
        assertArrayEquals(decs("1 2"), subtract(decs(), decs("1 2")));
        assertArrayEquals(decs("10"), subtract(decs("10"), decs("")));
        assertArrayEquals(decs("9"), subtract(decs("10"), decs("1")));
        assertArrayEquals(decs("9 8"), subtract(decs("10"), decs("1 2")));
        assertArrayEquals(decs("10 20"), subtract(decs("10 20"), decs("")));
        assertArrayEquals(decs("9 19"), subtract(decs("10 20"), decs("1")));
        assertArrayEquals(decs("9 18"), subtract(decs("10 20"), decs("1 2")));
    }
}
