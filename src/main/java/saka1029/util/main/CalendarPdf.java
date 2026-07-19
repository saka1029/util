package saka1029.util.main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Image;

public class CalendarPdf {

    static final float PDF_MARGIN_RATE = 0.08F;
    // A4横サイズ = 297mm * 210mm
    static final int DPI = 300;
    static final int IMAGE_WIDTH =  (int) (11.7 * DPI);
    static final int IMAGE_HEIGHT = (int) (8.3 * DPI);
    static final String FONT_NAME = "SansSerif";
    static final float HEADER_HEIGHT_RATE = 0.1F;
    static final float TITLE_HEIGHT_RATE = 0.02F;
    static final float FOHT_HEIGHT_RATE = 0.8F;
    static final int IMAGE_STROKE_WIDTH = 5;

    static void image(Graphics2D g, LocalDate day) {
        // 全体を白くする
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
        // 罫線の描画
        int headerHeight = (int)(IMAGE_HEIGHT * HEADER_HEIGHT_RATE);
        int titleHeight = (int)(IMAGE_HEIGHT * TITLE_HEIGHT_RATE);
        float cellWidth = (IMAGE_WIDTH - IMAGE_STROKE_WIDTH) / 7F;
        float cellHeight = (IMAGE_HEIGHT - headerHeight - titleHeight - IMAGE_STROKE_WIDTH) / 6F;
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(IMAGE_STROKE_WIDTH));
        for (int c = 0, y = headerHeight + titleHeight; c <= 6; ++c, y = (int)(y + cellHeight)) {
            g.drawLine(0, y, IMAGE_WIDTH - IMAGE_STROKE_WIDTH, y);
           for (int r = 0, x = (int)(IMAGE_STROKE_WIDTH / 2); r <= 7; ++r, x = (int)(x + cellWidth)) {
                g.drawLine(x, titleHeight + headerHeight, x, IMAGE_HEIGHT - IMAGE_STROKE_WIDTH);
            }
        }
        // タイトル
        Font font = new Font(FONT_NAME, Font.BOLD, (int)(IMAGE_HEIGHT * HEADER_HEIGHT_RATE));
        g.setFont(font);
        g.drawString(day.toString(), 0, (int)(IMAGE_HEIGHT * HEADER_HEIGHT_RATE * FOHT_HEIGHT_RATE));
    }

    static void printPdf(LocalDate yearMonth, int nMonth) throws IOException {
        File outFile = new File("calendar-%04d-%02d.pdf".formatted(yearMonth.getYear(), yearMonth.getMonthValue()));
        PdfDocument pdf = new PdfDocument(new PdfWriter(outFile));
        PageSize pageSize = PageSize.A4.rotate(); // A4横
        AreaBreak NEXT_PAGE = new AreaBreak(pageSize);
        try (Document document = new Document(pdf, pageSize)) {
            Rectangle rect = pdf.getDefaultPageSize();
            float marginHeight = rect.getHeight() * PDF_MARGIN_RATE;
            float marginWidth = rect.getWidth() * PDF_MARGIN_RATE;
            document.setMargins(marginHeight, marginWidth, marginHeight, marginWidth);
            LocalDate day = yearMonth;
            for (int i = 0; i < nMonth; ++i, day = day.plusDays(1)) {
                // 改ページする。これがないと横長のイメージが連続したとき１ページにまとめられる。
                if (i > 0) document.add(NEXT_PAGE);
                BufferedImage image = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
                Graphics2D g = image.createGraphics();
                try (Closeable c = () -> g.dispose()) {
                    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    image(g, day);
                    // iText用イメージ作成
                    Image imagePdf = new Image(ImageDataFactory.create(image, null));
                    float wScale = (rect.getWidth() - 2 * marginWidth) / imagePdf.getImageWidth();
                    float hScale = (rect.getHeight() - 2 * marginHeight) / imagePdf.getImageHeight();
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
        /*
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
            args[i].substring(0, 4) + "-" + args[i].substring(4, 2) + "-1");
        printPdf(yearMonth, nMonth);
        */
        printPdf(LocalDate.of(2026, 7, 1), 2);
    }

}
