package saka1029.util.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import saka1029.util.calculator.Expression;

public class Calculator {

    final BufferedReader reader;
    final PrintWriter writer;
    final Map<String, Expression> variables = new HashMap<>();

    public Calculator(Reader reader, Writer writer) {
        this.reader = new BufferedReader(reader);
        this.writer = new PrintWriter(writer);
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

    void expression(String e) {

    }

    void assign(String v, String e) {

    }

    public void run() throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.replaceFirst("#.*$", "");
            String[] split = line.trim().split("=");
            switch (split.length) {
            case 0:
                continue;
            case 1:
                expression(split[0]);
                break;
            case 2:
                assign(split[0].trim(), split[1].trim());
                break;
            }
        }
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
    }

}
