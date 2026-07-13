package saka1029.util.calendar;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

public class ImageWriter implements Closeable {

    public final int width, height;
    private final OutputStream os;
    private final BufferedImage image;
    private final Graphics2D graphics;

    public ImageWriter(OutputStream os, int width, int height) {
        this.os = os;
        this.width = width;
        this.height = height;
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        this.graphics = this.image.createGraphics();
        this.graphics.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
    }

    public Graphics2D graphics() {
        return this.graphics;
    }

    @Override
    public void close() throws IOException {
        graphics.dispose();
        ImageIO.write(image, "png", os);
    }
}
