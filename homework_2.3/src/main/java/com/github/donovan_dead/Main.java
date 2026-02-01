package com.github.donovan_dead;

import com.github.donovan_dead.views.CartesianToPolarView;
import com.github.donovan_dead.views.PolarRoseGrapherView;
import com.github.donovan_dead.views.PolarToCartesianView;

import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardPanel = new JPanel(cardLayout);

    public Main() {
        setTitle("Coordinate Converter & Polar Rose Grapher");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Navigation Panel
        JPanel navigationPanel = new JPanel(new FlowLayout());
        JButton p2cButton = new JButton("Polar to Cartesian");
        JButton c2pButton = new JButton("Cartesian to Polar");
        JButton grapherButton = new JButton("Polar Rose Grapher");
        navigationPanel.add(p2cButton);
        navigationPanel.add(c2pButton);
        navigationPanel.add(grapherButton);

        // Card Panel
        cardPanel.add(new PolarToCartesianView(), "P2C");
        cardPanel.add(new CartesianToPolarView(), "C2P");
        cardPanel.add(new PolarRoseGrapherView(), "Grapher");

        // Action Listeners
        p2cButton.addActionListener(e -> cardLayout.show(cardPanel, "P2C"));
        c2pButton.addActionListener(e -> cardLayout.show(cardPanel, "C2P"));
        grapherButton.addActionListener(e -> cardLayout.show(cardPanel, "Grapher"));

        add(navigationPanel, BorderLayout.NORTH);
        add(cardPanel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}