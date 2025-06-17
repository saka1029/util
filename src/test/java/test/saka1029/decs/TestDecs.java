package test.saka1029.decs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.BinaryOperator;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import ch.obermuhlner.math.big.BigDecimalMath;
import saka1029.util.decs.Decs;
import static saka1029.util.decs.Decs.*;
import saka1029.util.decs.DecsException;

public class TestDecs {

    static void assertDecsEquals(BigDecimal[] expected, BigDecimal[] actual) {
        assertEquals(List.of(expected), List.of(actual));
    }

    @Test
    public void testString() {
        assertEquals("0", string(dec(0)).toString());
        assertEquals("(0)", string(decs("0")));
        assertEquals("(1E+1)", string(decs("1e1")));
        assertEquals("10", dec("1e1").toPlainString());
    }

    @Test
    public void testDecs() {
        assertDecsEquals(decs(), decs("   "));
        assertDecsEquals(decs(dec(1), dec(3L), dec(2.34)), decs("1 3 2.34"));
        assertDecsEquals(decs(""), decs(""));
        assertDecsEquals(decs("1"), decs("1"));
        assertDecsEquals(decs("1 2"), decs("1 2"));
        assertNotEquals(decs("1 2"), decs("1"));
        assertNotEquals(decs("1 2"), "1 2");
        assertDecsEquals(decs("1"), decs(dec(BigInteger.ONE)));
        assertDecsEquals(decs("1"), decs(List.of(BigDecimal.ONE)));
    }

    @Test
    public void testHashCode() {
        assertEquals(Arrays.hashCode(EMPTY), Decs.hashCode(decs("")));
        assertEquals(Arrays.hashCode(decs(dec("1"))), Decs.hashCode(decs("1")));
        assertEquals(Arrays.hashCode(decs(dec(1), dec(2))), Decs.hashCode(decs("1 2")));
    }

    @Test
    public void testEquals() {
        assertFalse(dec("0").equals(dec("0.0")));
        assertFalse(dec("10").equals(dec("1E1")));
        assertTrue(Decs.equals(dec("0"), dec("0.0")));
        assertTrue(Decs.equals(dec("10"), dec("1E1")));
        assertFalse(Decs.equals(dec("12"), dec("1E1")));
        assertTrue(Decs.equals(decs("0"), decs("0.0")));
        assertTrue(Decs.equals(decs(""), decs("")));
        assertTrue(Decs.equals(decs("1"), decs("1")));
        assertTrue(Decs.equals(decs("1 2"), decs("1 2")));
        assertFalse(Decs.equals(decs("1 2"), decs("1 3")));
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
        assertDecsEquals(decs("0"), add(decs("")));
        assertDecsEquals(decs("1"), add(decs("1")));
        assertDecsEquals(decs("4"), add(decs("1 3")));
        assertDecsEquals(decs("9"), add(decs("1 3 5")));
    }

    @Test
    public void testSubtractUnary() {
        assertDecsEquals(decs("0"), subtract(decs("")));
        assertDecsEquals(decs("-1"), subtract(decs("1")));
        assertDecsEquals(decs("-2"), subtract(decs("1 3")));
        assertDecsEquals(decs("-7"), subtract(decs("1 3 5")));
    }

    @Test
    public void testMultUnary() {
        assertDecsEquals(decs("1"), mult(decs("")));
        assertDecsEquals(decs("1"), mult(decs("1")));
        assertDecsEquals(decs("3"), mult(decs("1 3")));
        assertDecsEquals(decs("15"), mult(decs("1 3 5")));
    }

    @Test
    public void testDivideUnary() {
        assertDecsEquals(decs("1"), divide(decs("")));
        assertDecsEquals(decs("0.5"), divide(decs("2")));
        assertDecsEquals(decs("0.25"), divide(decs("1 4")));
        assertDecsEquals(decs("0.1"), divide(decs("1 2 5")));
    }

    @Test
    public void testPowUnary() {
        assertDecsEquals(decs("1"), pow(decs("")));
        assertDecsEquals(decs("2"), pow(decs("2")));
        assertDecsEquals(decs("8"), pow(decs("2 3")));
        assertDecsEquals(decs("512"), pow(decs("2 3 2"))); // 2^(3^2)=512, (2^3)^2=64
        BigDecimal[] sqrt2 = pow(decs("2 0.5"));
        assertEquals(1, sqrt2.length);
        assertEquals(Math.sqrt(2), sqrt2[0].doubleValue(), 1e-6);
    }

    @Test
    public void testAndUnary() {
        assertEquals(0, decs("").length);
        assertDecsEquals(decs("1"), and(decs("")));
        assertDecsEquals(decs("1"), and(decs("2")));
        assertDecsEquals(decs("1"), and(decs("2 2")));
        assertDecsEquals(decs("0"), and(decs("2 0")));
        assertDecsEquals(decs("0"), and(decs("0 2")));
        assertDecsEquals(decs("0"), and(decs("0 0")));
    }

    @Test
    public void testOrUnary() {
        assertEquals(0, decs("").length);
        assertDecsEquals(decs("0"), or(decs("")));
        assertDecsEquals(decs("1"), or(decs("2")));
        assertDecsEquals(decs("1"), or(decs("2 2")));
        assertDecsEquals(decs("1"), or(decs("2 0")));
        assertDecsEquals(decs("1"), or(decs("0 2")));
        assertDecsEquals(decs("0"), or(decs("0 0")));
    }

    @Test
    public void testNegateUnary() {
        assertDecsEquals(decs(""), negate(decs("")));
        assertDecsEquals(decs("-1"), negate(decs("1")));
        assertDecsEquals(decs("-1 -3"), negate(decs("1 3")));
        assertDecsEquals(decs("-1 -3 -5"), negate(decs("1 3 5")));
    }

    @Test
    public void testNotUnary() {
        assertDecsEquals(decs(""), not(decs("")));
        assertDecsEquals(decs("0"), not(decs("3")));
        assertDecsEquals(decs("0 1"), not(decs("3 0")));
        assertDecsEquals(decs("1 0"), not(decs("0 3")));
    }

    @Test
    public void testRadianUnary() {
        assertDecsEquals(decs(""), radian(decs("")));
        BigDecimal[] pi = radian(decs("180"));
        assertEquals(1, pi.length);
        assertEquals(BigDecimalMath.pi(Decs.MATH_CONTEXT).doubleValue(),
            pi[0].doubleValue(), 1e-6);
    }

    @Test
    public void testDegreeUnary() {
        assertDecsEquals(decs(""), degree(decs("")));
        BigDecimal[] d180 = degree(decs(dec(Math.PI)));
        assertEquals(1, d180.length);
        assertEquals(180D, d180[0].doubleValue(), 1e-6);
    }

    @Test
    public void testSinUnary() {
        assertDecsEquals(decs(""), sin(decs("")));
        BigDecimal[] sin90 = sin(decs(dec(Math.PI / 2)));
        assertEquals(1, sin90.length);
        assertEquals(1D, sin90[0].doubleValue(), 1e-6);
    }

    @Test
    public void testCosUnary() {
        assertDecsEquals(decs(""), cos(decs("")));
        BigDecimal[] cos90 = cos(decs(dec(Math.PI / 2)));
        assertEquals(1, cos90.length);
        assertEquals(0D, cos90[0].doubleValue(), 1e-6);
    }

    @Test
    public void testTanUnary() {
        assertDecsEquals(decs(""), tan(decs("")));
        BigDecimal[] tan90 = tan(decs(dec(Math.PI / 4)));
        assertEquals(1, tan90.length);
        assertEquals(1D, tan90[0].doubleValue(), 1e-6);
    }

    @Test
    public void testIotaUnary() {
        assertDecsEquals(decs("1 2 3"), iota(decs("3")));
        assertDecsEquals(decs(""), iota(decs("-3")));
        try {
            iota(decs("1 2"));
            fail();
        } catch (DecsException e) {
            assertEquals("Single value expected but (1, 2)", e.getMessage());
        }
    }

    @Test
    public void testIota0Unary() {
        assertDecsEquals(decs("0 1 2"), iota0(decs("3")));
        assertDecsEquals(decs(""), iota0(decs("-3")));
    }

    @Test
    public void testZip() {
        BinaryOperator<BigDecimal> op = (a, b) -> a.add(b);
        assertDecsEquals(decs(), zip(decs(""), decs(""), op));
        assertDecsEquals(decs("1"), zip(decs(""), decs("1"), op));
        assertDecsEquals(decs("1 2"), zip(decs(""), decs("1 2"), op));
        assertDecsEquals(decs("10"), zip(decs("10"), decs(""), op));
        assertDecsEquals(decs("11"), zip(decs("10"), decs("1"), op));
        assertDecsEquals(decs("11 12"), zip(decs("10"), decs("1 2"), op));
        assertDecsEquals(decs("10 20"), zip(decs("10 20"), decs(""), op));
        assertDecsEquals(decs("11 21"), zip(decs("10 20"), decs("1"), op));
        assertDecsEquals(decs("11 22"), zip(decs("10 20"), decs("1 2"), op));
        try {
            zip(decs("10 20 30"), decs("1 2"), op);
            fail();
        } catch (DecsException e) {
            assertEquals("zip: Invalid size l=(10, 20, 30) r=(1, 2)", e.getMessage());
        }
    }

    @Test
    public void testAddBinary() {
        assertDecsEquals(decs(""), add(decs(""), decs("")));
        assertDecsEquals(decs("1"), add(decs(""), decs("1")));
        assertDecsEquals(decs("1 2"), add(decs(""), decs("1 2")));
        assertDecsEquals(decs("10"), add(decs("10"), decs("")));
        assertDecsEquals(decs("11"), add(decs("10"), decs("1")));
        assertDecsEquals(decs("11 12"), add(decs("10"), decs("1 2")));
        assertDecsEquals(decs("10 20"), add(decs("10 20"), decs("")));
        assertDecsEquals(decs("11 21"), add(decs("10 20"), decs("1")));
        assertDecsEquals(decs("11 22"), add(decs("10 20"), decs("1 2")));
    }

    @Test
    public void testSubtractBinary() {
        assertDecsEquals(decs(""), subtract(decs(""), decs("")));
        assertDecsEquals(decs("1"), subtract(decs(), decs("1")));
        assertDecsEquals(decs("1 2"), subtract(decs(), decs("1 2")));
        assertDecsEquals(decs("10"), subtract(decs("10"), decs("")));
        assertDecsEquals(decs("9"), subtract(decs("10"), decs("1")));
        assertDecsEquals(decs("9 8"), subtract(decs("10"), decs("1 2")));
        assertDecsEquals(decs("10 20"), subtract(decs("10 20"), decs("")));
        assertDecsEquals(decs("9 19"), subtract(decs("10 20"), decs("1")));
        assertDecsEquals(decs("9 18"), subtract(decs("10 20"), decs("1 2")));
    }

    @Test
    public void testMultiplyBinary() {
        assertDecsEquals(decs(""), Decs.multiply(decs(""), decs("")));
        assertDecsEquals(decs("1"), multiply(decs(), decs("1")));
        assertDecsEquals(decs("1 2"), multiply(decs(), decs("1 2")));
        assertDecsEquals(decs("10"), multiply(decs("10"), decs("")));
        assertDecsEquals(decs("10"), multiply(decs("10"), decs("1")));
        assertDecsEquals(decs("10 20"), multiply(decs("10"), decs("1 2")));
        assertDecsEquals(decs("10 20"), multiply(decs("10 20"), decs("")));
        assertDecsEquals(decs("10 20"), multiply(decs("10 20"), decs("1")));
        assertDecsEquals(decs("10 40"), multiply(decs("10 20"), decs("1 2")));
    }

    @Test
    public void testDivideBinary() {
        assertDecsEquals(decs(""), Decs.divide(decs(""), decs("")));
        assertDecsEquals(decs("1"), divide(decs(), decs("1")));
        assertDecsEquals(decs("1 2"), divide(decs(), decs("1 2")));
        assertDecsEquals(decs("10"), divide(decs("10"), decs("")));
        assertDecsEquals(decs("10"), divide(decs("10"), decs("1")));
        assertDecsEquals(decs("10 5"), divide(decs("10"), decs("1 2")));
        assertDecsEquals(decs("10 20"), divide(decs("10 20"), decs("")));
        assertDecsEquals(decs("10 20"), divide(decs("10 20"), decs("1")));
        assertDecsEquals(decs("10 10"), divide(decs("10 20"), decs("1 2")));
    }

    @Test
    public void testModBinary() {
        assertDecsEquals(decs(""), Decs.mod(decs(""), decs("")));
        assertDecsEquals(decs("1"), mod(decs(), decs("1")));
        assertDecsEquals(decs("1 2"), mod(decs(), decs("1 2")));
        assertDecsEquals(decs("10"), mod(decs("10"), decs("")));
        assertDecsEquals(decs("0"), mod(decs("10"), decs("1")));
        assertDecsEquals(decs("0 0"), mod(decs("10"), decs("1 2")));
        assertDecsEquals(decs("10 20"), mod(decs("10 20"), decs("")));
        assertDecsEquals(decs("0 0"), mod(decs("10 20"), decs("1")));
        assertDecsEquals(decs("0 1"), mod(decs("10 21"), decs("1 2")));
    }

    @Test
    public void testPowBinary() {
        assertDecsEquals(decs(""), Decs.pow(decs(""), decs("")));
        assertDecsEquals(decs("1"), pow(decs(), decs("1")));
        assertDecsEquals(decs("1 2"), pow(decs(), decs("1 2")));
        assertDecsEquals(decs("10"), pow(decs("10"), decs("")));
        assertDecsEquals(decs("100"), pow(decs("10"), decs("2")));
        assertDecsEquals(decs("10 100"), pow(decs("10"), decs("1 2")));
        assertDecsEquals(decs("10 20"), pow(decs("10 20"), decs("")));
        assertDecsEquals(decs("100 400"), pow(decs("10 20"), decs("2")));
        assertDecsEquals(decs("10 144"), pow(decs("10 12"), decs("1 2")));
        assertEquals(Math.sqrt(2), pow(decs("2"), decs("0.5"))[0].doubleValue(), 1e-6);
    }

    @Test
    public void testCompare() {
        assertDecsEquals(decs("0"), compare(decs("1"), decs("1")));
        assertDecsEquals(decs("-1"), compare(decs("1"), decs("2")));
        assertDecsEquals(decs("1"), compare(decs("2"), decs("1")));
        assertDecsEquals(decs("0 1"), compare(decs("1 2"), decs("1 1")));
        assertDecsEquals(decs("-1 0"), compare(decs("1 1"), decs("2 1")));
        assertDecsEquals(decs("1 0"), compare(decs("2 1"), decs("1 1")));
    }

    @Test
    public void testEq() {
        assertDecsEquals(decs("1"), eq(decs("1"), decs("1")));
        assertDecsEquals(decs("0"), eq(decs("1"), decs("2")));
        assertDecsEquals(decs("0"), eq(decs("2"), decs("1")));
        assertDecsEquals(decs("1 0"), eq(decs("1 2"), decs("1 1")));
        assertDecsEquals(decs("0 1"), eq(decs("1 1"), decs("2 1")));
        assertDecsEquals(decs("0 1"), eq(decs("2 1"), decs("1 1")));
    }

    @Test
    public void testNe() {
        assertDecsEquals(decs("0"), ne(decs("1"), decs("1")));
        assertDecsEquals(decs("1"), ne(decs("1"), decs("2")));
        assertDecsEquals(decs("1"), ne(decs("2"), decs("1")));
        assertDecsEquals(decs("0 1"), ne(decs("1 2"), decs("1 1")));
        assertDecsEquals(decs("1 0"), ne(decs("1 1"), decs("2 1")));
        assertDecsEquals(decs("1 0"), ne(decs("2 1"), decs("1 1")));
    }

    @Test
    public void testLt() {
        assertDecsEquals(decs("0"), lt(decs("1"), decs("1")));
        assertDecsEquals(decs("1"), lt(decs("1"), decs("2")));
        assertDecsEquals(decs("0"), lt(decs("2"), decs("1")));
        assertDecsEquals(decs("0 0"), lt(decs("1 2"), decs("1 1")));
        assertDecsEquals(decs("1 0"), lt(decs("1 1"), decs("2 1")));
        assertDecsEquals(decs("0 0"), lt(decs("2 1"), decs("1 1")));
    }

    @Test
    public void testLe() {
        assertDecsEquals(decs("1"), le(decs("1"), decs("1")));
        assertDecsEquals(decs("1"), le(decs("1"), decs("2")));
        assertDecsEquals(decs("0"), le(decs("2"), decs("1")));
        assertDecsEquals(decs("1 0"), le(decs("1 2"), decs("1 1")));
        assertDecsEquals(decs("1 1"), le(decs("1 1"), decs("2 1")));
        assertDecsEquals(decs("0 1"), le(decs("2 1"), decs("1 1")));
    }

    @Test
    public void testGt() {
        assertDecsEquals(decs("0"), gt(decs("1"), decs("1")));
        assertDecsEquals(decs("0"), gt(decs("1"), decs("2")));
        assertDecsEquals(decs("1"), gt(decs("2"), decs("1")));
        assertDecsEquals(decs("0 1"), gt(decs("1 2"), decs("1 1")));
        assertDecsEquals(decs("0 0"), gt(decs("1 1"), decs("2 1")));
        assertDecsEquals(decs("1 0"), gt(decs("2 1"), decs("1 1")));
    }

    @Test
    public void testGe() {
        assertDecsEquals(decs("1"), ge(decs("1"), decs("1")));
        assertDecsEquals(decs("0"), ge(decs("1"), decs("2")));
        assertDecsEquals(decs("1"), ge(decs("2"), decs("1")));
        assertDecsEquals(decs("1 1"), ge(decs("1 2"), decs("1 1")));
        assertDecsEquals(decs("0 1"), ge(decs("1 1"), decs("2 1")));
        assertDecsEquals(decs("1 1"), ge(decs("2 1"), decs("1 1")));
    }

    @Test
    public void testBase() {
        assertDecsEquals(decs("15 15 15 15"), base(decs("65535"), decs("16")));
        assertDecsEquals(decs("0"), base(decs("0"), decs("16")));
        assertDecsEquals(decs("1957 10 29"), base(decs("19571029"), decs("100 100")));
        assertDecsEquals(decs("0"), base(decs("0"), decs("100 100")));
        assertDecsEquals(decs("1 1"), base(decs("101"), decs("100 100")));
        System.out.println(string(base(decs("9"), decs("1.5"))));
        assertDecsEquals(decs("1 0.5 1.0 0.0 0.0"), base(decs("9"), decs("1.5")));
        try {
            base(decs("1 2"), decs("3"));
            fail();
        } catch (DecsException e) {
            assertEquals("Single value expected but (1, 2)", e.getMessage());
        }
    }
}
