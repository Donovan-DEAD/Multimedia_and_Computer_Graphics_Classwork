package com.github.donovan_dead.views;

import com.github.donovan_dead.SysyemCoordinates.CartesianCoord;

import javax.swing.*;
import java.awt.*;

public class PointGraphPanel extends JPanel {
    private CartesianCoord point;
    private boolean drawComponents = false;

    public PointGraphPanel() {
        setPreferredSize(new Dimension(200, 200));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
    }

    public void setDrawComponents(boolean drawComponents) {
        this.drawComponents = drawComponents;
    }

    public void drawPoint(CartesianCoord point) {
        this.point = point;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int width = getWidth();
        int height = getHeight();
        int centerX = width / 2;
        int centerY = height / 2;

        // Draw axes
        g.setColor(Color.LIGHT_GRAY);
        g.drawLine(0, centerY, width, centerY); // X-axis
        g.drawLine(centerX, 0, centerX, height); // Y-axis

        if (point != null) {
            // Dynamic scaling based on the point's coordinates
            double maxCoord = Math.max(Math.abs(point.getX()), Math.abs(point.getY()));
            double scale = (maxCoord > 0) ? (Math.min(width, height) / (2.5 * maxCoord)) : 1.0;


            int x = (int) (centerX + point.getX() * scale);
            int y = (int) (centerY - point.getY() * scale);

            if (drawComponents) {
                // Draw component lines
                Graphics2D g2d = (Graphics2D) g.create();
                Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
                g2d.setStroke(dashed);
                g2d.setColor(Color.GREEN);
                g2d.drawLine(x, y, x, centerY); // Line to X-axis
                g2d.setColor(Color.ORANGE);
                g2d.drawLine(x, y, centerX, y); // Line to Y-axis
                g2d.dispose();
            } else {
                // Draw line from origin to point
                g.setColor(Color.RED);
                g.drawLine(centerX, centerY, x, y);
            }

            // Draw the point
            g.setColor(Color.BLUE);
            g.fillOval(x - 5, y - 5, 10, 10);

            // Draw coordinates
            g.setColor(Color.BLACK);
            g.drawString(String.format("(%.2f, %.2f)", point.getX(), point.getY()), x + 10, y);
        }
    }
}