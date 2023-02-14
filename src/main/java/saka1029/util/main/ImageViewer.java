package saka1029.util.main;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ImageViewer extends JFrame {

    File file = new File("data/bike.png");
    JLabel label = new JLabel();
    Image image;

    void readImage() {
        try {
            image = ImageIO.read(file);
            setTitle(file.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    JPanel panel = new JPanel() {
        @Override
        public void paint(Graphics g) {
            Image scaled = getScaledImage(image, this.getWidth());
            g.drawImage(scaled, 0, 0, null);
        }
    };
    
    final File dir;
    final File[] files;
    int index;

    ImageViewer(File file) {
        dir = file.getParentFile();
        files = dir.listFiles();
        for (index = 0; index < files.length; ++index)
            if (files[index].equals(file))
                break;
        if (index >= files.length)
            throw new IllegalArgumentException("ファイル(" + file + ")がディレクトリ(" + dir + ")にありません。");
        setSize(1600, 900);
        setTitle("ImageViewer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        readImage();
        // ボーダーレイアウト
        setLayout(new BorderLayout());
        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(label, BorderLayout.SOUTH);
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
                label.setText(e.toString());
                switch (e.getKeyCode()) {
                    case 27: // ESC:プログラム終了
                        f.dispatchEvent(new WindowEvent(f, WindowEvent.WINDOW_CLOSING));
                    case 'F':
                        if (f.getExtendedState() == MAXIMIZED_BOTH) {
                            // 通常画面表示
                            f.setExtendedState(NORMAL);
//                            f.setUndecorated(false); // フレームのコンストラクタで呼び出す必要がある。
                        } else {
                            // 全画面表示
                            f.setExtendedState(JFrame.MAXIMIZED_BOTH);
//                            f.setUndecorated(true); // フレームのコンストラクタで呼び出す必要がある。
                        }
                        break;
                }
            }
        });
        // 全画面表示
//        setExtendedState(JFrame.MAXIMIZED_BOTH); 
//        setUndecorated(true);
        // Look and feel 変更（ただし効かない）
//        String lafClassName = "javax.swing.plaf.metal.MetalLookAndFeel";
//        try {
//            UIManager.setLookAndFeel(lafClassName);
//            SwingUtilities.updateComponentTreeUI(this);
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.exit(ABORT);
//        }

        setVisible(true);
    }

    static Image getScaledImage(Image src, int w) {
        double scale = (double) w / src.getWidth(null);
        int h = (int) (src.getHeight(null) * scale);
        BufferedImage resized = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resized.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(src, 0, 0, w, h, null);
        g2.dispose();
        return resized;
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
