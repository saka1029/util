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
        Context context = new Context();
        Parser parser = new Parser(context);
        assertDecsEquals(Decs.decs("3"), parser.eval(" 1 + 2 "));
        assertDecsEquals(Decs.decs("10"), parser.eval(" 2 * (2 + 3) "));
        assertDecsEquals(Decs.decs("8"), parser.eval(" 2 * 2 ^ 2 "));
    }
}
