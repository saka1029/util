package saka1029.util.main;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.stream.Stream;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

@SuppressWarnings("serial")
public class ImageViewer extends JFrame {

    static final String[] 拡張子 = {".jpg", ".jpeg", ".gif", ".png", ".bmp"};
    static final int 終了 = 27 /* ESC */, 拡大 = 59 /* ; */, 縮小 = 45 /* - */;
    static final int 前 = 37 /* ← */, 上 = 38 /* ↑ */, 次 = 39 /* → */, 下 = 40 /* ↓ */;
    static final int 左回転 = 'L', 右回転 = 'R', 左右反転 = 'M', 全画面 = 'F';
    static final float FONT_SIZE = 24F;

    final File dir;
    final File[] files;
    int index;
    BufferedImage image;
    int rotation = 0;
    JPopupMenu popup;

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
            Graphics2D g = (Graphics2D) graphics;
            int wp = getWidth(), hp = getHeight();
            int wi = image.getWidth(), hi = image.getHeight();
            double r = rotation % 180 == 0
                ? Math.min((double) wp / wi, (double) hp / hi)
                : Math.min((double) wp / hi, (double) hp / wi);
            int wr = (int) (wi * r), hr = (int) (hi * r);
            int wo = (wp - wr) / 2, ho = (hp - hr) / 2;
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            AffineTransform at = g.getTransform();
            at.rotate(Math.toRadians(rotation), wp / 2, hp / 2);
            g.setTransform(at);
            g.drawImage(image, wo, ho, wr, hr, null);
        }
    };

    static final FileFilter filter = f -> f.isFile() && Stream.of(拡張子)
        .anyMatch(e -> f.getName().toLowerCase().endsWith(e));

    void exit() {
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    void fullScreen() {
        if (getExtendedState() == MAXIMIZED_BOTH)
            setExtendedState(NORMAL);
        else
            setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    void rotateRight() {
        rotation = (rotation + 90) % 360;
        repaint();
    }

    void rotateLeft() {
        rotation = (rotation + 270) % 360;
        repaint();
    }

    void prevImage() {
        if (index <= 0)
            return;
        --index;
        readImage();
        repaint();
    }

    void nextImage() {
        if (index + 1 >= files.length)
            return;
        ++index;
        readImage();
        repaint();
    }

    void firstImage() {
        index = 0;
        readImage();
        repaint();
    }

    void lastImage() {
        index = files.length - 1;
        readImage();
        repaint();
    }

    JMenuItem menuItem(String label, ActionListener listener) {
        JMenuItem item = new JMenuItem(label);
        item.setFont(item.getFont().deriveFont(FONT_SIZE));
        item.addActionListener(listener);
        return item;
    }

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

        popup = new JPopupMenu();
        popup.add(menuItem("全画面", e -> fullScreen()));
        popup.add(menuItem("左回転", e -> rotateLeft()));
        popup.add(menuItem("右回転", e -> rotateRight()));
        popup.add(menuItem("先頭へ", e -> firstImage()));
        popup.add(menuItem("末尾へ", e -> lastImage()));
        popup.add(menuItem("終了", e -> exit()));

        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int w = ImageViewer.this.getWidth();
                int d = w / 5, x = e.getX();
                if (x <= d)
                    prevImage();
                else if (x >= d * 4)
                    nextImage();
                else
                    popup.show(e.getComponent(), e.getX(), e.getY());
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }

        });
        addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case 終了:
                    case 'X':
                    case 'Q':
                        exit();
                        break;
                    case 全画面:
                    case '\n':
                        fullScreen();
                        break;
                    case 右回転:
                        rotateRight();
                        break;
                    case 左回転:
                        rotateLeft();
                        break;
                    case 前:
                    case ',':
                        prevImage();
                        break;
                    case 次:
                    case '.':
                        nextImage();
                        break;
                    case '0':
                        firstImage();
                        break;
                    case '9':
                        lastImage();
                        break;
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
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
