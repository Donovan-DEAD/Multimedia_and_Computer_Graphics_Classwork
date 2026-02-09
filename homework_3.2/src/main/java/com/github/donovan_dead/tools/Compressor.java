package com.github.donovan_dead.tools;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;

import com.github.donovan_dead.Utils.Bytes.DynamicByte;
import com.github.donovan_dead.Utils.Bytes.DynamicByteContainer;
import com.github.donovan_dead.tools.Cluster.ClusterComparator;
import com.github.donovan_dead.tools.Cluster.ColorCluster;

public class Compressor {
    private static double[] tolerance = new double[]{0.1d, 0.1d, 0.1d};
    private static BufferedImage imgToCompress;
    private static FileOutputStream imgOut;

    private static ArrayList<ColorCluster> clusters = new ArrayList<ColorCluster>();
    private static ConcurrentHashMap<Color, DynamicByte> colorMap = new ConcurrentHashMap<Color, DynamicByte>();

    public static void InitCompressor(double[] tolerance, BufferedImage imgToCompress, String pathOut){
        Compressor.tolerance[0] = tolerance[0] * 255;
        Compressor.tolerance[1] = tolerance[1] * 255;
        Compressor.tolerance[2] = tolerance[2] * 255;

        Compressor.imgToCompress = imgToCompress;
        try{
        
            Compressor.imgOut = new FileOutputStream(pathOut + "/compressed.bimg");

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

        for (int i = 0; i < clusters.size(); i++) {
            ColorCluster c = clusters.get(i);
            Color centroid = c.getCentroid();

            DynamicByte db = new DynamicByte(i);
            Compressor.colorMap.put(centroid, db);
        }

    }

    public static DynamicByte FoundDynamicByte(Color c) {
        for (ColorCluster cl : clusters) {
            if (cl.isInRange(c)) {
                DynamicByte db = Compressor.colorMap.get(cl.getCentroid());
                Compressor.colorMap.put(c, db);

                return db;
            }
        }

        return null;
    }

    public static DynamicByteContainer ProccesImage(){
        DynamicByteContainer masterContainer = new DynamicByteContainer();
        ThreadFactory tf = Thread.ofVirtual().factory();

        ArrayList<Thread> threads = new ArrayList<Thread>();
        ArrayList<DynamicByteContainer> containers = new ArrayList<DynamicByteContainer>();

        
        for(int h = 0; h < Compressor.imgToCompress.getHeight(); h++){
            containers.add(new DynamicByteContainer());

            final int row = h;
            final DynamicByteContainer container = containers.get(h);

            Runnable task = ()->{
                for(int w = 0; w < Compressor.imgToCompress.getWidth(); w++){
                    Color c = new Color(Compressor.imgToCompress.getRGB(w, row));
                    
                    DynamicByte db = Compressor.colorMap.get(c);
                    if(db == null) db = Compressor.FoundDynamicByte(c);
                    
                    container.add(db);
                }
            };

            Thread t = tf.newThread(task);
            threads.add(t);
            t.start();      
        }

        for(Thread t : threads){
            try {
                t.join();
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }

        for(DynamicByteContainer dc : containers){
            masterContainer.append(dc);
        }

        return masterContainer;
    }

    public static void WriteImage(DynamicByteContainer imgDataCompressed){
        try{

            imgOut.write(imgToCompress.getWidth());
            imgOut.write(imgToCompress.getHeight());

            imgOut.write(clusters.size());
            for(ColorCluster c : clusters) imgOut.write(c.getCentroid().getRGB());
            
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
