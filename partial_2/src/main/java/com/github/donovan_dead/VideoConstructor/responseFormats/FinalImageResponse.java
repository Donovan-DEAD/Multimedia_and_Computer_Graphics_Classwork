package com.github.donovan_dead.VideoConstructor.responseFormats;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class FinalImageResponse {
    
    @JsonPropertyDescription("Frase motivacional que tenga que ver con todo el contenido del video presentado.")
    @JsonProperty(required = true)
    private String finalPhrase;

    public String getFinalPhrase() {
        return finalPhrase;
    }

    public void setFinalPhrase(String finalPhrase) {
        this.finalPhrase = finalPhrase;
    }
}
