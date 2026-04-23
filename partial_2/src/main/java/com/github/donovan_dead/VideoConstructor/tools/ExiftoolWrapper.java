package com.github.donovan_dead.VideoConstructor.tools;

import java.io.File;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import com.github.donovan_dead.FileInfo.FileType;
import com.github.donovan_dead.FileInfo.FileTypeDetector;
import com.github.donovan_dead.VideoConstructor.Components.GPSCoordinates;

/**
 * Clase de utilidad que envuelve la funcionalidad de Exiftool para extraer metadatos de archivos multimedia.
 */
public class ExiftoolWrapper {

    /**
     * Extrae la fecha de creación de un archivo multimedia.
     * 
     * @param file El archivo a analizar.
     * @return La fecha de creación como OffsetDateTime, o la fecha actual si no se encuentra.
     */
    public static OffsetDateTime getCreationDateFromFile(File file) {
        if (!file.exists() && !file.isFile()) return null;

        FileType file_type = FileTypeDetector.obtainFileTypeEnum(file);
        if (file_type == FileType.OTHER) return null;

        String paramaterName = "";
        
        switch (file_type) {
            case FileType.IMG_TIFF:
            case FileType.IMG_JPEG:
                paramaterName = "-DateTimeOriginal";
                break;
            case FileType.IMG_PNG:
                paramaterName = "-CreationTime";
                break;
            case FileType.VID_MOV:
            case FileType.VID_MP4:
                paramaterName = "-CreateDate";
                break;
            case FileType.VID_AVI:
            case FileType.VID_MKV:
                paramaterName = "-DateTimeOriginal";
                break;
            default:
                break;
        }

        ProcessBuilder pb = new ProcessBuilder(
            "exiftool",
            "-s3",
            "-d",
            "%Y-%m-%dT%H:%M:%S%z",
            paramaterName,
            file.getAbsolutePath()
        );

        pb.redirectErrorStream(true);
        try {
            Process p = pb.start();
            byte[] buffer;
            try (InputStream is = p.getInputStream()) {
                buffer = is.readAllBytes();
            }
            p.waitFor();

            String output = new String(buffer).trim();
            if (output.isEmpty()) {
                return OffsetDateTime.now();
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");
            return OffsetDateTime.parse(output, formatter);

        } catch (Exception e) {
            return OffsetDateTime.now();
        }
    }

    /**
     * Extrae las coordenadas GPS de un archivo multimedia.
     * 
     * @param file El archivo a analizar.
     * @return Objeto GPSCoordinates con la latitud y longitud encontradas, o (0,0) si fallase.
     */
    public static GPSCoordinates getGPSCoordinatesFromFile(File file) {
        ProcessBuilder pb = new ProcessBuilder(
            "exiftool",
            "-s3",
            "-c", "%.6f",
            "-GPSLatitude",
            "-GPSLongitude",
            file.getAbsolutePath()
        );

        try {
            Process p = pb.start();
            byte[] buffer = p.getInputStream().readAllBytes();
            p.waitFor();
            
            String output = new String(buffer).trim();
            if (output.isEmpty()) {
                return new GPSCoordinates(0.0, 0.0);
            }

            String[] lines = output.split("\\R");
            if (lines.length < 2) return new GPSCoordinates(0.0, 0.0);

            double lat = Double.parseDouble(lines[0].replaceAll("[^0-9.-]", ""));
            double lon = Double.parseDouble(lines[1].replaceAll("[^0-9.-]", ""));

            return new GPSCoordinates(lat, lon);

        } catch (Exception e) {
            return new GPSCoordinates(0.0, 0.0);
        }
    }

    /**
     * Extrae la duración de un archivo de video o audio.
     * 
     * @param file El archivo a analizar.
     * @return La duración en segundos como Double.
     */
    public static Double getDurationFromFile(File file) {
        if (!file.exists() && !file.isFile()) return 0.0;

        FileType file_type = FileTypeDetector.obtainFileTypeEnum(file);
        if (file_type == FileType.OTHER) return 0.0;

        ProcessBuilder pb = new ProcessBuilder(
            "exiftool",
            "-s3",
            "-Duration",
            file.getAbsolutePath()
        );

        try {
            Process p = pb.start();
            byte[] buffer = p.getInputStream().readAllBytes();
            p.waitFor();
            
            String output = new String(buffer).trim();
            if (output.isEmpty()) {
                return 0.0;
            }

            return Double.parseDouble(output.replaceAll("[^0-9.-]", ""));

        } catch (Exception e) {
            return 0.0;
        }
    }
}
