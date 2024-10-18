package saka1029.util.main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

public class Mabiku {

    static void usage() {
        throw new IllegalArgumentException("usage: java saka1029.util.main.Thin IN_DIR OUT_DIR SIZE");
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
        if (!Files.exists(outDir))
            Files.createDirectories(outDir);
        int selectSize = Integer.parseInt(args[2]);
        System.out.println("directory=" + inDir);
        System.out.println("file name=" + inDir.getFileName());
        List<Path> list = null;
        try (var stream = Files.walk(inDir, 1)) {
            list = stream
                .filter(Files::isRegularFile)
                .sorted(Comparator.comparing(Path::getFileName))
                .toList();
        }
        System.out.println("sorted:");
        list.stream().forEach(System.out::println);
        System.out.println("selected:");
        int size = list.size();
        double pitch = (double) size / selectSize;
        for (int i = 0; i < size; i = (int) (i + pitch))
            System.out.println(list.get(i));
    }

}
