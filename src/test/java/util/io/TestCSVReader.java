package util.io;

import static org.junit.Assert.assertEquals;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;
import org.junit.Test;

/**
 * mavenでテストするとき
 * mvn test -Dtest=TestCSVReader -Djava.util.logging.config.file=logging.properties
 */
public class TestCSVReader {
    
    static final Logger logger = Logger.getLogger(TestCSVReader.class.getName());
    @Test
    public void testPath() throws IOException {
        logger.info("*** " + Thread.currentThread().getStackTrace()[1].getMethodName());
        Path path = Paths.get("data/2018-04-05 明細 - シート1.csv");
        try (CSVReader r = new CSVReader(path)) {
            assertEquals(List.of("医療機関", "年", "月", "日", "診療科", "金額"), r.readLine());
            assertEquals(List.of("上毛病院", "29", "12", "20", "精神科", "1,210"), r.readLine());
        }
    }
    
    @Test
    public void testString() throws IOException {
        logger.info("*** " + Thread.currentThread().getStackTrace()[1].getMethodName());
        Path path = Paths.get("data/2018-04-05 明細 - シート1.csv");
        String csv = Files.readString(path);
        try (CSVReader r = new CSVReader(csv)) {
            assertEquals(List.of("医療機関", "年", "月", "日", "診療科", "金額"), r.readLine());
            assertEquals(List.of("上毛病院", "29", "12", "20", "精神科", "1,210"), r.readLine());
        }
    }
}