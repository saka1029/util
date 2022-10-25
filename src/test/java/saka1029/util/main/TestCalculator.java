package saka1029.util.main;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import org.junit.jupiter.api.Test;

import saka1029.util.calculator.EvaluationException;
import saka1029.util.calculator.ParseException;

class TestCalculator {
    
    @Test
    public void testIsVariableName() {
        assertTrue(Calculator.isVariableName("x"));
        assertTrue(Calculator.isVariableName("x12_y"));
        assertFalse(Calculator.isVariableName("12_y"));
        assertFalse(Calculator.isVariableName("x@y"));
    }

    @Test
    public void testGetPut() throws ParseException, EvaluationException {
        Calculator c = new Calculator();
        c.put("x", "2 * 3");
        assertEquals("2 * 3", c.get("x").toString());
        assertEquals(6, c.eval("x"));
    }

    @Test
    public void testEval() throws EvaluationException, ParseException {
        Calculator c = new Calculator();
        assertEquals(7.0, c.eval("1 + 2 * 3"));
    }

    /**
     * @param input 改行コードは"%n"とする。
     * @param expectedOutput 改行コードは"%n"とする。
     * @throws IOException
     */
    static void testRun(String input, String expectedOutput) throws IOException {
        input = input.formatted();
        expectedOutput = expectedOutput.formatted();
        Calculator calc = new Calculator();
        Reader reader = new StringReader(input);
        StringWriter writer = new StringWriter();
        calc.run(reader, writer);
        assertEquals(expectedOutput, writer.toString());
    }

    @Test
    public void testRun() throws IOException {
        testRun("1 + 2 * 3%n", "7.0%n");
        testRun("(1 + 2) * 3%n", "9.0%n");
    }

    @Test
    public void testRunComment() throws IOException {
        testRun("1 + 2 * 3 # inline comment%n", "7.0%n");
        testRun("   # line comment%n(1 + 2) * 3%n", "9.0%n");
    }
    
    @Test
    public void testRunAssignment() throws IOException {
        testRun("x = 1 + 2 * 3%nx%n" , "7.0%n");
        testRun("x = 1 + 2 * 3%"
            + "ny = x + 1%n"
            + "f = y ^ 2 + 1%n"
            + "f%n" // ((1 + 2 * 3) + 1) ^ 2 + 1 = 65
            + "x = 3%n"
            + "f%n",   // (3 + 1) ^ 2 + 1 = 17
            "65.0%n17.0%n");
    }
}
