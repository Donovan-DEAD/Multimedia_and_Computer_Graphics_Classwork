package com.github.donovan_dead.VideoConstructor.Components;

import java.io.File;
import java.time.OffsetDateTime;

import com.github.donovan_dead.FileInfo.FileType;
import com.github.donovan_dead.FileInfo.FileTypeDetector;

/**
 * Representa un bloque de información asociado a un archivo multimedia (imagen o video).
 * Contiene metadatos como fecha de creación, coordenadas GPS, duración y descripción.
 */
public class InfoBlock implements Comparable<InfoBlock> {
    
    private boolean normalized = false;
    private boolean audio_integrated = false;
    private File file_to_edit; 
    private File original_file;

    private OffsetDateTime creation_date; 
    private GPSCoordinates coords = new GPSCoordinates(23.7, -102.6); 
    private Double duration = 0.0;

    private String general_desc;
    private File generated_images;

    /**
     * Crea un nuevo InfoBlock a partir de un archivo.
     * 
     * @param file El archivo multimedia (imagen o video).
     * @throws Exception Si el archivo no existe, no es un archivo válido o no es soportado.
     */
    public InfoBlock(File file) throws Exception {
        if (!file.exists() || !file.isFile()) {
            throw new Exception("El archivo no existe o no es un archivo válido.");
        }
        FileType type = FileTypeDetector.obtainFileTypeEnum(file);
        if (type == FileType.OTHER || type.toString().contains("AUD")) {
            throw new Exception("Formato de archivo no soportado.");
        }
        this.file_to_edit = file;
        this.original_file = file;
    }

    public File getOriginalFile() {
        return this.original_file;
    }

    /**
     * Establece el archivo a editar para este bloque.
     * 
     * @param file El nuevo archivo.
     * @throws Exception Si el archivo no es válido o no es soportado.
     */
    public void setFile(File file) throws Exception {
        if (!file.exists() || !file.isFile()) {
            throw new Exception("El archivo no existe o no es un archivo válido.");
        }
        FileType type = FileTypeDetector.obtainFileTypeEnum(file);
        if (type == FileType.OTHER || type.toString().contains("AUD")) {
            throw new Exception("Formato de archivo no soportado.");
        }
        this.file_to_edit = file;
    }

    public File getFile() {
        return this.file_to_edit;
    }

    public OffsetDateTime getCreationDate() {
        return this.creation_date;
    }
    
    public void setCreationDate(OffsetDateTime date) {
        this.creation_date = date;
    }

    public GPSCoordinates getCoords() {
        return this.coords;
    }

    public void setCoords(GPSCoordinates coords) {
        this.coords = coords;
    }

    public Double getDuration() {
        return this.duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }

    public String getGeneralDesc() {
        return this.general_desc;
    }

    public void setGeneralDesc(String desc) {
        this.general_desc = desc;
    }

    public File getGeneratedImages() {
        return this.generated_images;
    }

    public void setGeneratedImages(File images) {
        this.generated_images = images;
    }

    public boolean isNormalized() {
        return this.normalized;
    }

    public void setNormalized(boolean normalized) {
        this.normalized = normalized;
    }

    public boolean isAudioIntegrated() {
        return this.audio_integrated;
    }

    public void setAudioIntegrated(boolean audio_integrated) {
        this.audio_integrated = audio_integrated;
    }

    /**
     * Compara este bloque con otro basándose en la fecha de creación.
     * 
     * @param o El otro bloque a comparar.
     * @return Un valor negativo, cero o positivo si este bloque es anterior, igual o posterior al otro.
     */
    @Override
    public int compareTo(InfoBlock o) {
        if (this.creation_date == null || o.creation_date == null) return 0;
        return this.creation_date.compareTo(o.creation_date);
    }

    @Override
    public String toString() {
        return "InfoBlock [creation_date=" + creation_date + ", coords=" + coords + ", duration=" + duration 
                + ", file=" + file_to_edit.getName() + ", description=" + general_desc + "]";
    }
}
