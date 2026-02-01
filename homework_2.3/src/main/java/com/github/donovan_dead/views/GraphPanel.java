package com.github.donovan_dead.views;

import com.github.donovan_dead.SysyemCoordinates.CartesianCoord;
import com.github.donovan_dead.Utilities.PolarRoseEq;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GraphPanel extends JPanel {
    private PolarRoseEq equation;
    private final List<Point> points = new ArrayList<>();

    public GraphPanel() {
        setPreferredSize(new Dimension(400, 400));
        setBackground(Color.WHITE);
    }

    public void setEquation(PolarRoseEq equation) {
        this.equation = equation;
        calculatePoints();
        repaint();
    }

    private void calculatePoints() {
        points.clear();
        if (equation == null) return;

        // Determine the range of the angle
        double maxAngle = 2 * Math.PI;
        if (equation.getFrequency() % 2 == 0) {
            maxAngle *= 2;
        }

        for (double angle = 0; angle < maxAngle; angle += 0.01) {
            CartesianCoord coord = equation.computeToCartesian(angle);
            int x = (int) (getWidth() / 2 + coord.getX() * (getWidth() / (2.5 * equation.getAmplitude()))); // Dynamic scaling
            int y = (int) (getHeight() / 2 - coord.getY() * (getHeight() / (2.5 * equation.getAmplitude())));
            points.add(new Point(x, y));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw axes
        g.setColor(Color.LIGHT_GRAY);
        g.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2); // X-axis
        g.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight()); // Y-axis

        if (!points.isEmpty()) {
            g.setColor(Color.BLUE);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setStroke(new BasicStroke(2));
            for (int i = 0; i < points.size() - 1; i++) {
                Point p1 = points.get(i);
                Point p2 = points.get(i + 1);
                g.drawLine(p1.x, p1.y, p2.x, p2.y);
            }
        }
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        calculatePoints();
    }
}