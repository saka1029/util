package saka1029.util.declisp;

import static org.junit.Assert.*;
import static saka1029.util.declisp.Common.*;

import java.math.BigDecimal;

import org.junit.Test;

public class TestCommon {

    @Test 
    public void testDec() {
        assertEquals(dec(2), dec(2.0));
        assertNotEquals(dec(2), dec(2.3));
        BigDecimal v = BigDecimal.valueOf(1.2);
        assertEquals(v.hashCode(), dec(v).hashCode());
    }

    @Test 
    public void testInteger() {
        try {
            toInt(bigdec("999999999999999"));
            fail();
        } catch (DecLispException x) {
            assertEquals(ArithmeticException.class, x.getCause().getClass());
            assertEquals("Overflow", x.getCause().getMessage());
        }
    }

    @Test 
    public void testLon() {
        try {
            toLong(bigdec("9999999999999999999999999"));
            fail();
        } catch (DecLispException x) {
            assertEquals(ArithmeticException.class, x.getCause().getClass());
            assertEquals("Overflow", x.getCause().getMessage());
        }
    }

}
