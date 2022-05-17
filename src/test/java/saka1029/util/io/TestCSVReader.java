package saka1029.util.io;

import static org.junit.Assert.assertEquals;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import org.junit.Test;

/**
 * mavenでテストするとき
 * mvn test -Dtest=TestCSVReader -Djava.util.logging.config.file=logging.properties
 */
public class TestCSVReader {
    
    @Test
    public void testPath() throws IOException {
        System.out.println("*** " + Thread.currentThread().getStackTrace()[1].getMethodName());
        Path path = Paths.get("data/2018-04-05 明細 - シート1.csv");
        try (CSVReader r = new CSVReader(path)) {
            assertEquals(List.of("医療機関", "年", "月", "日", "診療科", "金額"), r.readLine());
            assertEquals(List.of("上毛病院", "29", "12", "20", "精神科", "1,210"), r.readLine());
        }
    }
    
    @Test
    public void testString() throws IOException {
        System.out.println("*** " + Thread.currentThread().getStackTrace()[1].getMethodName());
        Path path = Paths.get("data/2018-04-05 明細 - シート1.csv");
        String csv = Files.readString(path);
        try (CSVReader r = new CSVReader(csv)) {
            assertEquals(List.of("医療機関", "年", "月", "日", "診療科", "金額"), r.readLine());
            assertEquals(List.of("上毛病院", "29", "12", "20", "精神科", "1,210"), r.readLine());
        }
    }

    @Test
    public void testEncoding() throws IOException {
        System.out.println("*** " + Thread.currentThread().getStackTrace()[1].getMethodName());
        String fe = "file.encoding";
        System.out.println(fe + "=" + System.getProperty(fe));
        OutputStream os = System.out;
        byte[] utf8 = "これはUTF-8の日本語\r\n".getBytes(StandardCharsets.UTF_8);
        byte[] ms932 = "これはMS932の日本語\r\n".getBytes(Charset.forName("MS932"));
        os.write(utf8);
        os.write(ms932);
        Properties p = System.getProperties();
        for (Entry<Object, Object>  e : p.entrySet())
            System.out.println(e);
    }
}
