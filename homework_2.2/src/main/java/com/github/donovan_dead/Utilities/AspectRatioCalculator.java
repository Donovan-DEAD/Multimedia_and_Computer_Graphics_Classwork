package com.github.donovan_dead.Utilities;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class AspectRatioCalculator {
    public static long[] calculateAspectRatio(long width, long height){
        if(width < 1 || height < 1) return null;

        long CommonFactor = MathFuncs.gcd(width, height);
        long[] aspectRatio = {width / CommonFactor, height / CommonFactor};
        return aspectRatio;
    }

    public static long[] calculateAspectRatioFromFile (File f){
        Image img = null;
        try{
            img = ImageIO.read(f);
        
        } catch (IOException e ){
        
            System.out.println(e.getMessage());
        }

        if(img == null) return null;
        
        long[] aspectRatio = calculateAspectRatio((long)img.getWidth(null), (long)img.getHeight(null));
        return aspectRatio;
    }
}
