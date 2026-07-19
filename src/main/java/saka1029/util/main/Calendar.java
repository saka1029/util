package saka1029.util.main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Image;

public class Calendar {

    // static final float PDF_MARGIN_RATE = 0.08F;
    // A4横サイズ = 297mm * 210mm
    static final int DPI = 300;
    static final int WIDTH =  (int) (11.7 * DPI);
    static final int HEIGHT = (int) (8.3 * DPI);
    static final String FONT_NAME = "SansSerif";
    static final float MARGIN_RATE = 0.08F;
    static final float TITLE_HEIGHT_RATE = 0.06F;
    static final float HEADER_HEIGHT_RATE = 0.04F;
    static final float FONT_HEIGHT_RATE = 0.8F;
    static final int STROKE_WIDTH = 2;
    static final Stroke DEFAULT_STROKE = new BasicStroke(STROKE_WIDTH);
    static final String[] HEADERS = {"日", "月", "火", "水", "木", "金", "土"};

    public static final String 祝日_CSV_URL = "https://www8.cao.go.jp/chosei/shukujitsu/syukujitsu.csv";
    static Map<LocalDate, String> holidays = new HashMap<>();
    static {
        try {
            URL url = new URI(祝日_CSV_URL).toURL();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/M/d");
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.connect();
            try (Closeable c = () -> connection.disconnect();
                InputStream is = connection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is, "SJIS"))) {
                String line = br.readLine();    // skip header
                if (line == null)
                    throw new IOException("祝日CSVが空です");
                while ((line = br.readLine()) != null) {
                    String[] fields = line.split(",");
                    holidays.put(LocalDate.parse(fields[0], formatter), fields[1]);        
                }
            }
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException("祝日CSVがロードできません", e);
        }
    }

    static void text(Graphics2D g, Color color, boolean outline, boolean center, int left, int top, int width, int height, String text) {
        Font font = new Font(FONT_NAME, Font.BOLD, (int)(height * FONT_HEIGHT_RATE));
        Rectangle2D r = font.getStringBounds(text, g.getFontRenderContext());
        float textLeft = center ? (float)(left + (width - r.getWidth()) / 2) : left;
        float textTop = (float)(top + (height - r.getY()) / 2);
        g.setFont(font);
        g.setColor(color);
        if (outline) {
            g.setStroke(new BasicStroke(8)); // 縁取りの太さを調整
            g.draw(font.createGlyphVector(g.getFontRenderContext(), text).getOutline(textLeft, textTop));
            g.setStroke(DEFAULT_STROKE);
            // 文字本体
            g.setColor(Color.WHITE);
        }
        g.drawString(text, textLeft, textTop);
    }

    static void image(Graphics2D g, LocalDate yearMonth) {
        // 全体を白くする
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        // 罫線の描画
        int headerHeight = (int)(HEIGHT * HEADER_HEIGHT_RATE);
        int titleHeight = (int)(HEIGHT * TITLE_HEIGHT_RATE);
        int left = (int)(WIDTH * MARGIN_RATE);
        int top = (int)(HEIGHT * MARGIN_RATE);
        int width = WIDTH - left * 2;
        int height = HEIGHT - top * 2;
        float cellWidth = width / 7F;
        float cellHeight = (height - headerHeight - titleHeight) / 6F;
        // 日付枠
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(STROKE_WIDTH));
        for (int r = 0; r <= 6; ++r) {
            int y = (int)(top + titleHeight + headerHeight + cellHeight * r);
            g.drawLine(left, y, left + width, y);
           for (int c = 0; c <= 7; ++c) {
                int x = (int)(left + cellWidth * c);
                g.drawLine(x, top + titleHeight + headerHeight, x, top + height);
            }
        }
        // タイトル
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年 M月");
        text(g, Color.BLACK, false, false, left, top, width, titleHeight, yearMonth.format(formatter));
        // ヘッダー
        for (int c = 0, x = left; c < 7; ++c, x = (int)(x + cellWidth))
            text(g, Color.BLACK, false, true, x, top + titleHeight, (int)cellWidth, headerHeight, HEADERS[c]);
        // 日付
        int dayWidth = (int)(cellWidth * 0.4f);
        int dayHeight = (int)(cellHeight * 0.4f);
        LocalDate day = yearMonth.minusDays(yearMonth.getDayOfWeek().getValue() % 7);
        for (int r = 0; r < 6; ++r) {
            int y = (int)(top + titleHeight + headerHeight + cellHeight * r);
           for (int c = 0; c < 7; ++c, day = day.plusDays(1)) {
                int x = (int)(left + cellWidth * c);
                String holiday = holidays.get(day);
                Color color = day.getMonth() == yearMonth.getMonth() ? Color.BLACK : Color.LIGHT_GRAY;
                boolean outline = day.getDayOfWeek() == DayOfWeek.SUNDAY || holiday != null;
                text(g, color, outline, true, x, y, dayWidth, dayHeight, "" + day.getDayOfMonth());
                if (holiday != null)
                    text(g, color, false, false,
                        x + dayWidth, (int)(y + dayHeight * 0.2F),
                        (int)(cellWidth - dayWidth), (int)(dayHeight * 0.4f),
                        holiday);
            }
        }
    }

    static void A4横(LocalDate yearMonth, int nMonth) throws IOException {
        File outFile = new File("calendar-%04d-%02d.pdf".formatted(yearMonth.getYear(), yearMonth.getMonthValue()));
        PdfDocument pdf = new PdfDocument(new PdfWriter(outFile));
        PageSize pageSize = PageSize.A4.rotate(); // A4横
        AreaBreak NEXT_PAGE = new AreaBreak(pageSize);
        try (Document document = new Document(pdf, pageSize)) {
            Rectangle rect = pdf.getDefaultPageSize();
            // float marginHeight = rect.getHeight() * PDF_MARGIN_RATE;
            // float marginWidth = rect.getWidth() * PDF_MARGIN_RATE;
            document.setMargins(0, 0, 0, 0);
            LocalDate day = yearMonth;
            for (int i = 0; i < nMonth; ++i, day = day.plusMonths(1)) {
                // 改ページする。これがないと横長のイメージが連続したとき１ページにまとめられる。
                if (i > 0) document.add(NEXT_PAGE);
                BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
                Graphics2D g = image.createGraphics();
                try (Closeable c = () -> g.dispose()) {
                    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    image(g, day);
                    // iText用イメージ作成
                    Image imagePdf = new Image(ImageDataFactory.create(image, null));
                    float wScale = rect.getWidth() / imagePdf.getImageWidth();
                    float hScale = rect.getHeight() / imagePdf.getImageHeight();
                    // 拡大率の小さい方でスケールする。
                    float scale = Math.min(wScale, hScale);
                    imagePdf.setWidth(imagePdf.getImageWidth() * scale);
                    imagePdf.setHeight(imagePdf.getImageHeight() * scale);
                    document.add(imagePdf);
                }
            }
            System.out.println(outFile.getAbsolutePath() + " " + pdf.getNumberOfPages() + "pages");
        }
    }

    static final String USAGE = "java saka1029.util.main.CalendarPdf [-n N] YYYYMM";

    public static void main(String[] args) throws IOException {
        int nMonth = 1;
        int i = 0, max = args.length;
        L: for ( ; i < max; ++i)
            switch (args[i]) {
            case "-n":
                if (++i < max)
                    nMonth = Integer.parseInt(args[i]);
                else
                    throw new IllegalArgumentException(USAGE);
                break;
            default:
                break L;
            }
        if (i >= max || args[i].length() != 6)
            throw new IllegalArgumentException(USAGE);
        LocalDate yearMonth = LocalDate.parse(
            args[i].substring(0, 4) + "-" + args[i].substring(4, 6) + "-01");
        A4横(yearMonth, nMonth);
    }

}
