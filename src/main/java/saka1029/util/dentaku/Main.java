package saka1029.util.dentaku;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;

public class Main {

    static void run(Reader input, Writer output, String prompt) throws IOException {
        BufferedReader in = new BufferedReader(input);
        PrintWriter out = new PrintWriter(output, true);
        Context context = Context.of();
        while (true) {
            out.print(prompt);
            out.flush();
            String line = in.readLine();
            if (line == null)
                break;
            try {
                Expression e = Parser.parse(line);
                Vector v = e.eval(context);
                if (v != Vector.NaN)
                    out.println(v);
            } catch (VectorException ex) {
                out.println(ex.getMessage());
            }
        }
        out.println();
    }

    static void usage() {
        System.err.printf("java %s%n", Main.class.getName());
        System.exit(1);
    }

    public static void main(String[] args) throws IOException {
        // usage();
        Reader input = new InputStreamReader(System.in);
        Writer output = new OutputStreamWriter(System.out);
        String prompt = "    ";
        run(input, output, prompt);
    }

}
