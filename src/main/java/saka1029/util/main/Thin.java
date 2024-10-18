package saka1029.util.main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Thin {

    static void usage() {
        throw new IllegalArgumentException("usage: java saka1029.util.main.Thin DIRECTORY SIZE");
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 2)
            usage();
        Path directory = Path.of(args[0]);
        if (!Files.exists(directory))
            throw new IllegalArgumentException("Path '%s' does not exist".formatted(directory));
        if (!Files.isDirectory(directory))
            throw new IllegalArgumentException("'%s' is not a directory".formatted(directory));
        int size = Integer.parseInt(args[1]);
        System.out.println("directory=" + directory);
        System.out.println("file name=" + directory.getFileName());
        List<Path> list = null;
        try (var stream = Files.walk(directory, 1)) {
            list = stream
                .filter(Files::isRegularFile)
                .sorted(Comparator.comparing(Path::getFileName))
                .toList();
        }
        System.out.println("sorted:");
        list.stream().forEach(System.out::println);
        
    }

}
