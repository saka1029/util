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

    final BufferedReader reader;
    final PrintWriter writer;
    final Map<String, Expression> variables = new HashMap<>();

    public Calculator(Reader reader, Writer writer) {
        this.reader = new BufferedReader(reader);
        this.writer = new PrintWriter(writer);
    }
    
    public double eval(String expression) throws EvaluationException, ParseException {
        return Expression.of(expression).eval(variables);
    }
    
    public void put(String name, String expression) throws ParseException {
        if (!isVariableName(name))
            throw new ParseException("'%s' is not variable", name);
        variables.put(name, Expression.of(expression));
    }

    static boolean isVariableName(String s) {
        int length = s.length();
        if (length <= 0 || !Expression.idFirst(s.charAt(0)))
            return false;
        for (int i = 1; i < length; ++i)
            if (!Expression.idRest(s.charAt(1)))
                return false;
        return true;
    }

    void print(Object message) {
        writer.println(message);
    }

    void error(String message) {
        writer.println("! " + message);
    }

    public void run() throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.replaceFirst("#.*$", "");
            String[] split = line.trim().split("=");
            Expression exp;
            try {
                switch (split.length) {
                case 0:
                    continue;
                case 1:
                    print(eval(split[0].trim()));
                    break;
                case 2:
                    put(split[0].trim(), split[1].trim());
                    break;
                default:
                    error("too many '='");
                }
            } catch (ParseException | EvaluationException e) {
                error(e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
    }

}
