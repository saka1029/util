package test.saka1029.util.calendar;

import java.awt.*;
import javax.swing.*;

public class FukuroMojiDrawer extends JPanel {
    static final String STRING = "文化の日";

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        Font font = new Font("SansSerif", Font.PLAIN, 48); // 適切な書体を選択してください。
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setFont(font);

        // 文字の縁取り
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(5)); // 縁取りの太さを調整
        g2d.draw(font.createGlyphVector(g2d.getFontRenderContext(), STRING)
                .getOutline(50, 50));
        // 文字本体
        g2d.setColor(Color.WHITE);
        g2d.drawString(STRING, 50, 50);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("FukuroMojiDrawer");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new FukuroMojiDrawer());
            frame.setSize(400, 200);
            frame.setVisible(true);
        });
    }
}