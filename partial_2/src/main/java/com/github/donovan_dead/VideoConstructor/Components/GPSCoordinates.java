package com.github.donovan_dead.VideoConstructor.Components;

/**
 * Representa las coordenadas GPS (latitud y longitud) de un archivo multimedia.
 * 
 * @param latitude  La latitud en grados decimales.
 * @param longitude La longitud en grados decimales.
 */
public record GPSCoordinates(Double latitude, Double longitude) {
    /**
     * Retorna una representación en cadena de las coordenadas.
     * 
     * @return Cadena formateada con latitud y longitud.
     */
    @Override
    public String toString() {
        return String.format("Lat: %.6f, Lon: %.6f", latitude, longitude);
    }
}
