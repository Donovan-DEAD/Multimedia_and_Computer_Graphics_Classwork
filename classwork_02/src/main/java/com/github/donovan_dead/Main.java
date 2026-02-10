package com.github.donovan_dead;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import com.github.donovan_dead.Figures.Triangule;
import com.github.donovan_dead.Utils.ColorPoint;

public class Main {
    public static void main(String[] args) {
        int width = 800;
        int height = 600;

        File imgF = new File("Barycentric_Triangule.jpg");

        BufferedImage img = new BufferedImage(width,height , BufferedImage.TYPE_INT_RGB);
        
        ColorPoint c1 = new ColorPoint(new Color(255, 0, 0), 0, height);
        ColorPoint c2 = new ColorPoint(new Color(0, 255, 0), width, height);
        ColorPoint c3 = new ColorPoint(new Color(0, 0, 255), width/2, 0);
        Triangule t = new Triangule(c1, c2, c3);

        for(int i = 0; i< width; i++ ){
            for(int ii = 0; ii < height; ii++){
                img.setRGB(i, ii, t.ReturnColorInTriangule(new Point(i,ii)).getRGB());
            }
        }

        try {
            ImageIO.write(img, "jpg", imgF); 
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}