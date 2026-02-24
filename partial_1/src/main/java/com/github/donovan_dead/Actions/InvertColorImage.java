package com.github.donovan_dead.Actions;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadFactory;

import com.github.donovan_dead.Utils.Calculator;
import com.github.donovan_dead.Utils.SortByDegree;

public class InvertColorImage implements Actionable {
    
    private Point2D[] points = new Point2D[4];
    private boolean isRectangle =false;

    
    public InvertColorImage(Point2D p1, Point2D p2){
        points[0] = p1;
        points[1] = p2;
        isRectangle = true;
    }

    public InvertColorImage(Point2D p1, Point2D p2, Point2D p3, Point2D p4){
        points[0] = p1;
        points[1] = p2;
        points[2] = p3;
        points[3] = p4;
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


        if(isRectangle) return invertRectangule(img, correctedPoints);
        else {
            SortByDegree.calculateCentroid(correctedPoints);
            SortByDegree sd = new SortByDegree();
            Arrays.sort(correctedPoints, sd);

            return invertIrregularRectangule(img, correctedPoints);
        }
    }

    private BufferedImage invertRectangule( BufferedImage img, Point2D[] pts ){
        int start_x = (int)Math.min(pts[0].getX(), pts[1].getX());
        int start_y = (int)Math.min(pts[0].getY(), pts[1].getY());

        int end_x = (int)Math.max(pts[1].getX(),pts[0].getX());
        int end_y = (int)Math.max(pts[1].getY(),pts[0].getY());

        for(int h = start_y; h < end_y; h++)
            for(int w = start_x; w < end_x; w++)
                img.setRGB(w, h, ~img.getRGB(w, h));
            
        return img;
    }

    private BufferedImage invertIrregularRectangule( BufferedImage img, Point2D[] pts ){
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

        ArrayList<Thread> threads = new ArrayList<>();
        ThreadFactory factory = Thread.ofVirtual().factory();

        final double cropArea = Calculator.CalculateAreaOfRectangule(pts);
        
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
                        img.setRGB(w, height, ~img.getRGB(w, height));
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

        return img;
    }
}
