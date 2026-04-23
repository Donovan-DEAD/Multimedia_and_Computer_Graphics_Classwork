package com.github.donovan_dead.VideoConstructor.responseFormats;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

/**
 * Representa la respuesta estructurada de OpenAI para la imagen final del video.
 */
public class FinalImageResponse {
    
    @JsonPropertyDescription("Frase motivacional corta e impactante basada en el contenido del video.")
    @JsonProperty(required = true)
    private String finalPhrase;

    /**
     * Obtiene la frase motivacional final.
     * @return La frase generada.
     */
    public String getFinalPhrase() {
        return finalPhrase;
    }

    /**
     * Establece la frase motivacional final.
     * @param finalPhrase La frase generada.
     */
    public void setFinalPhrase(String finalPhrase) {
        this.finalPhrase = finalPhrase;
    }
}
