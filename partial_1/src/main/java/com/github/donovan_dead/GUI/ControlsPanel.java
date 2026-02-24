package com.github.donovan_dead.GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import com.github.donovan_dead.Actions.Actionable;
import com.github.donovan_dead.Actions.CropImage;
import com.github.donovan_dead.Actions.InvertColorImage;
import com.github.donovan_dead.Actions.RotateImage;

public class ControlsPanel extends JPanel {

    private ImageEditorListener listener;

    // Point Input Panels
    private PointInputPanel p1Invert, p2Invert, p3Invert, p4Invert;
    private PointInputPanel p1Rotate, p2Rotate, p3Rotate, p4Rotate;
    private PointInputPanel p1Crop, p2Crop, p3Crop, p4Crop;

    // Angle Input for Rotate
    private JTextField rotateAngleField;

    public ControlsPanel(ImageEditorListener listener) {
        this.listener = listener;
        setLayout(new BorderLayout()); // Main layout
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Top Panel for non-tabbed controls ---
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.add(createFileOperationsPanel());
        topPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Spacer
        topPanel.add(createUndoRedoPanel());
        topPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Spacer

        add(topPanel, BorderLayout.NORTH);

        // --- Main Tabbed Pane for Tools ---
        JTabbedPane toolsTabbedPane = new JTabbedPane();
        toolsTabbedPane.addTab("Invert", createInvertPanel());
        toolsTabbedPane.addTab("Rotate", createRotatePanel());
        toolsTabbedPane.addTab("Crop", createCropPanel());

        add(toolsTabbedPane, BorderLayout.CENTER);
    }

    private JPanel createFileOperationsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 2, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("File"));

        JButton openBtn = new JButton("Open");
        openBtn.addActionListener(e -> listener.openImage(null));
        panel.add(openBtn);

        JButton saveBtn = new JButton("Save");
        saveBtn.addActionListener(e -> listener.saveImage(listener.getCurrentImage(), null));
        panel.add(saveBtn);

        return panel;
    }


    private JPanel createUndoRedoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 2, 5, 5));
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
        JPanel invertPanel = new JPanel();
        invertPanel.setLayout(new BoxLayout(invertPanel, BoxLayout.Y_AXIS));
        invertPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JTabbedPane tabbedPane = new JTabbedPane();

        // Panel for 2 points
        JPanel twoPointsPanel = new JPanel();
        twoPointsPanel.setLayout(new BoxLayout(twoPointsPanel, BoxLayout.Y_AXIS));
        p1Invert = new PointInputPanel("Point 1 (X, Y increasing upwards)");
        p2Invert = new PointInputPanel("Point 2 (X, Y increasing upwards)");
        twoPointsPanel.add(p1Invert);
        twoPointsPanel.add(p2Invert);
        tabbedPane.addTab("2 Points (Rectangle)", twoPointsPanel);

        // Panel for 4 points
        JPanel fourPointsPanel = new JPanel();
        fourPointsPanel.setLayout(new BoxLayout(fourPointsPanel, BoxLayout.Y_AXIS));
        PointInputPanel p1InvertFour = new PointInputPanel("Point 1 (X, Y increasing upwards)");
        PointInputPanel p2InvertFour = new PointInputPanel("Point 2 (X, Y increasing upwards)");
        p3Invert = new PointInputPanel("Point 3 (X, Y increasing upwards)");
        p4Invert = new PointInputPanel("Point 4 (X, Y increasing upwards)");
        fourPointsPanel.add(p1InvertFour);
        fourPointsPanel.add(p2InvertFour);
        fourPointsPanel.add(p3Invert);
        fourPointsPanel.add(p4Invert);
        tabbedPane.addTab("4 Points (Irregular)", fourPointsPanel);

        tabbedPane.addChangeListener(e -> {
            p1Invert.clearFields(); p2Invert.clearFields();
            p1InvertFour.clearFields(); p2InvertFour.clearFields();
            p3Invert.clearFields(); p4Invert.clearFields();
        });

        invertPanel.add(tabbedPane);

        JButton applyInvertBtn = new JButton("Apply Invert");
        applyInvertBtn.addActionListener(e -> {
            try {
                Actionable invertAction;
                if (tabbedPane.getSelectedIndex() == 0) { // 2 Points
                    invertAction = new InvertColorImage(p1Invert.getPoint(), p2Invert.getPoint());
                } else { // 4 Points
                    invertAction = new InvertColorImage(p1InvertFour.getPoint(), p2InvertFour.getPoint(), p3Invert.getPoint(), p4Invert.getPoint());
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
        JPanel rotatePanel = new JPanel();
        rotatePanel.setLayout(new BoxLayout(rotatePanel, BoxLayout.Y_AXIS));
        rotatePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel anglePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        anglePanel.add(new JLabel("Angle (degrees):"));
        rotateAngleField = new JTextField(5);
        anglePanel.add(rotateAngleField);
        rotatePanel.add(anglePanel);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Panel for 2 points
        JPanel twoPointsPanel = new JPanel();
        twoPointsPanel.setLayout(new BoxLayout(twoPointsPanel, BoxLayout.Y_AXIS));
        p1Rotate = new PointInputPanel("Point 1 (X, Y increasing upwards)");
        p2Rotate = new PointInputPanel("Point 2 (X, Y increasing upwards)");
        twoPointsPanel.add(p1Rotate);
        twoPointsPanel.add(p2Rotate);
        tabbedPane.addTab("2 Points (Rectangle)", twoPointsPanel);

        // Panel for 4 points
        JPanel fourPointsPanel = new JPanel();
        fourPointsPanel.setLayout(new BoxLayout(fourPointsPanel, BoxLayout.Y_AXIS));
        PointInputPanel p1RotateFour = new PointInputPanel("Point 1 (X, Y increasing upwards)");
        PointInputPanel p2RotateFour = new PointInputPanel("Point 2 (X, Y increasing upwards)");
        p3Rotate = new PointInputPanel("Point 3 (X, Y increasing upwards)");
        p4Rotate = new PointInputPanel("Point 4 (X, Y increasing upwards)");
        fourPointsPanel.add(p1RotateFour);
        fourPointsPanel.add(p2RotateFour);
        fourPointsPanel.add(p3Rotate);
        fourPointsPanel.add(p4Rotate);
        tabbedPane.addTab("4 Points (Irregular)", fourPointsPanel);
        
        tabbedPane.addChangeListener(e -> {
            p1Rotate.clearFields(); p2Rotate.clearFields();
            p1RotateFour.clearFields(); p2RotateFour.clearFields();
            p3Rotate.clearFields(); p4Rotate.clearFields();
        });

        rotatePanel.add(tabbedPane);

        JButton applyRotateBtn = new JButton("Apply Rotate");
        applyRotateBtn.addActionListener(e -> {
            try {
                double angle = Double.parseDouble(rotateAngleField.getText());
                Actionable rotateAction;
                if (tabbedPane.getSelectedIndex() == 0) { // 2 Points
                    rotateAction = new RotateImage((float) angle, p1Rotate.getPoint(), p2Rotate.getPoint());
                } else { // 4 Points
                    rotateAction = new RotateImage(angle, p1RotateFour.getPoint(), p2RotateFour.getPoint(), p3Rotate.getPoint(), p4Rotate.getPoint());
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
        JPanel cropPanel = new JPanel();
        cropPanel.setLayout(new BoxLayout(cropPanel, BoxLayout.Y_AXIS));
        cropPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JTabbedPane tabbedPane = new JTabbedPane();

        // Panel for 2 points
        JPanel twoPointsPanel = new JPanel();
        twoPointsPanel.setLayout(new BoxLayout(twoPointsPanel, BoxLayout.Y_AXIS));
        p1Crop = new PointInputPanel("Point 1 (X, Y increasing upwards)");
        p2Crop = new PointInputPanel("Point 2 (X, Y increasing upwards)");
        twoPointsPanel.add(p1Crop);
        twoPointsPanel.add(p2Crop);
        tabbedPane.addTab("2 Points (Rectangle)", twoPointsPanel);

        // Panel for 4 points
        JPanel fourPointsPanel = new JPanel();
        fourPointsPanel.setLayout(new BoxLayout(fourPointsPanel, BoxLayout.Y_AXIS));
        PointInputPanel p1CropFour = new PointInputPanel("Point 1 (X, Y increasing upwards)");
        PointInputPanel p2CropFour = new PointInputPanel("Point 2 (X, Y increasing upwards)");
        p3Crop = new PointInputPanel("Point 3 (X, Y increasing upwards)");
        p4Crop = new PointInputPanel("Point 4 (X, Y increasing upwards)");
        fourPointsPanel.add(p1CropFour);
        fourPointsPanel.add(p2CropFour);
        fourPointsPanel.add(p3Crop);
        fourPointsPanel.add(p4Crop);
        tabbedPane.addTab("4 Points (Irregular)", fourPointsPanel);

        tabbedPane.addChangeListener(e -> {
            p1Crop.clearFields(); p2Crop.clearFields();
            p1CropFour.clearFields(); p2CropFour.clearFields();
            p3Crop.clearFields(); p4Crop.clearFields();
        });

        cropPanel.add(tabbedPane);

        JButton applyCropBtn = new JButton("Apply Crop");
        applyCropBtn.addActionListener(e -> {
            try {
                Actionable cropAction;
                if (tabbedPane.getSelectedIndex() == 0) { // 2 Points
                    cropAction = new CropImage(p1Crop.getPoint(), p2Crop.getPoint());
                } else { // 4 Points
                    cropAction = new CropImage(p1CropFour.getPoint(), p2CropFour.getPoint(), p3Crop.getPoint(), p4Crop.getPoint());
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
