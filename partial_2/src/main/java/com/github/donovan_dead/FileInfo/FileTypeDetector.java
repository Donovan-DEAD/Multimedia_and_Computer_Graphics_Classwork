package com.github.donovan_dead.FileInfo;

import java.io.File;

/**
 * Clase de utilidad para detectar el tipo de archivo basado en su extensión.
 */
public class FileTypeDetector {

    /**
     * Determina el tipo de archivo (FileType) a partir de un objeto File.
     * 
     * @param file El archivo a analizar.
     * @return El tipo de archivo correspondiente o FileType.OTHER si no se reconoce.
     */
    public static FileType obtainFileTypeEnum(File file) {
        String file_name = file.getName();
        int index = file_name.lastIndexOf(".");
        if (index == -1) return FileType.OTHER;
        
        String extension = file_name.substring(index + 1).toLowerCase();
        
        return switch (extension) {
            case "jpg", "jpeg" -> FileType.IMG_JPEG;
            case "png" -> FileType.IMG_PNG;
            case "tiff" -> FileType.IMG_TIFF;
            case "mp4" -> FileType.VID_MP4;
            case "avi" -> FileType.VID_AVI;
            case "mov" -> FileType.VID_MOV;
            case "mkv" -> FileType.VID_MKV;
            case "mp3" -> FileType.AUD_MP3;
            case "wav" -> FileType.AUD_WAV;
            default -> FileType.OTHER;
        };
    }

    /**
     * Convierte un enum FileType a su representación en cadena de texto (extensión).
     * 
     * @param type El tipo de archivo.
     * @return La extensión del archivo en minúsculas, o null si es de tipo OTHER.
     */
    public static String obtainFileTypeString(FileType type) {
        return switch (type) {
            case IMG_JPEG -> "jpeg";
            case IMG_PNG -> "png";
            case IMG_TIFF -> "tiff";
            case VID_MP4 -> "mp4";
            case VID_AVI -> "avi";
            case VID_MOV -> "mov";
            case VID_MKV -> "mkv";
            case AUD_MP3 -> "mp3";
            case AUD_WAV -> "wav";
            default -> null;
        };
    }
}
