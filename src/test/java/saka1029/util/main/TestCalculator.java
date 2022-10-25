package saka1029.util.main;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import org.junit.jupiter.api.Test;

class TestCalculator {
    
    /**
     * @param input 改行コードは"%n"とする。
     * @param expectedOutput 改行コードは"%n"とする。
     * @throws IOException
     */
    static void testRun(String input, String expectedOutput) throws IOException {
        input = input.formatted();
        expectedOutput = expectedOutput.formatted();
        Reader reader = new StringReader(input);
        StringWriter writer = new StringWriter();
        Calculator calc = new Calculator(reader, writer);
        calc.run();
        assertEquals(expectedOutput, writer.toString());
    }

    @Test
    void testRun() throws IOException {
        testRun("1 + 2 * 3%n", "7.0%n");
        testRun("(1 + 2) * 3%n", "9.0%n");
    }
    
    @Test
    void testVariable() throws IOException {
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
