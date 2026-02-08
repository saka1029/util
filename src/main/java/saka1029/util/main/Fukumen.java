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
        java saka1029.util.main.Fukumen -e EXPRESSION"
        or"
        java saka1029.util.main.Fukumen EXPRESSION_FILE"
        """;

    public static void main(String[] args) throws IOException,
            IllegalAccessException, InvocationTargetException,
            NoSuchMethodException, SecurityException,
            ClassNotFoundException, CompileError {
        Problem problem;
        if (args.length == 1)
            problem = FukumenParser.parse(Files.readString(Paths.get(args[0])));
        else if (args.length == 2 && args[0].equals("-e"))
            problem = FukumenParser.parse(args[1]);
        else
            throw new RuntimeException("usage: java saka1029.util.main.Fukumen FUKUMEN_FILE");
        problem.solve();
    }

}
