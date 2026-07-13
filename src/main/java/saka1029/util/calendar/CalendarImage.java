package saka1029.util.calendar;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CalendarImage {

    public static final String 祝日_CSV_URL = "https://www8.cao.go.jp/chosei/shukujitsu/syukujitsu.csv";
    // A4横サイズ = 297mm * 210mm
    int DPI = 300;
    float POINT_PER_INCH = 72;
    int WIDTH = (int)(11.7 * DPI);
    int HEIGHT = (int)(8.3 * DPI);
    int TOP_MARGIN = 200;           // pixcel
    int BOTTOM_MARGIN = 200;        // pixcel
    int LEFT_MARGIN = 200;          // pixcel
    int RIGHT_MARGIN = 200;         // pixcel
    int TITLE_HEIGHT = 200;
    int TITLE_POINT = 80;
    Font TITLE_FONT = new Font("SansSerif", Font.PLAIN, TITLE_POINT);
    Color TITLE_COLOR = Color.BLACK;

    int point2pixcel(int point) {
        // return (int)(point / POINT_PER_INCH  * DPI);
        return point;
    }

    public void draw(LocalDate month, String outFile) throws IOException {
        month = LocalDate.of(month.getYear(), month.getMonth(), 1);
        int boxLeft = LEFT_MARGIN;
        int boxTop = TOP_MARGIN + TITLE_HEIGHT;
        int boxWidth = WIDTH - LEFT_MARGIN - RIGHT_MARGIN;
        int boxHeight = HEIGHT - TOP_MARGIN - BOTTOM_MARGIN - TITLE_HEIGHT;
        float cellHeight = (float)boxHeight / 6;
        float cellWidth = (float)boxWidth / 7;
        try (OutputStream os = Files.newOutputStream(Path.of(outFile));
            ImageWriter iw = new ImageWriter(os, WIDTH, HEIGHT)) {
            Graphics2D g = iw.graphics();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, WIDTH, HEIGHT);
            g.setColor(TITLE_COLOR);
            g.setFont(TITLE_FONT);
            int titleStringHeight = point2pixcel(TITLE_POINT);
            int titleTop = TOP_MARGIN + (TITLE_HEIGHT - titleStringHeight) / 2 + titleStringHeight;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年 MM月");
            g.drawString(month.format(formatter), LEFT_MARGIN, titleTop);
            // タイトル領域を囲むボックス
            // g.drawRect(LEFT_MARGIN, TOP_MARGIN, boxWidth, TITLE_HEIGHT);
            // g.drawRect(boxLeft, boxTop, boxWidth, boxHeight);
            for (float y = boxTop, i = 0; i <= 6; y += cellHeight, ++i)
                g.drawLine(boxLeft, (int)y, boxLeft + boxWidth, (int)y);
            for (float x = boxLeft, i = 0; i <= 7; x += cellWidth, ++i)
                g.drawLine((int)x, boxTop, (int)x, boxTop + boxHeight);
        }
    }
}
