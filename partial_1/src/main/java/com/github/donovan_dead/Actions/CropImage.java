package com.github.donovan_dead.Actions;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadFactory;

import com.github.donovan_dead.Utils.Calculator;
import com.github.donovan_dead.Utils.SortByDegree;

public class CropImage implements Actionable{
    private Point2D[] points = new Point2D[4];
    private boolean isRectangle =false;

    public CropImage(Point2D p1, Point2D p2){
        points[0] = p1;
        points[1] = p2;
        isRectangle = true;
    }

    public CropImage(Point2D p1, Point2D p2, Point2D p3, Point2D p4){
        points[0] = p1;
        points[1] = p2;
        points[2] = p3;
        points[3] = p4;
    }

    private void correctComponents(BufferedImage img){
        for(Point2D p : points) {
            if(p == null) continue;
            
            if(p.getY() > img.getHeight()) p.setLocation(p.getX(), img.getHeight());
            if(p.getY() < 0) p.setLocation(p.getX(), 0);

            if(p.getX() < 0) p.setLocation(0, p.getY());
            if(p.getX() > img.getWidth()) p.setLocation(img.getWidth(), p.getY());

            p.setLocation(p.getX(), img.getHeight() - p.getY());
        }
    }

    @Override
    public BufferedImage ApplyAction( BufferedImage img ){
        correctComponents(img);

        if(isRectangle) return cropRectangle(img);
        else {
            SortByDegree.calculateCentroid(points);
            SortByDegree sd = new SortByDegree();
            Arrays.sort(points, sd);

            return cropIrregularRectangule(img);
        }
    }

    private BufferedImage cropRectangle( BufferedImage img ){

        BufferedImage newImg = img.getSubimage(
            (int)Math.min(points[0].getX(), points[1].getX()), 
            (int)Math.min(points[0].getY(), points[1].getY()),
            (int)Math.abs(points[1].getX()-points[0].getX()), 
            (int)Math.abs(points[1].getY()-points[0].getY())
        );
        
        return newImg;
    }

    private BufferedImage cropIrregularRectangule( BufferedImage img ){
        double minX = img.getWidth() + 1;
        double minY = img.getHeight() + 1;
        double maxX = -1;
        double maxY = -1;


        for(Point2D p : points){
            if(p == null) continue;

            if(p.getX() < minX) minX = p.getX();
            if(p.getX() > maxX) maxX = p.getX();

            if(p.getY() < minY) minY = p.getY();
            if(p.getY() > maxY) maxY = p.getY();
        
        }

        BufferedImage newImg = new BufferedImage(
            (int) Math.abs(maxX - minX),
            (int) Math.abs(maxY - minY),
            BufferedImage.TYPE_INT_RGB
        );

        ArrayList<Thread> threads = new ArrayList<>();
        ThreadFactory factory = Thread.ofVirtual().factory();

        final double cropArea = Calculator.CalculateAreaOfRectangule(points);
        final double tolerance = 1d + 1e-6;
        
        for (int h = (int) minY; h < (int) maxY; h++) {
            final int height = h;
            final int startW = (int) minX;
            final int endW = (int) maxX;
            final int mY = (int) minY;

            Runnable r = () -> {
                for (int w = startW; w < endW; w++) {
                    Point2D p = new Point2D.Double(w, height);

                    double areaSum =
                      Calculator.CalculateAreaOfTriangule(points[0], points[1], p)
                    + Calculator.CalculateAreaOfTriangule(points[1], points[2], p)
                    + Calculator.CalculateAreaOfTriangule(points[2], points[3], p)
                    + Calculator.CalculateAreaOfTriangule(points[3], points[0], p);

                    boolean dentro = cropArea * tolerance >= areaSum;

                    if (dentro) {
                        newImg.setRGB(w - startW, height - mY, img.getRGB(w, height));
                    } else {
                        newImg.setRGB(w - startW, height - mY, Color.BLACK.getRGB());
                    }
                }
            };

            Thread t = factory.newThread(r);
            threads.add(t);
            t.start();
        }

        for(Thread t : threads){
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return newImg;
    }
}
