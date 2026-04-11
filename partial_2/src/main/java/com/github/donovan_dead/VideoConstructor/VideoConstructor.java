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
import com.github.donovan_dead.VideoConstructor.responseFormats.FinalImageResponse;
import com.github.donovan_dead.VideoConstructor.responseFormats.VideoResponse;
import com.github.donovan_dead.VideoConstructor.tools.ExiftoolWrapper;
import com.github.donovan_dead.VideoConstructor.tools.FFMPEGWrapper;
import com.github.donovan_dead.VideoConstructor.tools.OpenAiManager;
import com.github.donovan_dead.VideoConstructor.tools.OSMManager;

public class VideoConstructor {
    private static ArrayList<InfoBlock> video_blocks = new ArrayList<>();

    private static InfoBlock InitialVideo =  null;
    private static InfoBlock FinalVideo = null;
    private static File WorkingTempDir = null;

    public static File Run(){
        WorkingTempDir = new File("temp_production_" + System.currentTimeMillis());
        if (!WorkingTempDir.exists()) WorkingTempDir.mkdirs();

        try {
            obtainFilesFromConsole();
            
            obtainMetadataFromInfoBlocks();

            sortByCreationDate();

            regularizeFiles();

            callOpenAiApis();

            generateInitialImage();
            
            generateFinalMap();

            return generateFinalVideo();

        } catch (Exception e) {
            System.out.println("Error durante la producción: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            cleanupTempDir();
        }
    }

    public static void cleanupTempDir() {
        if (WorkingTempDir != null && WorkingTempDir.exists()) {
            File[] files = WorkingTempDir.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isDirectory()) {
                        deleteDirectory(f);
                    } else {
                        f.delete();
                    }
                }
            }
            WorkingTempDir.delete();
        }
    }

    public static void deleteDirectory(File directory) {
        File[] allContents = directory.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        directory.delete();
    }

    public static void generateFinalMap(){
        if (video_blocks.isEmpty()) {
            return;
        }

        InfoBlock first = video_blocks.get(0);
        InfoBlock last = video_blocks.get(video_blocks.size()-1);

        if (first == null || last == null) {
            return;
        }

        try {
            File mapImageDest = new File(WorkingTempDir, "final_map.png");
            File mapImage = OSMManager.getInstance().generateMapImage(first.getCoords(), last.getCoords(), 1024, 1536, mapImageDest.getAbsolutePath());
            if (mapImage != null) {
                // Generar frase con OpenAI usando FinalImageResponse
                byte[] fileContent = Files.readAllBytes(mapImage.toPath());
                String base64Image = java.util.Base64.getEncoder().encodeToString(fileContent);

                StringBuilder allDescs = new StringBuilder("Descripciones del video: ");
                for (InfoBlock block : video_blocks) {
                    if (block.getGeneralDesc() != null)
                        allDescs.append(block.getGeneralDesc()).append(". ");
                }

                String prompt = "Basado en el mapa de ruta y estas descripciones: " + allDescs.toString() + ". Genera una frase motivacional final que sea corta e impactante.";
                FinalImageResponse response = OpenAiManager.GenerateStructuredOutput(prompt, List.of(base64Image), FinalImageResponse.class);

                InputStream audioStream = OpenAiManager.GenerateAudioMp3(response.getFinalPhrase());

                File videoFile = FFMPEGWrapper.ImgToVid(mapImage, FileType.VID_MP4, 1024, 1536, 8);
                if (videoFile != null) {
                    File movedVideoFile = new File(WorkingTempDir, videoFile.getName());
                    Files.move(videoFile.toPath(), movedVideoFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    
                    if (response != null && response.getFinalPhrase() != null) {
                        FFMPEGWrapper.AddTextToVideo(movedVideoFile, response.getFinalPhrase(), "top");
                    }

                    if (audioStream != null) {
                        File audioFile = new File(WorkingTempDir, "temp_audio" + ".mp3");
                        try (java.io.FileOutputStream fos = new java.io.FileOutputStream(audioFile)){
                            byte[] buffer = audioStream.readAllBytes();
                            fos.write(buffer);
                        }
                        FFMPEGWrapper.mergeVideoAndAudio(movedVideoFile, audioFile);
                    }

                    InfoBlock mapBlock = new InfoBlock(movedVideoFile);
                    mapBlock.setGeneralDesc("Mapa del recorrido");
                    mapBlock.setDuration(8.0);
                    VideoConstructor.FinalVideo = mapBlock;
                }
            }
        } catch (Exception e) {
            System.out.println("Error al generar el mapa final: " + e.getMessage());
        }
    }

    public static void generateInitialImage(){
        StringBuilder fullDescription = new StringBuilder("Estas descripciones forman parte de un video general: ");
        for (InfoBlock block : video_blocks) {
            if(block.getGeneralDesc() != null)
                fullDescription.append(block.getGeneralDesc()).append(". ");
        }
        fullDescription.append("Crea una imagen representativa que capture la esencia de todo este contenido.");

        try {
            File imageFileDest = new File(WorkingTempDir, "initial_image.jpg");
            File imageFile = OpenAiManager.GenerateImage(fullDescription.toString(), 1, imageFileDest.getAbsolutePath());
            if (imageFile != null) {
                File videoFile = FFMPEGWrapper.ImgToVid(imageFile, FileType.VID_MP4, 1024, 1536, 4);
                if (videoFile != null) {
                    File movedVideoFile = new File(WorkingTempDir, videoFile.getName());
                    Files.move(videoFile.toPath(), movedVideoFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    
                    InfoBlock initialBlock = new InfoBlock(movedVideoFile);
                    initialBlock.setGeneralDesc("Imagen inicial representativa");
                    initialBlock.setDuration(5.0);
                    VideoConstructor.InitialVideo = initialBlock;
                }
            }
        } catch (Exception e) {
            System.out.println("Error al generar la imagen inicial: " + e.getMessage());
        }
    }

    public static File generateFinalVideo(){
        if (video_blocks.isEmpty()) return null;
        try {
            File finalVideo = new File("final_video.mp4");
            Files.copy(InitialVideo.getFile().toPath(), finalVideo.toPath(), StandardCopyOption.REPLACE_EXISTING);
            
            for (int i = 0; i < video_blocks.size(); i++) {
                FFMPEGWrapper.ConcatenateVideos(finalVideo, video_blocks.get(i).getFile());
            }
            FFMPEGWrapper.ConcatenateVideos(finalVideo, FinalVideo.getFile());

            File regularized = FFMPEGWrapper.RegularizeVideo(finalVideo);
            if (regularized != null) {
                Files.copy(regularized.toPath(), finalVideo.toPath(), StandardCopyOption.REPLACE_EXISTING);
                regularized.delete();
            }
            System.out.println("Video final generado: final_video.mp4");
            return finalVideo;
        } catch (Exception e) {
            System.out.println("Error al generar el video final: " + e.getMessage());
            return null;
        }
    }

    public static void callOpenAiApis(){
        OpenAiManager.InitClient();

        for (InfoBlock block : video_blocks) {
            if (block.isAudioIntegrated()) continue;

            try {
                File tempDir = FFMPEGWrapper.SampleVideo(block.getFile(), 7, 3);
                
                if (tempDir != null && tempDir.exists() && tempDir.isDirectory()) {
                    List<String> base64Images = new ArrayList<>();
                    File[] samples = tempDir.listFiles((dir, name) -> name.endsWith(".png"));
                    
                    if (samples != null) {
                        for (File sample : samples) {
                            byte[] fileContent = Files.readAllBytes(sample.toPath());
                            String base64 = java.util.Base64.getEncoder().encodeToString(fileContent);
                            base64Images.add(base64);
                            sample.delete(); 
                        }
                    }
                    tempDir.delete(); 

                    String prompt = "Analiza este video (a través de estos frames) y genera una descripción general y un guion de audio EN ESPAÑOL para un video de " + block.getDuration() + " segundos. Utiliza " + Math.ceil(2.3 * block.getDuration()) + " palabras" ;
                    VideoResponse response = 
                        OpenAiManager.GenerateStructuredOutput(prompt, base64Images, VideoResponse.class);

                    if (response != null) {
                        block.setGeneralDesc(response.getDescripcionGeneral());
                        
                        InputStream audioStream = OpenAiManager.GenerateAudioMp3(response.getAudioScript());
                        if (audioStream != null) {
                            File audioFile = new File(WorkingTempDir, "temp_audio_" + block.getFile().getName() + ".mp3");
                            try (java.io.FileOutputStream fos = new java.io.FileOutputStream(audioFile)) {
                                byte[] buffer = audioStream.readAllBytes();
                                fos.write(buffer);
                            }
                            
                            FFMPEGWrapper.mergeVideoAndAudio(block.getFile(), audioFile);
                            block.setAudioIntegrated(true);
                        }
                    }
                }

            } catch (Exception e) {
                System.out.println("Error en paso 5 para el bloque " + block.getFile().getName() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public static void regularizeFiles(){
        if (video_blocks.isEmpty()) return;

        for (InfoBlock block : video_blocks) {
            if (block.isNormalized() && block.getFile().getAbsolutePath().contains(WorkingTempDir.getAbsolutePath())) continue;

            File currentFile = block.getFile();
            File regularized = FFMPEGWrapper.AdecuateToFormat(currentFile, FileType.VID_MP4, 1024, 1536);
            
            if (regularized != null) {
                try {
                    File moved = new File(WorkingTempDir, regularized.getName());
                    Files.move(regularized.toPath(), moved.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    block.setFile(moved);
                    block.setNormalized(true);
                } catch (Exception e) {
                    System.out.println("Error al actualizar InfoBlock: " + e.getMessage());
                }
            } else {
                System.out.println("Error: No se pudo regularizar el archivo " + currentFile.getName());
            }
        }
    }

    public static void obtainFilesFromConsole(){
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

    public static void obtainMetadataFromInfoBlocks(){
        for (InfoBlock block : video_blocks) {
            if (block.isNormalized()) continue;

            File currentFile = block.getFile();
            FileType type = FileTypeDetector.obtainFileTypeEnum(currentFile);

            block.setCreationDate(ExiftoolWrapper.getCreationDateFromFile(currentFile));
            block.setCoords(ExiftoolWrapper.getGPSCoordinatesFromFile(currentFile));

            if (type.toString().contains("IMG")) {
                File converted = FFMPEGWrapper.ImgToVid(currentFile, FileType.VID_MP4, 1024, 1536, 8);
                if (converted != null) {
                    try {
                        File moved = new File(WorkingTempDir, converted.getName());
                        Files.move(converted.toPath(), moved.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        block.setFile(moved);
                        currentFile = moved;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            block.setDuration(ExiftoolWrapper.getDurationFromFile(currentFile));
            block.setNormalized(true);
        }
    }

    public static void sortByCreationDate(){
        Collections.sort(video_blocks);
    }

    public static void appendInfoBlock(File block) throws Exception{
        InfoBlock newBlock = new InfoBlock(block);
        video_blocks.add(newBlock);
    }

    public static void removeInfoBlock(File block){
        for (InfoBlock infoBlock : video_blocks) {
            if(infoBlock.getOriginalFile().equals(block)){
                video_blocks.remove(infoBlock);
                break;
            }        
        }
    }

    public static void reset() {
        cleanupTempDir();
        video_blocks.clear();
        InitialVideo = null;
        FinalVideo = null;
    }

    public static void initTempDir(){
        WorkingTempDir = new File("temp_production");
        if (!WorkingTempDir.exists()) WorkingTempDir.mkdirs();
    }

    public static ArrayList<InfoBlock> getVideoBlocks(){
        return video_blocks;
    }

    public static InfoBlock getInitialVideo(){
        return InitialVideo;
    }

    public static InfoBlock getFinalVideo(){
        return FinalVideo;
    }
}
