package com.github.donovan_dead.VideoConstructor.tools;

import java.io.File;
import com.github.donovan_dead.FileInfo.FileType;
import com.github.donovan_dead.FileInfo.FileTypeDetector;

/**
 * Clase de utilidad que envuelve comandos de FFMPEG para la manipulación de video y audio.
 */
public class FFMPEGWrapper {
    
    /**
     * Adecua un archivo al formato de video objetivo con dimensiones específicas.
     * 
     * @param file        Archivo original.
     * @param target_type Tipo de archivo destino.
     * @param width       Ancho deseado.
     * @param height      Alto deseado.
     * @return File procesado o null si el tipo no es compatible.
     */
    public static File AdecuateToFormat(File file, FileType target_type, int width, int height) {
        FileType file_type = FileTypeDetector.obtainFileTypeEnum(file);
        if (file_type == FileType.OTHER) return null;
        
        if (file_type.toString().contains("VID") && target_type.toString().contains("VID"))
            return ChangeVidFormat(file, target_type, width, height);
        else if (file_type.toString().contains("IMG") && target_type.toString().contains("VID"))
            return ImgToVid(file, target_type, width, height, 5);
        else    
            return null;
    }

    /**
     * Convierte una imagen en un video de duración determinada con pista de audio silenciosa.
     * 
     * @param file             Imagen de origen.
     * @param target_type      Tipo de video destino.
     * @param width            Ancho.
     * @param height           Alto.
     * @param duration_seconds Duración en segundos.
     * @return File del video generado o null.
     */
    public static File ImgToVid(File file, FileType target_type, int width, int height, int duration_seconds) {
        FileType file_type = FileTypeDetector.obtainFileTypeEnum(file);
        if (file_type == FileType.OTHER) return null;

        String result_file_name = file.getName().substring(0, file.getName().lastIndexOf(".")) + "_img.mp4";

        ProcessBuilder pb = new ProcessBuilder(
            "ffmpeg", "-y", "-loop", "1", 
            "-i", file.getAbsolutePath(),
            "-f", "lavfi", 
            "-i", "anullsrc=channel_layout=stereo:sample_rate=44100",
            "-vf", "scale=" + width + ":" + height + ":force_original_aspect_ratio=increase,crop=" + width + ":" + height + ",setsar=1/1",
            "-c:v", "libx264",
            "-t", String.valueOf(duration_seconds),
            "-c:a", "aac", 
            "-ar", "44100", 
            "-ac", "2",
            "-pix_fmt", "yuv420p", 
            "-r", "30",
            "-shortest",
            result_file_name
        );

        pb.inheritIO();
        try {
            Process process = pb.start();
            process.waitFor();

            File result_file = new File(result_file_name);
            return (result_file.exists() && result_file.isFile()) ? result_file : null;
        } catch (Exception e) {
        }
        return null;
    }

    public static File ImgToVid(File file, FileType target_type, int width, int height) {
        return ImgToVid(file, target_type, width, height, 5);
    }

    /**
     * Cambia el formato de un video.
     * 
     * @param file        Video original.
     * @param target_type Tipo destino.
     * @return File convertido o null.
     */
    public static File ChangeVidFormat(File file, FileType target_type) {
        FileType file_type = FileTypeDetector.obtainFileTypeEnum(file);
        if (file_type == FileType.OTHER) return null;
        
        String result_file_name = file.getName().substring(0, file.getName().lastIndexOf(".")) + "." + FileTypeDetector.obtainFileTypeString(target_type);

        ProcessBuilder pb = new ProcessBuilder(
            "ffmpeg",
            "-i", file.getAbsolutePath(),
            result_file_name
        );

        pb.inheritIO();
        try {
            pb.start().waitFor();
            File result_file = new File(result_file_name);
            return (result_file.exists() && result_file.isFile()) ? result_file : null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Cambia el formato, dimensiones y añade audio silencioso a un video.
     * 
     * @param file        Video original.
     * @param target_type Tipo destino.
     * @param width       Ancho.
     * @param height      Alto.
     * @return File procesado o null.
     */
    public static File ChangeVidFormat(File file, FileType target_type, int width, int height) {
        FileType file_type = FileTypeDetector.obtainFileTypeEnum(file);
        if (file_type == FileType.OTHER) return null;

        String result_file_name = (file.getParent() == null ? "" : file.getParent() + File.separator) + file.getName().substring(0, file.getName().lastIndexOf(".")) + "_formatted.mp4";
       
        ProcessBuilder pb = new ProcessBuilder(
            "ffmpeg",
            "-y",
            "-i", file.getAbsolutePath(),
            "-f", "lavfi",
            "-i", "anullsrc=channel_layout=stereo:sample_rate=44100",
            "-filter_complex", "[0:v]scale=" + width + ":" + height + ":force_original_aspect_ratio=increase,crop=" + width + ":" + height + ",fps=30,setsar=1/1[v]",
            "-map", "[v]",
            "-map", "1:a",
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
            return (result_file.exists() && result_file.isFile()) ? result_file : null;
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * Normaliza el audio de un video utilizando el filtro loudnorm.
     * 
     * @param file Video a regularizar.
     * @return File regularizado o null.
     */
    public static File RegularizeVideo(File file) {
        FileType file_type = FileTypeDetector.obtainFileTypeEnum(file);
        if (file_type == FileType.OTHER || file_type.toString().contains("IMG")) return null;

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
            return (result_file.exists() && result_file.isFile()) ? result_file : null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Concatena dos videos MP4 en un solo archivo (sobrescribe el primero).
     * 
     * @param first  Primer video (archivo base).
     * @param second Segundo video a añadir.
     */
    public static void ConcatenateVideos(File first, File second) {
        FileType first_type = FileTypeDetector.obtainFileTypeEnum(first);
        FileType second_type = FileTypeDetector.obtainFileTypeEnum(second);

        if (first_type != FileType.VID_MP4 || second_type != FileType.VID_MP4) {
            return;
        }

        String temp_file_path = first.getAbsolutePath().substring(0, first.getAbsolutePath().lastIndexOf(".")) + "_temp_concat.mp4";
        String filter = "[0:v]settb=AVTB,setpts=PTS-STARTPTS[v0]; " +
                    "[0:a]asettb=AVTB,asetpts=PTS-STARTPTS[a0]; " +
                    "[1:v]settb=AVTB,setpts=PTS-STARTPTS[v1]; " +
                    "[1:a]asettb=AVTB,asetpts=PTS-STARTPTS[a1]; " +
                    "[v0][a0][v1][a1]concat=n=2:v=1:a=1[v][a]";

        ProcessBuilder pb = new ProcessBuilder(
            "ffmpeg", "-y",
            "-i", first.getAbsolutePath(),
            "-i", second.getAbsolutePath(),
            "-filter_complex", filter,
            "-map", "[v]",
            "-map", "[a]",
            "-c:v", "libx264",
            "-c:a", "aac",
            "-b:a", "128k",
            "-pix_fmt", "yuv420p",
            "-r", "30",
            temp_file_path
        );

        pb.inheritIO();
        try {
            Process process = pb.start();
            process.waitFor();
            File tempFile = new File(temp_file_path);
            if (tempFile.exists() && tempFile.isFile()) {
                java.nio.file.Files.move(tempFile.toPath(), first.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception e) {
        }
    }

    /**
     * Concatena dos archivos de audio (sobrescribe el primero).
     * 
     * @param first  Primer audio.
     * @param second Segundo audio.
     */
    public static void concatenateAudios(File first, File second) {
        FileType first_type = FileTypeDetector.obtainFileTypeEnum(first);
        FileType second_type = FileTypeDetector.obtainFileTypeEnum(second);

        if (!first_type.toString().contains("AUD") || !second_type.toString().contains("AUD")) return;

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
                java.nio.file.Files.move(tempFile.toPath(), first.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception e) {
        }
    }

    /**
     * Combina una pista de video con una pista de audio MP3.
     * 
     * @param video Archivo de video.
     * @param audio Archivo de audio.
     */
    public static void mergeVideoAndAudio(File video, File audio) {
        FileType video_type = FileTypeDetector.obtainFileTypeEnum(video);
        FileType audio_type = FileTypeDetector.obtainFileTypeEnum(audio);

        if (!video_type.toString().contains("VID") || !audio_type.toString().contains("AUD")) return;

        double videoDuration = ExiftoolWrapper.getDurationFromFile(video);
        double audioDuration = ExiftoolWrapper.getDurationFromFile(audio);

        if (audioDuration > videoDuration) {
            AdjustAudioDuration(audio, videoDuration);
        }

        String temp_file_path = video.getAbsolutePath().substring(0, video.getAbsolutePath().lastIndexOf(".")) + "_merged.mp4";

        ProcessBuilder pb = new ProcessBuilder(
            "ffmpeg",
            "-y",
            "-i", video.getAbsolutePath(),
            "-i", audio.getAbsolutePath(),
            "-map", "0:v:0",
            "-map", "1:a:0",
            "-c:v", "copy",
            "-c:a", "aac",
            "-ar", "44100",
            "-ac", "2",
            "-b:a", "128k",
            "-shortest",
            temp_file_path
        );

        pb.inheritIO();
        try {
            Process process = pb.start();
            process.waitFor();
            File tempFile = new File(temp_file_path);
            if (tempFile.exists() && tempFile.isFile()) {
                java.nio.file.Files.move(tempFile.toPath(), video.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception e) {
        }
    }

    /**
     * Ajusta la duración de un audio utilizando el filtro atempo.
     * 
     * @param audio          Archivo de audio.
     * @param targetDuration Duración objetivo en segundos.
     */
    public static void AdjustAudioDuration(File audio, double targetDuration) {
        double currentDuration = ExiftoolWrapper.getDurationFromFile(audio);
        if (currentDuration <= 0 || targetDuration <= 0) return;

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
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Toma muestras (frames) de un video en intervalos regulares.
     * 
     * @param video            Archivo de video.
     * @param num_samples      Número de muestras a tomar.
     * @param interval_seconds Intervalo en segundos entre muestras.
     * @return Directorio temporal que contiene las imágenes capturadas.
     */
    public static File SampleVideo(File video, int num_samples, int interval_seconds) {
        FileType file_type = FileTypeDetector.obtainFileTypeEnum(video);
        if (file_type == FileType.OTHER || !file_type.toString().contains("VID")) return null;

        String temp_dir_path = System.getProperty("java.io.tmpdir") + File.separator + "video_samples_" + System.currentTimeMillis();
        File tempDir = new File(temp_dir_path);
        if (!tempDir.exists()) tempDir.mkdirs();

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
            return (tempDir.exists() && tempDir.isDirectory() && tempDir.list() != null && tempDir.list().length > 0) ? tempDir : null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String wrapText(String text, int limit) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < text.length()) {
            if (i + limit < text.length()) {
                int lastSpace = text.lastIndexOf(' ', i + limit);
                if (lastSpace > i) {
                    sb.append(text, i, lastSpace).append("\n");
                    i = lastSpace + 1;
                } else {
                    sb.append(text, i, i + limit).append("\n");
                    i += limit;
                }
            } else {
                sb.append(text.substring(i));
                break;
            }
        }
        return sb.toString();
    }

    /**
     * Añade un texto superpuesto al video en una posición determinada.
     * 
     * @param video    Archivo de video.
     * @param text     Texto a mostrar.
     * @param position Posición del texto ("top", "bottom" o centro por defecto).
     */
    public static void AddTextToVideo(File video, String text, String position) {
        String wrappedText = wrapText(text, 35);
        String escapedText = wrappedText.replace("'", "\\'").replace(":", "\\:");
        String x = "(w-text_w)/2";
        String y = "(h-text_h)/2";

        if (position.equalsIgnoreCase("top")) {
            y = "h/10";
        } else if (position.equalsIgnoreCase("bottom")) {
            y = "h-(h/8)";
        }

        String temp_file_path = video.getAbsolutePath().substring(0, video.getAbsolutePath().lastIndexOf(".")) + "_text.mp4";
        String drawtext = String.format("drawtext=text='%s':fontcolor=white:fontsize=48:x=%s:y=%s:borderw=2:bordercolor=black", 
                                        escapedText, x, y);

        ProcessBuilder pb = new ProcessBuilder(
            "ffmpeg", "-y",
            "-i", video.getAbsolutePath(),
            "-vf", drawtext,
            "-c:a", "copy",
            temp_file_path
        );

        pb.inheritIO();
        try {
            Process process = pb.start();
            process.waitFor();
            File tempFile = new File(temp_file_path);
            if (tempFile.exists() && tempFile.isFile()) {
                java.nio.file.Files.move(tempFile.toPath(), video.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
