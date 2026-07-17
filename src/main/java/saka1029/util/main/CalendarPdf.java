package saka1029.util.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
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

    static final String USAGE = "java saka1029.util.main.CalendarPdf [-n N] YYYY-MM";
    static final float MARGIN_RATE = 0.1F;
    // A4横サイズ = 297mm * 210mm
    static final int DPI = 300;
    static final int IMAGE_WIDTH =  (int) (11.7 * DPI);
    static final int IMAGE_HEIGHT = (int) (8.3 * DPI);
    static final String FONT_NAME = "SansSerif";
    static final Float HEADER_HEIGHT_RATE = 0.1F;

    static void image(Graphics2D g, LocalDate day) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
        g.setColor(Color.BLACK);
        Font font = new Font(FONT_NAME, Font.BOLD, (int)(IMAGE_HEIGHT * HEADER_HEIGHT_RATE));
        g.setFont(font);
        g.drawString(day.toString(), 0, (int)(IMAGE_HEIGHT * HEADER_HEIGHT_RATE));
    }

    static void printPdf(LocalDate yearMonth, int nMonth) throws IOException {
        File outFile = new File("calendar-%04d-%02d.pdf".formatted(yearMonth.getYear(), yearMonth.getMonthValue()));
        PdfDocument pdf = new PdfDocument(new PdfWriter(outFile));
        PageSize pageSize = PageSize.A4.rotate(); // A4横
        AreaBreak NEXT_PAGE = new AreaBreak(pageSize);
        try (Document document = new Document(pdf, pageSize)) {
            Rectangle rect = pdf.getDefaultPageSize();
            float margin = rect.getHeight() * MARGIN_RATE;
            document.setMargins(margin, margin, margin, margin);
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
                    float wScale = (rect.getWidth() - 2 * margin) / imagePdf.getImageWidth();
                    float hScale = (rect.getHeight() - 2 * margin) / imagePdf.getImageHeight();
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
        if (i >= max)
            throw new IllegalArgumentException(USAGE);
        LocalDate yearMonth = LocalDate.parse(args[i] + "-1");
        printPdf(yearMonth, nMonth);
        */
        printPdf(LocalDate.of(2026, 7, 1), 2);
    }

}
