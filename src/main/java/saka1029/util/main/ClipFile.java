package saka1029.util.main;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class ClipFile {

    public static void main(String[] args) throws UnsupportedFlavorException, IOException {
        String outFileName = args[0];
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        for (DataFlavor e : clipboard.getAvailableDataFlavors()) {
            System.out.println(e);
            if (e.equals(DataFlavor.stringFlavor))
                Files.write(Path.of(outFileName + ".txt"), ((String)clipboard.getData(e)).getBytes());
            else if (e.isMimeTypeEqual("image/png") && e.isRepresentationClassInputStream())
                Files.copy((InputStream)clipboard.getData(e), Path.of(outFileName + ".png"), StandardCopyOption.REPLACE_EXISTING);
            else if (e.isMimeTypeEqual("image/jpeg") && e.isRepresentationClassInputStream())
                Files.copy((InputStream)clipboard.getData(e), Path.of(outFileName + ".jpg"), StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
