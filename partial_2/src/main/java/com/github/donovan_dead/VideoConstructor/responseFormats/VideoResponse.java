package com.github.donovan_dead.VideoConstructor.responseFormats;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

/**
 * Representa la respuesta estructurada de OpenAI para el análisis de un segmento de video.
 */
public class VideoResponse {

    @JsonPropertyDescription("Descripción general del contenido visual de los frames analizados.")
    @JsonProperty(required = true)
    private String descripcionGeneral;

    @JsonPropertyDescription("Guion de audio optimizado para la duración del segmento de video.")
    @JsonProperty(required = true)
    private String audioScript;

    /**
     * Obtiene la descripción general del video.
     * @return Descripción textual.
     */
    public String getDescripcionGeneral() {
        return descripcionGeneral;
    }

    public void setDescripcionGeneral(String descripcionGeneral) {
        this.descripcionGeneral = descripcionGeneral;
    }

    /**
     * Obtiene el guion de audio generado.
     * @return Guion de audio.
     */
    public String getAudioScript() {
        return audioScript;
    }

    public void setAudioScript(String audioScript) {
        this.audioScript = audioScript;
    }
}
