package com.github.donovan_dead.GUI;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import com.github.donovan_dead.VideoConstructor.VideoConstructor;
import com.github.donovan_dead.VideoConstructor.Components.InfoBlock;

/**
 * Interfaz gráfica de usuario para el constructor de video.
 * Permite agregar archivos, visualizar el progreso del procesamiento y ver el video resultante.
 */
public class VideoGUI extends JFrame {
    private DefaultListModel<InfoBlock> listModel;
    private JList<InfoBlock> fileList;
    private JProgressBar progressBar;
    private JButton btnAdd, btnRun, btnView, btnClear;
    private JLabel lblStatus;
    private File finalVideoFile = null;

    /**
     * Constructor que inicializa los componentes de la interfaz de usuario.
     */
    public VideoGUI() {
        setTitle("Video Constructor GUI");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        listModel = new DefaultListModel<>();
        fileList = new JList<>(listModel);
        fileList.setCellRenderer(new InfoBlockRenderer());
        fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(fileList);
        add(scrollPane, BorderLayout.CENTER);

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

        Timer timer = new Timer(500, e -> {
            fileList.repaint();
            updateProgress();
        });
        timer.start();
    }

    /**
     * Abre un selector de archivos para agregar imágenes o videos al proyecto.
     */
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

    /**
     * Elimina el archivo seleccionado actualmente en la lista.
     */
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

    /**
     * Refresca la lista visual de archivos basándose en los bloques del VideoConstructor.
     */
    private void refreshList() {
        listModel.clear();
        ArrayList<InfoBlock> blocks = VideoConstructor.getVideoBlocks();
        for (InfoBlock block : blocks) {
            listModel.addElement(block);
        }
        btnRun.setEnabled(!blocks.isEmpty());
    }

    /**
     * Calcula y actualiza el porcentaje de progreso en la barra de progreso.
     */
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

    /**
     * Inicia el proceso de producción en un hilo separado (SwingWorker).
     */
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
                    finalVideoFile = get(); 
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

    /**
     * Limpia todos los archivos agregados y reinicia el estado del proyecto.
     */
    private void clearAll() {
        VideoConstructor.reset();
        refreshList();
        progressBar.setValue(0);
        lblStatus.setText("Listo (Limpio)");
        finalVideoFile = null;
        btnView.setEnabled(false);
    }

    /**
     * Abre el video resultante utilizando el reproductor de video predeterminado del sistema.
     */
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

    /**
     * Renderizador personalizado para la lista de InfoBlocks.
     * Cambia el color de fondo según el estado de procesamiento del bloque.
     */
    private class InfoBlockRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            InfoBlock block = (InfoBlock) value;
            String text = block.getOriginalFile().getName() + " [" + block.getCoords() + "]";
            
            JLabel label = (JLabel) super.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
            
            if (block.isAudioIntegrated()) {
                label.setBackground(new Color(144, 238, 144)); 
            } else if (block.isNormalized()) {
                label.setBackground(new Color(255, 255, 153)); 
            } else {
                label.setBackground(Color.WHITE); 
            }

            if (isSelected) {
                label.setBorder(BorderFactory.createLineBorder(Color.BLUE, 1));
            }

            label.setOpaque(true);
            return label;
        }
    }

    /**
     * Punto de entrada principal para lanzar la interfaz gráfica.
     * @param args Argumentos de línea de comandos.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new VideoGUI().setVisible(true);
        });
    }
}
