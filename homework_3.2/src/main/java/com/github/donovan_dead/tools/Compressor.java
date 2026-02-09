package com.github.donovan_dead.tools;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;
import java.util.zip.GZIPOutputStream;

import com.github.donovan_dead.Utils.Bytes.DynamicByte;
import com.github.donovan_dead.Utils.Bytes.DynamicByteContainer;
import com.github.donovan_dead.Utils.Colors.YCbCr;
import com.github.donovan_dead.tools.Cluster.ClusterComparator;
import com.github.donovan_dead.tools.Cluster.ColorCluster;

/**
 * The Compressor class provides functionality to compress a BufferedImage using a custom
 * run-length encoding scheme based on color clustering. It quantizes colors into clusters,
 * creates a color map, and then encodes the image data with run-length encoding.
 */
public class Compressor {
    /**
     * Tolerance values for Y, Cb, Cr channels used in color clustering.
     * These values are multiplied by 255 during initialization.
     */
    private static double[] tolerance = new double[]{0.1d, 0.1d, 0.1d};
    /**
     * The image to be compressed.
     */
    private static BufferedImage imgToCompress;
    /**
     * The output stream for writing the compressed image data.
     */
    private static DataOutputStream imgOut;

    /**
     * A list of color clusters identified in the image.
     */
    private static ArrayList<ColorCluster> clusters = new ArrayList<ColorCluster>();
    /**
     * A map from Color objects (specifically cluster centroids) to their corresponding
     * DynamicByte representation, which includes the color index and bits for that index.
     */
    private static ConcurrentHashMap<Color, DynamicByte> colorMap = new ConcurrentHashMap<Color, DynamicByte>();

    /**
     * Initializes the Compressor with specified tolerance, the image to compress, and the output path.
     * The tolerance values are scaled by 255.
     *
     * @param tolerance An array of doubles representing the tolerance for Y, Cb, Cr channels.
     * @param imgToCompress The BufferedImage object to be compressed.
     * @param pathOut The output directory path where the compressed image will be saved.
     */
    public static void InitCompressor(double[] tolerance, BufferedImage imgToCompress, String pathOut){
        Compressor.tolerance[0] = tolerance[0] * 255;
        Compressor.tolerance[1] = tolerance[1] * 255;
        Compressor.tolerance[2] = tolerance[2] * 255;

        Compressor.imgToCompress = imgToCompress;
        try{
        
            Compressor.imgOut = new DataOutputStream(
                new GZIPOutputStream(
                    new FileOutputStream(pathOut + "/compressed.bimg.gz")
                ) 
            );

        } catch(Exception e){

            System.out.println(e.getMessage());
        
        }
    }

    /**
     * Extracts color clusters from the image to compress. It iterates through each pixel
     * of the image, and if a pixel's color falls within the range of an existing cluster,
     * it adjusts that cluster's centroid. Otherwise, a new cluster is created for that color.
     * Finally, the clusters are sorted.
     */
    public static void ExtractColorClusters(){
        boolean founded = false;
        for(int i = 0; i < Compressor.imgToCompress.getWidth(); i++){
            for(int j = 0; j < Compressor.imgToCompress.getHeight(); j++){
                
                founded = false;
                
                for(ColorCluster c : clusters){
                    if(c.isInRange(new Color(imgToCompress.getRGB(i, j)))){
                        c.adjustCentroid(new Color(imgToCompress.getRGB(i, j)));
                        founded = true;
                        break;
                    }
                }
                

                if(!founded){
                    clusters.add(new ColorCluster(tolerance, new Color(imgToCompress.getRGB(i, j))));
                    founded = false;
                }
            }
        }

        System.out.println("Clusters registered " + clusters.size());
        Collections.sort(clusters, new ClusterComparator());
    }

    /**
     * Creates a color map by assigning a unique DynamicByte representation to each color cluster.
     * The number of bits required for the index is dynamically calculated based on the total
     * number of clusters.
     */
    public static void CreateColorMap() {
        int numClusters = clusters.size();
        // Calculate the number of bits needed to represent the cluster indices
        int bitsForIndex = (numClusters > 1) ? (int) Math.ceil(Math.log(numClusters) / Math.log(2)) : 1;

        for (int i = 0; i < clusters.size(); i++) {
            ColorCluster c = clusters.get(i);
            Color centroid = c.getCentroid();
            Compressor.colorMap.put(centroid, new DynamicByte(i, bitsForIndex));
        }
    }

    /**
     * Finds the most suitable DynamicByte for a given color by comparing it to existing color clusters.
     * It calculates the Euclidean distance in YCbCr color space to find the best matching cluster.
     * The result is cached in the colorMap for future use.
     *
     * @param c The Color for which to find the DynamicByte.
     * @return The DynamicByte associated with the best matching color cluster, or null if no match is found (should be unreachable).
     */
    public static DynamicByte FoundDynamicByte(Color c) {
        ColorCluster bestMatch = null;
        double minDistanceSq = Double.MAX_VALUE;
        YCbCr targetColorYCbCr = new YCbCr(c);

        for (ColorCluster cl : clusters) {
            YCbCr centroidYCbCr = new YCbCr(cl.getCentroid());
            
            double distY = targetColorYCbCr.getY() - centroidYCbCr.getY();
            double distCb = targetColorYCbCr.getCb() - centroidYCbCr.getCb();
            double distCr = targetColorYCbCr.getCr() - centroidYCbCr.getCr();
            
            double distanceSq = (distY * distY) + (distCb * distCb) + (distCr * distCr);

            if (distanceSq < minDistanceSq) {
                minDistanceSq = distanceSq;
                bestMatch = cl;
            }
        }

        if (bestMatch != null) {
            DynamicByte db = Compressor.colorMap.get(bestMatch.getCentroid());
            Compressor.colorMap.put(c, db);
            return db;
        }

        return null; // Should be unreachable
    }

    /**
     * Processes the image pixel by pixel, converting colors into their corresponding
     * DynamicByte representations and applying run-length encoding.
     * It uses virtual threads to process each row concurrently for improved performance.
     *
     * @return A DynamicByteContainer holding the compressed image data.
     */
    public static DynamicByteContainer ProccesImage(){
        DynamicByteContainer masterContainer = new DynamicByteContainer();
        ThreadFactory tf = Thread.ofVirtual().factory();

        ArrayList<Thread> threads = new ArrayList<>();
        ArrayList<DynamicByteContainer> containers = new ArrayList<>();
        
        int numClusters = clusters.size();
        final int bitsForIndex = (numClusters > 1) ? (int) Math.ceil(Math.log(numClusters) / Math.log(2)) : 1;

        for (int h = 0; h < Compressor.imgToCompress.getHeight(); h++) {
            containers.add(new DynamicByteContainer());
            final int row = h;
            final DynamicByteContainer container = containers.get(row);

            Runnable task = () -> {
                ArrayList<Long> rowIndex = new ArrayList<>();
                for (int x = 0; x < Compressor.imgToCompress.getWidth(); x++) {
                    Color c = new Color(Compressor.imgToCompress.getRGB(x, row));
                    DynamicByte db = Compressor.colorMap.get(c);
                    if (db == null) {
                        db = FoundDynamicByte(c);
                    }
                    rowIndex.add(db.getValue());
                }

                int i = 0;
                while (i < rowIndex.size()) {
                    Long currentIndex = rowIndex.get(i);
                    int runLength = 1;
                    while (runLength < 256 && (i + runLength) < rowIndex.size() && rowIndex.get(i + runLength).equals(currentIndex)) {
                        runLength++;
                    }

                    if (runLength > 1) {
                        container.write(currentIndex, bitsForIndex);
                        container.write(1, 1);
                        container.write(runLength - 1, 8);
                    } else {
                        container.write(currentIndex, bitsForIndex);
                        container.write(0, 1);
                    }
                    i += runLength;
                }
            };

            Thread t = tf.newThread(task);
            threads.add(t);
            t.start();
        }

        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }

        for (DynamicByteContainer dc : containers) {
            masterContainer.append(dc);
        }

        return masterContainer;
    }

    /**
     * Writes the compressed image data, including image dimensions, color palette, and pixel data,
     * to the initialized output stream.
     *
     * @param imgDataCompressed The DynamicByteContainer holding the compressed pixel data.
     */
    public static void WriteImage(DynamicByteContainer imgDataCompressed){
        try{

            imgOut.writeInt(imgToCompress.getWidth());
            imgOut.writeInt(imgToCompress.getHeight());

            imgOut.writeInt(clusters.size());
            for(ColorCluster c : clusters) imgOut.writeInt(c.getCentroid().getRGB());
            
            imgOut.write(imgDataCompressed.getPackedData());

            imgOut.flush();
            imgOut.close();

        } catch(Exception e){
            
            System.out.println(e.getMessage());
        
        }
        
    }



    /**
     * Executes the complete image compression process:
     * 1. Extracts color clusters from the image.
     * 2. Creates a color map based on the clusters.
     * 3. Processes the image to generate compressed data.
     * 4. Writes the compressed data to the output file.
     */
    public static void RunCompression(){
        Compressor.ExtractColorClusters();
        Compressor.CreateColorMap();
        DynamicByteContainer imgDataCompressed = Compressor.ProccesImage();
        Compressor.WriteImage(imgDataCompressed);
    }

}