package com.github.donovan_dead.VideoConstructor.responseFormats;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class VideoResponse {

    @JsonPropertyDescription("Descripción a grandes rasgos de lo que trata el video")
    @JsonProperty(required = true)
    private String descripcionGeneral;

    @JsonPropertyDescription("Guion de audio que servirá para generar un audio que será colocado en un video con una duración N")
    @JsonProperty(required = true)
    private String audioScript;

    public String getDescripcionGeneral() {
        return descripcionGeneral;
    }

    public void setDescripcionGeneral(String descripcionGeneral) {
        this.descripcionGeneral = descripcionGeneral;
    }

    public String getAudioScript() {
        return audioScript;
    }

    public void setAudioScript(String audioScript) {
        this.audioScript = audioScript;
    }
}
