package com.github.donovan_dead.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.github.donovan_dead.Actions.Actionable;
import com.github.donovan_dead.Actions.ActionExecutioner;

/**
 * The `MainFrame` class is the main window of the image editor application.
 * It sets up the user interface, including the menu bar, control panel, and image display area.
 * It also implements the `ImageEditorListener` to handle all image processing logic.
 */
public class MainFrame extends JFrame implements ImageEditorListener {

    /** The panel responsible for displaying the image. */
    private ImagePanel imagePanel;
    /** The executioner that manages and applies image actions. */
    private ActionExecutioner actionExecutioner;
    /** The original, unmodified image loaded by the user. */
    private BufferedImage originalImage;
    /** The file of the currently loaded image, used for context in file dialogs. */
    private File currentImageFile;

    /**
     * Constructs the main application frame and initializes all UI components.
     */
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

    /**
     * Sets up the main menu bar with "File" and "Edit" menus.
     */
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

    /**
     * Sets up the control panel on the east side of the frame.
     */
    private void setupControlsPanel() {
        ControlsPanel controlsPanel = new ControlsPanel(this);
        JScrollPane scrollPane = new JScrollPane(controlsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        // Let the BorderLayout manage the height.


        add(scrollPane, BorderLayout.EAST);
    }

    /**
     * Opens an image file using a `JFileChooser` and displays it.
     * This resets the action history and sets the new image as the original.
     * @param file (Unused) Can be extended to open a specific file directly.
     */
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

    /**
     * Saves the currently displayed image to a file using a `JFileChooser`.
     * It suggests a processed filename and determines the format from the extension.
     * @param image The image to be saved.
     * @param file (Unused) Can be extended to save to a specific file directly.
     */
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

    /**
     * Adds an action to the `ActionExecutioner`.
     * @param action The `Actionable` to be added to the queue.
     */
    @Override
    public void addAction(Actionable action) {
        if (originalImage == null) {
            JOptionPane.showMessageDialog(this, "Please open an image first.", "No Image", JOptionPane.WARNING_MESSAGE);
            return;
        }
        actionExecutioner.addAction(action);
    }

    /**
     * Executes all queued actions and updates the `ImagePanel` with the result.
     */
    @Override
    public void updateImageDisplay() {
        if (originalImage == null) return; // No image loaded yet

        BufferedImage processedImage = actionExecutioner.executeActions();
        imagePanel.setImage(processedImage);
    }

    /**
     * Undoes the last action by popping it from the `ActionExecutioner` stack
     * and updating the display.
     */
    @Override
    public void undoAction() {
        if (originalImage == null) return;
        actionExecutioner.popAction();
        updateImageDisplay();
    }

    /**
     * Redoes the last undone action by moving it back to the action stack
     * and updating the display.
     */
    @Override
    public void redoAction() {
        if (originalImage == null) return;
        actionExecutioner.regainLostAction();
        updateImageDisplay();
    }

    /**
     * Gets the currently displayed (processed) image from the `ImagePanel`.
     * @return The current `BufferedImage`.
     */
    @Override
    public BufferedImage getCurrentImage() {
        return imagePanel.getCurrentImage();
    }

    /**
     * Gets the original, unmodified image.
     * @return The original `BufferedImage`.
     */
    @Override
    public BufferedImage getOriginalImage() {
        return originalImage;
    }

    /**
     * The main entry point of the application.
     * Creates and shows the `MainFrame` on the Event Dispatch Thread.
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
