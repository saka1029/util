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

import saka1029.util.language.JavaCompilerInMemory;
import saka1029.util.language.JavaCompilerInMemory.CompileError;

public class Csp {
    
    static final String NL = System.lineSeparator();

    static class Problem {
        String className;
        final List<String> imports = new ArrayList<>();
        final Map<String, Variable> variables = new LinkedHashMap<>();
        final List<Constraint> constraints = new ArrayList<>();
        final List<String> functions = new ArrayList<>();
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("problem " + className + NL);
            for (String s : imports)
                sb.append("import " + s + NL);
            for (Variable v : variables.values())
                sb.append(v);
            for (Constraint c : constraints)
                sb.append(c);
            for (String f : functions)
                sb.append(f).append(NL);
            return sb.toString();
        }
    }
    
    static class Variable {
        String name;
        int start, end;
        final Set<Constraint> constraints = new HashSet<>();
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("variable %d %d %s%n".formatted(start, end, name));
            for (Constraint c : constraints)
                sb.append("  %s%n".formatted(c.predicate));
            return sb.toString();
        }
    }
    
    static class Constraint {
        String predicate;
        final Set<Variable> variables = new HashSet<>();
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("constraint %s%n".formatted(predicate));
            for (Variable v : variables)
                sb.append("  %s%n".formatted(v.name));
            return sb.toString();
        }
    }
    
    /**
     * <pre>
     * SYNTAX
     * definition = 'problem' className
     *              { 'import' fqcn }
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
                line = line.replaceAll("#.*", "").trim();
                if (line.isEmpty())
                    continue;
                String[] f = line.trim().split("\\s+", 2);
                switch (f[0]) {
                    case "problem":
                        problem.className = f[1];
                        break;
                    case "import":
                        problem.imports.add(f[1]);
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
                            if (e != null) {
                                constraint.variables.add(variable);
                                variable.constraints.add(constraint);
                            }
                        }
                        break;
                    case "allDifferent":
                        String[] d = f[1].split("\\s+");
                        List<Variable> diff = new ArrayList<>();
                        for (String e : d) {
                            Variable v = problem.variables.get(e);
                            if (v != null)
                                diff.add(v);
                            else
                                throw new RuntimeException("variable '" + e + "' is not defined");
                        }
                        for (int i = 0, max = diff.size(); i < max; ++i) {
                            Variable a = diff.get(i);
                            for (int j = i + 1; j < max; ++j) {
                                Variable b = diff.get(j);
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
                        problem.functions.add(line);
                        break;
                }
            }
        }
        return problem;
    }
    
    static String generate(Problem problem) {
        StringWriter sw = new StringWriter();
        try (PrintWriter w = new PrintWriter(sw)) {
            w.printf("import java.util.function.Consumer;%n");
            w.printf("import java.util.stream.Collectors;%n");
            w.printf("import java.util.stream.IntStream;%n");
            for (String s : problem.imports)
                w.printf("import %s;%n", s);
            w.printf("public class %s {%n", problem.className);
            w.printf("    static void solve(Consumer<int[]> callback) {%n");
            Set<Constraint> remainConstraints = new HashSet<>(problem.constraints);
            List<Variable> generatedVariables = new ArrayList<>();
            for (Variable v : problem.variables.values()) {
                w.printf("        for (int %1$s = %2$d; %1$s < %3$d; ++%1$s)%n", v.name, v.start, v.end);
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
            w.printf("        callback.accept(new int[] {%s});%n",
                problem.variables.keySet().stream().collect(Collectors.joining(", ")));
            w.printf("    }%n");
            for (String s : problem.functions)
                w.printf("%s%n", s);
            w.printf("    public static void main(String[] args) {%n");
            w.printf("       long start = System.currentTimeMillis();%n");
            w.printf("        System.out.println(%s);%n",
                problem.variables.keySet().stream().collect(Collectors.joining(",", "\"", "\"")));
            w.printf("        int[] count = {0};%n");
            w.printf("        Consumer<int[]> callback = a -> {%n");
            w.printf("            ++count[0];%n");
            w.printf("            System.out.println(IntStream.of(a)%n");
            w.printf("                .mapToObj(n -> \"\" + n)%n");
            w.printf("                .collect(Collectors.joining(\",\")));%n");
            w.printf("        };%n");
            w.printf("        solve(callback);%n");
            w.printf("        System.err.printf(\"solutions: \" + count[0] + \", elapse: %%d msec.%%n\", System.currentTimeMillis() - start);%n");
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
            throw new RuntimeException("usage: csp FILE");
        Problem problem = parse(Paths.get(args[0]));
        System.out.println(problem);
        String generatedSource = generate(problem);
        System.out.println(generatedSource);
        JavaCompilerInMemory.compile(problem.className, generatedSource, OPTIONS)
        .getMethod("main", String[].class).invoke(null, new Object[] {new String[0]});

    }

}
