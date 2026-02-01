package com.github.donovan_dead.views;

import com.github.donovan_dead.Utilities.PolarRoseEq;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PolarRoseGrapherView extends JPanel {
    private final JTextField amplitudeField = new JTextField(5);
    private final JTextField frequencyField = new JTextField(5);
    private final JTextField initialRotationField = new JTextField(5);
    private final JCheckBox inDegrees = new JCheckBox("Degrees");
    private final GraphPanel graphPanel = new GraphPanel();

    public PolarRoseGrapherView() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Polar Rose Grapher"));

        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.add(new JLabel("Amplitude (a):"));
        controlPanel.add(amplitudeField);
        controlPanel.add(new JLabel("Frequency (n):"));
        controlPanel.add(frequencyField);
        controlPanel.add(new JLabel("Initial Rotation:"));
        controlPanel.add(initialRotationField);
        controlPanel.add(inDegrees);

        JButton graphButton = new JButton("Graph");
        graphButton.addActionListener(new GraphListener());
        controlPanel.add(graphButton);

        add(controlPanel, BorderLayout.NORTH);
        add(graphPanel, BorderLayout.CENTER);
    }

    private class GraphListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                double amplitude = Double.parseDouble(amplitudeField.getText());
                double frequency = Double.parseDouble(frequencyField.getText());
                double initialRotation = Double.parseDouble(initialRotationField.getText());
                graphPanel.setEquation(new PolarRoseEq(amplitude, frequency, initialRotation, inDegrees.isSelected()));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(PolarRoseGrapherView.this, "Invalid input. Please enter numbers only.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}