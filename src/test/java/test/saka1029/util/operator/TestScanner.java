package test.saka1029.util.operator;

import static org.junit.Assert.assertEquals;
import java.util.List;
import org.junit.Test;
import saka1029.util.operator.Scanner;
import saka1029.util.operator.Scanner.Token;
import saka1029.util.operator.Scanner.Type;

public class TestScanner {

    @Test
    public void testSpecial() {
        assertEquals(List.of(Token.LP, new Token(Type.SPECIAL, "<="), Token.RP,
            new Token(Type.SPECIAL, "!!"), new Token(Type.SPECIAL, "$@")),
            Scanner.of("   (<=) !! $@  ").tokens());
    }

    @Test
    public void testId() {
        assertEquals(List.of(Token.LP, new Token(Type.ID, "abc"), Token.RP,
            new Token(Type.NUM, "3"), new Token(Type.ID, "x482"),
            new Token(Type.SPECIAL, "="), new Token(Type.NUM, "2")),
            Scanner.of("   (abc) 3x482=2  ").tokens());
    }

    @Test
    public void testNumber() {
        assertEquals(List.of(Token.LP, new Token(Type.NUM, "123.3e-4"), Token.RP,
            new Token(Type.NUM, "3")),
            Scanner.of("(123.3e-4) 3  ").tokens());
    }

}
