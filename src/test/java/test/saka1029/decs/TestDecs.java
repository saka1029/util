package test.saka1029.decs;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.function.BinaryOperator;
import java.util.Arrays;
import org.junit.Test;
import saka1029.util.decs.Decs;
import static saka1029.util.decs.Decs.*;
import saka1029.util.decs.DecsException;

public class TestDecs {

    @Test
    public void testDecs() {
        assertArrayEquals(decs(), decs("   "));
        assertArrayEquals(decs(dec(1), dec(3L), dec(2.34)), decs("1 3 2.34"));
    }

    @Test
    public void testHashCode() {
        assertEquals(Arrays.hashCode(EMPTY), Decs.hashCode(decs("")));
        assertEquals(Arrays.hashCode(decs(dec("1"))), Decs.hashCode(decs("1")));
        assertEquals(Arrays.hashCode(decs(dec(1), dec(2))), Decs.hashCode(decs("1 2")));
    }

    @Test
    public void testEqual() {
        assertArrayEquals(decs(""), decs(""));
        assertArrayEquals(decs("1"), decs("1"));
        assertArrayEquals(decs("1 2"), decs("1 2"));
        assertNotEquals(decs("1 2"), decs("1"));
        assertNotEquals(decs("1 2"), "1 2");
    }

    @Test
    public void testEquals() {
        assertTrue(Decs.equals(decs(""), decs("")));
        assertTrue(Decs.equals(decs("1"), decs("1")));
        assertTrue(Decs.equals(decs("1 2"), decs("1 2")));
        assertFalse(Decs.equals(decs("1 2"), decs("1")));
    }

    @Test
    public void testToString() {
        assertEquals("()", string(decs("")));
        assertEquals("(1)", string(decs("1")));
        assertEquals("(1, 3)", string(decs("1 3")));
        assertEquals("(1, 3, 5)", string(decs("1 3 5")));
    }

    @Test
    public void testAddUnary() {
        assertArrayEquals(decs("0"), add(decs("")));
        assertArrayEquals(decs("1"), add(decs("1")));
        assertArrayEquals(decs("4"), add(decs("1 3")));
        assertArrayEquals(decs("9"), add(decs("1 3 5")));
    }

    @Test
    public void testSubtractUnary() {
        assertArrayEquals(decs("0"), subtract(decs("")));
        assertArrayEquals(decs("-1"), subtract(decs("1")));
        assertArrayEquals(decs("-2"), subtract(decs("1 3")));
        assertArrayEquals(decs("-7"), subtract(decs("1 3 5")));
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
    public void testPowUnary() {
        assertArrayEquals(decs("1"), pow(decs("")));
        assertArrayEquals(decs("2"), pow(decs("2")));
        assertArrayEquals(decs("8"), pow(decs("2 3")));
        assertArrayEquals(decs("512"), pow(decs("2 3 2"))); // 2^(3^2)=512, (2^3)^2=64
        BigDecimal[] sqrt2 = pow(decs("2 0.5"));
        assertEquals(1, sqrt2.length);
        assertEquals(Math.sqrt(2), sqrt2[0].doubleValue(), 1e-6);
    }

    @Test
    public void testAndUnary() {
        assertEquals(0, decs("").length);
        assertArrayEquals(decs("1"), and(decs("")));
        assertArrayEquals(decs("1"), and(decs("2")));
        assertArrayEquals(decs("1"), and(decs("2 2")));
        assertArrayEquals(decs("0"), and(decs("2 0")));
        assertArrayEquals(decs("0"), and(decs("0 2")));
        assertArrayEquals(decs("0"), and(decs("0 0")));
    }

    @Test
    public void testOrUnary() {
        assertEquals(0, decs("").length);
        assertArrayEquals(decs("0"), or(decs("")));
        assertArrayEquals(decs("1"), or(decs("2")));
        assertArrayEquals(decs("1"), or(decs("2 2")));
        assertArrayEquals(decs("1"), or(decs("2 0")));
        assertArrayEquals(decs("1"), or(decs("0 2")));
        assertArrayEquals(decs("0"), or(decs("0 0")));
    }

    @Test
    public void testNegateUnary() {
        assertArrayEquals(decs(""), negate(decs("")));
        assertArrayEquals(decs("-1"), negate(decs("1")));
        assertArrayEquals(decs("-1 -3"), negate(decs("1 3")));
        assertArrayEquals(decs("-1 -3 -5"), negate(decs("1 3 5")));
    }

    @Test
    public void testNotUnary() {
        assertArrayEquals(decs(""), not(decs("")));
        assertArrayEquals(decs("0"), not(decs("3")));
        assertArrayEquals(decs("0 1"), not(decs("3 0")));
        assertArrayEquals(decs("1 0"), not(decs("0 3")));
    }

    @Test
    public void testIotaUnary() {
        assertArrayEquals(decs("1 2 3"), iota(decs("3")));
        assertArrayEquals(decs(""), iota(decs("-3")));
        try {
            iota(decs("1 2"));
            fail();
        } catch (DecsException e) {
            assertEquals("Single value expected but (1, 2)", e.getMessage());
        }
    }

    @Test
    public void testIota0Unary() {
        assertArrayEquals(decs("0 1 2"), iota0(decs("3")));
        assertArrayEquals(decs(""), iota0(decs("-3")));
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
        assertArrayEquals(decs(""), subtract(decs(""), decs("")));
        assertArrayEquals(decs("1"), subtract(decs(), decs("1")));
        assertArrayEquals(decs("1 2"), subtract(decs(), decs("1 2")));
        assertArrayEquals(decs("10"), subtract(decs("10"), decs("")));
        assertArrayEquals(decs("9"), subtract(decs("10"), decs("1")));
        assertArrayEquals(decs("9 8"), subtract(decs("10"), decs("1 2")));
        assertArrayEquals(decs("10 20"), subtract(decs("10 20"), decs("")));
        assertArrayEquals(decs("9 19"), subtract(decs("10 20"), decs("1")));
        assertArrayEquals(decs("9 18"), subtract(decs("10 20"), decs("1 2")));
    }

    @Test
    public void testCompare() {
        assertArrayEquals(decs("0"), compare(decs("1"), decs("1")));
        assertArrayEquals(decs("-1"), compare(decs("1"), decs("2")));
        assertArrayEquals(decs("1"), compare(decs("2"), decs("1")));
        assertArrayEquals(decs("0 1"), compare(decs("1 2"), decs("1 1")));
        assertArrayEquals(decs("-1 0"), compare(decs("1 1"), decs("2 1")));
        assertArrayEquals(decs("1 0"), compare(decs("2 1"), decs("1 1")));
    }

    @Test
    public void testEq() {
        assertArrayEquals(decs("1"), eq(decs("1"), decs("1")));
        assertArrayEquals(decs("0"), eq(decs("1"), decs("2")));
        assertArrayEquals(decs("0"), eq(decs("2"), decs("1")));
        assertArrayEquals(decs("1 0"), eq(decs("1 2"), decs("1 1")));
        assertArrayEquals(decs("0 1"), eq(decs("1 1"), decs("2 1")));
        assertArrayEquals(decs("0 1"), eq(decs("2 1"), decs("1 1")));
    }

    @Test
    public void testNe() {
        assertArrayEquals(decs("0"), ne(decs("1"), decs("1")));
        assertArrayEquals(decs("1"), ne(decs("1"), decs("2")));
        assertArrayEquals(decs("1"), ne(decs("2"), decs("1")));
        assertArrayEquals(decs("0 1"), ne(decs("1 2"), decs("1 1")));
        assertArrayEquals(decs("1 0"), ne(decs("1 1"), decs("2 1")));
        assertArrayEquals(decs("1 0"), ne(decs("2 1"), decs("1 1")));
    }

    @Test
    public void testLt() {
        assertArrayEquals(decs("0"), lt(decs("1"), decs("1")));
        assertArrayEquals(decs("1"), lt(decs("1"), decs("2")));
        assertArrayEquals(decs("0"), lt(decs("2"), decs("1")));
        assertArrayEquals(decs("0 0"), lt(decs("1 2"), decs("1 1")));
        assertArrayEquals(decs("1 0"), lt(decs("1 1"), decs("2 1")));
        assertArrayEquals(decs("0 0"), lt(decs("2 1"), decs("1 1")));
    }

    @Test
    public void testLe() {
        assertArrayEquals(decs("1"), le(decs("1"), decs("1")));
        assertArrayEquals(decs("1"), le(decs("1"), decs("2")));
        assertArrayEquals(decs("0"), le(decs("2"), decs("1")));
        assertArrayEquals(decs("1 0"), le(decs("1 2"), decs("1 1")));
        assertArrayEquals(decs("1 1"), le(decs("1 1"), decs("2 1")));
        assertArrayEquals(decs("0 1"), le(decs("2 1"), decs("1 1")));
    }

    @Test
    public void testGt() {
        assertArrayEquals(decs("0"), gt(decs("1"), decs("1")));
        assertArrayEquals(decs("0"), gt(decs("1"), decs("2")));
        assertArrayEquals(decs("1"), gt(decs("2"), decs("1")));
        assertArrayEquals(decs("0 1"), gt(decs("1 2"), decs("1 1")));
        assertArrayEquals(decs("0 0"), gt(decs("1 1"), decs("2 1")));
        assertArrayEquals(decs("1 0"), gt(decs("2 1"), decs("1 1")));
    }

    @Test
    public void testGe() {
        assertArrayEquals(decs("1"), ge(decs("1"), decs("1")));
        assertArrayEquals(decs("0"), ge(decs("1"), decs("2")));
        assertArrayEquals(decs("1"), ge(decs("2"), decs("1")));
        assertArrayEquals(decs("1 1"), ge(decs("1 2"), decs("1 1")));
        assertArrayEquals(decs("0 1"), ge(decs("1 1"), decs("2 1")));
        assertArrayEquals(decs("1 1"), ge(decs("2 1"), decs("1 1")));
    }

    @Test
    public void testBase() {
        assertArrayEquals(decs("15 15 15 15"), base(decs("65535"), decs("16")));
        assertArrayEquals(decs("0"), base(decs("0"), decs("16")));
        assertArrayEquals(decs("1957 10 29"), base(decs("19571029"), decs("100 100")));
        assertArrayEquals(decs("0"), base(decs("0"), decs("100 100")));
    }
}
