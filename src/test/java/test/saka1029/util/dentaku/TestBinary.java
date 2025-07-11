package test.saka1029.util.dentaku;

import org.junit.Test;
import ch.obermuhlner.math.big.BigDecimalMath;
import saka1029.util.dentaku.BinaryMap;
import saka1029.util.dentaku.Context;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static saka1029.util.dentaku.Value.*;
import java.math.BigDecimal;
import java.math.MathContext;

public class TestBinary {

    static BigDecimal pow(BigDecimal x, BigDecimal y) {
        return BigDecimalMath.pow(x, y, MathContext.DECIMAL128).stripTrailingZeros();
    }

    @Test
    public void testMap() {
        Context c = Context.of();
        assertArrayEquals(array("5 7 9"), BinaryMap.of(BigDecimal::add).apply(c, array("1 2 3"), array("4 5 6")));
        assertArrayEquals(array("5 6 7"), BinaryMap.of(BigDecimal::add).apply(c, array("1 2 3"), array("4")));
        assertArrayEquals(array("2 3 4"), BinaryMap.of(BigDecimal::add).apply(c, array("1"), array("1 2 3")));
        assertArrayEquals(EMPTY, BinaryMap.of(BigDecimal::add).apply(c, EMPTY, EMPTY));
        assertEquals(dec(27), pow(dec(3), dec(3)));
        assertEquals(dec(3), pow(dec(9), dec(0.5)));
        assertEquals(dec("3"), pow(dec(27), BigDecimal.ONE.divide(dec(3), MathContext.DECIMAL128)));
        // assertEquals(dec(0.5), pow(dec(2), dec(-1)));
        assertArrayEquals(array("1 4 27"), BinaryMap.of(TestBinary::pow).apply(c, array("1 2 3"), array("1 2 3")));
    }
}
