package saka1029.util.main;

import java.io.IOException;
import java.nio.file.StandardCopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

public class Mabiku {

    static void usage() {
        throw new IllegalArgumentException(
            "usage: java %s IN_DIR OUT_DIR SELECT_SIZE"
            .formatted(Mabiku.class.getName()));
    }

    static int run(Path inDir, Path outDir, int selectSize) throws IOException {
        List<Path> list = null;
        try (var stream = Files.walk(inDir, 1)) {
            list = stream
                .filter(Files::isRegularFile)
                .sorted(Comparator.comparing(Path::getFileName))
                .toList();
        }
        // System.out.println("sorted:");
        // list.stream().forEach(System.out::println);
        // System.out.println("selected:");
        if (!Files.exists(outDir))
            Files.createDirectories(outDir);
        int size = list.size(), count = 0;
        if (selectSize >= size)
            throw new RuntimeException("select size(%d) >= size(%d)".formatted(selectSize, size));
        double pos = 0, pitch = (double) size / selectSize;
        for (int i = 0; i < size; pos += pitch, i = (int)pos) {
            Path inFile = list.get(i);
            Path outFile = outDir.resolve(inFile.getFileName());
            Files.copy(inFile, outFile, StandardCopyOption.REPLACE_EXISTING);
            ++count;
        }
        return count;
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 3)
            usage();
        Path inDir = Path.of(args[0]);
        if (!Files.exists(inDir))
            throw new IllegalArgumentException("Path '%s' does not exist".formatted(inDir));
        if (!Files.isDirectory(inDir))
            throw new IllegalArgumentException("'%s' is not a directory".formatted(inDir));
        Path outDir = Path.of(args[1]);
        int selectSize = Integer.parseInt(args[2]);
        System.out.println("IN_DIR=" + inDir.toAbsolutePath());
        System.out.println("OUT_DIR=" + outDir.toAbsolutePath());
        System.out.println("SELECT_SIZE=" + selectSize);
        int count = run(inDir, outDir, selectSize);
        System.out.printf("output file count = %d%n", count);
    }

}
