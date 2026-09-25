package saka1029.util.declisp;

import static saka1029.util.declisp.Common.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.function.BinaryOperator;

public class Operators {

    private Operators() {}

    public static Expr[] removeLeadingZeros(Expr[] d) {
        int length = d.length;
        int start = 0;
        while (start < length && dec(d[start]).compareTo(BigDecimal.ZERO) == 0)
            ++start;
        return start == 0 ? d
            : start == length ? new Expr[] {dec(0)}
            : Arrays.copyOfRange(d, start, length);
    }

    public static BinaryOperator<Expr[]> polynomialAddOrSub(boolean add) {
        return (a, b) -> {
            int al = a.length, bl = b.length, cl = Math.max(al, bl);
            Expr[] c = new Expr[cl];
            for (int i = al - 1, j = bl - 1, k = cl - 1; k >= 0; --i, --j, --k) {
                BigDecimal d = BigDecimal.ZERO;
                if (i >= 0)
                    d = d.add(dec(a[i]), MC);
                if (j >= 0)
                    d = add ? d.add(dec(b[j])) : d.subtract(dec(b[j]));
                c[k] = dec(d);
            }
            return removeLeadingZeros(c);
        };
    }
    public static Expr[] POLYNOMIAL_ADD_UNIT = new Expr[] {dec(BigDecimal.ZERO)};
    public static Expr[] POLYNOMIAL_MULTIPLY_UNIT = new Expr[] {dec(BigDecimal.ONE)};
    public static BinaryOperator<Expr[]> POLYNOMIAL_ADD = polynomialAddOrSub(true);
    public static BinaryOperator<Expr[]> POLYNOMIAL_SUBTRACT = polynomialAddOrSub(false);
    public static BinaryOperator<Expr[]> POLYNOMIAL_MULTIPLY = (a, b) -> {
        int al = a.length, bl = b.length, cl = al + bl - 1;
        Expr[] c = new Expr[cl];
        Arrays.fill(c, dec(BigDecimal.ZERO));
        for (int i = al - 1; i >= 0; --i)
            for (int j = bl - 1; j >= 0; --j)
                c[i + j] = dec(dec(c[i + j]).add(dec(a[i]).multiply(dec(b[j]))));
        return removeLeadingZeros(c);
    };
    public static BinaryOperator<Expr[]> POLYNOMIAL_DIVIDE = (a, b) -> polyDivide(a, b)[0];
    public static BinaryOperator<Expr[]> POLYNOMIAL_MODULO = (a, b) -> polyDivide(a, b)[1];
    /**
     * 係数は最高次のものから降順に指定します。
     * ex) x^3 - 2 -> (1, 0, 0, -2)
     * @param left 割られる1変数多項式の係数を指定します。
     * @param right 割る1変数多項式の係数を指定します。
     * @return 商と余りを返します。(new Expr[][] {商, 余り})
     */
    public static Expr[][] polyDivide(Expr[] left, Expr[] right) {
        int ll = left.length, rl = right.length;
        if (ll < rl)
            return new Expr[][] {POLYNOMIAL_ADD_UNIT, left};
        int max = ll - rl + 1;
        Expr[] amari = left.clone();
        Expr[] syo = new Expr[max];
        for (int i = 0; i < max; ++i) {
            BigDecimal d = dec(amari[i]).divide(dec(right[0]), MC);
            syo[i] = dec(d);
            for (int j = 0; j < rl; ++j)
                amari[i + j] = dec(dec(amari[i + j]).subtract(dec(right[j]).multiply(d, MC), MC));
        }
        return new Expr[][] {syo, removeLeadingZeros(amari)};
    }

    // /**
    //  * 係数は最高次のものから降順に指定します。
    //  * ex) x^3 - 2 -> (1, 0, 0, -2)
    //  * @param left 割られる1変数多項式の係数を指定します。
    //  * @param right 割る1変数多項式の係数を指定します。
    //  * @return 商と余りを返します。(new BigDecimal[][] {商, 余り})
    //  */
    // public static BigDecimal[][] polyDivide(BigDecimal[] left, BigDecimal[] right) {
    //     int ll = left.length, rl = right.length;
    //     if (ll < rl)
    //         return new BigDecimal[][] {decs(0), left};
    //     int max = ll - rl + 1;
    //     BigDecimal[] amari = left.clone();
    //     BigDecimal[] syo = new BigDecimal[max];
    //     for (int i = 0; i < max; ++i) {
    //         BigDecimal d = amari[i].divide(right[0], MATH_CONTEXT);
    //         syo[i] = d;
    //         for (int j = 0; j < rl; ++j)
    //             amari[i + j] = amari[i + j].subtract(right[j].multiply(d, MATH_CONTEXT), MATH_CONTEXT);
    //     }
    //     return new BigDecimal[][] {syo, removeLeadingZeros(amari)};
    // }
    //  */

}
