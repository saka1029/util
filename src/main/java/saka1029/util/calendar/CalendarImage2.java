package saka1029.util.calendar;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CalendarImage2 {

    public static final String 祝日_CSV_URL = "https://www8.cao.go.jp/chosei/shukujitsu/syukujitsu.csv";
    // A4横サイズ = 297mm * 210mm
    int DPI = 300;
    int WIDTH = (int) (11.7 * DPI);
    int HEIGHT = (int) (8.3 * DPI);
    static final String FONT_NAME = "SansSerif";
    static final Color TITLE_COLOR = Color.BLACK;
    static final Color LINE_COLOR = Color.BLACK;
    static final Color HEADER_COLOR = Color.BLACK;
    static final Color DAY_COLOR = Color.BLACK;
    static final Color NDAY_COLOR = Color.LIGHT_GRAY;
    static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年 M月");
    static final String[] WEEK_NAME = {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};
    static final Stroke DEFAULT_STROKE = new BasicStroke(2);

    void drawCenter(Graphics2D g, Font font, Color color, boolean solid, int left, int top, int width, int height, String string) {
        Rectangle2D r = font.getStringBounds(string, g.getFontRenderContext());
        float l = (float)(left + (width - r.getWidth()) / 2);
        float t = (float)(top + (height - r.getY()) / 2);
        g.setFont(font);
        if (solid) {
            g.setColor(color);
            g.drawString(string, l, t);
        } else {
            g.setColor(color);
            g.setStroke(new BasicStroke(8)); // 縁取りの太さを調整
            g.draw(font.createGlyphVector(g.getFontRenderContext(), string).getOutline(l, t));
            g.setStroke(DEFAULT_STROKE);
            // 文字本体
            g.setColor(Color.WHITE);
            g.drawString(string, l, t);
        }
        g.setColor(LINE_COLOR);
        g.drawRect(left, top, width, height);
    }

    public void draw(LocalDate month, String outFile) throws IOException {
        int topMargin = (int) (HEIGHT * 1.0 / 12);
        int bottomMargin = (int) (HEIGHT * 1.0 / 12);
        int leftMargin = (int) (WIDTH * 1.0 / 12);
        int rightMargin = (int) (WIDTH * 1.0 / 12);
        int titleHeight = (int) (HEIGHT * 1.0 / 10);
        int headerHeight = (int) (HEIGHT * 1.0 / 25);
        int titlePoint = (int) (HEIGHT * 3.0 / 50);
        int headerPoint = (int) (HEIGHT * 3.0 / 200);
        int dayPoint = (int) (HEIGHT * 3.0 / 60);
        Font titleFont = new Font(FONT_NAME, Font.PLAIN, titlePoint);
        Font headerFont = new Font(FONT_NAME, Font.PLAIN, headerPoint);
        Font dayFont = new Font(FONT_NAME, Font.BOLD, dayPoint);
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
            g.setStroke(DEFAULT_STROKE);
            // 全体を白く塗る
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, WIDTH, HEIGHT);
            // タイトル（年月）
            g.setColor(TITLE_COLOR);
            drawCenter(g, titleFont, TITLE_COLOR, true, leftMargin, topMargin, boxWidth, titleHeight, firstDay.format(formatter));
            // ヘッダ（曜日名）
            g.setFont(headerFont);
            for (float x = boxLeft, i = 0; i < WEEK_NAME.length; x += cellWidth, ++i)
                drawCenter(g, headerFont, HEADER_COLOR, true, (int)x, topMargin + titleHeight, (int)cellWidth, headerHeight, WEEK_NAME[(int)i]);
                // g.drawString(WEEK_NAME[(int)i], x, boxTop);
            // 水平線
            LocalDate day = firstDay.minusDays(firstDay.getDayOfWeek().getValue() % 7);
            for (float y = boxTop, j = 0; j < 6; y += cellHeight, ++j) {
                for (float x = boxLeft, i = 0; i < 7; x += cellWidth, ++i, day = day.plusDays(1)) {
                    String dayString = "" + day.getDayOfMonth();
                    Color color = day.getMonthValue() == firstDay.getMonthValue() ? DAY_COLOR : NDAY_COLOR;
                    DayOfWeek week = day.getDayOfWeek();
                    boolean solid = color.equals(NDAY_COLOR) || week != DayOfWeek.SUNDAY; // && week != DayOfWeek.SATURDAY;
                    drawCenter(g, dayFont, color, solid, (int)x, (int)y, (int)cellWidth, (int)cellHeight, dayString);
                }
            }
            // for (float y = boxTop, i = 0; i <= 6; y += cellHeight, ++i)
            //     g.drawLine(boxLeft, (int) y, boxLeft + boxWidth, (int) y);
            // // 垂直線
            // for (float x = boxLeft, i = 0; i <= 7; x += cellWidth, ++i)
            //     g.drawLine((int) x, boxTop, (int) x, boxTop + boxHeight);
        }
    }
}
