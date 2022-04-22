package saka1029.util.main;


import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.regex.Pattern;

import saka1029.util.io.WindowsTreeEntry;
import saka1029.util.io.WindowsTreeReader;

/**
 * ドライブごとのtreeコマンド実行結果（/drivesにあるファイル）
 * についてgrepします。
 * usage:
 * java util.main.Drives [-f|-d|-a] [-t TOPDIR] 検索文字列
 * -f ファイルのみ検索
 * -d ファイルのみ検索
 * -a すべてを検索
 * 検索文字列 以下のワイルドカード使用可能です。
 *            *: 0文字以上の任意の文字列
 *            ?: 任意の1文字
 *
 */
public class Drives {

    static final File TOP_DIRECTORY = new File(
        new File(System.getProperty("user.home")),
        "git/history/drives");

    enum Target {
        FILE, DIRECTORY, ANY;
    }

    final Target target;
    final Pattern pattern;

    static Pattern pattern(String pat) {
        pat = pat.replace(".", "\\\\.")
            .replace("\\", "/")
            .replace("?", ".")
            .replace("*", ".*");
        return Pattern.compile(pat, Pattern.CASE_INSENSITIVE);
    }

    Drives(Target target, String pattern) {
        this.target = target;
        this.pattern = pattern(pattern);
    }

    boolean isTarget(boolean isFile) {
        return target == Target.ANY
            || target == Target.FILE && isFile
            || target == Target.DIRECTORY && !isFile;
    }

    void run(File file) throws IOException {
        if (file.isDirectory())
            for (File child : file.listFiles())
                run(child);
        else
            try (Reader r = new FileReader(file, WindowsTreeReader.DEFAULT_CHARSET);
                WindowsTreeReader reader = new WindowsTreeReader(r)) {
                WindowsTreeEntry e;
                while ((e = reader.read()) != null)
                    if (isTarget(e.isFile) && pattern.matcher(e.path()).find())
                        System.out.println(e);
            }
    }

    static String USAGE = ""
        + "usage:  java util.main.Drives [-f|-d|-a] [-t TOPDIR] 検索文字列%n"
        + " -f ファイルのみ検索%n"
        + " -d ファイルのみ検索%n"
        + " -a すべてを検索%n"
        + " -t TOPDIR treeファイルのある場所%n";

    static void usage() {
        throw new IllegalArgumentException(USAGE.formatted());
    }

    public static void main(String[] args) throws IOException {
        // System.out.println("args=" + Arrays.toString(args));
        Target target = Target.ANY;
        String pattern = null;
        File top = TOP_DIRECTORY;
        for (int i = 0, size = args.length; i < size; ++i)
        	if (args[i].startsWith("-"))
        		switch (args[i]) {
        		case "-f" : target = Target.FILE; break;
        		case "-d" : target = Target.DIRECTORY; break;
        		case "-a" : target = Target.ANY; break;
        		case "-t" :
        			if (++i < args.length)
        				top = new File(args[i]);
        			else
        				usage();
        			break;
        		}
            else if (pattern != null)
                usage();
            else
                pattern = args[i];
        if (pattern == null) usage();
        System.out.println("top=" + top);
        new Drives(target, pattern).run(top);
    }
}
