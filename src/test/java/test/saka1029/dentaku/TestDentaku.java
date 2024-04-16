package test.saka1029.dentaku;

import static org.junit.Assert.assertEquals;

import java.time.DateTimeException;
import java.util.List;
import java.util.function.Consumer;

import org.junit.Test;

import saka1029.util.dentaku.Context;
import saka1029.util.dentaku.Expression;
import saka1029.util.dentaku.Operators;
import saka1029.util.dentaku.Parser;
import saka1029.util.dentaku.Value;
import saka1029.util.dentaku.ValueException;

public class TestDentaku {

    static final String NL = "\n";

    static String eval(Context context, String input) {
        List<String> in = input.lines().map(String::trim).toList();
        StringBuilder result = new StringBuilder();
        Consumer<String> append = s -> result.append(s).append(NL);
        for (String line : in)
            try {
                if (line.startsWith(".solve")) {
                    line = line.replaceFirst("^\\S+\\s*", "");
                    Expression e = Parser.parse(context.operators(), line);
                    int count = Value.solve(e, context, append);
                    append.accept("number of solutions=%d".formatted(count));
                } else {
                    Expression e = Parser.parse(context.operators(), line);
                    Value value = e.eval(context);
                    if (value != Value.NaN)
                        append.accept(value.toString());
                }
            } catch (ValueException | ArithmeticException
                    | NumberFormatException | DateTimeException ex) {
                append.accept(ex.getMessage());
            }
        return result.toString();
    }

    static void test(Context c, String input, String expected) {
        assertEquals(expected, eval(c, input));
    }

    @Test
    public void testPlusMinus() {
        Operators functions = Operators.of();
        Context c = Context.of(functions);
        test(c,
            """
            + 1 2 3
            """, """
            6
            """
        );
        test(c,
            """
            - 1 2 3
            """, """
            -4
            """
        );
        test(c,
            """
            - 1 2
            """, """
            -1
            """
        );
        test(c,
            """
            - 1
            """, """
            -1
            """
        );
        test(c,
            """
            1 (-2) 3
            """, """
            1 -2 3
            """
        );
    }

    @Test
    public void testFilter() {
        Operators functions = Operators.of();
        Context c = Context.of(functions);
        test(c,
            """
            a = 1 2 3 4
            a
            a > 2 filter a
            """, """
            1 2 3 4
            3 4
            """);
    }

    @Test
    public void testReduceParens() {
        Operators functions = Operators.of();
        Context c = Context.of(functions);
        test(c,
            """
            sqrt + (11 100 111 ^ 4 / 2)
            """, """
            11221
            """);
    }

    @Test
    public void testTScore() {
        Operators functions = Operators.of();
        Context c = Context.of(functions);
        test(c,
            """
            kokugo = 55 60 70 60 65
            sansu = 25 95 40 90 60
            ave kokugo
            ave sansu
            variance kokugo
            variance sansu
            sd kokugo
            sd sansu
            standardScore kokugo round 2
            standardScore sansu round 2
            """, """
            62
            62
            26
            746
            5.099019513592785
            27.31300056749533
            36.27 46.08 65.69 46.08 55.88
            36.45 62.08 41.95 60.25 49.27
            """);
    }

    @Test
    public void testDistance() {
        Operators functions = Operators.of();
        Context c = Context.of(functions);
        test(c,
            """
            a distance b = sqrt + (a - b ^ 2)
            0 0 distance 1 1
            """, """
            1.414213562373095
            """);
        test(c,
            """
            0 0 0 distance 1 1 1
            """, """
            1.732050807568877
            """);
    }

    @Test
    public void testPi() {
        Operators functions = Operators.of();
        Context c = Context.of(functions);
        test(c,
            """
            term n = -1 ^ n / (2 * n + 1)
            Pi n = + term (0 to n) * 4
            Pi 9
            Pi 99
            Pi 999
            Pi 9999
            Pi 99999
            """, """
            3.04183961892940212
            3.131592903558552732
            3.1405926538397929396
            3.14149265359004322800
            3.141582653589793475400 
            """);
    }

    @Test
    public void testSolver() {
        Operators functions = Operators.of();
        Context c = Context.of(functions);
        test(c,
            """
            a = 1 to 10
            b = 1 to 10
            c = 1 to 10
            d = 1 to 10
            e = 1 to 10
            f = 1 to 10
            .solve * a b c d e f == + a b c d e f
            """, """
            a=1 b=1 c=1 d=1 e=2 f=6
            a=1 b=1 c=1 d=1 e=6 f=2
            a=1 b=1 c=1 d=2 e=1 f=6
            a=1 b=1 c=1 d=2 e=6 f=1
            a=1 b=1 c=1 d=6 e=1 f=2
            a=1 b=1 c=1 d=6 e=2 f=1
            a=1 b=1 c=2 d=1 e=1 f=6
            a=1 b=1 c=2 d=1 e=6 f=1
            a=1 b=1 c=2 d=6 e=1 f=1
            a=1 b=1 c=6 d=1 e=1 f=2
            a=1 b=1 c=6 d=1 e=2 f=1
            a=1 b=1 c=6 d=2 e=1 f=1
            a=1 b=2 c=1 d=1 e=1 f=6
            a=1 b=2 c=1 d=1 e=6 f=1
            a=1 b=2 c=1 d=6 e=1 f=1
            a=1 b=2 c=6 d=1 e=1 f=1
            a=1 b=6 c=1 d=1 e=1 f=2
            a=1 b=6 c=1 d=1 e=2 f=1
            a=1 b=6 c=1 d=2 e=1 f=1
            a=1 b=6 c=2 d=1 e=1 f=1
            a=2 b=1 c=1 d=1 e=1 f=6
            a=2 b=1 c=1 d=1 e=6 f=1
            a=2 b=1 c=1 d=6 e=1 f=1
            a=2 b=1 c=6 d=1 e=1 f=1
            a=2 b=6 c=1 d=1 e=1 f=1
            a=6 b=1 c=1 d=1 e=1 f=2
            a=6 b=1 c=1 d=1 e=2 f=1
            a=6 b=1 c=1 d=2 e=1 f=1
            a=6 b=1 c=2 d=1 e=1 f=1
            a=6 b=2 c=1 d=1 e=1 f=1
            number of solutions=30
            """);
    }

}
