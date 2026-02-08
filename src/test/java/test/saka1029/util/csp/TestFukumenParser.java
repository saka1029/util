package test.saka1029.util.csp;

import java.util.Map.Entry;

import org.junit.Test;

import saka1029.util.csp.FukumenParser;
import saka1029.util.csp.FukumenParser.Result;

public class TestFukumenParser {

    @Test
    public void testParser() {
        String problem = "SEND + MORE = MONEY";
        Result r = FukumenParser.parse(problem);
        for (Entry<Integer, Boolean> e : r.vars().entrySet())
            System.out.println(Character.toString(e.getKey()) + " : " + e.getValue());
        System.out.println("constriant: " + r.constraint());
    }

}
