package test.saka1029.util.csp;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.Character.UnicodeBlock;

import org.junit.Test;

public class TestHiragana {

    static boolean isHiragana(int ch) {
        return UnicodeBlock.of(ch) == UnicodeBlock.HIRAGANA;
    }

    static boolean isKatakana(int ch) {
        return UnicodeBlock.of(ch) == UnicodeBlock.KATAKANA;
    }

    static boolean isKanji(int ch) {
        UnicodeBlock ub = UnicodeBlock.of(ch);
        return ub == UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
            || ub == UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
            || ub == UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
            || ub == UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_C
            || ub == UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_D
            || ub == UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_E
            || ub == UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_F
            || ub == UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_G
            || ub == UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_H
            ;
    }

    @Test
    public void testIsHiragana() {
        assertTrue(isHiragana('あ'));
        assertTrue(isHiragana('ぁ'));
        assertFalse(isHiragana('ア'));
        assertFalse(isHiragana('ァ'));
        assertFalse(isHiragana('ｱ'));
        assertFalse(isHiragana('亜'));
        assertFalse(isHiragana("𩸽".codePoints().findFirst().getAsInt()));
    }

    @Test
    public void testIsKatakana() {
        assertFalse(isKatakana('あ'));
        assertFalse(isKatakana('ぁ'));
        assertTrue(isKatakana('ア'));
        assertTrue(isKatakana('ァ'));
        assertFalse(isKatakana('ｱ'));
        assertFalse(isKatakana('亜'));
        assertFalse(isKatakana("𩸽".codePoints().findFirst().getAsInt()));
    }

    @Test
    public void testIsKanji() {
        assertFalse(isKanji('あ'));
        assertFalse(isKanji('ぁ'));
        assertFalse(isKanji('ア'));
        assertFalse(isKanji('ァ'));
        assertFalse(isKanji('ｱ'));
        assertTrue(isKanji('亜'));
        assertTrue(isKanji("𩸽".codePoints().findFirst().getAsInt()));
    }


}
