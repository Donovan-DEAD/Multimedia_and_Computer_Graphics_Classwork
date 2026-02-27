package com.github.donovan_dead.GUI;

import com.github.donovan_dead.Actions.Actionable;

import java.awt.image.BufferedImage;
import java.io.File;

/**
 * The `ImageEditorListener` interface defines the contract for communication
 * between the GUI components (like `ControlsPanel`) and the main application logic
 * that manages the image state and operations.
 */
public interface ImageEditorListener {
    /**
     * Adds a new image manipulation action to the execution queue.
     *
     * @param action The `Actionable` action to be added.
     */
    void addAction(Actionable action);

    /**
     * Requests an update of the image display, typically after an action has been applied.
     */
    void updateImageDisplay();

    /**
     * Undoes the last applied action.
     */
    void undoAction();

    /**
     * Redoes the last undone action.
     */
    void redoAction();

    /**
     * Opens an image from a file. If the file is null, a file chooser should be shown.
     *
     * @param file The file to open, or null to prompt the user.
     */
    void openImage(File file);

    /**
     * Saves the provided image to a file. If the file is null, a file chooser should be shown.
     *
     * @param image The `BufferedImage` to save.
     * @param file The file to save to, or null to prompt the user.
     */
    void saveImage(BufferedImage image, File file);

    /**
     * Retrieves the current state of the image after all actions have been applied.
     *
     * @return The current `BufferedImage`.
     */
    BufferedImage getCurrentImage();

    /**
     * Retrieves the original, unmodified image.
     *
     * @return The original `BufferedImage`.
     */
    BufferedImage getOriginalImage();
}
