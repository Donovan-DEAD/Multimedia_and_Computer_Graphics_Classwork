package com.github.donovan_dead.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.github.donovan_dead.Actions.Actionable;
import com.github.donovan_dead.Actions.ActionExecutioner;

public class MainFrame extends JFrame implements ImageEditorListener {

    private ImagePanel imagePanel;
    private ActionExecutioner actionExecutioner;
    private BufferedImage originalImage;
    private File currentImageFile; // Keep track of the last opened/saved file location

    public MainFrame() {
        setTitle("Image Editor");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        imagePanel = new ImagePanel();
        JScrollPane imageScrollPane = new JScrollPane(imagePanel);
        imageScrollPane.setBackground(Color.DARK_GRAY);
        add(imageScrollPane, BorderLayout.CENTER);

        // Initialize with a placeholder or null image
        // Will be properly initialized when an image is opened
        actionExecutioner = new ActionExecutioner(new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB));
        originalImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        updateImageDisplay();

        setupMenuBar();
        setupControlsPanel();
    }

    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu editMenu = new JMenu("Edit");

        JMenuItem openItem = new JMenuItem("Open");
        openItem.addActionListener(e -> openImage(null));
        fileMenu.add(openItem);

        JMenuItem saveItem = new JMenuItem("Save");
        saveItem.addActionListener(e -> saveImage(imagePanel.getCurrentImage(), null));
        fileMenu.add(saveItem);

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);

        JMenuItem undoItem = new JMenuItem("Undo");
        undoItem.addActionListener(e -> undoAction());
        editMenu.add(undoItem);

        JMenuItem redoItem = new JMenuItem("Redo");
        redoItem.addActionListener(e -> redoAction());
        editMenu.add(redoItem);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        setJMenuBar(menuBar);
    }

    private void setupControlsPanel() {
        ControlsPanel controlsPanel = new ControlsPanel(this);
        JScrollPane scrollPane = new JScrollPane(controlsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        // Let the BorderLayout manage the height.


        add(scrollPane, BorderLayout.EAST);
    }

    @Override
    public void openImage(File file) {
        JFileChooser fileChooser = new JFileChooser();
        if (currentImageFile != null) {
            fileChooser.setCurrentDirectory(currentImageFile.getParentFile());
        }
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            currentImageFile = fileChooser.getSelectedFile();
            try {
                BufferedImage newImage = ImageIO.read(currentImageFile);
                if (newImage != null) {
                    originalImage = newImage;
                    actionExecutioner = new ActionExecutioner(originalImage); // Reset executioner
                    updateImageDisplay();
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error opening image: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void saveImage(BufferedImage image, File file) {
        if (imagePanel.getCurrentImage() == null) {
            JOptionPane.showMessageDialog(this, "No image to save.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        if (currentImageFile != null) {
            fileChooser.setCurrentDirectory(currentImageFile.getParentFile());
            fileChooser.setSelectedFile(new File(currentImageFile.getName().replaceFirst("[.][^.]+$", "") + "_processed.png"));
        } else {
            fileChooser.setSelectedFile(new File("processed_image.png")); // Default name
        }
        
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File outputFile = fileChooser.getSelectedFile();
            try {
                String format = "png"; // Default format
                String fileName = outputFile.getName();
                int dotIndex = fileName.lastIndexOf('.');
                if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
                    format = fileName.substring(dotIndex + 1);
                }
                ImageIO.write(imagePanel.getCurrentImage(), format, outputFile);
                JOptionPane.showMessageDialog(this, "Image saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving image: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void addAction(Actionable action) {
        if (originalImage == null) {
            JOptionPane.showMessageDialog(this, "Please open an image first.", "No Image", JOptionPane.WARNING_MESSAGE);
            return;
        }
        actionExecutioner.addAction(action);
    }

    @Override
    public void updateImageDisplay() {
        if (originalImage == null) return; // No image loaded yet

        BufferedImage processedImage = actionExecutioner.executeActions();
        imagePanel.setImage(processedImage);
    }

    @Override
    public void undoAction() {
        if (originalImage == null) return;
        actionExecutioner.popAction();
        updateImageDisplay();
    }

    @Override
    public void redoAction() {
        if (originalImage == null) return;
        actionExecutioner.regainLostAction();
        updateImageDisplay();
    }

    @Override
    public BufferedImage getCurrentImage() {
        return imagePanel.getCurrentImage();
    }

    @Override
    public BufferedImage getOriginalImage() {
        return originalImage;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
