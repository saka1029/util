package saka1029.util.eval;

import static org.junit.Assert.assertEquals;
import java.util.List;
import org.junit.Test;

public class TestParser {

    static List<String> tokens(String source) {
        return Parser.of(source).tokens();
    }

    @Test
    public void testTokens() {
        assertEquals(List.of("abc", "123"), tokens("    abc     123    "));
        assertEquals(List.of("(", "123", ")"), tokens("(123)"));
        assertEquals(List.of("123", ",", "3", "abc"), tokens("123, 3abc"));
    }

    @Test
    public void testId() {
        assertEquals(List.of("123","a","123","+","a","1","-","bb"), tokens(" 123a 123+a 1-bb"));
    }

    @Test
    public void testNumbers() {
        assertEquals(List.of("123","1.2","+2","+2.2","-3","-3.3","3e5","-3.4e6","-3.4e-66","e"),
            tokens("123 1.2 +2 +2.2 -3 -3.3 3e5 -3.4e6 -3.4e-66e"));
    }

    @Test
    public void testOperators() {
        assertEquals(List.of("123","<","e",";","(","ij","<=","0",")"),
            tokens(" 123 < e; (ij <= 0) "));
        assertEquals(List.of("+","a","-","b"), tokens("  +a -b"));
        assertEquals(List.of("++","a","-+","b"), tokens("  ++a -+b"));
    }
    
}
