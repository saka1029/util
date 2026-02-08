package test.saka1029.util.csp;

import org.junit.Test;

import saka1029.util.csp.FukumenParser;
import saka1029.util.csp.Problem;

public class TestFukumenParser {

    @Test
    public void testParser() {
        String question = "SEND + MORE = MONEY";
        Problem problem = FukumenParser.parse(question);
        System.out.println(problem);
    }

}
