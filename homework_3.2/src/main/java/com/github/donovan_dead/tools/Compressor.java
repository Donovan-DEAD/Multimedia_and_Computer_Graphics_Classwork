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

public class Compressor {
    private static double[] tolerance = new double[]{0.1d, 0.1d, 0.1d};
    private static BufferedImage imgToCompress;
    private static DataOutputStream imgOut;

    private static ArrayList<ColorCluster> clusters = new ArrayList<ColorCluster>();
    private static ConcurrentHashMap<Color, DynamicByte> colorMap = new ConcurrentHashMap<Color, DynamicByte>();

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



    public static void RunCompression(){
        Compressor.ExtractColorClusters();
        Compressor.CreateColorMap();
        DynamicByteContainer imgDataCompressed = Compressor.ProccesImage();
        Compressor.WriteImage(imgDataCompressed);
    }

}