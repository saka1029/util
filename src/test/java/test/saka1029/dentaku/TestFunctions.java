package test.saka1029.dentaku;

import static org.junit.Assert.assertEquals;
import java.math.BigDecimal;
import java.util.Arrays;
import org.junit.Test;
import saka1029.util.dentaku.Context;
import saka1029.util.dentaku.Operators;
import saka1029.util.dentaku.Parser;
import saka1029.util.dentaku.Value;

public class TestFunctions {

    static Value value() {
        return Value.EMPTY;
    }

    static Value value(String s) {
        return Value.of(Arrays.stream(s.split("\\s+"))
            .map(x -> new BigDecimal(x))
            .toArray(BigDecimal[]::new));
    }

    static Value eval(Context context, String input) {
        return Parser.parse(context.operators(), input).eval(context);
    }

    @Test
    public void testUnaryArithmeticOperators() {
        Operators f = Operators.of();
        Context c = Context.of(f);
        assertEquals(eval(c, "-1 -2 -3"), eval(c, "- 1 2 3"));
        assertEquals(eval(c, "6"), eval(c, "+ 1 2 3"));
        assertEquals(eval(c, "24"), eval(c, "* 1 2 3 4"));
        assertEquals(eval(c, "-1 1 0"), eval(c, "sign -1 2 0"));
    }

    @Test
    public void tesUnarytTrigonometricOperators() {
        Operators f = Operators.of();
        Context c = Context.of(f);
        assertEquals(eval(c, "0 1 -1 0"), eval(c, "sin 0 (PI / 2) (-PI / 2) PI round 0"));
        assertEquals(eval(c, "1 0 0 -1"), eval(c, "cos 0 (PI / 2) (-PI / 2) PI round 0"));
        assertEquals(eval(c, "0 1 -1 0"), eval(c, "tan 0 (PI / 4) (-PI / 4) PI round 0"));
        assertEquals(eval(c, "0 (PI / 2) (-PI / 2) 0 round 8"), eval(c, "asin 0 1 -1 0 round 8"));
        assertEquals(eval(c, "0 (PI / 2) (PI / 2) PI round 8"), eval(c, "acos 1 0 0 -1 round 8"));
        assertEquals(eval(c, "0 (PI / 4) (-PI / 4) 0 round 8"), eval(c, "atan 0 1 -1 0 round 8"));
    }

    @Test
    public void testUnaryLogOperators() {
        Operators f = Operators.of();
        Context c = Context.of(f);
        assertEquals(eval(c, "0 1 2 3"), eval(c, "log (E ^ (0 to 3))"));
        assertEquals(eval(c, "0 1 2 3"), eval(c, "log10 (10 ^ (0 to 3))"));
    }

    @Test
    public void testUnaryLogicalOperators() {
        Operators f = Operators.of();
        Context c = Context.of(f);
        assertEquals(eval(c, "0 1"), eval(c, "not -1 0"));
    }

    @Test
    public void testBinaryArithmeticOperators() {
        Operators f = Operators.of();
        Context c = Context.of(f);
        assertEquals(eval(c, "5 7 9"), eval(c, "1 2 3 + 4 5 6"));
        assertEquals(eval(c, "5 6 7"), eval(c, "1 2 3 + 4"));
        assertEquals(eval(c, "5 6 7"), eval(c, "4 + 1 2 3"));
        assertEquals(eval(c, "-3 -3 -3"), eval(c, "1 2 3 - 4 5 6"));
        assertEquals(eval(c, "-3 -2 -1"), eval(c, "1 2 3 - 4"));
        assertEquals(eval(c, "3 2 1"), eval(c, "4 - 1 2 3"));
        assertEquals(eval(c, "6 3 2 1"), eval(c, "6 / 1 2 3 6"));
        assertEquals(eval(c, "6 1.5 0.5 0.125"), eval(c, "6 3 2 1 / 1 2 4 8"));
        assertEquals(eval(c, "0.5 1 1.5 3"), eval(c, "1 2 3 6 / 2"));
        assertEquals(eval(c, "0 0 0 0"), eval(c, "6 % 1 2 3 6"));
        assertEquals(eval(c, "1 0 0 4"), eval(c, "1 2 3 4 % 3 2 1 5"));
        assertEquals(eval(c, "1 0 1 0"), eval(c, "1 2 3 6 % 2"));
        assertEquals(eval(c, "1 2 3 5"), eval(c, "1.2 2.3 3.4 4.5 round 0"));
        assertEquals(eval(c, "1.2 2.3 3.5 4.6"), eval(c, "1.23 2.34 3.45 4.56 round 1"));
        assertEquals(eval(c, "8 9 10"), eval(c, "8 to 10"));
        assertEquals(eval(c, "8"), eval(c, "8 to 8"));
        assertEquals(eval(c, "10 9 8"), eval(c, "10 to 8"));
        assertEquals(eval(c, "1 4 9 16"), eval(c, "1 to 4 ^ 2"));
        assertEquals(eval(c, "2 4 8 16"), eval(c, "2 ^ (1 to 4)"));
        assertEquals(eval(c, "1"), eval(c, "1 min 4"));
        assertEquals(eval(c, "1 2 1"), eval(c, "1 2 3 min 3 2 1"));
        assertEquals(eval(c, "4"), eval(c, "1 max 4"));
        assertEquals(eval(c, "3 2 3"), eval(c, "1 2 3 max 3 2 1"));
        assertEquals(eval(c, "1 3"), eval(c, "1 0 1 0 filter (1 to 4)"));
        assertEquals(Value.NaN, eval(c, "a = 1 to 4"));
        assertEquals(eval(c, "0 0 1 1"), eval(c, "a > 2"));
        assertEquals(eval(c, "3 4"), eval(c, "0 0 1 1 filter a"));
        assertEquals(eval(c, "3 4"), eval(c, "a > 2 filter a"));
        assertEquals(Value.EMPTY, eval(c, "a > 9 filter a"));
    }

    @Test
    public void testBinaryCompareOperators() {
        Operators f = Operators.of();
        Context c = Context.of(f);
        assertEquals(eval(c, "0 1 0"), eval(c, "0 == -1 0 1"));
        assertEquals(eval(c, "1 0 1"), eval(c, "0 != -1 0 1"));
        assertEquals(eval(c, "0 0 1"), eval(c, "0 < -1 0 1"));
        assertEquals(eval(c, "0 1 1"), eval(c, "0 <= -1 0 1"));
        assertEquals(eval(c, "1 0 0"), eval(c, "0 > -1 0 1"));
        assertEquals(eval(c, "1 1 0"), eval(c, "0 >= -1 0 1"));
    }

    @Test
    public void testBinaryLogicalOperators() {
        Operators f = Operators.of();
        Context c = Context.of(f);
        assertEquals(eval(c, "1 0 0 0"), eval(c, "1 1 0 0 and 1 0 1 0"));
        assertEquals(eval(c, "1 1 1 0"), eval(c, "1 1 0 0 or  1 0 1 0"));
        assertEquals(eval(c, "0 1 1 0"), eval(c, "1 1 0 0 xor 1 0 1 0"));
    }

    // @Test
    // public void testHighOrderOperators() {
    //     Operators f = Operators.of();
    //     Context c = Context.of(f);
    //     assertEquals(eval(c, "55"), eval(c, "@ + (1 to 10)"));
    //     assertEquals(eval(c, "55"), eval(c, "+ (1 to 10)"));
    //     assertEquals(eval(c, "1 3 6 10"), eval(c, "@@ + (1 to 4)"));
    //     assertEquals(eval(c, "1 2 6 24 120"), eval(c, "@@ * (1 to 5)"));
    //     assertEquals(eval(c, "-2"), eval(c, "@ min 1 3 9 -2 0"));
    //     assertEquals(eval(c, "1 1 1 -2 -2"), eval(c, "@@ min 1 3 9 -2 0"));
    //     assertEquals(eval(c, "9"), eval(c, "@ max 1 3 9 -2 0"));
    //     assertEquals(eval(c, "1 3 9 9 9"), eval(c, "@@ max 1 3 9 -2 0"));
    // }

    @Test
    public void testSortAndReverseOperators() {
        Operators f = Operators.of();
        Context c = Context.of(f);
        assertEquals(eval(c, "1 2 3 4 5"), eval(c, "sort 4 2 5 1 3"));
        assertEquals(eval(c, "5 4 3 2 1"), eval(c, "reverse sort 4 2 5 1 3"));
        assertEquals(eval(c, "100 to 1"), eval(c, "reverse (1 to 100)"));
        // assertEquals(eval(c, "1 .. 10"), eval(c, "shuffle (1 to 10)"));
    }
}