package saka1029.util.main;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.FileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class DriveTree {

    static class Visitor implements FileVisitor<Path> {
        final String volumeName;
        final PrintWriter writer;
        int level;

        Visitor(String volumeName, int level, PrintWriter writer) {
            this.volumeName = volumeName;
            this.writer = writer;
            this.level = level;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            writer.printf("%d %s\n", level, level == 0 ? volumeName : dir.getFileName().toString());
            ++level;
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            writer.printf("%d %s\n", level, file.getFileName().toString());
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            --level;
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
            System.err.println(exc);
            return FileVisitResult.CONTINUE;
        }
    }

    static void write(String volumeName, Path inDir, Path outDir) throws IOException {
        Path outFile = outDir.resolve(volumeName + ".txt");
        // System.out.printf("volumeName=%s inDir=%s outDIr=%s outFile=%s%n", volumeName, inDir, outDir, outFile);
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(outFile))) {
            Files.walkFileTree(inDir, new Visitor(volumeName, 0, writer));
        }
    }

    static void usage() {
        String message = String.format(
            "%nusage:%n"
            + "java %s VOLUME_NAME IN_DIR OUT_DIR%n",
            Files.class.getName());
        throw new IllegalArgumentException(message);
    }

    /**
     * USAGE:
     * java saka1029.util.main.Files VOLUME_NAME IN_DIR OUT_DIR
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            usage();
            return;
        }
        String volumeName = args[0];
        Path inDir = Paths.get(args[1]);
        Path outDir = Paths.get(args[2]);
        write(volumeName, inDir, outDir);
    }
}
