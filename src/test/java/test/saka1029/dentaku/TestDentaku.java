package test.saka1029.dentaku;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import saka1029.util.dentaku.Context;
import saka1029.util.dentaku.Expression;
import saka1029.util.dentaku.Parser;
import saka1029.util.dentaku.ValueException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static saka1029.util.dentaku.Value.*;

public class TestDentaku {

    BigDecimal[] eval(Context c, String input) {
        Expression e = Parser.parse(c, input);
        return e.eval(c);
    }

    @Test
    public void testVariable() {
        Context c = Context.of();
        assertArrayEquals(array("3.1415926535897932384626433832795028841971693993751058209749445923078164062862089986280348253421170679821480865132823066470938446095505822317253594081284811174502841027019385211055596446229489549303820"), eval(c, "PI"));
        assertArrayEquals(array("2.7182818284590452353602874713526624977572470936999595749669676277240766303535475945713821785251664274274663919320030599218174135966290435729003342952605956307381323286279434907632338298807531952510190"), eval(c, "E"));
        assertArrayEquals(array("0.000005"), eval(c, "EPSILON"));
    }

    @Test
    public void testUnaryAdd() {
        Context c = Context.of();
        assertArrayEquals(array("7"), eval(c, "+ (3, 4)"));
    }

    @Test
    public void testUnarySubtract() {
        Context c = Context.of();
        assertArrayEquals(array("-3"), eval(c, "- 3"));
        assertArrayEquals(array("-1"), eval(c, "- (3, 4)"));
        assertArrayEquals(array("-6"), eval(c, "- (3, 4, 5)"));
    }

    @Test
    public void testUnaryDivide() {
        Context c = Context.of();
        assertArrayEquals(array("0.5"), eval(c, "/ 2"));
        assertArrayEquals(array("0.25"), eval(c, "/ (1, 4)"));
        assertArrayEquals(array("1"), eval(c, "/ (8, 4, 2)"));
    }

    @Test
    public void testUnaryNot() {
        Context c = Context.of();
        assertArrayEquals(array("0 1"), eval(c, "not (1, 0)"));
    }

    @Test
    public void testUnaryMinMax() {
        Context c = Context.of();
        assertArrayEquals(array("1"), eval(c, "min (1, 2, 3)"));
        assertArrayEquals(array("3"), eval(c, "max (1, 2, 3)"));
    }

    @Test
    public void testUnaryFactorDivisor() {
        Context c = Context.of();
        assertArrayEquals(array("7 11 13"), eval(c, "factor 1001"));
        assertArrayEquals(array("7 11 13"), eval(c, "factor -1001"));
        assertArrayEquals(array("2 2 2 2 2 2 2 2 2 2"), eval(c, "factor 1024"));
        assertArrayEquals(array(""), eval(c, "factor 1"));
        assertArrayEquals(array("1 7 11 13 77 91 143 1001"), eval(c, "divisor 1001"));
    }

    @Test
    public void testUnaryLog() {
        Context c = Context.of();
        assertArrayEquals(array("3"), eval(c, "log (E ^ 3)"));
        assertArrayEquals(array("3"), eval(c, "log10 1000"));
        assertArrayEquals(array("2"), eval(c, "log2 4"));
    }

    @Test
    public void testUnaryTriangle() {
        Context c = Context.of();
        assertArrayEquals(array("0.000 1.000 0.000 -1.000"), eval(c, "sin radian ((0 to 3) * 90) round 3"));
        assertArrayEquals(array("1.000 0.000 -1.000 0.000"), eval(c, "cos radian ((0 to 3) * 90) round 3"));
        assertArrayEquals(array("-1.732 -1.000 0.000 1.000 1.732"), eval(c, "tan radian (-60, -45, 0, 45, 60) round 3"));
        assertArrayEquals(array("180.000 90.000 60.000 45.000"), eval(c, "degree (PI / (1, 2, 3, 4)) round 3"));
        assertArrayEquals(array("0.000 45.000"), eval(c, "degree asin (0, / sqrt 2) round 3"));
        assertArrayEquals(array("90.000 45.000"), eval(c, "degree acos (0, / sqrt 2) round 3"));
        assertArrayEquals(array("0.000 45.000"), eval(c, "degree atan (0, 1) round 3"));
    }

    @Test
    public void testUnaryCount() {
        Context c = Context.of();
        assertArrayEquals(array("3"), eval(c, "count (-3, -4, 5)"));
        assertArrayEquals(array("25"), eval(c, "count @prime? (1 to 100)"));
    }

    @Test
    public void testUnaryIntTrunc() {
        Context c = Context.of();
        assertArrayEquals(array("-3 -5 3 5"), eval(c, "int (-3.4, -4.5, 3.4, 4.5)"));
        assertArrayEquals(array("-3 -4 3 4"), eval(c, "trunc (-3.4, -4.5, 3.4, 4.5)"));
        assertArrayEquals(array("-3 -4 4 5"), eval(c, "ceiling (-3.4, -4.5, 3.4, 4.5)"));
        assertArrayEquals(array("-4 -5 3 4"), eval(c, "floor (-3.4, -4.5, 3.4, 4.5)"));
    }

    @Test
    public void testUnaryEvenOdd() {
        Context c = Context.of();
        assertArrayEquals(array("0 1 0 1"), eval(c, "even? (-3, -4, 3, 4)"));
        assertArrayEquals(array("1 0 1 0"), eval(c, "odd? (-3, -4, 3, 4)"));
    }

    @Test
    public void testUnaryFactGammaFib() {
        Context c = Context.of();
        assertArrayEquals(array("1 1 1 2 6 24 120"), eval(c, "fact (-3, 0, 1, 2, 3, 4, 5)"));
        assertArrayEquals(array("1 1 2 6 24 120"), eval(c, "gamma (1, 2, 3, 4, 5, 6)"));
        assertArrayEquals(array("-3 0 1 1 2 3 5 8"), eval(c, "fib (-3, 0, 1, 2, 3, 4, 5, 6)"));
    }

    @Test
    public void testUnaryAbs() {
        Context c = Context.of();
        assertArrayEquals(array("3 4 5 0"), eval(c, "abs (-3, -4, 5, 0)"));
    }

    @Test
    public void testUnarySign() {
        Context c = Context.of();
        assertArrayEquals(array("-1 -1 1 0"), eval(c, "sign (-3, -4, 5, 0)"));
    }

    @Test
    public void testUnaryMinus() {
        Context c = Context.of();
        assertArrayEquals(array("3 4 -5 0"), eval(c, "minus (-3, -4, 5, 0)"));
    }

    @Test
    public void testUnarySquareCube() {
        Context c = Context.of();
        assertArrayEquals(array("0 1 16 169"), eval(c, "square (0, 1, 4, 13)"));
        assertArrayEquals(array("0 1 64 2197"), eval(c, "cube (0, 1, 4, 13)"));
    }

    @Test
    public void testUnaryReciprocal() {
        Context c = Context.of();
        assertArrayEquals(array("-0.5 -0.25 0.5 0.25 2"), eval(c, "reciprocal (-2, -4, 2, 4, 0.5)"));
    }

    @Test
    public void testUnarySqrt() {
        Context c = Context.of();
        assertArrayEquals(array("2 5 0"), eval(c, "sqrt (4, 25, 0)"));
    }

    @Test
    public void testUnaryPrimeP() {
        Context c = Context.of();
        assertArrayEquals(array("0 1 1 0 1 0 1 0 0 0"), eval(c, "prime? (1 to 10)"));
        assertArrayEquals(array("2 3 5 7"), eval(c, "@prime? (1 to 10)"));
    }

    @Test
    public void testPrecisionScale() {
        Context c = Context.of();
        assertArrayEquals(array("2 4 6"), eval(c, "precision (1.1, 22.22, 333.333)"));
        assertArrayEquals(array("1 2 3"), eval(c, "scale (1.1, 22.22, 333.333)"));
        assertArrayEquals(array("1 2 3"), eval(c, "int_precision (1.1, 22.22, 333.333)"));
    }

    @Test
    public void testBinaryConcat() {
        Context c = Context.of();
        assertArrayEquals(array("3 5 -4"), eval(c, "1 + 2, 2 + 3, -4"));
    }

    @Test
    public void testBinaryAdd() {
        Context c = Context.of();
        assertArrayEquals(array("3"), eval(c, "1 + 2"));
        assertArrayEquals(array("4 6"), eval(c, "(1, 2) + (3, 4)"));
        assertArrayEquals(array("5 6"), eval(c, "2 + (3, 4)"));
        assertArrayEquals(array("4 5"), eval(c, "(1, 2) + 3"));
    }

    @Test
    public void testBinarySubtract() {
        Context c = Context.of();
        assertArrayEquals(array("-6"), eval(c, "- (340, 337, 3 ^ 2)"));
    }

    @Test
    public void testBinaryMod() {
        Context c = Context.of();
        assertArrayEquals(array("1 1 3"), eval(c, "(7, 7, 7) % (2, 3, 4)"));
    }

    @Test
    public void testBinaryCompare() {
        Context c = Context.of();
        assertArrayEquals(array("0 1 0"), eval(c, "(-1, 0, 1) = 0"));
        assertArrayEquals(array("1 0 1"), eval(c, "(-1, 0, 1) != 0"));
        assertArrayEquals(array("1 0 0"), eval(c, "(-1, 0, 1) < 0"));
        assertArrayEquals(array("1 1 0"), eval(c, "(-1, 0, 1) <= 0"));
        assertArrayEquals(array("0 0 1"), eval(c, "(-1, 0, 1) > 0"));
        assertArrayEquals(array("0 1 1"), eval(c, "(-1, 0, 1) >= 0"));
        // assertArrayEquals(array("0"), eval(c, "(-1, 0, 1) @ = 0"));
        // assertArrayEquals(array("-1 1"), eval(c, "(-1, 0, 1) @ != 0"));
        // assertArrayEquals(array("-1"), eval(c, "(-1, 0, 1) @ < 0"));
        // assertArrayEquals(array("-1 0"), eval(c, "(-1, 0, 1) @ <= 0"));
        // assertArrayEquals(array("1"), eval(c, "(-1, 0, 1) @ > 0"));
        // assertArrayEquals(array("0 1"), eval(c, "(-1, 0, 1) @ >= 0"));
        assertArrayEquals(array("1 1 1"), eval(c, "(-1, 0, 1) ~ (-0.999999, -0.000001, 0.999999)"));
        assertArrayEquals(array("0 0 0"), eval(c, "(-1, 0, 1) ~ (-0.999, -0.001, 0.999)"));
        assertArrayEquals(array("0 0 0"), eval(c, "(-1, 0, 1) !~ (-0.999999, -0.000001, 0.999999)"));
        assertArrayEquals(array("1 1 1"), eval(c, "(-1, 0, 1) !~ (-0.999, -0.001, 0.999)"));
    }

    @Test
    public void testLogical() {
        Context c = Context.of();
        assertArrayEquals(array("1 0 0 0"), eval(c, "(1, 1, 0, 0) and (1, 0, 1, 0)"));
        assertArrayEquals(array("1 1 1 0"), eval(c, "(1, 1, 0, 0) or (1, 0, 1, 0)"));
        assertArrayEquals(array("0 1 1 0"), eval(c, "(1, 1, 0, 0) xor (1, 0, 1, 0)"));
        assertArrayEquals(array("1 0 1 1"), eval(c, "(1, 1, 0, 0) imply (1, 0, 1, 0)"));
    }

    @Test
    public void testMinMax() {
        Context c = Context.of();
        assertArrayEquals(array("1 2 1"), eval(c, "(1, 2, 3) min (3, 2, 1)"));
        assertArrayEquals(array("1 2 2"), eval(c, "(1, 2, 3) min 2"));
        assertArrayEquals(array("3 2 3"), eval(c, "(1, 2, 3) max (3, 2, 1)"));
        assertArrayEquals(array("2 2 3"), eval(c, "(1, 2, 3) max 2"));
    }

    @Test
    public void testDate() {
        Context c = Context.of();
        assertArrayEquals(array("-4447"), eval(c, "days 19571029"));
        assertArrayEquals(array("19571029"), eval(c, "date -4447"));
        assertArrayEquals(array("2"), eval(c, "week 19571029"));
    }

    @Test
    public void testTime() {
        Context c = Context.of();
        assertArrayEquals(array("3661"), eval(c, "seconds 010101"));
        assertArrayEquals(array("010101"), eval(c, "time 3661"));
    }

    @Test
    public void testBinaryCat() {
        Context c = Context.of();
        assertArrayEquals(array("1 2"), eval(c, "1, 2"));
        assertArrayEquals(array("10 11"), eval(c, "7 + (3, 4)"));
        assertArrayEquals(array("10 11"), eval(c, "7 + (3, 4)"));
        assertArrayEquals(array("10 11"), eval(c, "(3, 4) + 7"));
        assertArrayEquals(array("-3 4"), eval(c, "-3, 4"));
        assertArrayEquals(array("-3 -4"), eval(c, "-3, -4"));
    }

    @Test
    public void testBinaryTo() {
        Context c = Context.of();
        assertArrayEquals(array("1 2 3"), eval(c, "1 to 3"));
        assertArrayEquals(array("3 2 1"), eval(c, "3 to 1"));
        assertArrayEquals(array("-3 -2 -1 0"), eval(c, "-3 to 0"));
    }

    @Test
    public void testBinaryPow() {
        Context c = Context.of();
        assertArrayEquals(array("8"), eval(c, "2 ^ 3"));
        assertArrayEquals(array("0.5"), eval(c, "2 ^ -1"));
        assertArrayEquals(array("3"), eval(c, "9 ^ 0.5"));
        assertArrayEquals(array("0 1 4 9"), eval(c, "(0 to 3) ^ 2"));
    }

    @Test
    public void testBinaryLog() {
        Context c = Context.of();
        assertArrayEquals(array("1"), eval(c, "(E log 2) ~ log 2"));
        assertArrayEquals(array("1"), eval(c, "(10 log 2) ~ log10 2"));
    }

    @Test
    public void testBinaryGcdLcm() {
        Context c = Context.of();
        assertArrayEquals(array("11"), eval(c, "33 gcd 1001"));
        assertArrayEquals(array("3003"), eval(c, "33 lcm 1001"));
    }

    @Test
    public void testBinaryPermutationCombination() {
        Context c = Context.of();
        assertArrayEquals(array("1 3 6 6"), eval(c, "3 P (0, 1, 2, 3)"));
        assertArrayEquals(array("1 3 3 1"), eval(c, "3 C (0, 1, 2, 3)"));
    }

    @Test
    public void testBinaryBaseDecimal() {
        Context c = Context.of();
        assertArrayEquals(array("1 0 1"), eval(c, "5 base 2"));
        assertArrayEquals(array("15 15 15 15"), eval(c, "65535 base 16"));
        assertArrayEquals(array("1 0 2 9"), eval(c, "1029 base 10"));
        assertArrayEquals(array("10 2 9"), eval(c, "1029 base (10, 10)"));
        assertArrayEquals(array("1957 10 29"), eval(c, "19571029 base (100, 100)"));
        assertArrayEquals(array("19 57 10 29"), eval(c, "19571029 base 100"));
        assertArrayEquals(array("65535"), eval(c, "(15, 15, 15, 15) decimal 16"));
        assertArrayEquals(array("65535"), eval(c, "(15, 15, 15, 15) decimal (16, 16, 16, 16)"));
        assertArrayEquals(array("65535"), eval(c, "(15, 15, 15, 15) decimal (16, 16, 16)"));
        assertArrayEquals(array("1501"), eval(c, "(1, 1, 1) decimal (24, 60)")); // 1日1時間1分は何分
        assertArrayEquals(array("90061"), eval(c, "(1, 1, 1, 1) decimal (24, 60, 60)")); // 1日1時間1分1秒は何秒
    }

    @Test
    public void testDefineVariable() {
        Context c = Context.of();
        assertTrue(NaN == eval(c, "a : 1, 2, 3"));
        assertTrue(NaN == eval(c, "b : + a"));
        assertArrayEquals(array("1 2 3"), eval(c, "a"));
        assertArrayEquals(array("6"), eval(c, "b"));
    }

    @Test
    public void testDefineUnary() {
        Context c = Context.of();
        assertTrue(NaN == eval(c, "sum a : + a"));
        assertArrayEquals(array("6"), eval(c, "sum (1, 2, 3)"));
        assertTrue(NaN == eval(c, "x a : + a + b"));
        assertTrue(NaN == eval(c, "b : 8"));
        assertArrayEquals(array("14"), eval(c, "x (1, 2, 3)"));
        assertTrue(NaN == eval(c, "b : 5"));
        assertArrayEquals(array("11"), eval(c, "x (1, 2, 3)"));
    }

    @Test
    public void testDefineUnarySelect() {
        Context c = Context.of();
        assertTrue(NaN == eval(c, "is_even a : a % 2 = 0"));
        assertArrayEquals(array("0 1 0 1"), eval(c, "is_even (1, 2, 3, 4)"));
        assertArrayEquals(array("2 4"), eval(c, "@ is_even (1, 2, 3, 4)"));
    }

    @Test
    public void testDefineBinary() {
        Context c = Context.of();
        assertTrue(NaN == eval(c, "a hypot b : sqrt + ((a - b) ^ 2)"));
        assertArrayEquals(eval(c, "sqrt 2"), eval(c, "(0, 0) hypot (1, 1)"));
        assertArrayEquals(eval(c, "sqrt 3"), eval(c, "(0, 0, 0) hypot (1, 1, 1)"));
    }

    @Test
    public void testSolve() {
        Context c = Context.of();
        assertTrue(NaN == eval(c, "x : -3 to 3"));
        assertTrue(NaN == eval(c, "y : 0 to 5"));
        assertTrue(NaN == eval(c, "z : 1"));
        List<String> vars = new ArrayList<>();
        assertEquals(5, c.solve(Parser.parse(c, "x + y = z + 3"), s -> vars.add(s)));
        assertEquals("x=-1 y=5 z=1", vars.get(0));
        assertEquals("x=0 y=4 z=1", vars.get(1));
        assertEquals("x=1 y=3 z=1", vars.get(2));
        assertEquals("x=2 y=2 z=1", vars.get(3));
        assertEquals("x=3 y=1 z=1", vars.get(4));
    }

    @Test
    public void testSolve2() {
        Context c = Context.of();
        assertTrue(NaN == eval(c, "a : 1 to 10"));
        assertTrue(NaN == eval(c, "b : 1 to 10"));
        assertTrue(NaN == eval(c, "c : 1 to 10"));
        assertTrue(NaN == eval(c, "d : 1 to 10"));
        assertTrue(NaN == eval(c, "e : 1 to 10"));
        assertTrue(NaN == eval(c, "f : 1 to 10"));
        List<String> vars = new ArrayList<>();
        assertEquals(30, c.solve(Parser.parse(c, "a * b * c * d * e * f = a + b + c + d + e + f"), s -> vars.add(s)));
        assertEquals("a=1 b=1 c=1 d=1 e=2 f=6", vars.get(0));
    }

    @Test
    public void testError() {
        Context c = Context.of();
        try {
            eval(c, "@ + (1, 2)");
            fail();
        } catch (ValueException ex) {
            assertEquals("Cannot select", ex.getMessage());
        }
        // try {
        //     eval(c, "1 @ to 2");
        //     fail();
        // } catch (ValueException ex) {
        //     assertEquals("Cannot select", ex.getMessage());
        // }
    }
}

