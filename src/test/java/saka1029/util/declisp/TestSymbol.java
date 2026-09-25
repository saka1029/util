package saka1029.util.declisp;

import static org.junit.Assert.*;
import static saka1029.util.declisp.Common.*;

import org.junit.Test;

public class TestSymbol {

    @Test 
    public void testSym() {
        assertEquals("name", sym(sym("name")));
    }
}
