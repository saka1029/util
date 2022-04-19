package util.language.csp;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import util.language.JavaCompilerInMemory;
import util.language.JavaCompilerInMemory.CompileError;

public class Compiler {

    private Compiler() {
    }

    public static final String TEMPLATE;
    static {
        try (InputStream is = Compiler.class
            .getResourceAsStream("Compiler.template")) {
            TEMPLATE = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    static final String NAME = "\\p{L}[\\p{L}\\p{IsDigit}_]*";
    static final Pattern MACRO_VAR = Pattern.compile("#" + NAME + "#(\r\n)*");

    static RuntimeException error(String format, Object... arguments) {
        return new RuntimeException(String.format(format, arguments));
    }

    public static class Problem {
        final Map<String, Variable> variables = new LinkedHashMap<>();
        final List<Constraint> constraints = new ArrayList<>();
        final String fqcn;
        final List<String> imports;
        String functions = null;

        public Problem(String fqcn, String... imports) {
            this.fqcn = fqcn;
            this.imports = List.of(imports);
        }

        public Variable variable(String name, int[] domain) {
            if (variables.containsKey(name))
                throw new IllegalArgumentException(
                    "duplicated variable: " + name);
            if (!constraints.isEmpty())
                throw new IllegalStateException("constraint already added");
            Variable v = new Variable(name, domain);
            variables.put(name, v);
            return v;
        }

        public Constraint constraint(String expression, Variable... variables) {
            Constraint c = new Constraint(expression, variables);
            constraints.add(c);
            return c;
        }

        public void allDifferent(Variable... variables) {
            for (int i = 0, max = variables.length; i < max; ++i)
                for (int j = i + 1; j < max; ++j)
                    constraint(variables[i] + " != " + variables[j],
                        variables[i], variables[j]);
        }

        static void append(StringBuilder sb, String format,
            Object... arguments) {
            sb.append(String.format(format, arguments));
        }

        void generate(Map<String, String> map) {
            int index = fqcn.lastIndexOf('.');
            map.put("#PACKAGE#",
                index >= 0
                    ? String
                        .format("package " + fqcn.substring(0, index) + ";%n")
                    : "");
            map.put("#CLASS#", index >= 0 ? fqcn.substring(index + 1) : fqcn);
            StringBuilder sb = new StringBuilder();
            sb.setLength(0);
            for (String s : imports)
                append(sb, "import %s;%n", s);
            map.put("#IMPORTS#", sb.toString());
            sb.setLength(0);
            for (Variable v : variables.values())
                append(sb, "int[] _%s_domain = {%s};%n", v.name,
                    IntStream.of(v.domain).mapToObj(n -> "" + n)
                        .collect(Collectors.joining(", ")));
            map.put("#DECLARE#", sb.toString());
            sb.setLength(0);
            Set<Constraint> remains = new LinkedHashSet<>(constraints);
            List<Variable> generated = new ArrayList<>();
            for (Variable v : variables.values()) {
                append(sb, "for (int %s : _%s_domain)%n", v.name, v.name);
                generated.add(v);
                List<Constraint> gen = remains.stream()
                    .filter(c -> generated.containsAll(c.variables)).toList();
                if (!gen.isEmpty()) {
                    append(sb, "if (%s)%n", gen.stream().map(c -> c.expression)
                        .collect(Collectors.joining(" && ")));
                    remains.removeAll(gen);
                }
            }
            if (!remains.isEmpty())
                throw error("illegal constraints: " + remains);
            append(sb, "callback.accept(new int[] {%s});%n", variables.values()
                .stream().map(v -> v.name).collect(Collectors.joining(", ")));
            map.put("#FOR#", sb.toString());
            map.put("#FUNCTIONS#", functions != null ? functions : "");
            sb.setLength(0);
            map.put("#VARIABLES#", "\"" + variables.values().stream()
                .map(v -> v.name).collect(Collectors.joining(",")) + "\"");
        }

        public String generate() {
            Map<String, String> map = new HashMap<>();
            generate(map);
            StringBuilder sb = new StringBuilder();
            Matcher m = MACRO_VAR.matcher(TEMPLATE);
            while (m.find()) {
                String repl = map.computeIfAbsent(m.group().trim(),
                    k -> "!UNDEFINED " + k + "!");
                m.appendReplacement(sb, repl);
            }
            m.appendTail(sb);
            return sb.toString();
        }

        // public void compile(File dest) {
        // JavaCompilerWriteClassFile.compile(dest, null,
        // List.of(new JavaCompilerWriteClassFile.Source(fqcn, generate())));
        // }

        static final List<String> OPTIONS = List.of("-g:none");

        public String compileGo()
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, ClassNotFoundException, CompileError {
            String generatedSource = generate();
            JavaCompilerInMemory.compile(fqcn, generatedSource, OPTIONS)
                .getMethod("main", String[].class).invoke(null, new Object[] {new String[0]});
            return generatedSource;
        }
    }

    public static class Variable {
        final String name;
        final int[] domain;
        final List<Constraint> constraints = new ArrayList<>();

        Variable(String name, int[] domain) {
            this.name = name;
            this.domain = domain.clone();
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static class Constraint {
        final String expression;
        final List<Variable> variables;

        Constraint(String expression, Variable... variables) {
            this.expression = expression;
            this.variables = List.of(variables);
            for (Variable v : variables)
                v.constraints.add(this);
        }

        @Override
        public String toString() {
            return "制約(" + expression + variables + ")";
        }
    }

    static Pattern FQCN = Pattern.compile("^" + NAME + "(\\." + NAME + ")*");
    static Pattern IMPORT = Pattern
        .compile("^(static\\s+)?" + NAME + "(\\." + NAME + ")*(\\.\\*)?");
    static Pattern VARIABLE = Pattern.compile("^" + NAME);
    static Pattern CVARIABLE = Pattern.compile(NAME);
    static Pattern INT = Pattern.compile("^-?\\d+");

    public static Problem parse(String source) {
        return new Object() {
            int length = source.length();
            int index = 0;
            String token;

            void spaces() {
                while (index < length) {
                    int ch = source.charAt(index);
                    if (Character.isWhitespace(ch))
                        ++index;
                    else if (ch == '#')
                        do {
                            ch = source.charAt(index++);
                        } while (index < length && ch != '\r' && ch != '\n');
                    else
                        break;
                }
            }

            boolean match(String e) {
                spaces();
                if (!source.startsWith(e, index))
                    return false;
                token = e;
                index += token.length();
                return true;
            }

            boolean match(Pattern e) {
                spaces();
                Matcher m = e.matcher(source.substring(index));
                if (!m.find())
                    return false;
                token = m.group();
                index += token.length();
                return true;
            }

            void semicolon() {
                if (!match(";"))
                    throw error("';' expeced after " + token);
            }

            String problem() {
                String fqcn;
                if (match("problem"))
                    if (match(FQCN)) {
                        fqcn = token;
                        semicolon();
                    } else
                        throw error("FQCN expected after 'problem'");
                else
                    throw error("'problem' expected");
                return fqcn;
            }

            List<String> imports() {
                List<String> imports = new ArrayList<>();
                while (match("import"))
                    if (match(IMPORT)) {
                        imports.add(token);
                        semicolon();
                    } else
                        throw error("FQCN expected after 'import'");
                return imports;
            }

            int[] domain() {
                List<Integer> d = new ArrayList<>();
                if (match("[")) {
                    while (match(INT)) {
                        int start = Integer.parseInt(token);
                        if (match(".."))
                            if (match(INT))
                                for (int i = start, end = Integer.parseInt(token); i <= end; ++i)
                                    d.add(i);
                            else
                                throw error("integer expected after '..'");
                        else
                            d.add(start);
                    }
                    if (!match("]"))
                        throw error("']' expected");
                } else
                    throw error("'[' expected");
                return d.stream().mapToInt(i -> i).toArray();
            }

            void variables(Problem problem) {
                int count = 0;
                while (match("variable")) {
                    int[] domain = domain();
                    while (match(VARIABLE))
                        problem.variable(token, domain);
                    semicolon();
                    ++count;
                }
                if (count <= 0)
                    throw error("no 'variable' statements");
            }

            void constraint(Problem problem) {
                while (index < length) {
                    if (match("constraint")) {
                        spaces();
                        StringBuilder sb = new StringBuilder();
                        while (index < length && source.charAt(index) != ';')
                            sb.append(source.charAt(index++));
                        semicolon();
                        String expression = sb.toString();
                        Set<Variable> variables = new LinkedHashSet<>();
                        Matcher m = CVARIABLE.matcher(expression);
                        while (m.find()) {
                            String name = m.group();
                            Variable v = problem.variables.get(name);
                            if (v != null)
                                variables.add(v);
                        }
                        problem.constraint(expression, variables.toArray(Variable[]::new));
                    } else if (match("different")) {
                        List<Variable> variables = new ArrayList<>();
                        while (match(VARIABLE)) {
                            Variable v = problem.variables.get(token);
                            if (v != null)
                                variables.add(v);
                            else
                                throw error("variable '%s' is not defined", token);
                        }
                        problem.allDifferent(variables.toArray(Variable[]::new));
                        semicolon();
                    } else
                        break;
                }
            }

            Problem parse() {
                String fqcn = problem();
                List<String> imports = imports();
                Problem problem = new Problem(fqcn, imports.toArray(String[]::new));
                variables(problem);
                constraint(problem);
                if (index < length)
                    problem.functions = source.substring(index);
                return problem;
            }
        }.parse();
    }

    static IllegalArgumentException usage() {
        System.err
            .println("usage: java " + Compiler.class.getName() + " [-v] CSP_FILE");
        return new IllegalArgumentException();
    }

    public static void main(String[] args) throws IOException, IllegalAccessException,
        IllegalArgumentException, InvocationTargetException, NoSuchMethodException,
        SecurityException, ClassNotFoundException, CompileError {
        boolean verbose = false;
        int i = 0;
        L: for (int max = args.length; i < max; ++i)
            switch (args[i]) {
            default:
                if (args[i].startsWith("-"))
                    switch (args[i].charAt(1)) {
                    case 'v':
                        verbose = true;
                        break;
                    default:
                        throw usage();
                    }
                else
                    break L;
            }
        if (i >= args.length)
            throw usage();
        String source = Files.readString(Path.of(args[i]));
        Problem problem = parse(source);
        String generatedSource = problem.compileGo();
        if (verbose)
            System.out.println(generatedSource);
    }
}