package test.saka1029.util.vec;

import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import saka1029.util.vec.Scanner;
import saka1029.util.vec.Scanner.Token;

public class TestScanner {

    static List<String> parse(String input) {
        List<String> tokens = new ArrayList<>();
        Scanner s = Scanner.of(input);
        while (true) {
            Token t = s.read();
            if (t.type() == -1)
                break;
            String e = switch (t.type()) {
                case 'n' -> "n:" + t.string();
                case 'i' -> "i:" + t.string();
                default -> "" + (char)t.type();
            };
            tokens.add(e);
        }
        return tokens;
    }

    @Test
    public void testRead() {
        assertEquals(List.of("(", "n:123", "+", "i:KL", ")"), parse("  ( 123 + KL ) "));
    }

    @Test
    public void testOperators() {
        assertEquals(List.of("=", "+", "-", "*", "/", "%", "^"), parse("=+-*/%^"));
    }

    @Test
    public void testNumber() {
        assertEquals(List.of("-", "n:123", "n:345.5", "n:-123"), parse("  - 123 345.5 -123 "));
        assertEquals(List.of( "n:-123.456", "n:345.5e+33", "n:-123E-3"), parse("  -123.456 345.5e+33 -123E-3 "));
    }

}
