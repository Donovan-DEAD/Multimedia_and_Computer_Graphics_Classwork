package com.github.donovan_dead.GUI;

import com.github.donovan_dead.Actions.Actionable;
import com.github.donovan_dead.Actions.CropImage;
import com.github.donovan_dead.Actions.InvertColorImage;
import com.github.donovan_dead.Actions.RotateImage;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;

public class ControlsPanel extends JPanel {

    private ImageEditorListener listener;

    // Action Panels
    private JPanel invertPanel;
    private JPanel rotatePanel;
    private JPanel cropPanel;

    // Point Input Panels
    private PointInputPanel p1Invert, p2Invert, p3Invert, p4Invert;
    private PointInputPanel p1Rotate, p2Rotate, p3Rotate, p4Rotate;
    private PointInputPanel p1Crop, p2Crop, p3Crop, p4Crop;

    // Angle Input for Rotate
    private JTextField rotateAngleField;

    public ControlsPanel(ImageEditorListener listener) {
        this.listener = listener;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setPreferredSize(new Dimension(250, 0)); // Set a preferred width

        add(createFileOperationsPanel());
        add(Box.createRigidArea(new Dimension(0, 10))); // Spacer
        add(createUndoRedoPanel());
        add(Box.createRigidArea(new Dimension(0, 10))); // Spacer

        add(createInvertPanel());
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(createRotatePanel());
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(createCropPanel());
        add(Box.createVerticalGlue()); // Pushes everything to the top
    }

    private JPanel createFileOperationsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 2, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("File"));

        JButton openBtn = new JButton("Open");
        openBtn.addActionListener(e -> listener.openImage(null)); // null to trigger file chooser
        panel.add(openBtn);

        JButton saveBtn = new JButton("Save");
        saveBtn.addActionListener(e -> listener.saveImage(listener.getCurrentImage(), null)); // null to trigger file chooser
        panel.add(saveBtn);

        return panel;
    }


    private JPanel createUndoRedoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 2, 5, 5)); // 1 row, 2 columns, 5px gap
        panel.setBorder(BorderFactory.createTitledBorder("History"));

        JButton undoBtn = new JButton("Undo");
        undoBtn.addActionListener(e -> listener.undoAction());
        panel.add(undoBtn);

        JButton redoBtn = new JButton("Redo");
        redoBtn.addActionListener(e -> listener.redoAction());
        panel.add(redoBtn);

        return panel;
    }

    private JPanel createInvertPanel() {
        invertPanel = new JPanel();
        invertPanel.setLayout(new BoxLayout(invertPanel, BoxLayout.Y_AXIS));
        invertPanel.setBorder(BorderFactory.createTitledBorder("Invert Colors"));

        JRadioButton twoPointsRadio = new JRadioButton("2 Points (Rectangle)");
        JRadioButton fourPointsRadio = new JRadioButton("4 Points (Irregular)");
        ButtonGroup group = new ButtonGroup();
        group.add(twoPointsRadio);
        group.add(fourPointsRadio);
        twoPointsRadio.setSelected(true); // Default selection

        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        radioPanel.add(twoPointsRadio);
        radioPanel.add(fourPointsRadio);
        invertPanel.add(radioPanel);

        p1Invert = new PointInputPanel("Point 1 (X, Y increasing upwards)");
        p2Invert = new PointInputPanel("Point 2 (X, Y increasing upwards)");
        p3Invert = new PointInputPanel("Point 3 (X, Y increasing upwards)");
        p4Invert = new PointInputPanel("Point 4 (X, Y increasing upwards)");

        JPanel p3p4InvertWrapper = new JPanel();
        p3p4InvertWrapper.setLayout(new BoxLayout(p3p4InvertWrapper, BoxLayout.Y_AXIS));
        p3p4InvertWrapper.add(p3Invert);
        p3p4InvertWrapper.add(p4Invert);

        invertPanel.add(p1Invert);
        invertPanel.add(p2Invert);
        invertPanel.add(p3p4InvertWrapper);

        // Initially show only 2 points
        p3p4InvertWrapper.setVisible(false);

        twoPointsRadio.addActionListener(e -> {
            p3p4InvertWrapper.setVisible(false);
            this.revalidate();
            this.repaint();
        });
        fourPointsRadio.addActionListener(e -> {
            p3p4InvertWrapper.setVisible(true);
            this.revalidate();
            this.repaint();
        });

        JButton applyInvertBtn = new JButton("Apply Invert");
        applyInvertBtn.addActionListener(e -> {
            try {
                Point2D p1 = p1Invert.getPoint();
                Point2D p2 = p2Invert.getPoint();
                Actionable invertAction;
                if (twoPointsRadio.isSelected()) {
                    invertAction = new InvertColorImage(p1, p2);
                } else {
                    Point2D p3 = p3Invert.getPoint();
                    Point2D p4 = p4Invert.getPoint();
                    invertAction = new InvertColorImage(p1, p2, p3, p4);
                }
                listener.addAction(invertAction);
                listener.updateImageDisplay();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid number format for points.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        invertPanel.add(applyInvertBtn);

        return invertPanel;
    }

    private JPanel createRotatePanel() {
        rotatePanel = new JPanel();
        rotatePanel.setLayout(new BoxLayout(rotatePanel, BoxLayout.Y_AXIS));
        rotatePanel.setBorder(BorderFactory.createTitledBorder("Rotate Image"));

        JRadioButton twoPointsRadio = new JRadioButton("2 Points (Rectangle)");
        JRadioButton fourPointsRadio = new JRadioButton("4 Points (Irregular)");
        ButtonGroup group = new ButtonGroup();
        group.add(twoPointsRadio);
        group.add(fourPointsRadio);
        twoPointsRadio.setSelected(true); // Default selection

        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        radioPanel.add(twoPointsRadio);
        radioPanel.add(fourPointsRadio);
        rotatePanel.add(radioPanel);

        rotatePanel.add(new JLabel("Angle (degrees):"));
        rotateAngleField = new JTextField(5);
        rotatePanel.add(rotateAngleField);

        p1Rotate = new PointInputPanel("Point 1 (X, Y increasing upwards)");
        p2Rotate = new PointInputPanel("Point 2 (X, Y increasing upwards)");
        p3Rotate = new PointInputPanel("Point 3 (X, Y increasing upwards)");
        p4Rotate = new PointInputPanel("Point 4 (X, Y increasing upwards)");

        JPanel p3p4RotateWrapper = new JPanel();
        p3p4RotateWrapper.setLayout(new BoxLayout(p3p4RotateWrapper, BoxLayout.Y_AXIS));
        p3p4RotateWrapper.add(p3Rotate);
        p3p4RotateWrapper.add(p4Rotate);

        rotatePanel.add(p1Rotate);
        rotatePanel.add(p2Rotate);
        rotatePanel.add(p3p4RotateWrapper);

        // Initially show only 2 points
        p3p4RotateWrapper.setVisible(false);

        twoPointsRadio.addActionListener(e -> {
            p3p4RotateWrapper.setVisible(false);
            this.revalidate();
            this.repaint();
        });
        fourPointsRadio.addActionListener(e -> {
            p3p4RotateWrapper.setVisible(true);
            this.revalidate();
            this.repaint();
        });

        JButton applyRotateBtn = new JButton("Apply Rotate");
        applyRotateBtn.addActionListener(e -> {
            try {
                double angle = Double.parseDouble(rotateAngleField.getText());
                Point2D p1 = p1Rotate.getPoint();
                Point2D p2 = p2Rotate.getPoint();
                Actionable rotateAction;
                if (twoPointsRadio.isSelected()) {
                    rotateAction = new RotateImage((float) angle, p1, p2);
                } else {
                    Point2D p3 = p3Rotate.getPoint();
                    Point2D p4 = p4Rotate.getPoint();
                    rotateAction = new RotateImage(angle, p1, p2, p3, p4);
                }
                listener.addAction(rotateAction);
                listener.updateImageDisplay();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid number format for angle or points.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        rotatePanel.add(applyRotateBtn);

        return rotatePanel;
    }

    private JPanel createCropPanel() {
        cropPanel = new JPanel();
        cropPanel.setLayout(new BoxLayout(cropPanel, BoxLayout.Y_AXIS));
        cropPanel.setBorder(BorderFactory.createTitledBorder("Crop Image"));

        JRadioButton twoPointsRadio = new JRadioButton("2 Points (Rectangle)");
        JRadioButton fourPointsRadio = new JRadioButton("4 Points (Irregular)");
        ButtonGroup group = new ButtonGroup();
        group.add(twoPointsRadio);
        group.add(fourPointsRadio);
        twoPointsRadio.setSelected(true); // Default selection

        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        radioPanel.add(twoPointsRadio);
        radioPanel.add(fourPointsRadio);
        cropPanel.add(radioPanel);

        p1Crop = new PointInputPanel("Point 1 (X, Y increasing upwards)");
        p2Crop = new PointInputPanel("Point 2 (X, Y increasing upwards)");
        p3Crop = new PointInputPanel("Point 3 (X, Y increasing upwards)");
        p4Crop = new PointInputPanel("Point 4 (X, Y increasing upwards)");

        JPanel p3p4CropWrapper = new JPanel();
        p3p4CropWrapper.setLayout(new BoxLayout(p3p4CropWrapper, BoxLayout.Y_AXIS));
        p3p4CropWrapper.add(p3Crop);
        p3p4CropWrapper.add(p4Crop);

        cropPanel.add(p1Crop);
        cropPanel.add(p2Crop);
        cropPanel.add(p3p4CropWrapper);

        // Initially show only 2 points
        p3p4CropWrapper.setVisible(false);

        twoPointsRadio.addActionListener(e -> {
            p3p4CropWrapper.setVisible(false);
            this.revalidate();
            this.repaint();
        });
        fourPointsRadio.addActionListener(e -> {
            p3p4CropWrapper.setVisible(true);
            this.revalidate();
            this.repaint();
        });

        JButton applyCropBtn = new JButton("Apply Crop");
        applyCropBtn.addActionListener(e -> {
            try {
                Point2D p1 = p1Crop.getPoint();
                Point2D p2 = p2Crop.getPoint();
                Actionable cropAction;
                if (twoPointsRadio.isSelected()) {
                    cropAction = new CropImage(p1, p2);
                } else {
                    Point2D p3 = p3Crop.getPoint();
                    Point2D p4 = p4Crop.getPoint();
                    cropAction = new CropImage(p1, p2, p3, p4);
                }
                listener.addAction(cropAction);
                listener.updateImageDisplay();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid number format for points.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        cropPanel.add(applyCropBtn);

        return cropPanel;
    }
}
