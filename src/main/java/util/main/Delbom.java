package util.main;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Delbom {

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.err.println("usage: delbom INFILE OUTFILE");
            System.exit(1);
        }
        try (InputStream in = new FileInputStream(args[0]);
            OutputStream out = new FileOutputStream(args[1])) {
            in.read(); in.read(); in.read();
            byte[] buffer = new byte[4096];
            int length;
            while ((length = in.read(buffer)) != -1)
                out.write(buffer, 0, length);
        }
    }

}
