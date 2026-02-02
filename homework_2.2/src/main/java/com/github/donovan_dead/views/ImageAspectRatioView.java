package com.github.donovan_dead.views;

import com.github.donovan_dead.Utilities.AspectRatioCalculator;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageAspectRatioView extends JPanel {
    private final JButton chooseFileButton = new JButton("Choose Image...");
    private final JLabel imageLabel = new JLabel();
    private final JLabel resultLabel = new JLabel("Select an image to see its aspect ratio.");
    private final JFileChooser fileChooser = new JFileChooser();

    public ImageAspectRatioView() {
        super(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(chooseFileButton);
        topPanel.add(resultLabel);

        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        JScrollPane scrollPane = new JScrollPane(imageLabel);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        chooseFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnValue = fileChooser.showOpenDialog(ImageAspectRatioView.this);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    try {
                        BufferedImage img = ImageIO.read(selectedFile);
                        if (img == null) {
                            resultLabel.setText("Error: The selected file is not a valid image.");
                            imageLabel.setIcon(null);
                            return;
                        }

                        long[] ratio = AspectRatioCalculator.calculateAspectRatio(img.getWidth(), img.getHeight());

                        if (ratio != null) {
                            resultLabel.setText("Aspect Ratio: " + ratio[0] + ":" + ratio[1] + " Original dimensions (w,h) : (" + img.getWidth() + " , " + img.getHeight() + ") ");
                        } else {
                            resultLabel.setText("Could not calculate aspect ratio.");
                        }

                        int panelWidth = getWidth() > 0 ? getWidth() : 400;
                        int panelHeight = getHeight() > 0 ? getHeight() : 300;
                        Image scaledImg = img.getScaledInstance(panelWidth, panelHeight, Image.SCALE_SMOOTH);
                        imageLabel.setIcon(new ImageIcon(scaledImg));

                    } catch (IOException ex) {
                        resultLabel.setText("Error reading file: " + ex.getMessage());
                        imageLabel.setIcon(null);
                    }
                }
            }
        });
    }
}
