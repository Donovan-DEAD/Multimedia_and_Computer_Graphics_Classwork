package com.github.donovan_dead.VideoConstructor.Components;

public record GPSCoordinates (Double latitude, Double longitude){
    @Override
    public String toString() {
        return String.format("Lat: %.6f, Lon: %.6f", latitude, longitude);
    }
}
