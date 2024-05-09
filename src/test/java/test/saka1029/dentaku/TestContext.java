package test.saka1029.dentaku;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

import saka1029.util.dentaku.Context;

public class TestContext {

    @Test
    public void testVariables() {
        Context c = Context.of();
        assertEquals(Set.of("PI", "E", "EPSILON"), c.variables().map(s -> s.t).collect(Collectors.toSet()));
    }

    @Test
    public void testUnarys() {
        Context c = Context.of();
        Set<String> set = c.unarys().map(s -> s.t).collect(Collectors.toSet());
        assertTrue(set.contains("+"));
        assertTrue(set.contains("count"));
    }

    @Test
    public void testBinarys() {
        Context c = Context.of();
        Set<String> set = c.binarys().map(s -> s.t).collect(Collectors.toSet());
        assertTrue(set.contains("+"));
        assertTrue(set.contains("P"));
    }

}
