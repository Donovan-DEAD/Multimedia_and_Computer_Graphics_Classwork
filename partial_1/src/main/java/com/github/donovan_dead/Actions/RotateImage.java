package com.github.donovan_dead.Actions;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadFactory;

import com.github.donovan_dead.Utils.Calculator;
import com.github.donovan_dead.Utils.ImageUtils;
import com.github.donovan_dead.Utils.SortByDegree;

public class RotateImage implements Actionable {
    private double angleRad;
    private Point2D[] points = new Point2D[4];
    private boolean isRectangle =false;

    private Point2D centroid;

    public RotateImage(float angelDegree, Point2D p1, Point2D p2){
        this.angleRad = Math.toRadians(angelDegree);
        this.points[0] = p1;
        this.points[1] = p2;
        isRectangle = true;
    }

    public RotateImage(double angelDegree, Point2D p1, Point2D p2, Point2D p3, Point2D p4){
        this.angleRad = Math.toRadians(angelDegree);
        this.points[0] = p1;
        this.points[1] = p2;
        this.points[2] = p3;
        this.points[3] = p4;
    }
    
    private Point2D[] getCorrectedPoints(BufferedImage img){
        Point2D[] correctedPoints = new Point2D[points.length];
        for(int i = 0; i < points.length; i++) {
            if(points[i] == null) continue;
            
            double x = points[i].getX();
            double y = points[i].getY();

            if(y > img.getHeight()) y = img.getHeight();
            if(y < 0) y = 0;

            if(x < 0) x = 0;
            if(x > img.getWidth()) x = img.getWidth();

            correctedPoints[i] = new Point2D.Double(x, img.getHeight() - y);
        }
        return correctedPoints;
    }


    @Override
    public BufferedImage ApplyAction(BufferedImage img){
        Point2D[] correctedPoints = getCorrectedPoints(img);

        if(isRectangle) return rotateRectangule(img, correctedPoints);
        else {
            SortByDegree.calculateCentroid(correctedPoints);
            SortByDegree sd = new SortByDegree();
            Arrays.sort(correctedPoints, sd);
           
            return rotateIrregularRectangule(img, correctedPoints);
        }
    }

    private BufferedImage rotateRectangule( BufferedImage img, Point2D[] pts ){
        centroid = new Point2D.Double((pts[0].getX() + pts[1].getX())/2, (pts[0].getY() + pts[1].getY())/2);
        BufferedImage newImg = ImageUtils.deepCopy(img);

        final double minX = Math.min(pts[0].getX(), pts[1].getX());
        final double minY = Math.min(pts[0].getY(), pts[1].getY());
        final double maxX = Math.max(pts[0].getX(), pts[1].getX());
        final double maxY = Math.max(pts[0].getY(), pts[1].getY());

        // Color the space in black where it was the original image
        for(int h = (int)minY; h < maxY; h++)
            for(int w = (int)minX; w < maxX; w++)
                if(h <= maxY && w <= maxX && h >= minY && w >= minX) newImg.setRGB(w, h, Color.BLACK.getRGB());


        for(int h = (int)minY; h < maxY; h++)
            for(int w = (int)minX; w < maxX; w++){
                double vecX = w - centroid.getX();
                double vecY = h - centroid.getY();

                double newX = Math.cos(angleRad) * vecX - Math.sin(angleRad) * vecY + centroid.getX();
                double newY = Math.sin(angleRad) * vecX + Math.cos(angleRad) * vecY + centroid.getY();

                if((int)Math.round(newX) < 0 || (int)Math.round(newX) >= img.getWidth() || (int)Math.round(newY) < 0 || (int)Math.round(newY) >= (img.getHeight())) continue;
                newImg.setRGB((int)Math.round(newX), (int)Math.round(newY), img.getRGB(w, h));
            }
            
        return newImg;
    }

    private BufferedImage rotateIrregularRectangule( BufferedImage img, Point2D[] pts ){
        centroid = SortByDegree.getCentroid();

        BufferedImage newImg = ImageUtils.deepCopy(img);        
        ArrayList<Thread> threads = new ArrayList<>();
        ThreadFactory factory = Thread.ofVirtual().factory();

        final double cropArea = Calculator.CalculateAreaOfRectangule(pts);

        double minX = img.getWidth() + 1;
        double minY = img.getHeight() + 1;
        double maxX = -1;
        double maxY = -1;


        for(Point2D p : pts){
            if(p.getX() < minX) minX = p.getX();
            if(p.getX() > maxX) maxX = p.getX();

            if(p.getY() < minY) minY = p.getY();
            if(p.getY() > maxY) maxY = p.getY();
        }

        for (int h = (int) minY; h < (int) maxY; h++){
            for (int w = (int) minX; w < (int) maxX; w++) {
                Point2D p = new Point2D.Double(w, h);

                if( cropArea < 
                      Calculator.CalculateAreaOfTriangule(pts[0], pts[1], p)
                    + Calculator.CalculateAreaOfTriangule(pts[1], pts[2], p)
                    + Calculator.CalculateAreaOfTriangule(pts[2], pts[3], p)
                    + Calculator.CalculateAreaOfTriangule(pts[3], pts[0], p)
                ) continue;

                newImg.setRGB(w, h, Color.BLACK.getRGB());
            }
        }


        for (int h = (int) minY; h < (int) maxY; h++) {
            final int height = h;
            final int startW = (int) minX;
            final int endW = (int) maxX;

            Runnable r = () -> {
                for (int w = startW; w < endW; w++) {
                    Point2D p = new Point2D.Double(w, height);

                    double areaSum =
                      Calculator.CalculateAreaOfTriangule(pts[0], pts[1], p)
                    + Calculator.CalculateAreaOfTriangule(pts[1], pts[2], p)
                    + Calculator.CalculateAreaOfTriangule(pts[2], pts[3], p)
                    + Calculator.CalculateAreaOfTriangule(pts[3], pts[0], p);

                    boolean dentro = cropArea >= areaSum;

                    if (dentro) {
                        double vecX = w - centroid.getX();
                        double vecY = height - centroid.getY();

                        double newX = Math.cos(angleRad) * vecX - Math.sin(angleRad) * vecY + centroid.getX();
                        double newY = Math.sin(angleRad) * vecX + Math.cos(angleRad) * vecY + centroid.getY();

                        if((int)Math.round(newX) < 0 || (int)Math.round(newX) >= img.getWidth() || (int)Math.round(newY) < 0 || (int)Math.round(newY) >= (img.getHeight())) continue;
                        newImg.setRGB((int)Math.round(newX), (int)Math.round(newY), img.getRGB(w, height));
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
