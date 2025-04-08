package saka1029.util.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
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

    static boolean found(Pattern pattern, int start, int max) {
        for (int i = start; i < max; ++i)
            if (pattern.matcher(names[i]).find())
                return true;
        return false;
    }

    static void find(Path inFile, Pattern pattern, boolean allFlag) {
        try (BufferedReader reader = Files.newBufferedReader(inFile)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] f = line.split(" ", 2);
                int i = Integer.parseInt(f[0]);
                String name = f[1];
                names[i] = name;
                if (found(pattern, allFlag ? 1 : i, i + 1))
                // Matcher m = pattern.matcher(f[1]);
                // if (m.find())
                    System.out.println(Arrays.stream(names)
                        .limit(i + 1)
                        .collect(Collectors.joining("/")));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static void find(Path inDir, String string, boolean allFlag) throws IOException {
        try (Stream<Path> walk = Files.walk(inDir)) {
            Pattern pattern = escape(string);
            walk.filter(Files::isRegularFile)
                .forEach(p -> find(p, pattern, allFlag));
        }
    }

    static String USAGE = String.format(
        "USAGE:%n"
        + "java %s -d IN_DIR [-a] STRING%n"
        + "  -d IN_DIR : ドライブファイルのあるディレクトリを指定する%n"
        + "  -a        : パス名をすべて検索する%n"
        + "              (通常はディレクトリ/ファイル名のみを検索する)%n", Drive.class.getName()
    );

    static void usage() {
        throw new IllegalArgumentException(USAGE);
    }

    /**
     * USAGE:
     * java saka1029.util.main.Drive IN_DIR STRING
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        Path inDir = null;
        boolean allFlag = false;
        int argsLength = args.length, i = 0;
        for (i = 0; i < argsLength; ++i)
            if (args[i].startsWith("-"))
                switch(args[i].substring(1)) {
                    case "a":
                        allFlag = true;
                        break;
                    case "d":
                        if (++i >= argsLength)
                            usage();
                        inDir = Paths.get(args[i]);
                        break;
                    default: usage();
                }
            else
                break;
        if (args.length - i != 1 || inDir == null)
            usage();
        String string = args[i];
        find(inDir, string, allFlag);
        // System.out.printf("inDir=%s string=%s%n", inDir, string);
    }
}
