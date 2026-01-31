package com.github.donovan_dead;

import java.io.IOException;
import java.awt.Color;
import java.awt.image.BufferedImage;


import com.github.donovan_dead.Drawer.Drawer;
import com.github.donovan_dead.FileController.WritterForImage;


public class Main {
    public static void main(String[] args) throws IOException {

        int multiplier = 2;
        double minutes = 90 + Float.valueOf(args[1]) * 6;
        double hour = 90 +Float.valueOf(args[0]) * 30 + Float.valueOf(args[1]) * 0.5;

        BufferedImage circuleImg = new BufferedImage(800*multiplier, 600* multiplier,BufferedImage.TYPE_INT_RGB);
        WritterForImage circule = new WritterForImage("circule.jpg", circuleImg);

        Drawer.FillWithColor(circuleImg, Color.black.getRGB());
        Drawer.DrawCirculePerimeter(circuleImg.getWidth()/2, circuleImg.getHeight()/2, circuleImg, Color.white.getRGB(), 350);
        // Drawer.DrawCirculePerimeter(circuleImg.getWidth()/2, circuleImg.getHeight()/2, circuleImg, 250, 30);
        Drawer.DrawCirculeOnPoints(circuleImg.getWidth()/2, circuleImg.getHeight()/2, circuleImg, Color.white.getRGB(), 250, 30, 12);
        Drawer.DrawLine(circuleImg.getWidth()/2, circuleImg.getHeight()/2, circuleImg, Color.white.getRGB(), (float)hour, 250);
        Drawer.DrawLine(circuleImg.getWidth()/2, circuleImg.getHeight()/2, circuleImg, Color.white.getRGB(), (float)minutes, 320);
        circule.saveImage();


        BufferedImage grassImg = new BufferedImage(800 * multiplier, 600 * multiplier, BufferedImage.TYPE_INT_RGB);
        WritterForImage grass = new WritterForImage("grass.jpg", grassImg);

        Drawer.FillWithColor(grassImg, Color.white.getRGB());
        Drawer.DrawLine(grassImg.getWidth()/4-200, grassImg.getHeight()/4, grassImg, Color.orange.getRGB(),180, 400);
        Drawer.DrawLine(grassImg.getWidth()/4, grassImg.getHeight()/4-200, grassImg, Color.orange.getRGB(),270, 400);
        Drawer.DrawLine(grassImg.getWidth()/4+100, grassImg.getHeight()/4 + 100, grassImg, Color.orange.getRGB(),45, 300);
        Drawer.DrawLine(grassImg.getWidth()/4-100, grassImg.getHeight()/4+100, grassImg, Color.orange.getRGB(),135, 300);
        Drawer.DrawCicrule(grassImg.getWidth()/4, grassImg.getHeight()/4, grassImg, Color.yellow.getRGB(), 100);
        Drawer.PaintWave(grassImg.getHeight()*3/4, grassImg, Color.green.getRGB(), 300, 50, false);

        grass.saveImage();
    }
}