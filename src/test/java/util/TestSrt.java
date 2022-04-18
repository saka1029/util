package util;

import static org.junit.Assert.*;

import java.util.regex.Matcher;

import org.junit.Test;
import util.srt.Srt;

public class TestSrt {

    @Test
    public void testMatcher() {
        Matcher m = Srt.NO_PAT.matcher("1");
        assertTrue(m.matches());
        assertEquals("1", m.group());
    }
}
