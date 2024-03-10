package test.saka1029.util.vector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import saka1029.util.vector.Lexer;
import saka1029.util.vector.Lexer.Token;

public class TestLexer {

    @Test
    public void testDigit() {
        assertTrue(Character.isDigit('3'));
        assertTrue(Character.isDigit('３'));
        assertFalse(Character.isDigit('三'));
        assertFalse(Character.isDigit('a'));
    }

    @Test
    public void testAlphabetic() {
        assertTrue(Character.isAlphabetic('a'));
        assertTrue(Character.isAlphabetic('あ'));
        assertTrue(Character.isAlphabetic('漢'));
        assertTrue(Character.isAlphabetic('三'));
        assertTrue(Character.isAlphabetic(0x29e3d)); // 𩸽
        assertFalse(Character.isAlphabetic('3'));
        assertFalse(Character.isAlphabetic('３'));
        assertFalse(Character.isAlphabetic('．'));
    }

    static String lex(String input) {
        Lexer l = Lexer.of(input);
        StringBuilder sb = new StringBuilder();
        Token t;
        while ((t = l.read()) != null)
            sb.append(" ").append(t);
        return sb.substring(1);
    }

    @Test
    public void testRead() {
        assertEquals("( n:1 + n:234 )", lex("  ( 1 + 234 )"));
        assertEquals("n:234.30e-3", lex("234.30e-3"));
        assertEquals("+ - * / % ^", lex("+-*/%^"));
        assertEquals("i:漢字 i:𩸽２", lex(" 漢字  𩸽２  "));
    }

}
