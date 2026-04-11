package com.github.donovan_dead.GUI;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import com.github.donovan_dead.VideoConstructor.VideoConstructor;
import com.github.donovan_dead.VideoConstructor.Components.InfoBlock;

public class VideoGUI extends JFrame {
    private DefaultListModel<InfoBlock> listModel;
    private JList<InfoBlock> fileList;
    private JProgressBar progressBar;
    private JButton btnAdd, btnRun, btnView, btnClear;
    private JLabel lblStatus;
    private File finalVideoFile = null;

    public VideoGUI() {
        setTitle("Video Constructor GUI");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // List model and JList
        listModel = new DefaultListModel<>();
        fileList = new JList<>(listModel);
        fileList.setCellRenderer(new InfoBlockRenderer());
        fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(fileList);
        add(scrollPane, BorderLayout.CENTER);

        // Top Panel for adding files
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnAdd = new JButton("Agregar Archivo");
        btnAdd.addActionListener(e -> addFile());
        topPanel.add(btnAdd);

        JButton btnDelete = new JButton("Eliminar Seleccionado (X)");
        btnDelete.addActionListener(e -> deleteSelectedFile());
        topPanel.add(btnDelete);

        btnClear = new JButton("Limpiar Todo");
        btnClear.addActionListener(e -> clearAll());
        topPanel.add(btnClear);

        add(topPanel, BorderLayout.NORTH);

        // Bottom Panel for progress and run
        JPanel bottomPanel = new JPanel(new BorderLayout());
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        bottomPanel.add(progressBar, BorderLayout.NORTH);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        lblStatus = new JLabel("Listo");
        controls.add(lblStatus);

        btnRun = new JButton("Correr Producción");
        btnRun.addActionListener(e -> runProduction());
        controls.add(btnRun);

        btnView = new JButton("Ver Resultado");
        btnView.setEnabled(false);
        btnView.addActionListener(e -> viewResult());
        controls.add(btnView);

        bottomPanel.add(controls, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Timer to update UI status periodically
        Timer timer = new Timer(500, e -> {
            fileList.repaint();
            updateProgress();
        });
        timer.start();
    }

    private void addFile() {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Imágenes y Videos", "jpg", "jpeg", "png", "tiff", "mp4", "avi", "mov", "mkv");
        chooser.setFileFilter(filter);
        chooser.setMultiSelectionEnabled(true);

        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File[] files = chooser.getSelectedFiles();
            for (File f : files) {
                try {
                    VideoConstructor.appendInfoBlock(f);
                    refreshList();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error al agregar " + f.getName() + ": " + ex.getMessage());
                }
            }
        }
    }

    private void deleteSelectedFile() {
        int index = fileList.getSelectedIndex();
        if (index != -1) {
            InfoBlock block = listModel.get(index);
            VideoConstructor.removeInfoBlock(block.getOriginalFile());
            refreshList();
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un archivo para eliminar.");
        }
    }

    private void refreshList() {
        listModel.clear();
        ArrayList<InfoBlock> blocks = VideoConstructor.getVideoBlocks();
        for (InfoBlock block : blocks) {
            listModel.addElement(block);
        }
        btnRun.setEnabled(!blocks.isEmpty());
    }

    private void updateProgress() {
        ArrayList<InfoBlock> blocks = VideoConstructor.getVideoBlocks();
        if (blocks.isEmpty()) {
            progressBar.setValue(0);
            return;
        }

        int totalTasks = blocks.size() * 2 + 2; 
        int completedTasks = 0;

        for (InfoBlock b : blocks) {
            if (b.isNormalized()) completedTasks++;
            if (b.isAudioIntegrated()) completedTasks++;
        }

        if (VideoConstructor.getInitialVideo() != null) completedTasks++;
        if (VideoConstructor.getFinalVideo() != null) completedTasks++;

        int percentage = (completedTasks * 100) / totalTasks;
        progressBar.setValue(percentage);
    }

    private void runProduction() {
        if (VideoConstructor.getVideoBlocks().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Agregue archivos antes de iniciar.");
            return;
        }

        btnAdd.setEnabled(false);
        btnRun.setEnabled(false);
        btnClear.setEnabled(false);
        btnView.setEnabled(false);
        lblStatus.setText("Procesando...");

        SwingWorker<File, String> worker = new SwingWorker<>() {
            @Override
            protected File doInBackground() throws Exception {
                VideoConstructor.initTempDir();
                
                publish("Obteniendo metadata...");
                VideoConstructor.obtainMetadataFromInfoBlocks();
                
                publish("Ordenando...");
                VideoConstructor.sortByCreationDate();
                
                publish("Regularizando...");
                VideoConstructor.regularizeFiles();
                
                publish("Llamando a OpenAI APIs...");
                VideoConstructor.callOpenAiApis();
                
                publish("Generando Imagen Inicial...");
                VideoConstructor.generateInitialImage();
                
                publish("Generando Mapa Final...");
                VideoConstructor.generateFinalMap();
                
                publish("Generando Video Final...");
                return VideoConstructor.generateFinalVideo();
            }

            @Override
            protected void process(java.util.List<String> chunks) {
                lblStatus.setText(chunks.get(chunks.size() - 1));
            }

            @Override
            protected void done() {
                try {
                    finalVideoFile = get(); // Obtener el archivo resultante de generateFinalVideo
                    if (finalVideoFile != null && finalVideoFile.exists()) {
                        lblStatus.setText("Completado: " + finalVideoFile.getName());
                        btnView.setEnabled(true);
                    } else {
                        lblStatus.setText("Error: No se pudo generar el video");
                    }
                } catch (Exception e) {
                    lblStatus.setText("Excepción: " + e.getMessage());
                    e.printStackTrace();
                } finally {
                    btnAdd.setEnabled(true);
                    btnRun.setEnabled(true);
                    btnClear.setEnabled(true);
                }
            }
        };

        worker.execute();
    }

    private void clearAll() {
        VideoConstructor.reset();
        refreshList();
        progressBar.setValue(0);
        lblStatus.setText("Listo (Limpio)");
        finalVideoFile = null;
        btnView.setEnabled(false);
    }

    private void viewResult() {
        if (finalVideoFile != null && finalVideoFile.exists()) {
            try {
                Desktop.getDesktop().open(finalVideoFile);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "No se pudo abrir el video: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "El archivo de video no existe.");
        }
    }

    private class InfoBlockRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            InfoBlock block = (InfoBlock) value;
            String text = block.getOriginalFile().getName() + " [" + block.getCoords() + "]";
            
            JLabel label = (JLabel) super.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
            
            if (block.isAudioIntegrated()) {
                label.setBackground(new Color(144, 238, 144)); // Light Green (Completado)
            } else if (block.isNormalized()) {
                label.setBackground(new Color(255, 255, 153)); // Light Yellow (A mitad de proceso)
            } else {
                label.setBackground(Color.WHITE); // Sin procesar
            }

            if (isSelected) {
                label.setBorder(BorderFactory.createLineBorder(Color.BLUE, 1));
            }

            label.setOpaque(true);
            return label;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new VideoGUI().setVisible(true);
        });
    }
}
