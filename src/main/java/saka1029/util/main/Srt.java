package saka1029.util.main;


import java.io.File;
import java.io.IOException;

public class Srt {

    static final String USAGE = String.format(
        "java Srt 入力ファイル 出力ファイル 入力開始時刻 入力終了時刻 出力開始時刻 出力終了時刻%n"
        + "    時刻の形式: HH:MM:SS,FFF%n");

    static void usage() {
        System.err.println(USAGE);
        throw new IllegalArgumentException();
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 6) usage();
        String encoding = System.getProperty("file.encoding");
        File in = new File(args[0]);
        if (!in.exists())
            throw new IOException("ファイルがありません: " + in);
        File out = new File(args[1]);
        saka1029.util.srt.Srt srt = new saka1029.util.srt.Srt();
        srt.read(in, encoding);
        srt.scale(args[2], args[3], args[4], args[5]);
        srt.write(out, encoding, true);
        System.out.println("done.");
    }
}
