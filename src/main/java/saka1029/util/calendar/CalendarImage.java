package saka1029.util.calendar;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.Closeable;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.chrono.JapaneseChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.imageio.IIOException;

public class CalendarImage {

    public static final String 祝日_CSV_URL = "https://www8.cao.go.jp/chosei/shukujitsu/syukujitsu.csv";
    static Map<LocalDate, String> holidays = null;
    static void getHolidays() throws IOException {
        if (holidays != null)
            return;
        holidays = new HashMap<>();
        URL url;
        try {
            url = new URI(祝日_CSV_URL).toURL();
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/M/d");
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.connect();
        try (Closeable c = () -> connection.disconnect();
            InputStream is = connection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "SJIS"))) {
            String line = br.readLine();    // skip header
            if (line == null)
                throw new IIOException("祝日CSVが空です");
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                holidays.put(LocalDate.parse(fields[0], formatter), fields[1]);        
            }
        }
    }

    // A4横サイズ = 297mm * 210mm
    int DPI = 300;
    int WIDTH =  (int) (11.7 * DPI);
    int HEIGHT = (int) (8.3 * DPI);
    static final String FONT_NAME = "SansSerif";
    static final Color TITLE_COLOR = Color.BLACK;
    static final Color LINE_COLOR = Color.BLACK;
    static final Color HEADER_COLOR = Color.BLACK;
    static final Color DAY_COLOR = Color.BLACK;
    static final Color NDAY_COLOR = Color.LIGHT_GRAY;
    static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年 M月");
    static final DateTimeFormatter formatterJPN = DateTimeFormatter.ofPattern("GGGGy年", Locale.JAPAN)
        .withChronology(JapaneseChronology.INSTANCE)
        .withResolverStyle(ResolverStyle.SMART);

    static final String[] WEEK_NAME = {"日", "月", "火", "水", "木", "金", "土"};
    static final Stroke DEFAULT_STROKE = new BasicStroke(2);

    void drawText(Graphics2D g, Font font, Color color, boolean solid, boolean center, int left, int top, int width, int height, String string) {
        Rectangle2D r = font.getStringBounds(string, g.getFontRenderContext());
        float l = center ? (float)(left + (width - r.getWidth()) / 2) : left;
        float t = (float)(top + (height - r.getY()) / 2);
        g.setFont(font);
        g.setColor(color);
        if (!solid) {
            g.setStroke(new BasicStroke(8)); // 縁取りの太さを調整
            g.draw(font.createGlyphVector(g.getFontRenderContext(), string).getOutline(l, t));
            g.setStroke(DEFAULT_STROKE);
            // 文字本体
            g.setColor(Color.WHITE);
        }
        g.drawString(string, l, t);
    }

    public void draw(LocalDate from, int nMonth, String outFilePattern) throws IOException {
        getHolidays();
        for (int i = 0; i < nMonth; ++i) {
            LocalDate month = from.plusMonths(i);
            String outFile = outFilePattern.formatted(month.getYear(), month.getMonthValue());
            draw(month, outFile);
        }
    }

    void draw(LocalDate month, String outFile) throws IOException {
        int topMargin = (int) (HEIGHT * 1.0 / 12);
        int bottomMargin = (int) (HEIGHT * 1.0 / 12);
        int leftMargin = (int) (WIDTH * 1.0 / 12);
        int rightMargin = (int) (WIDTH * 1.0 / 12);
        int titleHeight = (int) (HEIGHT * 1.0 / 10);
        int headerHeight = (int) (HEIGHT * 1.0 / 25);
        int titlePoint = (int) (HEIGHT * 1.0 / 15);
        int headerPoint = (int) (HEIGHT * 1.0 / 50);
        int dayPoint = (int) (HEIGHT * 1.0 / 30);
        int holidayPoint = (int) (HEIGHT * 1.0 / 80);
        Font titleFont = new Font(FONT_NAME, Font.PLAIN, titlePoint);
        Font headerFont = new Font(FONT_NAME, Font.PLAIN, headerPoint);
        Font dayFont = new Font(FONT_NAME, Font.BOLD, dayPoint);
        Font holidayFont = new Font(FONT_NAME, Font.PLAIN, holidayPoint);
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
            drawText(g, titleFont, TITLE_COLOR, true, false, leftMargin, topMargin, boxWidth, titleHeight,
                firstDay.format(formatter) + " (" + firstDay.format(formatterJPN) + ")");
            // ヘッダ（曜日名）
            g.setFont(headerFont);
            for (float x = boxLeft, i = 0; i < WEEK_NAME.length; x += cellWidth, ++i)
                drawText(g, headerFont, HEADER_COLOR, true, true, (int)x, topMargin + titleHeight, (int)cellWidth, headerHeight, WEEK_NAME[(int)i]);
            // 日付
            LocalDate day = firstDay.minusDays(firstDay.getDayOfWeek().getValue() % 7);
            for (float y = boxTop, j = 0; j < 6; y += cellHeight, ++j) {
                for (float x = boxLeft, i = 0; i < 7; x += cellWidth, ++i, day = day.plusDays(1)) {
                    // 日付の枠の描画
                    g.setStroke(DEFAULT_STROKE);
                    g.setColor(LINE_COLOR);
                    g.drawRect((int)x, (int)y, (int)cellWidth, (int)cellHeight);
                    // 日付の描画
                    String dayString = "" + day.getDayOfMonth();
                    boolean inMonth = day.getMonthValue() == firstDay.getMonthValue();
                    Color color = inMonth ? DAY_COLOR : NDAY_COLOR;
                    DayOfWeek week = day.getDayOfWeek();
                    boolean solid = !inMonth || week != DayOfWeek.SUNDAY;
                    String holidayName = holidays.get(day);
                    if (holidayName != null) {
                        drawText(g, dayFont, color, false, true, (int)x, (int)y, (int)(cellWidth / 3), (int)(cellHeight / 3), dayString);
                        drawText(g, holidayFont, color, true, false, (int)(x + cellWidth / 3), (int)y, (int)(cellWidth), (int)(cellHeight / 3), holidayName);
                    } else
                        drawText(g, dayFont, color, solid, true, (int)x, (int)y, (int)(cellWidth / 3), (int)(cellHeight / 3), dayString);
                }
            }
        }
    }
}
