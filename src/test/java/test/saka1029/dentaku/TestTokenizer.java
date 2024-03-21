package test.saka1029.dentaku;

import static org.junit.Assert.assertEquals;
import java.util.stream.Collectors;
import org.junit.Test;
import saka1029.util.dentaku.Tokenizer;

public class TestTokenizer {

    static String tokens(String input) {
        return Tokenizer.tokens(input).stream()
            .map(t -> t.toString())
            .collect(Collectors.joining(" "));
    }

    @Test
    public void testSpecial() {
        assertEquals("SPECIAL:<< SPECIAL:++", tokens("   << ++  "));
    }

    @Test
    public void testID() {
        assertEquals("LP:( ID:a23 RP:)", tokens("   (a23)  "));
        assertEquals("ID:漢字23 SPECIAL:==", tokens("   漢字23==  "));
    }

    @Test
    public void testNumber() {
        assertEquals("NUMBER:123 SPECIAL:+", tokens("   123+  "));
        assertEquals("NUMBER:123.45 SPECIAL:==", tokens("   123.45==  "));
        assertEquals("NUMBER:123.45e2 ID:e", tokens("   123.45e2e  "));
        assertEquals("NUMBER:123.45e-2 ID:e", tokens("   123.45e-2e  "));
    }
}
