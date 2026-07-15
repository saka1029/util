package saka1029.util.calendar;

import java.awt.Graphics2D;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

public class CalendarWriter implements Closeable {

    // A4横サイズ = 297mm * 210mm
    static final int DPI = 300;
    static final int WIDTH =  (int) (11.7 * DPI);
    static final int HEIGHT = (int) (8.3 * DPI);

    final ImageWriter iw;
    final OutputStream os;
    final Graphics2D g;
    final int w, h;
    final LocalDate m;

    CalendarWriter(int width, int height, LocalDate yearMonth, String outFile) throws IOException {
        this.w = width;
        this.h = height;
        this.m = LocalDate.of(yearMonth.getYear(), yearMonth.getDayOfMonth(), 1);
        this.os = Files.newOutputStream(Path.of(outFile));
        this.iw = new ImageWriter(os, width, height);
        this.g = iw.graphics();
    }

    @Override
    public void close() throws IOException {
        os.close();
        iw.close();
        // gはiwがdispose()する。
    }

    public static void draw(LocalDate yearMonth, String outFile) throws IOException {
        try (CalendarWriter cw = new CalendarWriter(WIDTH, HEIGHT, yearMonth, outFile)) {
            cw.drawAll();
        }
    }

    void title() {
    }

    void header() {
    }

    void day() {
    }

    void days() {
    }

    void drawAll() {
        title();
        header();
        days();
    }
}
