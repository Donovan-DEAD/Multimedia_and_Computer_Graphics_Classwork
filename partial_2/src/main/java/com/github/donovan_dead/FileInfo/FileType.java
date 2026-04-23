package com.github.donovan_dead.FileInfo;

/**
 * Enumeración que representa los tipos de archivos soportados por la aplicación.
 * Incluye categorías para imágenes, videos y audio.
 */
public enum FileType {
    /** Tipo de archivo no reconocido o no soportado. */
    OTHER,
    /** Imagen en formato JPEG. */
    IMG_JPEG,
    /** Imagen en formato PNG. */
    IMG_PNG,
    /** Imagen en formato TIFF. */
    IMG_TIFF,
    /** Video en formato MP4. */
    VID_MP4,
    /** Video en formato AVI. */
    VID_AVI,
    /** Video en formato MOV. */
    VID_MOV,
    /** Video en formato MKV. */
    VID_MKV,
    /** Audio en formato MP3. */
    AUD_MP3,
    /** Audio en formato WAV. */
    AUD_WAV
}
