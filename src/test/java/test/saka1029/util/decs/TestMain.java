package test.saka1029.util.decs;

import static org.junit.Assert.assertEquals;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Test;

public class TestMain {

    static final String TEST_FILE = "test.saka1029.util.decs.TestMain.txt";

    static String[] COMMAND_LINE = {
        "java",
        "-cp", "target/classes" + File.pathSeparator + "target/dependency/*",
        "saka1029.util.decs.Main", "-f", TEST_FILE
    };

    static final String NL = "\n";
    static final Charset CHARSET = StandardCharsets.UTF_8;

    static String file(String input) throws IOException, InterruptedException {
        Files.write(Paths.get(TEST_FILE), input.getBytes(CHARSET));
        ProcessBuilder builder = new ProcessBuilder(COMMAND_LINE);
        Process process = builder.start();
        try (InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, CHARSET);
            BufferedReader reader = new BufferedReader(isr)) {
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null)
                sb.append(line).append(NL);
            process.waitFor();
            Files.delete(Paths.get(TEST_FILE));
            return sb.toString();
        }
    }

    @Test
    public void testFile() throws IOException, InterruptedException {
        assertEquals("""
                1 + 2 + 3
            6    
                3 +
                4 + 5
            12
            """, file("""
            1 + 2 + 3        
            3 +
            4 + 5
            """));
    }

    @Test
    public void testHelp() throws IOException, InterruptedException {
        assertEquals("""
                help
            help syntax
            help variable
            help unary
            help binary
            help NAME
            """, file("""
            help
            """));
    }

    @Test
    public void testSyntaxError() throws IOException, InterruptedException {
        assertEquals("""
                )
            unexpected token ')'
                unknown
            variable 'unknown' undefined
            """, file("""
            )
            unknown
            """));
    }
}
