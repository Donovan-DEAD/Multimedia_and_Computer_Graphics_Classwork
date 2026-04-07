package com.github.donovan_dead.VideoConstructor;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.List;

import com.github.donovan_dead.FileInfo.FileType;
import com.github.donovan_dead.FileInfo.FileTypeDetector;
import com.github.donovan_dead.VideoConstructor.Components.InfoBlock;
import com.github.donovan_dead.VideoConstructor.responseFormats.VideoResponse;
import com.github.donovan_dead.VideoConstructor.tools.ExiftoolWrapper;
import com.github.donovan_dead.VideoConstructor.tools.FFMPEGWrapper;
import com.github.donovan_dead.VideoConstructor.tools.OpenAiManager;
import com.github.donovan_dead.VideoConstructor.tools.OSMManager;

public class VideoConstructor {
    private static ArrayList<InfoBlock> video_blocks = new ArrayList<>();

    public static void Run(){
        System.out.println("[DEBUG] Iniciando VideoConstructor.Run()...");
        obtainFilesFromConsole();
        
        System.out.println("[DEBUG] Obteniendo metadatos de los InfoBlocks...");
        obtainMetadataFromInfoBlocks();

        System.out.println("[DEBUG] Ordenando por fecha de creación...");
        sortByCreationDate();

        System.out.println("[DEBUG] Regularizando archivos...");
        regularizeFiles();

        System.out.println("[DEBUG] Llamando a las APIs de OpenAI...");
        callOpenAiApis();

        System.out.println("[DEBUG] Generando imagen inicial...");
        generateInitialImage();
        System.out.println("\n--- InfoBlocks después de generar Imagen Inicial (Paso 6) ---");
        for (InfoBlock block : video_blocks) {
            System.out.println(block);
        }

        System.out.println("[DEBUG] Generando mapa final...");
        generateFinalMap();
        System.out.println("\n--- InfoBlocks después de generar Mapa Final (Paso 7) ---");
        for (InfoBlock block : video_blocks) {
            System.out.println(block);
        }

        System.out.println("[DEBUG] Generando video final...");
        generateFinalVideo();
        System.out.println("[DEBUG] VideoConstructor.Run() finalizado.");

        for(InfoBlock block : video_blocks){
            System.out.println(block);
            block.getFile().delete();
        }
    }

    private static void generateFinalMap(){
        System.out.println("[DEBUG] Iniciando generateFinalMap()...");
        if (video_blocks.isEmpty()) {
            System.out.println("[DEBUG] video_blocks está vacío, saliendo de generateFinalMap().");
            return;
        }

        InfoBlock first = video_blocks.get(1);
        InfoBlock last = video_blocks.get(video_blocks.size()-1);

        if (first == null || last == null) {
            System.out.println("[DEBUG] No se encontraron suficientes coordenadas GPS en los bloques.");
            return;
        }

        System.out.println("[DEBUG] Generando mapa con coordenadas:");
        System.out.println("  - Inicio: " + first.getCoords());
        System.out.println("  - Fin: " + last.getCoords());

        try {
            // Pasamos tanto el punto inicial como el final para que aparezcan marcadores
            File mapImage = OSMManager.getInstance().generateMapImage(first.getCoords(), last.getCoords(), 1024, 1536, "final_map.png");
            if (mapImage != null) {
                System.out.println("[DEBUG] Mapa generado: " + mapImage.getAbsolutePath());
                File videoFile = FFMPEGWrapper.ImgToVid(mapImage, FileType.VID_MP4, 1024, 1536, 5);
                if (videoFile != null) {
                    System.out.println("[DEBUG] Video del mapa generado: " + videoFile.getAbsolutePath());
                    InfoBlock mapBlock = new InfoBlock(videoFile);
                    mapBlock.setGeneralDesc("Mapa del recorrido");
                    mapBlock.setDuration(5.0);
                    video_blocks.add(mapBlock);
                }
            }
        } catch (Exception e) {
            System.out.println("Error al generar el mapa final: " + e.getMessage());
        }
    }

    private static void generateInitialImage(){
        System.out.println("[DEBUG] Iniciando generateInitialImage()...");
        StringBuilder fullDescription = new StringBuilder("Estas descripciones forman parte de un video general: ");
        for (InfoBlock block : video_blocks) {
            if(block.getGeneralDesc() != null)
                fullDescription.append(block.getGeneralDesc()).append(". ");
        }
        fullDescription.append("Crea una imagen representativa que capture la esencia de todo este contenido.");

        try {
            File imageFile = OpenAiManager.GenerateImage(fullDescription.toString(), 1, "initial_image.jpg");
            if (imageFile != null) {
                System.out.println("[DEBUG] Imagen inicial generada: " + imageFile.getAbsolutePath());
                File videoFile = FFMPEGWrapper.ImgToVid(imageFile, FileType.VID_MP4, 1024, 1536, 5);
                if (videoFile != null) {
                    System.out.println("[DEBUG] Video de imagen inicial generado: " + videoFile.getAbsolutePath());
                    InfoBlock initialBlock = new InfoBlock(videoFile);
                    initialBlock.setGeneralDesc("Imagen inicial representativa");
                    initialBlock.setDuration(5.0);
                    video_blocks.add(0, initialBlock);
                }
            }
        } catch (Exception e) {
            System.out.println("Error al generar la imagen inicial: " + e.getMessage());
        }
    }

    private static void generateFinalVideo(){
        System.out.println("[DEBUG] Iniciando generateFinalVideo()...");
        if (video_blocks.isEmpty()) return;
        try {
            File finalVideo = new File("final_video.mp4");
            java.nio.file.Files.copy(video_blocks.get(0).getFile().toPath(), finalVideo.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            System.out.println("[DEBUG] Copiado primer bloque a final_video.mp4: " + video_blocks.get(0).getFile().getName());
            
            for (int i = 1; i < video_blocks.size(); i++) {
                System.out.println("[DEBUG] Concatenando bloque " + i + ": " + video_blocks.get(i).getFile().getName());
                FFMPEGWrapper.ConcatenateVideos(finalVideo, video_blocks.get(i).getFile());
            }
            File regularized = FFMPEGWrapper.RegularizeVideo(finalVideo);
            Files.copy( regularized.toPath(), finalVideo.toPath(), StandardCopyOption.REPLACE_EXISTING);
            regularized.delete();
            System.out.println("Video final generado: final_video.mp4");
        } catch (Exception e) {
            System.out.println("Error al generar el video final: " + e.getMessage());
        }

        
    }

    private static void callOpenAiApis(){
        System.out.println("[DEBUG] Iniciando callOpenAiApis()...");
        OpenAiManager.InitClient();

        for (InfoBlock block : video_blocks) {
            try {
                System.out.println("[DEBUG] Procesando bloque para OpenAI: " + block.getFile().getName());
                // Sample video: max 7 images, 1 every 3 seconds
                File tempDir = FFMPEGWrapper.SampleVideo(block.getFile(), 7, 3);
                
                if (tempDir != null && tempDir.exists() && tempDir.isDirectory()) {
                    List<String> base64Images = new ArrayList<>();
                    File[] samples = tempDir.listFiles((dir, name) -> name.endsWith(".png"));
                    
                    if (samples != null) {
                        for (File sample : samples) {
                            byte[] fileContent = java.nio.file.Files.readAllBytes(sample.toPath());
                            String base64 = java.util.Base64.getEncoder().encodeToString(fileContent);
                            base64Images.add(base64);
                            sample.delete(); // Delete sample after reading
                        }
                    }
                    tempDir.delete(); // Delete temp directory

                    String prompt = "Analiza este video (a través de estos frames) y genera una descripción general y un guion de audio para un video de " + block.getDuration() + " segundos. Utiliza " + Math.ceil(2.3 * block.getDuration()) + " palabras" ;
                    System.out.println("[DEBUG] Llamando a OpenAI Vision para " + block.getFile().getName());
                    VideoResponse response = 
                        OpenAiManager.GenerateStructuredOutput(prompt, base64Images, VideoResponse.class);

                    if (response != null) {
                        block.setGeneralDesc(response.getDescripcionGeneral());
                        System.out.println("[DEBUG] Descripción generada: " + block.getGeneralDesc());
                        
                        System.out.println("[DEBUG] Generando audio TTS para el guion...");
                        InputStream audioStream = OpenAiManager.GenerateAudioMp3(response.getAudioScript());
                        if (audioStream != null) {
                            File audioFile = new File("temp_audio_" + block.getFile().getName() + ".mp3");
                            try (java.io.FileOutputStream fos = new java.io.FileOutputStream(audioFile)) {
                                byte[] buffer = new byte[4096];
                                int bytesRead;
                                while ((bytesRead = audioStream.read(buffer)) != -1) {
                                    fos.write(buffer, 0, bytesRead);
                                }
                            }
                            System.out.println("[DEBUG] Audio guardado en: " + audioFile.getAbsolutePath());
                            
                            System.out.println("[DEBUG] Mezclando video y audio para " + block.getFile().getName());
                            FFMPEGWrapper.mergeVideoAndAudio(block.getFile(), audioFile);
                            // audioFile.delete();
                        }
                    }
                }

            } catch (Exception e) {
                System.out.println("Error en paso 5 para el bloque " + block.getFile().getName() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private static void regularizeFiles(){
        System.out.println("[DEBUG] Iniciando regularizeFiles()...");
        if (video_blocks.isEmpty()) return;

        for (InfoBlock block : video_blocks) {
            File currentFile = block.getFile();
            System.out.println("[DEBUG] Regularizando: " + currentFile.getName());
            // Regularizar a MP4 1024x1536
            File regularized = FFMPEGWrapper.AdecuateToFormat(currentFile, FileType.VID_MP4, 1024, 1536);
            
            if (regularized != null) {
                try {
                    block.setFile(regularized);
                    System.out.println("[DEBUG] Archivo regularizado exitosamente: " + regularized.getName());
                } catch (Exception e) {
                    System.out.println("Error al actualizar InfoBlock: " + e.getMessage());
                }
            } else {
                System.out.println("Error: No se pudo regularizar el archivo " + currentFile.getName());
            }
        }
    }

    private static void obtainFilesFromConsole(){
        System.out.println("[DEBUG] Iniciando obtainFilesFromConsole()...");
        Scanner scanner = new Scanner(System.in);
        System.out.println("¿Cuántos archivos desea agregar?");
        int count = 0;
        try {
            count = Integer.parseInt(scanner.nextLine());
        } catch (Exception e) {
            System.out.println("Entrada inválida.");
            return;
        }

        for (int i = 0; i < count; i++) {
            boolean success = false;
            while (!success) {
                System.out.println("Ingrese el path del archivo " + (i + 1) + ":");
                String path = scanner.nextLine();
                try {
                    InfoBlock block = new InfoBlock(new File(path));
                    video_blocks.add(block);
                    System.out.println("[DEBUG] Bloque añadido: " + path);
                    success = true;
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                    System.out.println("¿Desea volver a intentarlo? (s/n)");
                    String option = scanner.nextLine();
                    if (!option.equalsIgnoreCase("s")) {
                        success = true; 
                    }
                }
            }
        }
    }

    private static void obtainMetadataFromInfoBlocks(){
        System.out.println("[DEBUG] Iniciando obtainMetadataFromInfoBlocks()...");
        for (InfoBlock block : video_blocks) {
            File currentFile = block.getFile();
            FileType type = FileTypeDetector.obtainFileTypeEnum(currentFile);
            System.out.println("[DEBUG] Procesando metadata para: " + currentFile.getName() + " (Tipo: " + type + ")");

            block.setCreationDate(ExiftoolWrapper.getCreationDateFromFile(currentFile));
            block.setCoords(ExiftoolWrapper.getGPSCoordinatesFromFile(currentFile));

            if (type.toString().contains("IMG")) {
                System.out.println("[DEBUG] Convirtiendo imagen a video para metadata...");
                File converted = FFMPEGWrapper.ImgToVid(currentFile, FileType.VID_MP4, 1024, 1536, 5);
                if (converted != null) {
                    try {
                        block.setFile(converted);
                        currentFile = converted;
                        System.out.println("[DEBUG] Imagen convertida a video: " + currentFile.getName());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            block.setDuration(ExiftoolWrapper.getDurationFromFile(currentFile));
            System.out.println("[DEBUG] Metadata obtenida: Fecha=" + block.getCreationDate() + ", Coords=" + block.getCoords() + ", Duración=" + block.getDuration());
        }
    }

    private static void sortByCreationDate(){
        System.out.println("[DEBUG] Ordenando InfoBlocks por fecha de creación...");
        Collections.sort(video_blocks);
    }
}
