package test.saka1029.util.dentaku;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.stream.Collectors;
import org.junit.Test;
import saka1029.util.dentaku.Lexer;
import saka1029.util.dentaku.Lexer.Token;

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

    static BigDecimal num(double value) {
        return new BigDecimal(value, MathContext.DECIMAL128);
    }
    @Test
    public void testBigDecimal() {
        assertEquals("123456", num(123456).toString());
        assertEquals("9.740826547200000282107043858559297032629610129675762450907755551910",
            num(1.23456).multiply(num(7.89012)).toString());
        assertEquals("0.3333333333333333333333333333333333",
            num(1).divide(num(3), MathContext.DECIMAL128).toString());
    }

    static String lex(String input) {
        return Lexer.of(input).tokens().stream()
            .map(t -> t.toString())
            .collect(Collectors.joining(" "));
    }

    @Test
    public void testRead() {
        assertEquals("( NUMBER:1 + NUMBER:234 )", lex("  ( 1 + 234 )"));
        assertEquals("NUMBER:234.30e-3", lex("234.30e-3"));
        assertEquals("+ - * / % ^", lex("+-*/%^"));
        assertEquals("ID:漢字 ID:𩸽２", lex(" 漢字  𩸽２  "));
    }

}
