package saka1029.util.main;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * 階層の深い位置にあるファイルを最上位にコピーします。
 * <pre>
 * directory/
 *   +--- sub1/
 *   |     +--- f0.jpg
 *   |     +--- f1.jpg
 *   +--- sub2/
 *   |     +--- sub3/
 *   |           +--- f0.png
 *   +--- 000000.jpg              (directory/sub1/f0.jpgのコピー)
 *   +--- 000001.jpg              (directory/sub1/f2.jpgのコピー)
 *   +--- 000002.png              (directory/sub2/sub3/f0.pngのコピー)
 * </pre>
 * ファイル名はコピー順に6桁の連番とします。
 * 拡張子は同じものとします。
 */
public class Flat {

    static final String USAGE = "java Flat DIRECTORY";

    public static void main(String[] args) throws IOException {
        if (args.length != 1)
            throw new IllegalArgumentException(USAGE);
        Path dir = Path.of(args[0]);
        var visitor = new SimpleFileVisitor<Path>() {
            int i = 0;

            String ext(Path path) {
                String name = path.toString();
                int i = name.lastIndexOf('.');
                return i == -1 ? "": name.substring(i);
            }

            Path destination(Path path) {
                return dir.resolve(String.format("%06d%s", i++, ext(path)));
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path dest = destination(file);
                Files.copy(file, dest);
                return FileVisitResult.CONTINUE;
            }
        };
        Files.walkFileTree(dir, visitor);
        System.out.println(dir + " " + visitor.i + " files(s)");
    }

}
