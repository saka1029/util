package saka1029.util.main;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Image;

/**
 * 指定したディレクトリの下に拡張子
 * .png, .jpg, .jpegのイメージファイルがあったら、
 * それらをまとめて単一のPDFファイルに出力する。
 * 出力するファイル名は「ディレクトリのパス名.pdf」とする。
 * ディレクトリの下にディレクトリがある場合は
 * 再帰的に処理する。
 *
 * <pre>
 * [使い方]
 * java Pdf [-m マージン] [-l] ディレクトリ
 * </pre>
 */
public class Pdf {

    static final String USAGE = "java Pdf [-m margin] [-l] DIRECTORY";
    static final FileFilter IS_DIRECTORY = f -> f.isDirectory();
    static final FileFilter IS_IMAGE_FILE = f -> f.isFile()
        && f.getName().matches("(?i).*\\.(png|jpg|jpeg)$");
    static final AreaBreak NEXT_PAGE = new AreaBreak(PageSize.A4);

    static void printPdf(File outFile, File[] imageFiles, float margin, boolean landscape) throws IOException {
        PdfDocument pdf = new PdfDocument(new PdfWriter(outFile));
        PageSize pageSize = PageSize.A4;
        if (landscape)
            pageSize = pageSize.rotate();
        try (Document document = new Document(pdf, pageSize)) {
            document.setMargins(margin, margin, margin, margin);
            Rectangle rect = pdf.getDefaultPageSize();
            boolean first = true;
            for (File imageFile : imageFiles) {
                // 改ページする。これがないと横長のイメージが連続したとき１ページにまとめられる。
                if (first)
                    first = false;
                else
                    document.add(NEXT_PAGE);
                Image image = new Image(ImageDataFactory.create(imageFile.toString()));
                float wScale = (rect.getWidth() - 2 * margin) / image.getImageWidth();
                float hScale = (rect.getHeight() - 2 * margin) / image.getImageHeight();
                // 拡大率の小さい方でスケールする。
                float scale = Math.min(wScale, hScale);
                image.setWidth(image.getImageWidth() * scale);
                image.setHeight(image.getImageHeight() * scale);
                document.add(image);
            }
            System.out.println(outFile.getAbsolutePath() + " " + pdf.getNumberOfPages() + "pages");
        }
    }

    static void makePdf(File dir, float margin, boolean landscape) throws IOException {
        File[] imageFiles = dir.listFiles(IS_IMAGE_FILE);
        // WindowsとLinuxで同じソート順になるようにファイル名の上昇順でソートします。
        Arrays.sort(imageFiles, Comparator.comparing(f -> f.getName()));
        if (imageFiles.length > 0)
            printPdf(new File(dir.getPath() + ".pdf"), imageFiles, margin, landscape);
        for (File subDir : dir.listFiles(IS_DIRECTORY))
            makePdf(subDir, margin, landscape);
    }

    public static void main(String[] args) throws IOException {
        int margin = 0;
        boolean landscape = false;
        int i = 0, max = args.length;
        L: for ( ; i < max; ++i)
            switch (args[i]) {
            case "-m":
                if (++i < max)
                    margin = Integer.parseInt(args[i]);
                else
                    throw new IllegalArgumentException(USAGE);
                break;
            case "-l":
                landscape = true;
                break;
            default:
                break L;
            }
        if (i >= max)
            throw new IllegalArgumentException(USAGE);
        File dir = new File(args[i]);
        if (!dir.isDirectory())
            throw new IllegalArgumentException(USAGE);
        makePdf(dir, margin, landscape);
    }

}
