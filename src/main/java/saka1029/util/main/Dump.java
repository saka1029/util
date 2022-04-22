package saka1029.util.main;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Dump {

    static int read(InputStream input, byte[] buffer) throws IOException {
        int length = buffer.length;
        int i = 0;
        while (length > 0) {
            int read = input.read(buffer, i, length);
            if (read == -1)
                return i == 0 ? -1 : i;
            i += read;
            length -= read;
        }
        return i;
    }

    static void dump(byte[] buffer, int length, int offset) {
        StringBuilder hex = new StringBuilder();
//        StringBuilder tex = new StringBuilder();
        byte[] text = new byte[buffer.length];
        for (int i = 0, max = buffer.length; i < max; ++i) {
            hex.append(i < length
                ? String.format(" %02x", buffer[i] & 0xff)
                : "   ");
            text[i] = i >= length
                ? (byte)' '
                : buffer[i] >= 0 && buffer[i] < ' ' ? (byte)'.' : buffer[i];
//            tex.append(i < length
//                ? (buffer[i] < ' ' ? '.' : (char)buffer[i])
//                : ' ');
        }
//        System.out.printf("%8d:%s %s%n", offset, hex, tex);
        System.out.printf("%8d:%s %s%n", offset, hex, new String(text));
    }

    static final int BUFFER_SIZE = 20;

    static void dump(InputStream input) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        int offset = 0;
        while (true) {
            int length = read(input, buffer);
            if (length == -1)
                break;
            dump(buffer, length, offset);
            offset += length;
        }
    }

    public static void main(String[] args) throws IOException {
        switch (args.length) {
        case 0:
            dump(System.in);
            break;
        case 1:
            try (InputStream input = new FileInputStream(args[0])) {
                dump(input);
            }
            break;
        default:
            throw new IllegalArgumentException("Usage: java Dump [INFILE]");
        }
    }

}
