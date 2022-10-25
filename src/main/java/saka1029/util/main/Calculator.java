package saka1029.util.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import saka1029.util.calculator.EvaluationException;
import saka1029.util.calculator.Expression;
import saka1029.util.calculator.ParseException;

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
        BufferedReader r = new BufferedReader(reader);
        PrintWriter w = new PrintWriter(writer);
        String line;
        while ((line = r.readLine()) != null) {
            line = line.replaceFirst("#.*$", "");
            String[] split = line.trim().split("=");
            try {
                switch (split.length) {
                case 1:
                    String exp = split[0].trim();
                    if (exp.isEmpty())
                        continue;
                    w.println(eval(exp));
                    break;
                case 2:
                    put(split[0].trim(), split[1].trim());
                    break;
                default:
                    w.println("! too many '='");
                }
            } catch (ParseException | EvaluationException e) {
                w.println("! " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
    }

}
