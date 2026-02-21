package com.github.donovan_dead;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Scanner;

import javax.imageio.ImageIO;

import com.github.donovan_dead.Actions.ActionExecutioner;
import com.github.donovan_dead.Actions.CropImage;
import com.github.donovan_dead.Actions.InvertColorImage;
import com.github.donovan_dead.Actions.RotateImage;


public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        BufferedImage img;
        try {
            img = ImageIO.read(new File("img_1.jpg"));
        } catch (Exception e) {
            e.printStackTrace();
            sc.close();
            return;
        }

        ActionExecutioner ex  = new ActionExecutioner(img);

        Point2D p1 = new Point2D.Double(0, 0);
        Point2D p2 = new Point2D.Double(300, 500);
        Point2D p3 = new Point2D.Double(380, 0);
        Point2D p4 = new Point2D.Double(1000, 350);

        CropImage crop = new CropImage(p1, p2, p3, p4);
        ex.addAction(crop);

        p1 = new Point2D.Double(0, 0);
        p2 = new Point2D.Double(100, 500);
        p3 = new Point2D.Double(800, 450);
        p4 = new Point2D.Double(1000, 0);
        InvertColorImage invert = new InvertColorImage(p1, p2, p3, p4);
        ex.addAction(invert);

        p1 = new Point2D.Double(100, 100);
        p2 = new Point2D.Double(200, 200);
        InvertColorImage invert2 = new InvertColorImage(p1, p2);
        ex.addAction(invert2);

        p1 = new Point2D.Double(100, 100);
        p2 = new Point2D.Double(500, 300);

        
        p1 = new Point2D.Double(100, 100);
        p2 = new Point2D.Double(200, 100);
        p3 = new Point2D.Double(200, 300);
        p4 = new Point2D.Double(    100, 200);
        RotateImage rotate = new RotateImage( 35 , p1, p2, p3, p4);
        ex.addAction(rotate);

        ex.executeActions();
        BufferedImage newImg = ex.getLastImage();

        try {
            System.out.println("Saving image...");
            ImageIO.write(newImg, "jpg", new File("img_1_proccesed.jpg"));
            System.out.println("Image saved!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        sc.close();
    }
}