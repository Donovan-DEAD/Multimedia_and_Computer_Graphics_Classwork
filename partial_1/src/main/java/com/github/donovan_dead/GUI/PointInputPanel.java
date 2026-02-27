package com.github.donovan_dead.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;

/**
 * A `JPanel` that provides a standardized UI for inputting a 2D point with X and Y coordinates.
 * It consists of labeled text fields for the X and Y values.
 */
public class PointInputPanel extends JPanel {
    /** Text field for the X coordinate. */
    private JTextField xField;
    /** Text field for the Y coordinate. */
    private JTextField yField;

    /**
     * Constructs a new `PointInputPanel` with a specified label.
     *
     * @param label The text to be displayed in the panel's border.
     */
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

    /**
     * Parses the text fields and returns the entered point.
     *
     * @return A `Point2D.Double` object representing the user's input.
     * @throws NumberFormatException if the input in the text fields is not a valid number.
     */
    public Point2D getPoint() throws NumberFormatException {
        double x = Double.parseDouble(xField.getText());
        double y = Double.parseDouble(yField.getText());
        return new Point2D.Double(x, y);
    }

    /**
     * Sets the text fields to the coordinates of a given point.
     *
     * @param point The `Point2D` to display. If null, fields are cleared.
     */
    public void setPoint(Point2D point) {
        if (point != null) {
            xField.setText(String.valueOf(point.getX()));
            yField.setText(String.valueOf(point.getY()));
        } else {
            xField.setText("");
            yField.setText("");
        }
    }

    /**
     * Clears the X and Y text fields.
     */
    public void clearFields() {
        xField.setText("");
        yField.setText("");
    }
}
