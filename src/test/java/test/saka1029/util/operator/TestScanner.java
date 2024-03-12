package test.saka1029.util.operator;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import saka1029.util.operator.Scanner;
import saka1029.util.operator.Scanner.Token;

public class TestScanner {

    String scan(String input) {
        Scanner s = Scanner.of(input);
        StringBuilder sb = new StringBuilder();
        Token t;
        while ((t = s.read()) != null)
            sb.append(" ").append(t);
        return sb.substring(1);
    }

    @Test
    public void testSpecial() {
        assertEquals("LP SP:<= RP SP:!! SP:$@", scan("   (<=) !! $@  "));
    }

    @Test
    public void testId() {
        assertEquals("LP ID:abc RP NUM:3 ID:x482 SP:= NUM:2", scan("   (abc) 3x482=2  "));
    }

    @Test
    public void testNumber() {
        assertEquals("LP NUM:123.3e-4 RP NUM:3", scan("   (123.3e-4) 3  "));
    }

}
