package saka1029.util.main;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.Closeable;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ImageViewer extends JFrame {


    ImageViewer() {
        this.setSize(1200, 600);
        this.setTitle("ImageViewer");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ImageIcon pic = new ImageIcon("data/bike.png");// important
        Image image = pic.getImage(); // transform it 
//        Image resized = image.getScaledInstance(120, 120,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way  
        Image resized = getScaledImage(image, this.getWidth());
        JLabel piclabel = new JLabel(new ImageIcon(resized));
        JPanel panel = new JPanel();
        panel.add(piclabel);// important
        this.add(panel);
        this.setVisible(true);
    }
    
    private Image getScaledImage(Image src, int w){
        double scale = (double)w / src.getWidth(null);
        int h = (int)(src.getHeight(null) * scale);
        BufferedImage resized = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resized.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(src, 0, 0, w, h, null);
        g2.dispose();
        return resized;
    }

    public static void main(String[] args) {
        new ImageViewer();
    }
}
