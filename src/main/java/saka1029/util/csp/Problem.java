package saka1029.util.csp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Problem {

    static final Pattern VARIABLE_PATTERN = Pattern.compile(
        "[a-z_\\p{IsHan}\\p{IsHiragana}\\p{IsKatakana}]"
        + "[a-z_\\p{IsHan}\\p{IsHiragana}\\p{IsKatakana}\\d]*",
        Pattern.CASE_INSENSITIVE);

    static class Variable {
        final String name;
        final int min, max;
        final Set<Constraint> constraints = new HashSet<>();
        Variable(String name, int min, int max) {
            this.name = name;
            this.min = min;
            this.max = max;
        }
    }

    public class Constraint {
        final String predicate;
        final Set<Variable> variables = new HashSet<>();
        Constraint(String predicate) {
            this.predicate = predicate;
        }
    }

    String className;
    final Map<String, Variable> variables = new HashMap<>();
    final List<Constraint> constraints = new ArrayList<>();
    final List<String> anyCodes = new ArrayList<>();

    public void className(String className) {
        this.className = className;
    }

    public Variable variable(String name, int min, int max) {
        if (constraints.size() > 0)
            throw new RuntimeException("define all variables before define constraint");
        Variable variable = new Variable(name, min, max);
        this.variables.put(name, variable);
        return variable;
    }

    public void constraint(String predicate) {
        Constraint constraint = new Constraint(predicate);
        Matcher m = VARIABLE_PATTERN.matcher(predicate);
        while (m.find()) {
            String variableName = m.group();
            Variable variable = variables.get(variableName);
            if (variable != null) {
                constraint.variables.add(variable);
                variable.constraints.add(constraint);
            }
        }
        constraints.add(constraint);
    }

    public void allDifferent(String... names) {
        int size = names.length;
        for (int i = 0; i < size; ++i)
            for (int j = i + 1; j < size; ++j)
                constraint("%s != %s".formatted(names[i], names[j]));
    }

    public void anyCode(String line) {
        anyCodes.add(line);
    }

    static final String NL = System.lineSeparator();

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("problem ").append(className).append(NL);
        for (Variable v : variables.values()) {
            sb.append("  variable ")
                .append(v.name)
                .append(" [").append(v.min).append(", ").append(v.max).append("]").append(NL);
            for (Constraint c : v.constraints)
                sb.append("    ").append(c.predicate).append(NL);
        }
        for (Constraint c : constraints) {
            sb.append("  constraint ")
                .append(c.predicate)
                .append(" : ")
                .append(c.variables.stream().map(v -> v.name).collect(Collectors.joining(" "))).append(NL);
        }
        sb.append("  any codes:").append(NL);
        for (String line : anyCodes)
            sb.append("    ").append(line).append(NL);
        return sb.toString();
    }
}
