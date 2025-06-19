package test.saka1029.decs;

import static org.junit.Assert.assertEquals;
import java.math.BigDecimal;
import java.util.List;
import org.junit.Test;
import saka1029.util.decs.Context;
import saka1029.util.decs.Decs;
import saka1029.util.decs.Parser;

public class TestParser {

    static void assertDecsEquals(BigDecimal[] expected, BigDecimal[] actual) {
        assertEquals(List.of(expected), List.of(actual));
    }

    @Test
    public void testParser() {
        Parser parser = new Parser();
        Context context = new Context();
        assertDecsEquals(Decs.decs("3"), parser.parse(" 1 + 2 ").apply(context));
        assertDecsEquals(Decs.decs("0"), parser.parse(" 1 + (2 - 3) ").apply(context));
    }
}
