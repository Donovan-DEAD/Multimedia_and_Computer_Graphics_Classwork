package com.github.donovan_dead.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * The `ImagePanel` class is a `JPanel` specialized for displaying a `BufferedImage`.
 * The image is automatically centered within the panel.
 */
public class ImagePanel extends JPanel {

    /** The image to be displayed on the panel. */
    private BufferedImage image;

    /**
     * Constructs a new `ImagePanel`.
     * Sets the initial background color and enables autoscrolling.
     */
    public ImagePanel() {
        setBackground(Color.DARK_GRAY);
        setAutoscrolls(true);
    }

    /**
     * Sets the image to be displayed and updates the panel's preferred size.
     *
     * @param image The `BufferedImage` to display.
     */
    public void setImage(BufferedImage image) {
        this.image = image;
        setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
        revalidate();
        repaint();
    }

    /**
     * Retrieves the currently displayed image.
     *
     * @return The current `BufferedImage`.
     */
    public BufferedImage getCurrentImage() {
        return image;
    }

    /**
     * Overridden to paint the image on the panel. The image is drawn centered.
     *
     * @param g The `Graphics` context in which to paint.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            // Calculate position to center the image
            int x = (getWidth() - image.getWidth()) / 2;
            int y = (getHeight() - image.getHeight()) / 2;
            g.drawImage(image, x, y, this);
        }
    }
}
