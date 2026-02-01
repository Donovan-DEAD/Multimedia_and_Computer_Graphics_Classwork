package com.github.donovan_dead.views;

import com.github.donovan_dead.SysyemCoordinates.CartesianCoord;
import com.github.donovan_dead.SysyemCoordinates.PolarCoord;
import com.github.donovan_dead.Utilities.SystemConverter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CartesianToPolarView extends JPanel {
    private final JTextField xField = new JTextField(5);
    private final JTextField yField = new JTextField(5);
    private final JCheckBox showInDegrees = new JCheckBox("Show in Degrees");
    private final JTextField rResultField = new JTextField(10);
    private final JTextField angleResultField = new JTextField(10);
    private final PointGraphPanel graphPanel = new PointGraphPanel();

    public CartesianToPolarView() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Cartesian to Polar"));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("x:"), gbc);

        gbc.gridx = 1;
        formPanel.add(xField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("y:"), gbc);

        gbc.gridx = 1;
        formPanel.add(yField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JButton convertButton = new JButton("Convert");
        convertButton.addActionListener(new ConvertListener());
        formPanel.add(convertButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("r:"), gbc);

        gbc.gridx = 1;
        rResultField.setEditable(false);
        formPanel.add(rResultField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Angle:"), gbc);

        gbc.gridx = 1;
        angleResultField.setEditable(false);
        formPanel.add(angleResultField, gbc);

        gbc.gridx = 2;
        formPanel.add(showInDegrees, gbc);


        add(formPanel, BorderLayout.NORTH);
        add(graphPanel, BorderLayout.CENTER);
    }

    private class ConvertListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                double x = Double.parseDouble(xField.getText());
                double y = Double.parseDouble(yField.getText());
                CartesianCoord cartesian = new CartesianCoord(x, y);
                PolarCoord polar = SystemConverter.ToPolar(cartesian);
                rResultField.setText(String.format("%.4f", polar.getRadius()));
                angleResultField.setText(String.format("%.4f", polar.getAngle(showInDegrees.isSelected())));
                graphPanel.drawPoint(cartesian);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(CartesianToPolarView.this, "Invalid input. Please enter numbers only.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}