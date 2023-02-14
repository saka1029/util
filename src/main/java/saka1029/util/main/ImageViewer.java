package saka1029.util.main;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.stream.Stream;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ImageViewer extends JFrame {

    static final String[] 拡張子 = {".jpg", ".jpeg", ".gif", ".png", ".bmp"};
    static final int 終了 = 27 /* ESC */, 拡大 = 59 /* ; */, 縮小 = 45 /* - */;
    static final int 前 = 37 /* ← */, 上 = 38 /* ↑ */, 次 = 39 /* → */, 下 = 40 /* ↓ */;
    static final int 左回転 = 'L', 右回転 = 'R', 左右反転 = 'M', 全画面 = 'F';
    
    final File dir;
    final File[] files;
    int index;
    BufferedImage image;
    int rotation = 0;

    void readImage() {
        try {
            File f = files[index];
            image = ImageIO.read(f);
            setTitle("%s %d x %d".formatted(f, image.getWidth(), image.getHeight()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    JPanel panel = new JPanel() {
        @Override
        public void paint(Graphics graphics) {
            Graphics2D g = (Graphics2D)graphics;
            int wp = getWidth(), hp = getHeight();
            int wi = image.getWidth(), hi = image.getHeight();
            double r = rotation % 180 == 0
                ? Math.min((double)wp / wi, (double)hp / hi)
                : Math.min((double)wp / hi, (double)hp / wi);
            int wr = (int)(wi * r), hr = (int)(hi * r);
            int wo = (wp - wr) / 2, ho = (hp - hr) / 2;
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            AffineTransform at = g.getTransform();
            at.rotate(Math.toRadians(rotation), wp / 2, hp / 2);
            g.setTransform(at);
            g.drawImage(image, wo, ho, wr, hr, null);
        }
    };
    
    static final FileFilter filter = f -> 
        f.isFile() && Stream.of(拡張子)
            .anyMatch(e -> f.getName().toLowerCase().endsWith(e));

    ImageViewer(File file) {
        dir = file.getParentFile();
        files = dir.listFiles(filter);
        for (index = 0; index < files.length; ++index)
            if (files[index].equals(file))
                break;
        if (index >= files.length)
            throw new IllegalArgumentException("ファイル(" + file + ")がディレクトリ(" + dir + ")にありません。");
        setSize(1200, 675);
        setTitle("ImageViewer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        readImage();
        getContentPane().add(panel);
        JFrame f = this;
        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case 終了:
                    case 'X':
                        f.dispatchEvent(new WindowEvent(f, WindowEvent.WINDOW_CLOSING));
                    case 全画面:
                    case '\n':
                        if (f.getExtendedState() == MAXIMIZED_BOTH)
                            f.setExtendedState(NORMAL);
                        else
                            f.setExtendedState(JFrame.MAXIMIZED_BOTH);
                        break;
                    case 右回転:
                        rotation = (rotation + 90) % 360;
                        repaint();
                        break;
                    case 左回転:
                        rotation = (rotation + 270) % 360;
                        repaint();
                        break;
                    case 前:
                    case ',':
                        if (index <= 0)
                            return;
                        --index;
                        readImage();
                        repaint();
                        break;
                    case 次:
                    case '.':
                        if (index + 1 >= files.length)
                            return;
                        ++index;
                        readImage();
                        repaint();
                        break;
                    case '0':
                        index = 0;
                        readImage();
                        repaint();
                        break;
                    case '9':
                        index = files.length - 1;
                        readImage();
                        repaint();
                        break;
                }
            }
        });
        setVisible(true);
    }

    public static void main(String[] args) {
        if (args.length != 1)
            throw new IllegalArgumentException("イメージファイルを指定してください。");
        File file = new File(args[0]);
        if (!file.exists())
            throw new IllegalArgumentException("ファイル(" + file + ")は存在しません。");
        new ImageViewer(file);
    }
}
