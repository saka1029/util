package saka1029.util.eval;

import static org.junit.Assert.assertEquals;
import java.util.Arrays;
import org.junit.Test;

public class TestFunc {

    static Number n(double value) {
        return Number.of(value);
    }

    static Variable v(String name) {
        return Variable.of(name);
    }

    static Funcall f(String name, Expression... arguments) {
        return Funcall.of(name, arguments);
    }

    /**
     * hypot(x, y) = √(x² + y²)
     * hypot(1 + 2, 1 + 3)
     * -> 5.0
     */
    @Test
    public void testFunc() {
        Context c = Context.of();
        c.putFunc("+", (x, a) -> Arrays.stream(a).sum());
        c.putFunc("-", (x, a) -> a[0] - Arrays.stream(a, 1, a.length).sum());
        c.putFunc("hypot", (x, a) -> Math.sqrt(a[0] * a[0] + a[1] * a[1]));
        Funcall call = f("hypot", f("+", n(1), n(2)), f("+", n(1), n(3)));
        assertEquals(5.0, call.eval(c), 0.0005);
    }

    /**
     * hypot(x, y) = √(x² + y²)
     * hypot(1 + 2, 1 + 3)
     * -> 5.0
     */
    @Test
    public void testUserFunc() {
        Context c = Context.of();
        c.putFunc("+", (x, a) -> a[0] + a[1]);
        c.putFunc("-", (x, a) -> a[0] - a[1]);
        c.putFunc("*", (x, a) -> a[0] * a[1]);
        c.putFunc("sqrt", (x, a) -> Math.sqrt(a[0]));
        c.putFunc("hypot", UserFunc.of(f("sqrt", f("+", f("*", v("x"), v("x")), f("*", v("y"), v("y")))), "x", "y"));
        Funcall call = f("hypot", f("+", n(1), n(2)), f("+", n(1), n(3)));
        assertEquals(5.0, call.eval(c), 0.0005);
    }
}
