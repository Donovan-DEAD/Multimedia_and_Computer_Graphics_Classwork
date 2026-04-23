package com.github.donovan_dead.VideoConstructor.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

import com.github.donovan_dead.VideoConstructor.Components.GPSCoordinates;

/**
 * Gestor para la generación de mapas estáticos utilizando la API de MapBox.
 */
public class OSMManager {
    private static OSMManager instance;

    /**
     * Obtiene la instancia única de OSMManager.
     * @return Instancia de OSMManager.
     */
    public static OSMManager getInstance() {
        if (instance == null) {
            instance = new OSMManager();
        }
        return instance;
    }

    /**
     * Genera una imagen de mapa estático que muestra una ruta entre dos coordenadas.
     * 
     * @param start    Coordenadas de inicio.
     * @param end      Coordenadas de fin.
     * @param width    Ancho deseado para la imagen.
     * @param height   Alto deseado para la imagen.
     * @param fileName Nombre del archivo de salida.
     * @return File con la imagen generada o null si ocurre un error.
     */
    public File generateMapImage(GPSCoordinates start, GPSCoordinates end, int width, int height, String fileName) {
        String token = System.getenv("OpenMapToken");
        if (token == null || token.isEmpty()) {
            return null;
        }

        if (start == null || end == null) {
            return null;
        }

        int finalWidth = Math.min(width, 1280);
        int finalHeight = Math.min(height, 1280);

        String markerStart = String.format(Locale.US, "pin-s-a+2ecc71(%.6f,%.6f)", start.longitude(), start.latitude());
        String markerEnd = String.format(Locale.US, "pin-s-b+e74c3c(%.6f,%.6f)", end.longitude(), end.latitude());
        
        String pathGeojson = String.format(Locale.US, 
            "{\"type\":\"Feature\",\"properties\":{\"stroke\":\"#2980b9\",\"stroke-width\":5,\"stroke-opacity\":0.8},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[%.6f,%.6f],[%.6f,%.6f]]}}",
            start.longitude(), start.latitude(), end.longitude(), end.latitude()
        );

        String urlString = "";
        try {
            String encodedPath = java.net.URLEncoder.encode(pathGeojson, "UTF-8").replace("+", "%20");
            String overlay = "geojson(" + encodedPath + ")," + markerStart + "," + markerEnd;

            urlString = String.format(Locale.US,
                "https://api.mapbox.com/styles/v1/mapbox/streets-v12/static/%s/auto/%dx%d?padding=100,100,100,100&access_token=%s",
                overlay, finalWidth, finalHeight, token
            );
        } catch (Exception e) {
            return null;
        }

        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                File file = new File(fileName);
                try (InputStream in = connection.getInputStream();
                     FileOutputStream out = new FileOutputStream(file)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                }
                return file;
            }
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * Genera una imagen de mapa estático centrada en una única coordenada.
     * 
     * @param lat      Latitud.
     * @param lon      Longitud.
     * @param width    Ancho.
     * @param height   Alto.
     * @param fileName Nombre del archivo.
     * @return File con la imagen o null.
     */
    public File generateMapImage(double lat, double lon, int width, int height, String fileName) {
        GPSCoordinates coords = new GPSCoordinates(lat, lon);
        return generateMapImage(coords, coords, width, height, fileName);
    }
}
