package test.saka1029.dentaku;

import static org.junit.Assert.assertEquals;
import java.util.List;
import org.junit.Test;
import saka1029.util.dentaku.Lexer;
import saka1029.util.dentaku.Lexer.Token;
import saka1029.util.dentaku.Lexer.Type;

public class TestLexer {
    
    @Test
    public void testTokens() {
        assertEquals(List.of(new Token(Type.OTHER, "+")), Lexer.tokens("  +"));
    }
}
