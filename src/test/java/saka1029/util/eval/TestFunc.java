package saka1029.util.eval;

import static org.junit.Assert.assertEquals;
import java.util.Arrays;
import org.junit.Test;
import saka1029.util.cal.Context;
import saka1029.util.cal.Expression;
import saka1029.util.cal.Funcall;
import saka1029.util.cal.Number;
import saka1029.util.cal.UserFunc;
import saka1029.util.cal.Variable;

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
        c.function("+", (x, a) -> Arrays.stream(a).sum());
        c.function("-", (x, a) -> a[0] - Arrays.stream(a, 1, a.length).sum());
        c.function2("hypot", (x, a, b) -> Math.sqrt(a * a + b * b));
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
        c.function2("+", (x, a, b) -> a + b);
        c.function2("-", (x, a, b) -> a - b);
        c.function2("*", (x, a, b) -> a * b);
        c.function1("sqrt", (x, a) -> Math.sqrt(a));
        c.function("hypot", UserFunc.of(f("sqrt", f("+", f("*", v("x"), v("x")), f("*", v("y"), v("y")))), "x", "y"));
        Funcall call = f("hypot", f("+", n(1), n(2)), f("+", n(1), n(3)));
        assertEquals(5.0, call.eval(c), 0.0005);
    }
}
