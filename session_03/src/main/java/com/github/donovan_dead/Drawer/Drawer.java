package com.github.donovan_dead.Drawer;

import java.awt.image.BufferedImage;

public class Drawer {
    

    public static void DrawLine(int xCenter, int yCenter, BufferedImage img, int c, float degree, int length){
        double angleInRadians = Math.toRadians(degree);
        

        for(int i = 0 ; i <= length; i++)
            img.setRGB((int)(xCenter - i * Math.cos(angleInRadians)), (int)(yCenter - i * Math.sin(angleInRadians)), c);

    }

    public static void DrawCicrule(int xCenter, int yCenter, BufferedImage img, int c, int radius){
       for(float angle = 0; angle < 360f; angle += 0.01f)
        DrawLine(xCenter, yCenter, img, c, angle, radius);
            
    }

    public static void DrawCirculePerimeter(int xCenter, int yCenter, BufferedImage img, int c, int radius){
        for(float angle = 0; angle < 360f; angle += 0.1f)    
            img.setRGB((int)(xCenter - radius * Math.cos(Math.toRadians(angle))), (int)(yCenter - radius * Math.sin(Math.toRadians(angle))), c);   
    }
    
    public static void DrawCirculePerimeter(int xCenter, int yCenter, BufferedImage img, int c, int radius, float step){
        for(float angle = 0; angle < 360f; angle += step)    
            img.setRGB((int)(xCenter - radius * Math.cos(Math.toRadians(angle))), (int)(yCenter - radius * Math.sin(Math.toRadians(angle))), c);   
    }

        
    public static void DrawCirculeOnPoints(int xCenter, int yCenter, BufferedImage img, int c, int radius, float step, int rMinor){
        for(float angle = 0; angle < 360f; angle += step)    
            DrawCirculePerimeter((int)(xCenter - radius * Math.cos(Math.toRadians(angle))), (int)(yCenter - radius * Math.sin(Math.toRadians(angle))), img, c, rMinor);
    }

    public static void FillWithColor(BufferedImage img, int c){
        for(int i = 0; i < img.getWidth(); i++){
            for(int ii = 0 ; ii < img.getHeight(); ii++){
                img.setRGB(i, ii, c);
            }
        }
    }

    public static void PaintWave(int height, BufferedImage img, int c, int waveLength, int size, boolean up){
        
        for(int i = 0; i< img.getWidth(); i++){
            for(int ii = 0; ii < img.getHeight(); ii++){
                if(up){
                    if(ii < height + Math.sin(Math.toRadians((double)i%(double)waveLength/(double)waveLength * 360f))*(double)size){
                        img.setRGB(i, ii, c);
                    }
                } else if(ii > height + Math.sin(Math.toRadians((double)i%(double)waveLength/(double)waveLength * 360f))*(double)size)
                    img.setRGB(i, ii, c);
            }
        }
    }

    public static void DrawGradient (){
        
    }

}
