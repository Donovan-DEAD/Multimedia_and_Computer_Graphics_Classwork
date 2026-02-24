package com.github.donovan_dead.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;

public class PointInputPanel extends JPanel {
    private JTextField xField;
    private JTextField yField;

    public PointInputPanel(String label) {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        setBorder(BorderFactory.createTitledBorder(label));

        add(new JLabel("X:"));
        xField = new JTextField(5);
        add(xField);

        add(new JLabel("Y:"));
        yField = new JTextField(5);
        add(yField);
    }

    public Point2D getPoint() throws NumberFormatException {
        double x = Double.parseDouble(xField.getText());
        double y = Double.parseDouble(yField.getText());
        return new Point2D.Double(x, y);
    }

    public void setPoint(Point2D point) {
        if (point != null) {
            xField.setText(String.valueOf(point.getX()));
            yField.setText(String.valueOf(point.getY()));
        } else {
            xField.setText("");
            yField.setText("");
        }
    }

    public void clearFields() {
        xField.setText("");
        yField.setText("");
    }
}
