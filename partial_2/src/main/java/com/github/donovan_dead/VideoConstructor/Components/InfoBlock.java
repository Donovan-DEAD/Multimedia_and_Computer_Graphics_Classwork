package com.github.donovan_dead.VideoConstructor.Components;

import java.io.File;
import java.io.InputStream;
import java.time.OffsetDateTime;

import com.github.donovan_dead.FileInfo.FileType;
import com.github.donovan_dead.FileInfo.FileTypeDetector;

public class InfoBlock implements Comparable<InfoBlock>{
    
    boolean proccesed = false; 
    File file_to_edit; 

    OffsetDateTime creation_date; 
    GPSCoordinates coords = new GPSCoordinates(23.7, -102.6); 
    Double duration = 0.0;

    String general_desc;
    File generated_images;
    InputStream generatedAudio;

    public InfoBlock(File file) throws Exception{
        if(!file.exists() || !file.isFile()) throw new Exception("File does not exist or is not a file, please try with the correct path.");
        if(FileTypeDetector.obtainFileTypeEnum(file) == FileType.OTHER || FileTypeDetector.obtainFileTypeEnum(file).toString().contains("AUD"))  throw new Exception("This file is not supported.");
        this.file_to_edit = file;
    }

    public void setFile(File file) throws Exception{
        if(!file.exists() || !file.isFile()) throw new Exception("File does not exist or is not a file, please try with the correct path.");
        if(FileTypeDetector.obtainFileTypeEnum(file) == FileType.OTHER || FileTypeDetector.obtainFileTypeEnum(file).toString().contains("AUD"))  throw new Exception("This file is not supported.");
        
        this.file_to_edit = file;
    }

    public File getFile(){
        return this.file_to_edit;
    }

    public OffsetDateTime getCreationDate(){
        return this.creation_date;
    }
    
    public void setCreationDate(OffsetDateTime date){
        this.creation_date = date;
    }

    public GPSCoordinates getCoords(){
        return this.coords;
    }

    public void setCoords(GPSCoordinates coords){
        this.coords = coords;
    }

    public Double getDuration(){
        return this.duration;
    }

    public void setDuration(Double duration){
        this.duration = duration;
    }

    public String getGeneralDesc(){
        return this.general_desc;
    }

    public void setGeneralDesc(String desc){
        this.general_desc = desc;
    }

    public File getGeneratedImages(){
        return this.generated_images;
    }

    public void setGeneratedImages(File images){
        this.generated_images = images;
    }

    public InputStream getSpeechResponse(){
        return this.generatedAudio;
    }

    public void setSpeechResponse(InputStream response){
        this.generatedAudio = response;
    }

    @Override
    public int compareTo(InfoBlock o) {
        return this.creation_date.compareTo(o.creation_date);
    }

    @Override
    public String toString() {
        return "InfoBlock [creation_date=" + creation_date + ", coords=" + coords.toString() + ", duration=" + duration + ", file_to_edit=" + file_to_edit
                + ", general_desc=" + general_desc + ", generated_images=" + generated_images + ", generatedAudio="
                + generatedAudio + "]";
    }
    
}
