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

import saka1029.util.language.JavaCompilerInMemory;
import saka1029.util.language.JavaCompilerInMemory.CompileError;

public class Csp {
    
    static class Problem {
        String className;
        final Map<String, Variable> variables = new LinkedHashMap<>();
        final List<Constraint> constraints = new ArrayList<>();
        final List<String> anyCodes = new ArrayList<>();
    }
    
    static class Variable {
        String name;
        int start, end;
        final Set<Constraint> constraints = new HashSet<>();
    }
    
    static class Constraint {
        String predicate;
        final Set<Variable> variables = new HashSet<>();
    }
    
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
                        problem.className = f[1];
                        break;
                    case "variable":
                        String[] g = f[1].split("\\s+");
                        if (g.length < 3 || !g[0].matches("[+-]?\\d+") || !g[1].matches("[+-]?\\d+"))
                            throw new RuntimeException("expected 'variable int int variable...'");
                        int start = Integer.parseInt(g[0]), end = Integer.parseInt(g[1]);
                        for (int i = 2; i < g.length; ++i) {
                            Variable variable = new Variable();
                            variable.name = g[i];
                            variable.start = start;
                            variable.end = end;
                            problem.variables.put(g[i], variable);
                        }
                        break;
                    case "constraint":
                        Constraint constraint = new Constraint();
                        constraint.predicate = f[1];
                        problem.constraints.add(constraint);
                        for (String e : f[1].split("(?i)[^a-z0-9]+")) {
                            Variable variable = problem.variables.get(e);
                            if (variable != null) {
                                constraint.variables.add(variable);
                                variable.constraints.add(constraint);
                            }
                        }
                        break;
                    case "allDifferent":
                        String[] d = f[1].split("\\s+");
                        for (int i = 0, max = d.length; i < max; ++i) {
                            Variable a = problem.variables.get(d[i]);
                            if (a == null)
                                throw new RuntimeException("variable '%s' notdefined".formatted(d[i]));
                            for (int j = i + 1; j < max; ++j) {
                                Variable b = problem.variables.get(d[j]);
                                Constraint c = new Constraint();
                                problem.constraints.add(c);
                                c.predicate = a.name + " != " + b.name;
                                c.variables.add(a);
                                c.variables.add(b);
                                a.constraints.add(c);
                                b.constraints.add(c);
                            }
                        }
                        break;
                    default:
                        problem.anyCodes.add(line);
                        break;
                }
            }
        }
        return problem;
    }
    
    static String generate(Problem problem) {
        StringWriter sw = new StringWriter();
        try (PrintWriter w = new PrintWriter(sw)) {
            boolean outImport = false;
            for (String s : problem.anyCodes)
                if (s.trim().startsWith("import ")) {
                    w.printf("%s%n", s);
                    outImport = true;
                }
            if (outImport)
                w.println();
            w.printf("public class %s {%n", problem.className);
            w.println();
            w.printf("    static int solve() {%n");
            w.printf("        int count = 0;%n");
            w.printf("        System.out.println(%s);%n",
                problem.variables.keySet().stream().collect(Collectors.joining(",", "\"", "\"")));
            Set<Constraint> remainConstraints = new HashSet<>(problem.constraints);
            List<Variable> generatedVariables = new ArrayList<>();
            for (Variable v : problem.variables.values()) {
                w.printf("        for (int %1$s = %2$d; %1$s <= %3$d; ++%1$s)%n", v.name, v.start, v.end);
                generatedVariables.add(v);
                List<Constraint> generatedConstraints = remainConstraints.stream()
                    .filter(c -> generatedVariables.containsAll(c.variables)).toList();
                if (!generatedConstraints.isEmpty()) {
                    w.printf("        if (%s)%n",
                        generatedConstraints.stream().map(c -> c.predicate).collect(Collectors.joining(" && ")));
                    remainConstraints.removeAll(generatedConstraints);
                }
            }
            if (!remainConstraints.isEmpty())
                throw new RuntimeException("constraints does not generated: "
                    + remainConstraints.stream().map(c -> c.predicate).collect(Collectors.joining(", ")));
//            w.printf("        callback.accept(new int[] {%s});%n",
            w.printf("        {%n");
            w.printf("            ++count;%n");
            w.printf("            System.out.printf(\"%s%%n\", %s);%n",
                IntStream.range(0, problem.variables.size()).mapToObj(i -> "%d").collect(Collectors.joining(",")),
                problem.variables.keySet().stream().collect(Collectors.joining(", ")));
            w.printf("        }%n");
            w.printf("        return count;%n");
            w.printf("    }%n");
            w.println();
            boolean outAnyCode = false;
            for (String s : problem.anyCodes)
                if (!s.trim().startsWith("import ")) {
                    w.printf("%s%n", s);
                    outAnyCode = true;
                }
            if (outAnyCode)
                w.println();
            w.printf("    public static void main(String[] args) {%n");
            w.printf("        long start = System.currentTimeMillis();%n");
            w.printf("        int count = solve();%n");
            w.printf("        System.err.printf(\"solutions: \" + count + \", elapse: %%d msec.%%n\", System.currentTimeMillis() - start);%n");
            w.printf("    }%n");
            w.printf("}%n");
        }
        return sw.toString();
    }
    
    static final List<String> OPTIONS = List.of("-g:none");

    public static void main(String[] args) throws IOException,
            IllegalAccessException, InvocationTargetException,
            NoSuchMethodException, SecurityException,
            ClassNotFoundException, CompileError {
        if (args.length != 1)
            throw new RuntimeException("usage: java saka1029.util.main.Csp CSP_FILE");
        Problem problem = parse(Paths.get(args[0]));
        String generatedSource = generate(problem);
        System.out.println(generatedSource);
        JavaCompilerInMemory.compile(problem.className, generatedSource, OPTIONS)
            .getMethod("main", String[].class).invoke(null, new Object[] {new String[0]});
    }

}
