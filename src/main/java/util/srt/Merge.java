package util.srt;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class Merge {

    static final String NL = String.format("%n");
    
    Map<String, String> readSrt(BufferedReader s) throws IOException {
        Map<String, String> map = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        String no = null;
        while (true) {
            String line = s.readLine();
            if (line == null) break;
            if (line.matches("^\\d+$")) {
                if (no != null)
                    map.put(no, sb.toString());
                no = String.format("[%s]", line);
                sb.setLength(0);
                sb.append(line).append(NL);
            } else if (line.matches("^[0-9:,-> ]+$"))
                sb.append(line).append(NL);
        }
        if (no != null)
            map.put(no, sb.toString());
        return map;
    }

    Map<String, String> map = new HashMap<>();
    
    public void run(BufferedReader s, BufferedReader j, PrintWriter w) throws IOException {
        Map<String, String> map = readSrt(s);
        while (true) {
            String line = j.readLine();
            if (line == null)
                break;
            else if (line.matches("^\\[\\d+\\]$"))
                w.print(map.get(line));
            else
                w.printf("%s%n", line);
        }
    }

    public void run(String srt, String jpn) throws IOException {
        try (BufferedReader s = new BufferedReader(new FileReader(srt));
            BufferedReader j = new BufferedReader(new FileReader(jpn));
            PrintWriter w = new PrintWriter(new FileWriter(jpn + ".srt"))) {
            run(s, j, w);
        }
    }

    public static void main(String[] args) throws FileNotFoundException, IOException {
        new Merge().run(args[0], args[1]);
    }
}
