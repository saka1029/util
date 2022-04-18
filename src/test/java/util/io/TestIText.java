package util.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.Test;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.AreaBreakType;

public class TestIText {

    static final AreaBreak NEXT_PAGE = new AreaBreak(AreaBreakType.NEXT_PAGE);

    /**
     * テキストファイルの出力サンプル
     */
    @Test
    public void testTextFile() throws IOException {
        File textFile = new File("src/test/java/util/io/TestIText.java");
        PdfFont normal = PdfFontFactory.createFont("c:/windows/fonts/msgothic.ttc,0", "Identity-H");
        File dest = new File("data/test.pdf");
        PdfDocument pdf = new PdfDocument(new PdfWriter(dest));
        try (Document document = new Document(pdf)) {
            Paragraph title = new Paragraph()
                .setFont(normal).setFontSize(10).setBold().add(textFile.getName());
            document.add(title);
            String text = Files.readString(textFile.toPath());
            Paragraph para = new Paragraph()
                .setFont(normal).setFontSize(10).add(text.replace(' ', '\u00a0'));
            document.add(para);
        }
    }

}
