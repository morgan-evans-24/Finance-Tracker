import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import javax.swing.*;
import java.awt.*;
import java.util.random.RandomGenerator;

public class ChartPanelWithCircle extends ChartPanel {
    public ChartPanelWithCircle(JFreeChart chart) {
        super(chart);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        int radius = 80;

        g2d.setColor(Color.WHITE);
        g2d.fillOval(centerX - radius, centerY - radius, 2 * radius, 2 * radius);

        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval(centerX - radius, centerY - radius, 2 * radius, 2 * radius);

        g2d.setFont(new Font("Arial", Font.BOLD, 22));
        String centerText = this.getChart().getTitle().getText();
        FontMetrics metrics = g2d.getFontMetrics(g.getFont());
        int textX = getWidth() / 2 - metrics.stringWidth(centerText) / 2;
        int textY = getHeight() / 2 + metrics.getHeight() / 4;
        g2d.drawString(centerText, textX, textY);
    }

}
