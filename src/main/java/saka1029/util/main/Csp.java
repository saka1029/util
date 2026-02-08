package saka1029.util.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import saka1029.util.csp.Problem;
import saka1029.util.language.JavaCompilerInMemory.CompileError;

public class Csp {
    
    /**
     * <pre>
     * SYNTAX
     * definition = 'problem' className
     *              { 'variable' int int var { var } }
     *              { 'constraint' predicate }
     *              { functions }
     * </pre>
     */
    static Problem parse(Path file) throws IOException {
        Problem problem = new Problem();
        try (BufferedReader br = Files.newBufferedReader(file)) {
            String line;
            while ((line = br.readLine()) != null) {
                String trimed = line.replaceAll("#.*", "").trim();
                if (trimed.isEmpty())
                    continue;
                String[] f = trimed.split("\\s+", 2);
                switch (f[0]) {
                    case "problem":
                        problem.className(f[1]);
                        break;
                    case "variable":
                        String[] g = f[1].split("\\s+");
                        if (g.length < 3 || !g[0].matches("[+-]?\\d+") || !g[1].matches("[+-]?\\d+"))
                            throw new RuntimeException("expected 'variable int int variable...'");
                        int min = Integer.parseInt(g[0]), max = Integer.parseInt(g[1]);
                        for (int i = 2; i < g.length; ++i)
                            problem.variable(min, max, g[i]);
                        break;
                    case "constraint":
                        problem.constraint(f[1]);
                        break;
                    case "allDifferent":
                        String[] d = f[1].split("\\s+");
                        problem.allDifferent(d);
                        break;
                    default:
                        problem.anyCode(line);
                        break;
                }
            }
        }
        return problem;
    }
    

    public static void main(String[] args) throws IOException,
            IllegalAccessException, InvocationTargetException,
            NoSuchMethodException, SecurityException,
            ClassNotFoundException, CompileError {
        if (args.length != 1)
            throw new RuntimeException("usage: java saka1029.util.main.Csp CSP_FILE");
        Problem problem = parse(Paths.get(args[0]));
        problem.solve();
    }

}
