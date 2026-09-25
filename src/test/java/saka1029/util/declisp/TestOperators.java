package saka1029.util.declisp;

import static org.junit.Assert.assertArrayEquals;
import static saka1029.util.declisp.Common.*;
import static saka1029.util.declisp.Operators.*;

import org.junit.Test;

public class TestOperators {

    @Test 
    public void testPolynomialAdd() {
        // (x²+2x+1)+(x+1) = x²+3x+2
        Expr[] a = {dec(1), dec(2), dec(1)};
        Expr[] b = {dec(1), dec(1)};
        Expr[] c = {dec(1), dec(3), dec(2)};
        assertArrayEquals(c, POLYNOMIAL_ADD.apply(a, b));
        // ()+(x+1) = x+1
        Expr[] d = {};
        Expr[] e = {dec(1), dec(1)};
        Expr[] f = {dec(1), dec(1)};
        assertArrayEquals(f, POLYNOMIAL_ADD.apply(d, e));
    }

    @Test 
    public void testPolynomialSubtract() {
        Expr[] a = {dec(1), dec(2), dec(1)};
        Expr[] b = {dec(1), dec(1)};
        Expr[] c = {dec(1), dec(1), dec(0)};
        assertArrayEquals(c, POLYNOMIAL_SUBTRACT.apply(a, b));
        Expr[] d = POLYNOMIAL_ADD_UNIT;
        Expr[] e = {dec(1), dec(1)};
        Expr[] f = {dec(-1), dec(-1)};
        assertArrayEquals(f, POLYNOMIAL_SUBTRACT.apply(d, e));
        Expr[] g = {dec(1), dec(0)};
        Expr[] h = {dec(1), dec(1)};
        Expr[] i = {dec(-1)};
        assertArrayEquals(i, POLYNOMIAL_SUBTRACT.apply(g, h));
    }

    @Test 
    public void testPolynomialMultiply() {
        // (x²+2x+1)(x+1) = x³+3x²+3x+1
        Expr[] a = {dec(1), dec(2), dec(1)};
        Expr[] b = {dec(1), dec(1)};
        Expr[] c = {dec(1), dec(3), dec(3), dec(1)};
        assertArrayEquals(c, POLYNOMIAL_MULTIPLY.apply(a, b));
        // 5(x+1) = 5x+5
        Expr[] d = {dec(5)};
        Expr[] e = {dec(1), dec(1)};
        Expr[] f = {dec(5), dec(5)};
        assertArrayEquals(f, POLYNOMIAL_MULTIPLY.apply(d, e));
        // ()(x+1) = ()
        Expr[] g = {};
        Expr[] h = {dec(1), dec(1)};
        Expr[] i = {dec(0)};
        assertArrayEquals(i, POLYNOMIAL_MULTIPLY.apply(g, h));
        // (1)(x²+2x+3) = x²+2x+3
        Expr[] j = POLYNOMIAL_MULTIPLY_UNIT;
        Expr[] k = {dec(1), dec(2), dec(3)};
        Expr[] l = {dec(1), dec(2), dec(3)};
        assertArrayEquals(l, POLYNOMIAL_MULTIPLY.apply(j, k));
    }

    @Test 
    public void testPolyDivide() {
        // x³+3x²+3x+1 / (x+1) = (x²+2x+1)
        Expr[] a = {dec(1), dec(3), dec(3), dec(1)};
        Expr[] b = {dec(1), dec(1)};
        Expr[] c = {dec(1), dec(2), dec(1)};
        Expr[] zero = {dec(0)};
        Expr[][] r = polyDivide(a, b);
        assertArrayEquals(c, r[0]); // 商
        assertArrayEquals(zero, r[1]); // 余り
        // -2x⁴ / (x²-1) = (-2x²-1) 余り -2
        Expr[] d = {dec(-2), dec(0), dec(0), dec(0), dec(0)};
        Expr[] e = {dec(1), dec(0), dec(-1)};
        Expr[] f = {dec(-2), dec(0), dec(-2)};
        Expr[] g = {dec(-2)};
        Expr[][] s = polyDivide(d, e);
        assertArrayEquals(f, s[0]); // 商
        assertArrayEquals(g, s[1]); // 余り
    }
}
