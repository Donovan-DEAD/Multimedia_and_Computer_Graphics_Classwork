package com.github.donovan_dead;

import com.github.donovan_dead.GUI.MainFrame;

import javax.swing.SwingUtilities;

/**
 * The `Main` class serves as the entry point for the image editor application.
 */
public class Main {

    /**
     * The main method that launches the application.
     * It creates and displays the main application window (`MainFrame`) on the
     * AWT Event Dispatch Thread to ensure thread safety for the Swing components.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}