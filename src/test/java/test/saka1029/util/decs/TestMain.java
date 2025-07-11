package test.saka1029.util.decs;

import java.io.BufferedReader;
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
        "-cp", "target/classes:target/dependency/*",
        "saka1029.util.decs.Main", "-f", TEST_FILE
    };

    static final String NL = "\n";
    static final Charset CHARSET = StandardCharsets.UTF_8;

    static String run(String input) throws IOException {
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
            try {
                process.waitFor();
            } catch (InterruptedException e) {
            }
            Files.delete(Paths.get(TEST_FILE));
            return sb.toString();
        }
    }

    @Test
    public void testFile() throws IOException {
        String input = """
            1 + 2 + 3
            """;
        String output = run(input);
        System.out.println(output);
    }
}
