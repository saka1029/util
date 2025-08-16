package test.saka1029.util.polynomial;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import saka1029.util.polynomial.Exponent;
import saka1029.util.polynomial.Expression;
import saka1029.util.polynomial.Factor;
import saka1029.util.polynomial.Term;
import saka1029.util.polynomial.Variable;

public class TestPolynomial {

    Term term(Factor... factors) {
        return Term.of(factors);
    }

    Factor factor(int coeficient, Exponent... exponents) {
        return Factor.of(coeficient, exponents);
    }

    Factor factor(Exponent... exponents) {
        return Factor.of(exponents);
    }

    Exponent exp(String name) {
        return Exponent.of(Variable.of(name));
    }

    Exponent exp(String name, int pow) {
        return Exponent.of(Variable.of(name), pow);
    }

    @Test
    public void testToString() {
        Expression e = term(factor(1, exp("x", 2)), factor(-2, exp("x"), exp("y")), factor(1, exp("y", 2)));
        assertEquals("x^2-2*x*y+y^2", e.toString());
    }

    @Test
    public void testFactorToString() {
        Expression e = term(factor(3, exp("x", 2), exp("x"), exp("x", 3)));
        assertEquals("3*x^6", e.toString());
    }

}
