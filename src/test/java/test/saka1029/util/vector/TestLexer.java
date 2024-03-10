package test.saka1029.util.vector;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

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
        assertFalse(Character.isAlphabetic('3'));
        assertFalse(Character.isAlphabetic('３'));
        assertFalse(Character.isAlphabetic('．'));
    }

}
