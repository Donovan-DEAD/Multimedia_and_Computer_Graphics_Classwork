package com.github.donovan_dead.VideoConstructor.tools;

import java.io.File;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import com.github.donovan_dead.FileInfo.FileType;
import com.github.donovan_dead.FileInfo.FileTypeDetector;
import com.github.donovan_dead.VideoConstructor.Components.GPSCoordinates;

public class ExiftoolWrapper {
    public static OffsetDateTime getCreationDateFromFile(File file){
        System.out.println("[DEBUG] Exiftool: Obteniendo fecha de creación para: " + file.getName());
        if(!file.exists() && !file.isFile()) return null;

        FileType file_type = FileTypeDetector.obtainFileTypeEnum(file);
        if(file_type == FileType.OTHER) return null;

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
                System.out.println("[DEBUG] Exiftool: No se encontró fecha, usando actual.");
                return OffsetDateTime.now();
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");
            OffsetDateTime dt = OffsetDateTime.parse(output, formatter);
            System.out.println("[DEBUG] Exiftool: Fecha obtenida: " + dt);
            return dt;

        } catch (Exception e) {
            System.out.println("[DEBUG] Exiftool Error: " + e.getMessage());
            return OffsetDateTime.now();
        }
    }

    public static GPSCoordinates getGPSCoordinatesFromFile(File file){
        System.out.println("[DEBUG] Exiftool: Obteniendo coordenadas GPS para: " + file.getName());
        ProcessBuilder pb = new ProcessBuilder(
            "exiftool",
            "-s3",
            "-c", "%.6f", // Formato decimal
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
                System.out.println("[DEBUG] Exiftool: No se encontraron coordenadas.");
                return new GPSCoordinates(0.0, 0.0);
            }

            // ExifTool devuelve los valores en líneas separadas debido a -s3
            String[] lines = output.split("\\R");
            if (lines.length < 2) return new GPSCoordinates(0.0, 0.0);

            double lat = Double.parseDouble(lines[0].replaceAll("[^0-9.-]", ""));
            double lon = Double.parseDouble(lines[1].replaceAll("[^0-9.-]", ""));

            GPSCoordinates coords = new GPSCoordinates(lat, lon);
            System.out.println("[DEBUG] Exiftool: Coordenadas obtenidas: " + coords);
            return coords;

        } catch (Exception e) {
            System.out.println("[DEBUG] Exiftool Error (GPS): " + e.getMessage());
            return new GPSCoordinates(0.0, 0.0);
        }
    }

    public static Double getDurationFromFile(File file){
        System.out.println("[DEBUG] Exiftool: Obteniendo duración para: " + file.getName());
        if(!file.exists() && !file.isFile()) return 0.0;

        FileType file_type = FileTypeDetector.obtainFileTypeEnum(file);
        if(file_type == FileType.OTHER) return 0.0;

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
                System.out.println("[DEBUG] Exiftool: No se encontró duración.");
                return 0.0;
            }

            double duration = Double.parseDouble(output.replaceAll("[^0-9.-]", ""));
            System.out.println("[DEBUG] Exiftool: Duración obtenida: " + duration + "s");
            return duration;

        } catch (Exception e) {
            System.out.println("[DEBUG] Exiftool Error (Duración): " + e.getMessage());
            return 0.0;
        }
    }


}
