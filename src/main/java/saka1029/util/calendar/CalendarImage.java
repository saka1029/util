package saka1029.util.calendar;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
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
    int WIDTH = (int) (11.7 * DPI);
    int HEIGHT = (int) (8.3 * DPI);
    static final String FONT_NAME = "SansSerif";
    static final Color TITLE_COLOR = Color.BLACK;
    static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年 MM月");
    static final String[] WEEK_NAME = {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};

    void drawCenter(Graphics2D g, Font font, int left, int top, int width, int height, String string) {
        Rectangle2D r = font.getStringBounds(string, g.getFontRenderContext());
        float l = (float)(left + (width - r.getWidth()) / 2);
        float t = (float)(top + (height - r.getY()) / 2);
        g.setFont(font);
        g.drawString(string, l, t);
        g.drawRect(left, top, width, height);
    }

    public void draw(LocalDate month, String outFile) throws IOException {
        int topMargin = (int) (HEIGHT * 1.0 / 12);
        int bottomMargin = (int) (HEIGHT * 1.0 / 12);
        int leftMargin = (int) (WIDTH * 1.0 / 12);
        int rightMargin = (int) (WIDTH * 1.0 / 12);
        int titleHeight = (int) (HEIGHT * 1.0 / 10);
        int headerHeight = (int) (HEIGHT * 1.0 / 25);
        int titlePoint = (int) (HEIGHT * 3.0 / 100);
        int headerPoint = (int) (HEIGHT * 3.0 / 200);
        Font titleFont = new Font(FONT_NAME, Font.PLAIN, titlePoint);
        Font headerFont = new Font(FONT_NAME, Font.PLAIN, headerPoint);
        LocalDate firstDay = LocalDate.of(month.getYear(), month.getMonth(), 1);
        int boxLeft = leftMargin;
        int boxTop = topMargin + titleHeight + headerHeight;
        int boxWidth = WIDTH - leftMargin - rightMargin;
        int boxHeight = HEIGHT - topMargin - bottomMargin - titleHeight;
        float cellHeight = (float) boxHeight / 6;
        float cellWidth = (float) boxWidth / 7;
        try (OutputStream os = Files.newOutputStream(Path.of(outFile));
                ImageWriter iw = new ImageWriter(os, WIDTH, HEIGHT)) {
            Graphics2D g = iw.graphics();
            // 全体を白く塗る
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, WIDTH, HEIGHT);
            // タイトル（年月）
            g.setColor(TITLE_COLOR);
            drawCenter(g, titleFont, leftMargin, topMargin, boxWidth, titleHeight, firstDay.format(formatter));
            // ヘッダ（曜日名）
            g.setFont(headerFont);
            for (float x = boxLeft, i = 0; i < WEEK_NAME.length; x += cellWidth, ++i)
                drawCenter(g, headerFont, (int)x, topMargin + titleHeight, (int)cellWidth, headerHeight, WEEK_NAME[(int)i]);
                // g.drawString(WEEK_NAME[(int)i], x, boxTop);
            // 水平線
            for (float y = boxTop, i = 0; i <= 6; y += cellHeight, ++i)
                g.drawLine(boxLeft, (int) y, boxLeft + boxWidth, (int) y);
            // 垂直線
            for (float x = boxLeft, i = 0; i <= 7; x += cellWidth, ++i)
                g.drawLine((int) x, boxTop, (int) x, boxTop + boxHeight);
        }
    }
}
