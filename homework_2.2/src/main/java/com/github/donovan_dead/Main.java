package com.github.donovan_dead;

import com.github.donovan_dead.views.ImageAspectRatioView;
import com.github.donovan_dead.views.NumericAspectRatioView;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Aspect Ratio Calculator");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 500);

            JTabbedPane tabbedPane = new JTabbedPane();
            tabbedPane.addTab("From Numbers", new NumericAspectRatioView());
            tabbedPane.addTab("From Image File", new ImageAspectRatioView());

            frame.add(tabbedPane);
            frame.setLocationRelativeTo(null); 
            frame.setVisible(true);
        });
    }
}