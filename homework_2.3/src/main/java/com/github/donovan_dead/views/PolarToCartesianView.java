package com.github.donovan_dead.views;

import com.github.donovan_dead.SysyemCoordinates.CartesianCoord;
import com.github.donovan_dead.SysyemCoordinates.PolarCoord;
import com.github.donovan_dead.Utilities.SystemConverter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PolarToCartesianView extends JPanel {
    private final JTextField rField = new JTextField(5);
    private final JTextField angleField = new JTextField(5);
    private final JCheckBox inDegrees = new JCheckBox("Degrees");
    private final JTextField xResultField = new JTextField(10);
    private final JTextField yResultField = new JTextField(10);
    private final PointGraphPanel graphPanel = new PointGraphPanel();

    public PolarToCartesianView() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Polar to Cartesian"));

        graphPanel.setDrawComponents(true);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("r:"), gbc);

        gbc.gridx = 1;
        formPanel.add(rField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Angle:"), gbc);

        gbc.gridx = 1;
        formPanel.add(angleField, gbc);

        gbc.gridx = 2;
        formPanel.add(inDegrees, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JButton convertButton = new JButton("Convert");
        convertButton.addActionListener(new ConvertListener());
        formPanel.add(convertButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("x:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        xResultField.setEditable(false);
        formPanel.add(xResultField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("y:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        yResultField.setEditable(false);
        formPanel.add(yResultField, gbc);

        add(formPanel, BorderLayout.NORTH);
        add(graphPanel, BorderLayout.CENTER);
    }

    private class ConvertListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                double r = Double.parseDouble(rField.getText());
                double angle = Double.parseDouble(angleField.getText());
                PolarCoord polar = new PolarCoord(angle, r, inDegrees.isSelected());
                CartesianCoord cartesian = SystemConverter.ToCartesian(polar);
                xResultField.setText(String.format("%.4f", cartesian.getX()));
                yResultField.setText(String.format("%.4f", cartesian.getY()));
                graphPanel.drawPoint(cartesian);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(PolarToCartesianView.this, "Invalid input. Please enter numbers only.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}