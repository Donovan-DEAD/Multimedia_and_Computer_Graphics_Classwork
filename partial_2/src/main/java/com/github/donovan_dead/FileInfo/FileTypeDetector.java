package com.github.donovan_dead.FileInfo;

import java.io.File;

public class FileTypeDetector {
    public static FileType obtainFileTypeEnum(File file){
        String file_name = file.getName();
        int index = file_name.lastIndexOf(".");
        String extension = file_name.substring(index + 1);
        
        switch(extension){
            case "jpg":
            case "jpeg":
                return FileType.IMG_JPEG;
            case "png":
                return FileType.IMG_PNG;
            case "tiff":
                return FileType.IMG_TIFF;
            case "mp4":
                return FileType.VID_MP4;
            case "avi":
                return FileType.VID_AVI;
            case "mov":
                return FileType.VID_MOV;
            case "mkv":
                return FileType.VID_MKV;
            case "mp3":
                return FileType.AUD_MP3;
            case "wav":
                return FileType.AUD_WAV;
            default:
                return FileType.OTHER;
        }
    }

    public static String obtainFileTypeString(FileType type){
        switch (type) {
            case IMG_JPEG:
                return "jpeg";
            case IMG_PNG:
                return "png";
            case IMG_TIFF:
                return "tiff";
            case VID_MP4:
                return "mp4";
            case VID_AVI:
                return "avi";
            case VID_MOV:
                return "mov";
            case VID_MKV:
                return "mkv";
            case AUD_MP3:
                return "mp3";
            case AUD_WAV:
                return "wav";
            default:
                return null;
        }
    }
}
