package util.io;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Windowsのtreeコマンドの出力を読み取ります。
 * ファイルまたはディレクトリの先頭文字列は以下のいずれかを１回以上繰り返したものです。
 * (1) "    " (半角スペース４個）
 * (2) "│  " (縦棒+半角スペース２個)
 * (3) "├─" (├+横棒)
 * (4) "└─" (└+横棒)
 * 繰り返した数がパスにおける相対位置を示します。
 * この先頭文字が横棒を含む場合はディレクトリです。
 *
 * [サンプル入力]
 * <pre>
 * フォルダー パスの一覧:  ボリューム BUFFALO2TB
 * ボリューム シリアル番号は 4230-073B です
 * D:\
 * ├─@arc
 * ├─@done
 * ├─arc
 * ├─Downloads
 * ├─git
 * │  ├─CSP-J
 * │  │  │  config
 * │  │  │  description
 * │  │  │  HEAD
 * │  │  │
 * │  │  ├─hooks
 * │  │  │      applypatch-msg.sample
 * │  │  │      commit-msg.sample
 * │  │  │      post-update.sample
 * │  │  │      pre-applypatch.sample
 * │  │  │      pre-commit.sample
 * │  │  │      pre-push.sample
 * │  │  │      pre-rebase.sample
 * │  │  │      prepare-commit-msg.sample
 * │  │  │      update.sample
 * │  │  │
 * <pre>
 */
public class WindowsTreeReader implements Closeable {

    public static final Charset DEFAULT_CHARSET = Charset.forName("MS932");
    static final Pattern VOLUME = Pattern.compile("^フォルダー パスの一覧:  ボリューム\\s*(?<N>.+)");
    static final Pattern ENTRY = Pattern.compile("^(?<P>(│  |    |├─|└─)+)(?<N>.*)$");
    static final String DIR_CHAR = "─";

    final BufferedReader reader;
    String[] path = new String[4];
    String volume = "";

    public WindowsTreeReader(Reader reader) {
        this.reader = new BufferedReader(reader);
    }

    int level(String preceedings) {
        String all = preceedings.replace("  ", "＊");    // 半角スペース２個を１文字に置き換えます。
        if (all.length() < 2)
            throw new IllegalArgumentException("preceedings=" + preceedings);
        return all.length() / 2 - 1;
    }

    String[] path(int level) {
        int size = level + 1;
        String[] result = new String[size];
        System.arraycopy(path, 0, result, 0, size);
        return result;
    }

    void addPath(int index, String entry) {
        while (index >= path.length)
            path = Arrays.copyOf(path, path.length * 2);
        path[index] = entry;
    }

    public WindowsTreeEntry read() throws IOException {
        while (true) {
            String line = reader.readLine();
            if (line == null) return null;
            Matcher v =  VOLUME.matcher(line);
            if (v.find())
                volume = v.group("N");
            else {
                Matcher entry = ENTRY.matcher(line);
                if (entry.find()) {
                    String name = entry.group("N");
                    if (name.length() > 0) {
                        String preceedings = entry.group("P");
                        int level = level(preceedings);
                        addPath(level, name);
                        return new WindowsTreeEntry(volume, path(level), !preceedings.contains(DIR_CHAR));
                    }
                }
            }
        }
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }

}
