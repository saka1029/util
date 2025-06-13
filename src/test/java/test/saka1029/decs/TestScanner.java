package test.saka1029.decs;

import org.junit.Test;
import saka1029.util.decs.Scanner;
import static org.junit.Assert.assertEquals;
import static saka1029.util.decs.Scanner.*;
import static saka1029.util.decs.Scanner.TokenType.*;
import java.util.List;

public class TestScanner {

    Token t(TokenType t, String s) {
        return new Token(t, s);
    }

    @Test
    public void testScanner() {
        Scanner s = new Scanner();
        assertEquals(List.of(t(PLUS, "+"), t(NUM, "123.456e-2"), t(NE, "!=")), s.scan("  +  123.456e-2 !="));
        assertEquals(List.of(t(DOTID, ".exit")), s.scan("  .exit  "));
    }

}
