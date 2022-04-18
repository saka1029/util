package util.srt;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Extract {

    public void run(BufferedReader r, PrintWriter w) throws IOException {
        while (true) {
            String line = r.readLine();
            if (line == null)
                break;
            else if (line.matches("^$"))
                w.println();
            else if (line.matches("^\\d+$"))
                w.printf("[%s]%n", line);
        // 00:09:16,639 --> 00:09:20,018 
            else if (line.matches("^[0-9:,-> ]+$"))
                continue;
            else
                w.println(line);
        }
    }

    public void run(String infile) throws FileNotFoundException, IOException {
        try (BufferedReader r = new BufferedReader(new FileReader(infile));
            PrintWriter w = new PrintWriter(new FileWriter(infile + ".eng"))) {
            run(r, w);
        }
    }

    public static void main(String[] args) throws FileNotFoundException, IOException {
        new Extract().run(args[0]);
    }
}
