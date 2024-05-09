package test.saka1029.dentaku;

import java.util.List;
import org.junit.Test;
import saka1029.util.dentaku.Tokenizer;
import saka1029.util.dentaku.ValueException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static saka1029.util.dentaku.Tokenizer.*;

public class TestTokenizer {

    static Token t(Type type, String s) {
        return new Token(type, s);
    }
    
    @Test
    public void testId() {
        assertEquals(List.of(t(Type.ID, "a12"), t(Type.SPECIAL, "+"), t(Type.ID, "_3")), Tokenizer.tokens("a12+_3"));
    }

    @Test
    public void testSelectInsert() {
        assertEquals(List.of(t(Type.SELECT, "@"), t(Type.SPECIAL, "+")), Tokenizer.tokens(" @+ "));
        // assertEquals(List.of(t(Type.INSERT, ":"), t(Type.SPECIAL, "+")), Tokenizer.tokens(" :+ "));
    }

    @Test
    public void testNumber() {
        assertEquals(List.of(t(Type.NUMBER, "12"), t(Type.SPECIAL, "*"), t(Type.NUMBER, "3.45e-3")), Tokenizer.tokens("12*3.45e-3"));
        assertEquals(List.of(t(Type.NUMBER, "12E+3")), Tokenizer.tokens("12E+3"));
        assertEquals(List.of(t(Type.NUMBER, "12E3")), Tokenizer.tokens("12E3"));
    }

    @Test
    public void testComp() {
        assertEquals(List.of(t(Type.SPECIAL, "=="), t(Type.SPECIAL, "==")), Tokenizer.tokens("===="));
        assertEquals(List.of(t(Type.SPECIAL, "!="), t(Type.SPECIAL, "==")), Tokenizer.tokens("!==="));
        // assertEquals(List.of(t(Type.SPECIAL, "<"), t(Type.INSERT, ":")), Tokenizer.tokens("<:"));
        assertEquals(List.of(t(Type.SPECIAL, "<="), t(Type.SPECIAL, "==")), Tokenizer.tokens("<==="));
        assertEquals(List.of(t(Type.SPECIAL, ">"), t(Type.SELECT, "@")), Tokenizer.tokens(">@"));
        assertEquals(List.of(t(Type.SPECIAL, ">="), t(Type.SPECIAL, "==")), Tokenizer.tokens(">==="));
        assertEquals(List.of(t(Type.SPECIAL, "~"), t(Type.SELECT, "@")), Tokenizer.tokens("~@"));
        assertEquals(List.of(t(Type.SPECIAL, "!~"), t(Type.SPECIAL, "==")), Tokenizer.tokens("!~=="));
    }

    @Test
    public void testOther() {
        List<Token> expected = List.of(
            t(Type.LP, "("),
            t(Type.LP, "("),
            t(Type.SPECIAL, "+"),
            t(Type.ASSIGN, "="),
            t(Type.SPECIAL, "%"),
            t(Type.SPECIAL, "<="),
            t(Type.SPECIAL, "-"),
            t(Type.SPECIAL, "-"),
            t(Type.CONCAT, ","),
            t(Type.RP, ")"),
            t(Type.RP, ")"));
        List<Token> actual = Tokenizer.tokens("((+=%<=--,))");
        assertEquals(expected, actual);
        assertEquals(List.of(t(Type.SPECIAL, "^"), t(Type.SPECIAL, "/")), Tokenizer.tokens("^/"));
    }

    @Test
    public void testUnknown() {
        try {
            Tokenizer.tokens("!@");
            fail();
        } catch (ValueException e) {
            assertEquals("Unknown token '!'", e.getMessage());
        }
        try {
            Tokenizer.tokens("$");
            fail();
        } catch (ValueException e) {
            assertEquals("Unknown character '$'(0x0024)", e.getMessage());
        }
        try {
            Tokenizer.tokens("12.=");
            fail();
        } catch (ValueException e) {
            assertEquals("Digit expected but '='", e.getMessage());
        }
    }
    
}
