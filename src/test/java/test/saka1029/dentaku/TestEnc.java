package test.saka1029.dentaku;

import java.util.stream.Collectors;

import org.junit.Test;

public class TestEnc {

    static final String ENC = "Tb hklt colj axfiv ifcb qexq tb bufpq clo lqebo"
            + " mblmib cfopq lc xii, clo telpb pjfibp xka"
            + " tbii-ybfkd lro ltk exmmfkbpp abmbkap.";

    static int dec(int c, int n) {
        if (!Character.isAlphabetic(c))
            return c;
        char base = Character.isUpperCase(c) ? 'A' : 'a';
        return (c - base + n) % 26 + base;
    }

    static String dec(String s, int n) {
        return s.chars()
            .map(c -> dec(c, n))
            .mapToObj(i -> Character.toString((char) i))
            .collect(Collectors.joining());
    }

    @Test
    public void testDecode() {
        for (int i = 0; i < 26; ++i)
            System.out.printf("%3d %s%n", i, dec(ENC, i));
    }
}