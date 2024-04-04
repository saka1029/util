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
        assertEquals(List.of(
            new Token(Type.OTHER, "("),
            new Token(Type.OTHER, "+"),
            new Token(Type.OTHER, ")")
        ), Lexer.tokens("(+)"));
        assertEquals(List.of(new Token(Type.NUMBER, "-123.45e2")), Lexer.tokens("  -123.45e2"));
        assertEquals(List.of(new Token(Type.OTHER, "-")), Lexer.tokens("  -"));
        assertEquals(List.of(
            new Token(Type.ID, "aB2"),
            new Token(Type.OTHER, "+")
        ), Lexer.tokens("  aB2+ "));
        assertEquals(List.of(
            new Token(Type.OTHER, "<"),
            new Token(Type.OTHER, "-")
        ), Lexer.tokens("  <-"));
        assertEquals(List.of(
            new Token(Type.OTHER, "<"),
            new Token(Type.NUMBER, "-2")
        ), Lexer.tokens("  <-2"));
        assertEquals(List.of(
            new Token(Type.OTHER, "-"),
            new Token(Type.OTHER, "-"),
            new Token(Type.ID, "a")
        ), Lexer.tokens("  --a"));
        assertEquals(List.of(
            new Token(Type.OTHER, "<="),
            new Token(Type.OTHER, ">")
        ), Lexer.tokens("  <=>"));
        assertEquals(List.of(
            new Token(Type.OTHER, ">="),
            new Token(Type.OTHER, "<")
        ), Lexer.tokens("  >=<"));
        assertEquals(List.of(
            new Token(Type.OTHER, "="),
            new Token(Type.OTHER, "<=")
        ), Lexer.tokens("  =<="));
    }
}
