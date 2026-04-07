package com.github.donovan_dead.VideoConstructor.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

import com.github.donovan_dead.VideoConstructor.Components.GPSCoordinates;

public class OSMManager {
    private static OSMManager instance;

    public static OSMManager getInstance() {
        if (instance == null) {
            instance = new OSMManager();
        }
        return instance;
    }

    /**
     * Genera una imagen de mapa estático usando MapBox Static Images API.
     * Señala los puntos de inicio y fin con marcadores de colores.
     * @param start Coordenadas de inicio
     * @param end Coordenadas de fin
     * @param width Ancho de la imagen (Máx 1280 para MapBox)
     * @param height Alto de la imagen (Máx 1280 para MapBox)
     * @param fileName Nombre del archivo de salida
     * @return File de la imagen generada o null si falla
     */
    public File generateMapImage(GPSCoordinates start, GPSCoordinates end, int width, int height, String fileName) {
        System.out.println(start.latitude() + ", " + start.longitude() + " - " + end.latitude() + ", " + end.longitude());
        
        String token = System.getenv("OpenMapToken");
        if (token == null || token.isEmpty()) {
            System.out.println("[DEBUG] Error: La variable de entorno OpenMapToken no está definida.");
            return null;
        }

        if (start == null || end == null) {
            System.out.println("[DEBUG] Coordenadas insuficientes para generar el mapa.");
            return null;
        }

        // Limitamos el tamaño al máximo permitido por MapBox (1280x1280) para evitar errores.
        // FFMPEG se encargará de ajustarlo al formato portrait final en VideoConstructor.
        int finalWidth = Math.min(width, 1280);
        int finalHeight = Math.min(height, 1280);

        // Definimos los marcadores: pin-s-a+2ecc71 (verde) y pin-s-b+e74c3c (rojo)
        String markerStart = String.format(Locale.US, "pin-s-a+2ecc71(%.6f,%.6f)", start.longitude(), start.latitude());
        String markerEnd = String.format(Locale.US, "pin-s-b+e74c3c(%.6f,%.6f)", end.longitude(), end.latitude());
        
        // Creamos un path azul (LineString) entre los puntos usando GeoJSON
        // Usamos el azul #2980b9 con un ancho de 5px
        String pathGeojson = String.format(Locale.US, 
            "{\"type\":\"Feature\",\"properties\":{\"stroke\":\"#2980b9\",\"stroke-width\":5,\"stroke-opacity\":0.8},\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[%.6f,%.6f],[%.6f,%.6f]]}}",
            start.longitude(), start.latitude(), end.longitude(), end.latitude()
        );

        String urlString = "";
        try {
            // El GeoJSON debe estar codificado para la URL. 
            // Reemplazamos los "+" que genera URLEncoder por "%20" para mayor compatibilidad con MapBox
            String encodedPath = java.net.URLEncoder.encode(pathGeojson, "UTF-8").replace("+", "%20");
            
            // Combinamos el path y los marcadores en la sección de {overlay}
            String overlay = "geojson(" + encodedPath + ")," + markerStart + "," + markerEnd;

            // La URL usa "auto" para ajustar el zoom automáticamente. 
            // Añadimos padding (en píxeles) para alejar un poco la cámara y que se vean nombres de calles o ciudades.
            urlString = String.format(Locale.US,
                "https://api.mapbox.com/styles/v1/mapbox/streets-v12/static/%s/auto/%dx%d?padding=100,100,100,100&access_token=%s",
                overlay, finalWidth, finalHeight, token
            );
        } catch (Exception e) {
            System.out.println("[DEBUG] Error al construir URL de MapBox: " + e.getMessage());
            return null;
        }

        System.out.println("[DEBUG] Intentando obtener mapa desde MapBox con path y marcadores...");

        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);

            int responseCode = connection.getResponseCode();
            System.out.println("[DEBUG] Código de respuesta HTTP (MapBox): " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                File file = new File(fileName);
                try (InputStream in = connection.getInputStream();
                     FileOutputStream out = new FileOutputStream(file)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                }
                System.out.println("[DEBUG] Mapa generado exitosamente con MapBox: " + fileName);
                return file;
            } else {
                System.out.println("[DEBUG] Error en MapBox. Respuesta del servidor: " + responseCode);
            }
        } catch (Exception e) {
            System.out.println("[DEBUG] Excepción al generar mapa con MapBox: " + e.getMessage());
        }
        return null;
    }

    public File generateMapImage(double lat, double lon, int width, int height, String fileName) {
        GPSCoordinates coords = new GPSCoordinates(lat, lon);
        return generateMapImage(coords, coords, width, height, fileName);
    }
}
