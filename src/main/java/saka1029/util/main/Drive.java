package saka1029.util.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Drive {

    static Pattern escape(String pat) {
        pat = pat.replace(".", "\\\\.")
            .replace("\\", "/")
            .replace("?", ".")
            .replace("*", ".*");
        return Pattern.compile(pat, Pattern.CASE_INSENSITIVE);
    }

    static String[] names = new String[64];

    static void find(Path inFile, Pattern pattern) {
        try (BufferedReader reader = Files.newBufferedReader(inFile)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] f = line.split(" ", 2);
                int i = Integer.parseInt(f[0]);
                String name = f[1];
                names[i] = name;
                Matcher m = pattern.matcher(f[1]);
                if (m.find())
                    System.out.println(Arrays.stream(names)
                        .limit(i + 1)
                        .collect(Collectors.joining("/")));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static void find(Path inDir, String string) throws IOException {
        try (Stream<Path> walk = Files.walk(inDir)) {
            Pattern pattern = escape(string);
            walk.filter(Files::isRegularFile)
                .forEach(p -> find(p, pattern));
        }
    }

    static String USAGE = String.format(
        "USAGE:%n"
        + "java %s IN_DIR STRING%n", Drive.class.getName()
    );

    /**
     * USAGE:
     * java saka1029.util.main.Drive IN_DIR STRING
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        if (args.length != 2)
            throw new IllegalArgumentException(USAGE);
        Path inDir = Paths.get(args[0]);
        String string = args[1];
        find(inDir, string);
        System.out.printf("inDir=%s string=%s%n", inDir, string);
    }
}
