package io.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.Test;
import util.io.CSVReader;

public class TestCSVReader {
    
    @Test
    public void testPath() throws IOException {
        Path path = Paths.get("data/2018-04-05 明細 - シート1.csv");
        try (CSVReader r = new CSVReader(path)) {
            while (true) {
                List<String> line = r.readLine();
                if (line == null)
                    break;
                System.out.println(line);
            }
        }
    }
    
    @Test
    public void testString() throws IOException {
        Path path = Paths.get("data/2018-04-05 明細 - シート1.csv");
        String csv = Files.readString(path);
        try (CSVReader r = new CSVReader(csv)) {
            while (true) {
                List<String> line = r.readLine();
                if (line == null)
                    break;
                System.out.println(line);
            }
        }
    }
}