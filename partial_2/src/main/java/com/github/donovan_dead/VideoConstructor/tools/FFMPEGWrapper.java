package com.github.donovan_dead.VideoConstructor.tools;

import java.io.File;

import com.github.donovan_dead.FileInfo.FileType;
import com.github.donovan_dead.FileInfo.FileTypeDetector;

public class FFMPEGWrapper {
    
    public static File AdecuateToFormat(File file, FileType target_type, int width, int height){
        FileType file_type = FileTypeDetector.obtainFileTypeEnum(file);
        if(file_type == FileType.OTHER) return null;
        
        if(file_type.toString().contains("VID") && target_type.toString().contains("VID"))
            return ChangeVidFormat(file, target_type, width, height);
        else if (file_type.toString().contains("IMG") && target_type.toString().contains("VID"))
            return ImgToVid(file, target_type, width, height, 5);
        else    
            return null;
    }

    public static File ImgToVid(File file, FileType target_type, int width, int height, int duration_seconds){
        System.out.println("[DEBUG] FFMPEG: Convirtiendo imagen a video: " + file.getName());
        FileType file_type = FileTypeDetector.obtainFileTypeEnum(file);
        if(file_type == FileType.OTHER) return null;

        String result_file_name = file.getName().substring(0,file.getName().lastIndexOf(".")) + "_img.mp4";

        // Añadimos un track de audio silencioso para que la concatenación no falle
        ProcessBuilder pb = new ProcessBuilder(
            "ffmpeg",
            "-y", 
            "-loop", "1",
            "-i", file.getAbsolutePath(),
            "-vf", "scale=" + width + ":" + height + ":force_original_aspect_ratio=increase,crop=" + width + ":" + height + ",setsar=1/1",
            "-c:v", "libx264",
            "-t", String.valueOf(duration_seconds),
            "-pix_fmt", "yuv420p", // Formato de píxel estándar
            "-r", "30",            // Framerate constante
            "-an",                 // <--- IMPORTANTE: Desactiva cualquier rastro de audio
            result_file_name
        );

        pb.inheritIO();
        try {
            Process process = pb.start();
            process.waitFor();

            File result_file = new File(result_file_name);
            
            if(result_file.exists() && result_file.isFile()) {
                System.out.println("[DEBUG] FFMPEG: Video de imagen creado: " + result_file_name);
                return result_file;
            } else
                return null;

        } catch (Exception e) {
            System.out.println("[DEBUG] FFMPEG Error en ImgToVid: " + e.getMessage());
        }

        return null;
    }

    public static File ImgToVid(File file, FileType target_type, int width, int height){
        FileType file_type = FileTypeDetector.obtainFileTypeEnum(file);
        if(file_type == FileType.OTHER) return null;

        return ImgToVid(file, target_type, width, height, 5);
    }

    public static File ChangeVidFormat(File file, FileType target_type){
        FileType file_type = FileTypeDetector.obtainFileTypeEnum(file);
        if(file_type == FileType.OTHER) return null;
        
        String result_file_name = file.getName().substring(0,file.getName().lastIndexOf(".")) + "." + FileTypeDetector.obtainFileTypeString(target_type);

        ProcessBuilder pb = new ProcessBuilder(
            "ffmpeg",
            "-i", file.getAbsolutePath(),
            result_file_name
        );

        pb.inheritIO();
        try {
            pb.start().waitFor();

            File result_file = new File(result_file_name);
            
            if(result_file.exists() && result_file.isFile())
                return result_file;
            else
                return null;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static File ChangeVidFormat(File file, FileType target_type, int width, int height){
        System.out.println("[DEBUG] FFMPEG: Cambiando formato de video: " + file.getName());
        FileType file_type = FileTypeDetector.obtainFileTypeEnum(file);
        
        if(file_type == FileType.OTHER) return null;

        String result_file_name = (file.getParent() == null ? "" : file.getParent() + File.separator) + file.getName().substring(0, file.getName().lastIndexOf(".")) + "_formatted.mp4";
       
        // Usamos una aproximación más robusta: 
        // Si el video original no tiene audio, simplemente le añadimos el silencio.
        // Si tiene audio, lo mezclamos o lo ignoramos (en este caso lo mezclamos con el silencio para asegurar que siempre haya una pista de audio compatible)
        ProcessBuilder pb = new ProcessBuilder(
            "ffmpeg",
            "-y",
            "-i", file.getAbsolutePath(),
            "-f", "lavfi",
            "-i", "anullsrc=channel_layout=stereo:sample_rate=44100",
            "-filter_complex", "[0:v]scale=" + width + ":" + height + ":force_original_aspect_ratio=increase,crop=" + width + ":" + height + ",fps=30,setsar=1/1[v]",
            "-map", "[v]",
            "-map", "1:a", // Usamos el audio de anullsrc para garantizar que siempre haya audio
            "-c:v", "libx264",
            "-c:a", "aac",
            "-shortest",
            "-pix_fmt", "yuv420p",
            result_file_name
        );

        pb.inheritIO();
        try {
            Process process = pb.start();
            process.waitFor();

            File result_file = new File(result_file_name);
            if(result_file.exists() && result_file.isFile()) {
                System.out.println("[DEBUG] FFMPEG: Video formateado: " + result_file_name);
                return result_file;
            } else
                return null;

        } catch (Exception e) {
            System.out.println("[DEBUG] FFMPEG Error en ChangeVidFormat: " + e.getMessage());
        }
        return null;
    }

    public static File RegularizeVideo (File file){
        FileType file_type = FileTypeDetector.obtainFileTypeEnum(file);
        if(file_type == FileType.OTHER) return null;
        if(file_type.toString().contains("IMG")) return null;

        String result_file_name = (file.getParent() == null ? "" : file.getParent() + File.separator) + file.getName().substring(0, file.getName().lastIndexOf(".")) + "_regularized.mp4";
        
        ProcessBuilder pb = new ProcessBuilder(
            "ffmpeg",
            "-y",
            "-i", file.getAbsolutePath(),
            "-af", "loudnorm=I=-16:TP=-1.5:LRA=10",
            result_file_name
        );

        pb.inheritIO();
        try {
            Process process = pb.start();
            process.waitFor();

            File result_file = new File(result_file_name);
            if(result_file.exists() && result_file.isFile())
                return result_file;
            else
                return null;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void ConcatenateVideos(File first, File second){
        System.out.println("[DEBUG] FFMPEG: Concatenando " + first.getName() + " y " + second.getName());
        FileType first_type = FileTypeDetector.obtainFileTypeEnum(first);
        FileType second_type = FileTypeDetector.obtainFileTypeEnum(second);

        if(first_type != FileType.VID_MP4 || second_type != FileType.VID_MP4) {
            System.out.println("[DEBUG] FFMPEG: Fallo en concatenación, tipos no son MP4: " + first_type + ", " + second_type);
            return;
        }

        String temp_file_path = first.getAbsolutePath().substring(0, first.getAbsolutePath().lastIndexOf(".")) + "_temp_concat.mp4";
        ProcessBuilder pb = new ProcessBuilder(
            "ffmpeg",
            "-y",
            "-i", first.getAbsolutePath(),
            "-i", second.getAbsolutePath(),
            "-filter_complex", "[0:v][0:a][1:v][1:a]concat=n=2:v=1:a=1[v][a]",
            "-map", "[v]",
            "-map", "[a]",
            "-c:v", "libx264",
            "-c:a", "aac",
            "-pix_fmt", "yuv420p",
            temp_file_path
        );

        pb.inheritIO();
        try {
            Process process = pb.start();
            process.waitFor();

            File tempFile = new File(temp_file_path);
            if (tempFile.exists() && tempFile.isFile()) {
                java.nio.file.Path source = tempFile.toPath();
                java.nio.file.Path target = first.toPath();
                java.nio.file.Files.move(source, target, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                System.out.println("[DEBUG] FFMPEG: Concatenación exitosa.");
            }

        } catch (Exception e) {
            System.out.println("[DEBUG] FFMPEG Error en concatenación: " + e.getMessage());
        }
    }

    public static void concatenateAudios(File first, File second){
        FileType first_type = FileTypeDetector.obtainFileTypeEnum(first);
        FileType second_type = FileTypeDetector.obtainFileTypeEnum(second);

        if(!first_type.toString().contains("AUD") || !second_type.toString().contains("AUD")) return;

        String temp_file_path = first.getAbsolutePath().substring(0, first.getAbsolutePath().lastIndexOf(".")) + "_temp." + FileTypeDetector.obtainFileTypeString(first_type);

        ProcessBuilder pb = new ProcessBuilder(
            "ffmpeg",
            "-y",
            "-i", first.getAbsolutePath(),
            "-i", second.getAbsolutePath(),
            "-filter_complex", "concat=n=2:v=0:a=1",
            temp_file_path
        );

        pb.inheritIO();
        try {
            Process process = pb.start();
            process.waitFor();

            File tempFile = new File(temp_file_path);
            if (tempFile.exists() && tempFile.isFile()) {
                java.nio.file.Path source = tempFile.toPath();
                java.nio.file.Path target = first.toPath();
                java.nio.file.Files.move(source, target, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }

        } catch (Exception e) {
            System.out.println("[DEBUG] FFMPEG Error en concatenateAudios: " + e.getMessage());
        }
    }

    public static void mergeVideoAndAudio(File video, File audio){
        System.out.println("[DEBUG] FFMPEG: Mezclando video " + video.getName() + " con audio " + audio.getName());
        FileType video_type = FileTypeDetector.obtainFileTypeEnum(video);
        FileType audio_type = FileTypeDetector.obtainFileTypeEnum(audio);

        if(!video_type.toString().contains("VID") || !audio_type.toString().contains("AUD")) return;

        double videoDuration = ExiftoolWrapper.getDurationFromFile(video);
        double audioDuration = ExiftoolWrapper.getDurationFromFile(audio);
        System.out.println("[DEBUG] Duraciones - Video: " + videoDuration + ", Audio: " + audioDuration);

        if(audioDuration > videoDuration){
            System.out.println("[DEBUG] Ajustando duración del audio...");
            AdjustAudioDuration(audio, videoDuration);
        }

        System.out.println("____________________________________________________________");

        String temp_file_path = video.getAbsolutePath().substring(0, video.getAbsolutePath().lastIndexOf(".")) + "_merged.mp4";
        // Re-codificamos el audio a AAC para asegurar compatibilidad total en la mezcla
        ProcessBuilder pb = new ProcessBuilder(
            "ffmpeg",
            "-y",
            "-i", video.getAbsolutePath(),
            "-i", audio.getAbsolutePath(),
            "-map", "0:v:0",           // Tomamos el video del original
            "-map", "1:a:0",           // Tomamos el audio del MP3
            "-c:v", "copy",            // No tocamos el video
            "-c:a", "aac",             // Convertimos a AAC
            "-ar", "44100",            // <--- FORZAMOS 44.1kHz (Vital para compatibilidad)
            "-ac", "2",                // <--- FORZAMOS STEREO
            "-b:a", "128k",            // Bitrate más conservador para evitar el error de bits
            "-shortest",               // Cortar al terminar el video
            temp_file_path
        );

        pb.inheritIO();
        try {
            Process process = pb.start();
            process.waitFor();

            File tempFile = new File(temp_file_path);
            if (tempFile.exists() && tempFile.isFile()) {
                java.nio.file.Path source = tempFile.toPath();
                java.nio.file.Path target = video.toPath();
                java.nio.file.Files.move(source, target, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                System.out.println("[DEBUG] FFMPEG: Mezcla exitosa.");
            }

        } catch (Exception e) {
            System.out.println("[DEBUG] FFMPEG Error en mergeVideoAndAudio: " + e.getMessage());
        }
    }

    public static void AdjustAudioDuration(File audio, double targetDuration){
        System.out.println("[DEBUG] FFMPEG: Ajustando duración de audio a " + targetDuration + "s");
        double currentDuration = ExiftoolWrapper.getDurationFromFile(audio);
        if(currentDuration <= 0 || targetDuration <= 0) return;

        double factor = currentDuration / targetDuration;
        
        if (factor < 0.5) factor = 0.5;
        if (factor > 2.0) factor = 2.0;

        String temp_audio_path = audio.getAbsolutePath().substring(0, audio.getAbsolutePath().lastIndexOf(".")) + "_adjusted." + FileTypeDetector.obtainFileTypeString(FileTypeDetector.obtainFileTypeEnum(audio));

        ProcessBuilder pb = new ProcessBuilder(
            "ffmpeg",
            "-y",
            "-i", audio.getAbsolutePath(),
            "-filter:a", String.format(java.util.Locale.US, "atempo=%.2f", factor),
            "-vn",
            temp_audio_path
        );

        pb.inheritIO();
        try {
            Process process = pb.start();
            process.waitFor();

            File tempFile = new File(temp_audio_path);
            if (tempFile.exists() && tempFile.isFile()) {
                String originalPath = audio.getAbsolutePath();
                if (audio.delete()) {
                    tempFile.renameTo(new File(originalPath));
                    System.out.println("[DEBUG] FFMPEG: Ajuste de audio exitoso.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static File SampleVideo(File video, int num_samples, int interval_seconds){
        FileType file_type = FileTypeDetector.obtainFileTypeEnum(video);
        if(file_type == FileType.OTHER || !file_type.toString().contains("VID")) return null;

        String temp_dir_path = System.getProperty("java.io.tmpdir") + File.separator + "video_samples_" + System.currentTimeMillis();
        File tempDir = new File(temp_dir_path);
        if(!tempDir.exists()){
            tempDir.mkdirs();
        }

        String output_pattern = tempDir.getAbsolutePath() + File.separator + "sample_%03d.png";

        ProcessBuilder pb = new ProcessBuilder(
            "ffmpeg",
            "-y",
            "-i", video.getAbsolutePath(),
            "-vf", "fps=1/" + interval_seconds,
            "-vframes", String.valueOf(num_samples),
            output_pattern
        );

        pb.inheritIO();
        try {
            Process process = pb.start();
            process.waitFor();

            if(tempDir.exists() && tempDir.isDirectory() && tempDir.list() != null && tempDir.list().length > 0)
                return tempDir;
            else
                return null;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
