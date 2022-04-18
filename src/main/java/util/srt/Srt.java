package util.srt;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Srt {

    public static final Pattern NO_PAT = Pattern.compile("\\d+");
    public static final String TIME_REX = "(\\d\\d):(\\d\\d):(\\d\\d),(\\d\\d\\d)";
    public static final Pattern TIME_PAT = Pattern.compile(TIME_REX);
    public static final Pattern TIMES_PAT = Pattern.compile(TIME_REX + "\\s*-->\\s*" + TIME_REX + "\\s*");
    public static final String UTF_8_BOM = "\ufeff";

    public static class Script {

        public int no;
        public long begin, end;
        public String first;
        public String second;

        @Override
        public String toString() {
            String r = String.format("%d%n%s --> %s%n", no, t(begin), t(end));
            if (first != null) r += String.format("%s%n", first);
            if (second != null) r += String.format("%s%n", second);
            r += String.format("%n");
            return r;
        }
    }
    
    final List<Script> scripts = new ArrayList<>();
    final Map<Integer, Script> map = new HashMap<>();
    
    private void add(Script script) {
        scripts.add(script);
        map.put(script.no, script);
    }
    
    public static long t(String s) { return Long.parseLong(s); }
    
    public static long t(String h, String m, String s, String f) {
        return ((((t(h) * 60) + t(m)) * 60) + t(s)) * 1000 + t(f);
    }

    public static String t(long t) {
        long f = t % 1000;  t /= 1000;
        long s = t % 60; t /= 60;
        long m = t % 60; t /= 60;
        long h = t;
        return String.format("%02d:%02d:%02d,%03d", h, m, s, f);
    }

    public void read(BufferedReader reader) throws IOException {
        int seq = 0;
        Script s = null;
        boolean first = true;
        while (true) {
            String line = reader.readLine();
            if (line == null) break;
            if (first) {
                first = false;
                if (line.startsWith(UTF_8_BOM))
                    line = line.substring(1);
            }
            ++seq;
            Matcher m;
            if ((m = NO_PAT.matcher(line)).matches()) {
                if (s != null) add(s);
                s = new Script();
                s.no = Integer.parseInt(m.group());
            } else if ((m = TIMES_PAT.matcher(line)).matches()) {
                if (s == null) throw new IOException("NO missing:" + seq + ":" + line);
                s.begin = t(m.group(1), m.group(2), m.group(3), m.group(4));
                s.end = t(m.group(5), m.group(6), m.group(7), m.group(8));
            } else if (line.equals("")) {
                continue;
            } else {
                if (s == null) throw new IOException("NO, TIMEがありません:" + seq + ":" + line);
                if (s.first == null) s.first = line;
                else if (s.second == null) s.second = line;
                else throw new IOException("スクリプトが2行を超えています:" + seq + ":" + line);
            }
        }
        if (s != null) add(s);
    }

    public void read(File file, String encoding) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding))) {
            read(reader);
        }
    }
    
    public void write(BufferedWriter writer) throws IOException {
        for (Script s : scripts)
            writer.write(s.toString());
    }

    public void write(File file, String encoding, boolean writeBom) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), encoding))) {
            if (writeBom) writer.write(UTF_8_BOM);
            write(writer);
        }
    }
    
    public long time(String s) {
        int sign = 1;
        if (s.startsWith("-")) {
            sign = -1;
            s = s.substring(1);
        }
        Matcher m = TIME_PAT.matcher(s);
        if (!m.matches()) throw new IllegalArgumentException("Illegal time pattern s : " + s);
        return sign * t(m.group(1), m.group(2), m.group(3), m.group(4));
    }

    /**
     * 字幕のタイミングをずらします。
     * 
     * @param orgBegin ずらす字幕の最初のセリフの時刻をHH:MM:SS,FFF形式で指定します。
     * @param orgEnd ずらす字幕の最後のセリフの時刻をHH:MM:SS,FFF形式で指定します。
     * @param destBegin ずらした後の最初のセリフの時刻をHH:MM:SS,FFF形式で指定します。
     * @param destEnd ずらした後の最後のセリフの時刻をHH:MM:SS,FFF形式で指定します。
     */
    public void scale(String orgBegin, String orgEnd, String destBegin, String destEnd) {
        long ob = time(orgBegin);
        long oe = time(orgEnd);
        long db = time(destBegin);
        long de = time(destEnd);
        double k = ((double)(de - db)) / ((double)(oe - ob));
        for (Script s : scripts) {
            s.begin = (long)((s.begin - ob) * k + db);
            s.end = (long)((s.end - ob) * k + db);
        }
    }
    
    public void add(String offset) {
        long o = time(offset);
        for (Script s : scripts) {
            s.begin = s.begin + o;
            s.end = s.end + o;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Script s : scripts)
            sb.append(s);
        return sb.toString();
    }
}
