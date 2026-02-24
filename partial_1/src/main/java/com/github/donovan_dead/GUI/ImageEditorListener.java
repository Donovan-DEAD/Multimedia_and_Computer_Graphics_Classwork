package com.github.donovan_dead.GUI;

import com.github.donovan_dead.Actions.Actionable;

import java.awt.image.BufferedImage;
import java.io.File;

public interface ImageEditorListener {
    void addAction(Actionable action);
    void updateImageDisplay();
    void undoAction();
    void redoAction();
    void openImage(File file);
    void saveImage(BufferedImage image, File file);
    BufferedImage getCurrentImage();
    BufferedImage getOriginalImage();
}
