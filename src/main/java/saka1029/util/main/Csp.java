package saka1029.util.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Csp {
    
    static final String NL = System.lineSeparator();

    static class Problem {
        String fqcn;
        final List<String> imports = new ArrayList<>();
        final List<Variable> variables = new ArrayList<>();
        final List<Constraint> constraints = new ArrayList<>();
        final List<String> functions = new ArrayList<>();
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("problem " + fqcn + NL);
            for (String s : imports)
                sb.append("import " + s + NL);
            for (Variable v : variables)
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
     * definition = 'problem' fqcn
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
                        problem.fqcn = f[1];
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
                            problem.variables.add(variable);
                        }
                        break;
                    case "constraint":
                        Constraint constraint = new Constraint();
                        constraint.predicate = f[1];
                        problem.constraints.add(constraint);
                        for (String e : f[1].split("(?i)[^a-z0-9]+")) {
                            for (Variable variable : problem.variables)
                                if (e.equals(variable.name)) {
                                    constraint.variables.add(variable);
                                    variable.constraints.add(constraint);
                                }
                        }
                        break;
                    case "allDifferent":
                        String[] d = f[1].split("\\s+");
                        List<Variable> diff = new ArrayList<>();
                        L: for (String e : d) {
                            for (Variable v : problem.variables)
                                if (e.equals(v.name)) {
                                    diff.add(v);
                                    continue L;
                                }
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

    public static void main(String[] args) throws IOException {
        if (args.length != 1)
            throw new RuntimeException("usage: csp FILE");
        Problem problem = parse(Paths.get(args[0]));
        System.out.println(problem);
    }

}
