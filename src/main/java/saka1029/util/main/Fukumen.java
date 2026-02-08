package saka1029.util.main;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import saka1029.util.csp.FukumenParser;
import saka1029.util.csp.Problem;
import saka1029.util.language.JavaCompilerInMemory.CompileError;

public class Fukumen {

    static Problem parse(Path file) throws IOException {
        String input = Files.readString(file);
        Problem problem = FukumenParser.parse(input);
        return problem;
    }

    static final String USAGE = """
        usage:
        java saka1029.util.main.Fukumen [-s] [-e 式] [-f ファイル名]
        -s             生成されたJavaのソースコードを表示します。
        -e 式          式を引数で直接指定します。
        -f ファイル名  式をテキストファイルで指定します。
        """;

    static void usage() {
        throw new IllegalArgumentException(USAGE);
    }

    public static void main(String[] args) throws IOException,
            IllegalAccessException, InvocationTargetException,
            NoSuchMethodException, SecurityException,
            ClassNotFoundException, CompileError {
        Problem problem = null;
        boolean displaySource = false;
        for (int i = 0; i < args.length; ++i)
            switch (args[i]) {
                case "-s":
                    displaySource = true;
                    break;
                case "-f":
                    if (++i >= args.length)
                        usage();
                    problem = FukumenParser.parse(Files.readString(Paths.get(args[i])));
                    break;
                case "-e":
                    if (++i >= args.length)
                        usage();
                    problem = FukumenParser.parse(args[i]);
                    break;
                default:
                    usage();
                    break;
            }
        if (problem == null)
            usage();
        problem.solve(displaySource);
    }

}
