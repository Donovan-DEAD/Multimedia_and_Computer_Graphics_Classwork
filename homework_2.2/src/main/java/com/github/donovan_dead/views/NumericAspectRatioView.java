package com.github.donovan_dead.views;

import com.github.donovan_dead.Utilities.AspectRatioCalculator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NumericAspectRatioView extends JPanel {
    private final JTextField widthField = new JTextField(10);
    private final JTextField heightField = new JTextField(10);
    private final JButton calculateButton = new JButton("Calculate");
    private final JLabel resultLabel = new JLabel("Aspect Ratio will be shown here.");

    public NumericAspectRatioView() {
        super(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        inputPanel.add(new JLabel("Width:"));
        inputPanel.add(widthField);
        inputPanel.add(new JLabel("Height:"));
        inputPanel.add(heightField);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.add(calculateButton);
        bottomPanel.add(resultLabel);

        add(inputPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    long width = Long.parseLong(widthField.getText());
                    long height = Long.parseLong(heightField.getText());

                    if (width <= 0 || height <= 0) {
                        resultLabel.setText("Width and height must be positive numbers.");
                        return;
                    }

                    long[] ratio = AspectRatioCalculator.calculateAspectRatio(width, height);
                    if (ratio != null) {
                        resultLabel.setText("Aspect Ratio: " + ratio[0] + ":" + ratio[1]);
                    } else {
                        resultLabel.setText("Could not calculate aspect ratio.");
                    }
                } catch (NumberFormatException ex) {
                    resultLabel.setText("Invalid input. Please enter valid numbers.");
                }
            }
        });
    }
}
