package saka1029.util.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import saka1029.util.calculator.EvaluationException;
import saka1029.util.calculator.Expression;
import saka1029.util.calculator.ParseException;

/**
 * <pre>
 * SYNTAX
 * line        = statement { ';' statement }
 * statement   = [ id '=' ] expression
 * </pre>
 */
public class Calculator {

    final Map<String, Expression> variables = new HashMap<>();

    public static boolean isVariableName(String s) {
        int length = s.length();
        if (length <= 0 || !Expression.idFirst(s.charAt(0)))
            return false;
        for (int i = 1; i < length; ++i)
            if (!Expression.idRest(s.charAt(1)))
                return false;
        return true;
    }

    public double eval(String expression) throws EvaluationException, ParseException {
        return Expression.of(expression).eval(variables);
    }

    public Expression get(String name) {
        return variables.get(name);
    }

    public void put(String name, String expression) throws ParseException {
        if (!isVariableName(name))
            throw new ParseException("'%s' is not variable", name);
        variables.put(name, Expression.of(expression));
    }

    public void run(Reader reader, Writer writer) throws IOException {
        run(reader, writer, "");
    }

    public void run(Reader reader, Writer writer, String prompt) throws IOException {
        BufferedReader r = new BufferedReader(reader);
        PrintWriter w = new PrintWriter(writer, true);
        String line;
        w.print(prompt);
        w.flush();
        while ((line = r.readLine()) != null) {
            line = line.replaceFirst("#.*$", "");
            String[] statements = line.split(";");
            for (String statement : statements) {
                String[] split = statement.trim().split("=");
                try {
                    switch (split.length) {
                    case 1:
                        String exp = split[0].trim();
                        if (exp.isEmpty())
                            continue;
                        if (exp.startsWith("."))
                            w.println(get(exp.substring(1).trim()));
                        else
                            w.println(eval(exp));
                        break;
                    case 2:
                        String name = split[0].trim();
                        put(name, split[1].trim());
                        w.println(eval(name));
                        break;
                    default:
                        w.println("! too many '='");
                    }
                } catch (ParseException | EvaluationException e) {
                    w.println("! " + e.getMessage());
                }
                w.print(prompt);
                w.flush();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Calculator c = new Calculator();
        Reader reader = new InputStreamReader(System.in);
        Writer writer = new OutputStreamWriter(System.out);
        c.run(reader, writer, "> ");
    }
}
